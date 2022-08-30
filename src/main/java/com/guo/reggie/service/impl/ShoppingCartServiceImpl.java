package com.guo.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guo.reggie.common.BaseContext;
import com.guo.reggie.common.R;
import com.guo.reggie.mapper.ShoppingCartMapper;
import com.guo.reggie.pojo.ShoppingCart;
import com.guo.reggie.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    /**
     * 查询当前菜品或套餐是否在购物车中，存在number+1，不存在添加到购物车
     * @param shoppingCart
     * @return
     */
    @Override
    public ShoppingCart add(ShoppingCart shoppingCart) {
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId());
        if(dishId!=null){
            //添加到购物车的是菜品
            lambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            //添加到购物车的是套餐
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart shoppingCart1 = shoppingCartMapper.selectOne(lambdaQueryWrapper);
        if(shoppingCart1!=null){
            //如果存在修改为Number+1
            shoppingCart1.setNumber(shoppingCart1.getNumber()+1);
            this.updateById(shoppingCart1);
        }else {
            //如果不存在插入信息
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            this.save(shoppingCart);
            shoppingCart1 = shoppingCart;
        }

        return shoppingCart1;
    }

    /**
     * 手机端查询购物车信息
     * @param currentId
     * @return
     */
    @Override
    public List<ShoppingCart> list(Long currentId) {
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,currentId);
        lambdaQueryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectList(lambdaQueryWrapper);
        return shoppingCarts;
    }

    /**
     * 清空购物车
     * @param currentId
     */
    @Override
    public void clean(Long currentId) {
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,currentId);
        shoppingCartMapper.delete(lambdaQueryWrapper);
    }

    /**
     * 购物车减少菜品，套餐
     * @param shoppingCart
     */
    @Override
    public void sub(ShoppingCart shoppingCart) {
        Long currentId = BaseContext.getCurrentId();
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,currentId);
        if(dishId!=null){
            lambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,setmealId);
        }
        ShoppingCart shoppingCart1 = shoppingCartMapper.selectOne(lambdaQueryWrapper);
        if(shoppingCart1.getNumber()>1){
            shoppingCart1.setNumber(shoppingCart1.getNumber()-1);
            this.updateById(shoppingCart1);
        }else {
            this.removeById(shoppingCart1);
        }

    }
}
