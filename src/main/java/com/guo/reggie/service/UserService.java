package com.guo.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guo.reggie.pojo.User;

public interface UserService extends IService<User> {
    //插入用户手机信息
    public User insertPhone(String phone);
}
