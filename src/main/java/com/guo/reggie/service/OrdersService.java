package com.guo.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guo.reggie.pojo.Orders;

public interface OrdersService extends IService<Orders> {
    //用户下单
    public void submit(Orders orders);
    //回显订单信息
    public Page userPage(int page, int pageSize);
}
