package com.guo.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guo.reggie.common.BaseContext;
import com.guo.reggie.common.CustomException;
import com.guo.reggie.dto.OrdersDto;
import com.guo.reggie.mapper.OrdersMapper;
import com.guo.reggie.pojo.*;
import com.guo.reggie.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     * @param orders
     */
    @Override
    @Transactional
    public void submit(Orders orders) {
        //获取当前用户的id
        Long userId = BaseContext.getCurrentId();
        //查询当前购物车的数据
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(lambdaQueryWrapper);
        if(shoppingCarts == null&&shoppingCarts.size()==0){
            throw new CustomException("购物车为空，不能下单");
        }
        //查询用户数据
        User user = userService.getById(userId);
        //查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        //生成订单号
        long orderId = IdWorker.getId();
        //计算订单总金额
        //原子类累加器
        AtomicInteger atomicInteger = new AtomicInteger(0);
        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item)->{
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            atomicInteger.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());
        //向订单表插入数据
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(atomicInteger.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        this.save(orders);
        //向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);
        //提交订单成功后，清空购物车数据
        shoppingCartService.remove(lambdaQueryWrapper);
    }

    /**
     * 回显订单信息
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Page<OrdersDto> userPage(int page, int pageSize) {
        //分页构造器
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();
        //用户id
        Long currentId = BaseContext.getCurrentId();
        //查询订单信息
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Orders::getUserId,currentId);
        this.page(pageInfo,lambdaQueryWrapper);
        BeanUtils.copyProperties(pageInfo,ordersDtoPage,"records");
        //查询订单明细信息
        List<Orders> orders = pageInfo.getRecords();
        List<OrdersDto> list = orders.stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item,ordersDto);

            Long orderid = item.getId();//订单号

            //根据订单号查询订单详情
            //构造条件构造器
            LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            //添加过滤条件
            lambdaQueryWrapper1.eq(OrderDetail::getOrderId, orderid);
            //执行查询
            List<OrderDetail> orderDetailList = orderDetailService.list(lambdaQueryWrapper1);

            ordersDto.setOrderDetails(orderDetailList);
            ordersDto.setSumNum(orderDetailList.size());

            return ordersDto;
        }).collect(Collectors.toList());
        ordersDtoPage.setRecords(list);
        return ordersDtoPage;
    }

}
