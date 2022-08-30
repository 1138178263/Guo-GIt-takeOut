package com.guo.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guo.reggie.common.R;
import com.guo.reggie.pojo.Category;
import com.guo.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 */
@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category:{}",category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * 分类管理分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page<Category>> page(int page ,int pageSize){
        Page<Category> pageInfo = categoryService.page(page, pageSize);
        return R.success(pageInfo);
    }

    /**
     * 根据 id，删除分类
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("根据id，删除分类");
        categoryService.remove(ids);
        return R.success("删除分类成功");
    }

    /**
     * 根据id修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息：{}",category);
        //UPDATE category SET name=?, sort=?, update_time=?, update_user=? WHERE id=?
        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }

    /**
     * 根据条件来查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        List<Category> categories = categoryService.list(category);
        return R.success(categories);
    }

}
