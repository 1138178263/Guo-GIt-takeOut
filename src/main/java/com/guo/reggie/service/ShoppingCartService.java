package com.guo.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guo.reggie.common.R;
import com.guo.reggie.pojo.ShoppingCart;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface ShoppingCartService extends IService<ShoppingCart> {
    //查询当前菜品或套餐是否在购物车中，存在number+1，不存在添加到购物车
    public ShoppingCart add(ShoppingCart shoppingCart);
    //手机端查询购物车信息
    public List<ShoppingCart> list(Long currentId);
    //清空购物车
    public void clean(Long currentId);
    //购物车减少菜品，套餐
    public void sub(ShoppingCart shoppingCart);
}
