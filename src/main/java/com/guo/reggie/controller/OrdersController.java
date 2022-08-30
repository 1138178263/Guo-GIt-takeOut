package com.guo.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guo.reggie.common.R;
import com.guo.reggie.dto.OrdersDto;
import com.guo.reggie.pojo.Orders;
import com.guo.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("submit")
    public R<String> submit(@RequestBody Orders orders){
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 回显订单信息
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> userPage(int page, int pageSize){
        Page ordersDtoPage = ordersService.userPage(page, pageSize);
        return R.success(ordersDtoPage);
    }

}
