package com.guo.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guo.reggie.common.CustomException;
import com.guo.reggie.mapper.CategoryMapper;
import com.guo.reggie.pojo.Category;
import com.guo.reggie.pojo.Dish;
import com.guo.reggie.pojo.Setmeal;
import com.guo.reggie.service.CategoryService;
import com.guo.reggie.service.DishService;
import com.guo.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper,Category> implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    /**
     * 分类管理分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Page<Category> page(int page, int pageSize) {
        Page<Category> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(Category::getSort);
        categoryMapper.selectPage(pageInfo, lambdaQueryWrapper);
        return pageInfo;
    }

    /**
     * 根据Id，删除分类
     * @param id
     */
    @Override
    public void remove(Long id) {
        //查询当前分类是否关联了菜品，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(dishLambdaQueryWrapper);
        if(count>0){
            //已经关联需要抛出异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }
        //查询当前分类是否关联了套餐，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper= new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count1 = setmealService.count(setmealLambdaQueryWrapper);
        if(count1>0){
            //已经关联需要抛出异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }
        //正常删除
        super.removeById(id);
    }

    /**
     * 根据条件来查询分类数据
     * @param category
     * @return
     */
    @Override
    public List<Category> list(Category category) {
        //条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        lambdaQueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //添加排序条件
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByAsc(Category::getUpdateTime);
        //执行
        /*SELECT
            id,type,name,sort,create_time,update_time,create_user,update_user
            FROM
                category
            WHERE
                (type = ?)
            ORDER BY
                sort ASC,
                update_time ASC*/
        List<Category> categories = categoryMapper.selectList(lambdaQueryWrapper);
        return categories;
    }


}
