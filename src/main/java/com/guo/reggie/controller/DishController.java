package com.guo.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guo.reggie.common.R;
import com.guo.reggie.dto.DishDto;
import com.guo.reggie.pojo.Category;
import com.guo.reggie.pojo.Dish;
import com.guo.reggie.service.DishFlavorService;
import com.guo.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 菜品分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page<DishDto>> Page(int page, int pageSize,String name){
        Page<DishDto> pageInfo = dishService.page(page, pageSize, name);
        return R.success(pageInfo);
    }

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 根据id查询菜品信息和对应口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto byIdWithFlavor = dishService.getByIdWithFlavor(id);
        return R.success(byIdWithFlavor);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 根据id，单个或批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long[] ids){
        dishService.deleteWithFlavor(ids);
        return R.success("删除成功");
    }

    /**
     *单个或批量0停售，1起售
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status,Long[] ids){
        dishService.status(status,ids);
        return R.success("修改状态成功");
    }

    /**
     * 回显每个分类的菜品
     * @param categoryId
     * @return
     */
    @GetMapping("list")
    public R<List<DishDto>> list(Long categoryId){
        List<DishDto> list = dishService.list(categoryId);
        return R.success(list);
    }
}
