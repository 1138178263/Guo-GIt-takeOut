package com.guo.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guo.reggie.common.CustomException;
import com.guo.reggie.dto.SetmealDishDto;
import com.guo.reggie.dto.SetmealDto;
import com.guo.reggie.mapper.DishMapper;
import com.guo.reggie.mapper.SetmealDishMapper;
import com.guo.reggie.mapper.SetmealMapper;
import com.guo.reggie.pojo.Category;
import com.guo.reggie.pojo.Dish;
import com.guo.reggie.pojo.Setmeal;
import com.guo.reggie.pojo.SetmealDish;
import com.guo.reggie.service.CategoryService;
import com.guo.reggie.service.DishService;
import com.guo.reggie.service.SetmealDishService;
import com.guo.reggie.service.SetmealService;
import com.sun.deploy.ui.DialogTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;
    /**
     * 新增菜品
     * @param setmealDto
     */
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //添加基本套餐的信息到 setmeal表
        setmealMapper.insert(setmealDto);
        //添加套餐的菜品到 setmeal_dish表
        Long setmealId = setmealDto.getId();       //套餐id
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();    //菜品信息
        setmealDishes = setmealDishes.stream().map((item)-> {
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public Page page(int page, int pageSize, String name) {
        //分页构造器
        Page<Setmeal> pageInfo = new Page<>();
        Page<SetmealDto> setmealDtoPage= new Page<>();
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //根据名字模糊查询
        queryWrapper.like(name!=null,Setmeal::getName,name);
        //根据更新时间降序排序
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealMapper.selectPage(pageInfo,queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo,setmealDtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item)->{
            SetmealDto setmealDto = new SetmealDto();
            //拷贝Setmeal基本信息到setmealDto
            BeanUtils.copyProperties(item,setmealDto);
            //分类的id
            Long categoryId = item.getCategoryId();
            //根据id，查询分类
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                //获取分类名字
                String categoryName = category.getName();
                //将分类名字赋值给setmealDto的categoryName属性
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        //将加入分类名字的信息重新赋值给setmealDtoPage分页构造器
        setmealDtoPage.setRecords(list);
        return setmealDtoPage;
    }

    /**
     * 单个或批量，1起售，0停售
     * @param status
     * @param ids
     */
    @Override
    public void status(Integer status, Long[] ids) {
        LambdaUpdateWrapper<Setmeal> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.in(Setmeal::getId,ids);
        lambdaUpdateWrapper.set(Setmeal::getStatus,status);
        this.update(lambdaUpdateWrapper);
    }

    /**
     * 删除套餐
     * @param ids
     */
    @Override
    @Transactional
    public void deleteWithDish(List<Long> ids) {
        //判断状态是否可以删除
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Setmeal::getId,ids);
        lambdaQueryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(lambdaQueryWrapper);
        if(count>0){
            throw new CustomException("当前套餐为起售状态禁止删除");
        }
        //先删除setmeal表
        setmealMapper.deleteBatchIds(ids);
        //再删除setmeal_dish表
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lambdaQueryWrapper1);
    }

    /**
     * 回显套餐信息
     * @param categoryId
     * @param status
     * @return
     */
    @Override
    public List<Setmeal> list(Long categoryId, Integer status) {
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Setmeal::getStatus,status);
        lambdaQueryWrapper.eq(Setmeal::getCategoryId,categoryId);
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmeals = setmealMapper.selectList(lambdaQueryWrapper);
        return setmeals;
    }

    /**
     * 手机端回显套餐包含的菜品信息
     * @param id
     * @return
     */
    @Override
    public List<SetmealDishDto> saveDish(Long id) {
        //查询套餐包含的菜品的基本信息
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> setmealDishes = setmealDishMapper.selectList(lambdaQueryWrapper);
        //利用SetmealDishDto类保存餐包含的菜品的基本信息和菜品图片位置信息
        List<SetmealDishDto> dishes = setmealDishes.stream().map((item)->{
            SetmealDishDto setmealDishDto = new SetmealDishDto();
            //复制类属性
            BeanUtils.copyProperties(item,setmealDishDto);
            LambdaQueryWrapper<Dish> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            lambdaQueryWrapper1.eq(Dish::getId,item.getDishId());
            Dish dish = dishMapper.selectOne(lambdaQueryWrapper1);
            String image = dish.getImage();
            setmealDishDto.setImage(image);
            return setmealDishDto;
        }).collect(Collectors.toList());
        return dishes;
    }

}
