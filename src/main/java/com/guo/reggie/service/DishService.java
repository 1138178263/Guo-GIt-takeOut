package com.guo.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guo.reggie.common.R;
import com.guo.reggie.dto.DishDto;
import com.guo.reggie.pojo.Dish;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface DishService extends IService<Dish> {
    //菜品分页查询
    public Page<DishDto> page(int page,int pageSize,String name);
    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish、dish_flavor
    public void saveWithFlavor(DishDto dishDto);
    //根据id查询菜品信息和对应口味信息
    public DishDto getByIdWithFlavor(Long id);
    //修改菜品
    public void updateWithFlavor(DishDto dishDto);
    //单个或批量删除菜品
    public void deleteWithFlavor(Long[] ids);
    //单个或批量停售，起售
    public void status(Integer status,Long[] ids);
    //回显每个分类的菜品
    public List<DishDto> list(Long categoryId);
}
