package com.guo.reggie.controller;

import com.guo.reggie.common.BaseContext;
import com.guo.reggie.common.R;
import com.guo.reggie.pojo.Category;
import com.guo.reggie.pojo.ShoppingCart;
import com.guo.reggie.service.CategoryService;
import com.guo.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 手机端查询购物车信息
     * @return
     */
   @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
       Long currentId = BaseContext.getCurrentId();
       List<ShoppingCart> list = shoppingCartService.list(currentId);
       return R.success(list);
    }

    /**
     * 查询当前菜品或套餐是否在购物车中，存在number+1，不存在添加到购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        //设置用户id，指定当前是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        //查询当前菜品或套餐是否在购物车中，存在number+1，不存在添加到购物车
        ShoppingCart add = shoppingCartService.add(shoppingCart);
        return R.success(add);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        Long currentId = BaseContext.getCurrentId();
        shoppingCartService.clean(currentId);
        return R.success("清空购物车成功");
    }

    /**
     * 购物车减少菜品，套餐
     * @return
     */
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart){
        shoppingCartService.sub(shoppingCart);
        return R.success("减少此订单成功");
    }


}
