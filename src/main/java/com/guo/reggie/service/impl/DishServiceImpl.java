package com.guo.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guo.reggie.common.CustomException;
import com.guo.reggie.dto.DishDto;
import com.guo.reggie.mapper.DishFlavorMapper;
import com.guo.reggie.mapper.DishMapper;
import com.guo.reggie.pojo.Category;
import com.guo.reggie.pojo.Dish;
import com.guo.reggie.pojo.DishFlavor;
import com.guo.reggie.service.CategoryService;
import com.guo.reggie.service.DishFlavorService;
import com.guo.reggie.service.DishService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ch.qos.logback.core.util.OptionHelper.isEmpty;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 菜品分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Page<DishDto> page(int page, int pageSize,String name) {
        //构造分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //查询不含分类名的菜品信息
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        lambdaQueryWrapper.orderByAsc(Dish::getSort);
        dishMapper.selectPage(pageInfo,lambdaQueryWrapper);
        //查询菜品的分类名字
        //将Dish在分页构造器的信息copy到DishDto的分页构造器
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        //获取Dish分页构造器的信息
        List<Dish> records = pageInfo.getRecords();
        //遍历集合
        List<DishDto> list = records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            //将每一个Dish实体类数据copy到DishDto
            BeanUtils.copyProperties(item,dishDto);
            //根据Dish表中的category_id字段查询分类对象
            Long categoryId = item.getCategoryId();     //分类id
            Category category = categoryService.getById(categoryId);
            //获取分类名字
            String categoryName = category.getName();
            //将分类名字添加到dishDto的实体类
            dishDto.setCategoryName(categoryName);
            return dishDto;
        }).collect(Collectors.toList());
        //将新数据添加到dishDto的分页构造器中
        dishDtoPage.setRecords(list);
        return dishDtoPage;
    }

    /**
     * 新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish、dish_flavor
     * @param dishDto
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜单表dish
        dishMapper.insert(dishDto);
        Long dishId = dishDto.getId();  //菜品的 id
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item)-> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息和对应口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        DishDto dishDto = new DishDto();
        //查询菜品的基本信息，从dish表查询
        Dish dish = dishMapper.selectById(id);
        BeanUtils.copyProperties(dish,dishDto);
        //查询当前菜品的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
        dishDto.setFlavors(dishFlavorList);
        return dishDto;
    }

    /**
     * 修改菜品
     * @param dishDto
     */
    @Transactional
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        dishMapper.updateById(dishDto);
        //清理当前菜品对应口味数据dish_flavor表
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(lambdaQueryWrapper);
        //添加当前提交过来的口味数据dish_flavor表
        List<DishFlavor> flavors = dishDto.getFlavors();
        Long dishDtoId = dishDto.getId();
        flavors = flavors.stream().map((item)-> {
            item.setDishId(dishDtoId);
            return item;
        }).collect(Collectors.toList());
        dishDto.setFlavors(flavors);
    }

    /**
     * 根据id，单个或批量删除菜品
     * @param ids
     */
    @Transactional
    @Override
    public void deleteWithFlavor(Long[] ids) {
        List<Long> longs = Arrays.asList(ids);
        //先判断菜品状态，是否可删
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Dish::getId,longs);
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        int count = dishMapper.selectCount(lambdaQueryWrapper);
        if(count>0){
            throw new CustomException("菜品为启售状态不可删除");
        }
        //停售状态，可删除
        this.removeByIds(longs);
       //删除菜品口味
       LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper1 =new LambdaQueryWrapper<>();
       lambdaQueryWrapper1.in(DishFlavor::getDishId,longs);
       dishFlavorService.remove(lambdaQueryWrapper1);
    }

    /**
     * 单个或批量停售,起售
     * @param ids
     */
    @Transactional
    @Override
    public void status(Integer status,Long[] ids) {
        List<Long> longs = Arrays.asList(ids);
        //设置status,0停售,1起售
        LambdaUpdateWrapper<Dish> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.in(Dish::getId,longs);
        lambdaUpdateWrapper.set(Dish::getStatus,status);
        this.update(lambdaUpdateWrapper);
    }

    /**
     * 回显新增套餐的菜品
     * @param categoryId
     * @return
     */
    @Override
    public List<DishDto> list(Long categoryId) {
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(categoryId!=null,Dish::getCategoryId,categoryId);
        //查询为起售状态的菜品
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishes = dishMapper.selectList(lambdaQueryWrapper);

        List<DishDto> dishDtos = dishes.stream().map((item)->{
            DishDto dishDto = new DishDto();
            //将每一个Dish实体类数据copy到DishDto
            BeanUtils.copyProperties(item,dishDto);
            //根据Dish表中的category_id字段查询分类对象
            Category category = categoryService.getById(categoryId);
            //获取分类名字
            String categoryName = category.getName();
            //将分类名字添加到dishDto的实体类
            dishDto.setCategoryName(categoryName);
            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            lambdaQueryWrapper1.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper1);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        return dishDtos;
    }


}
