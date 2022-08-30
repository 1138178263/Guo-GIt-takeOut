package com.guo.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guo.reggie.pojo.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {
    //分类管理分页查询
    public Page<Category> page(int page,int pageSize);
    //根据Id，删除分类
    public void remove(Long id);
    //根据条件来查询分类数据
    public List<Category> list(Category category);
}
