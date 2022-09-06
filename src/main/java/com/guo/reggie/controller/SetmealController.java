package com.guo.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guo.reggie.common.R;
import com.guo.reggie.dto.SetmealDishDto;
import com.guo.reggie.dto.SetmealDto;
import com.guo.reggie.pojo.Dish;
import com.guo.reggie.pojo.Setmeal;
import com.guo.reggie.pojo.SetmealDish;
import com.guo.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 套餐
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("新增套餐：{}",setmealDto.getName());
        setmealService.saveWithDish(setmealDto);
        return R.success("新增菜品成功");
    }

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page setmealDtoPage = setmealService.page(page, pageSize, name);
        return R.success(setmealDtoPage);
    }

    /**
     * 单个或批量，1起售，0停售
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> status(@PathVariable Integer status,Long[] ids){
        setmealService.status(status,ids);
        return R.success("状态修改成功");
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids){
        if(ids.isEmpty()){
            return R.success("请选择要删除的套餐");
        }
        setmealService.deleteWithDish(ids);
        return R.success("删除成功");
    }

    /**
     * 回显套餐信息
     * @param categoryId
     * @param status
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "setmealCache",key="#categoryId+'_'+#status")
    public R<List<Setmeal>> list(Long categoryId,Integer status){
        List<Setmeal> list = setmealService.list(categoryId, status);
        return R.success(list);
    }

    /**
     * 手机端回显套餐包含的菜品信息
     * @return
     */
    @GetMapping("/dish/{id}")
    public R<List<SetmealDishDto>> saveDish(@PathVariable Long id){
        List<SetmealDishDto> SetmealDishDtos = setmealService.saveDish(id);
        return R.success(SetmealDishDtos);
    }

}
