package com.guo.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guo.reggie.common.R;
import com.guo.reggie.dto.SetmealDishDto;
import com.guo.reggie.dto.SetmealDto;
import com.guo.reggie.pojo.Dish;
import com.guo.reggie.pojo.Setmeal;
import com.guo.reggie.pojo.SetmealDish;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    //新增套餐
    public void saveWithDish(SetmealDto setmealDto);
    //套餐分页查询
    public Page page(int page,int pageSize,String name);
    //单个或批量，1起售，0停售
    public void status(Integer status, Long[] ids);
    //删除套餐
    public void deleteWithDish(List<Long> ids);
    //回显套餐信息
    public List<Setmeal> list(Long categoryId,Integer status);
    //回显套餐的菜品信息
    public List<SetmealDishDto> saveDish(Long id);
}
