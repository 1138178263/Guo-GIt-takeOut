package com.guo.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guo.reggie.pojo.Employee;

public interface EmployeeService extends IService<Employee> {
    //员工登录
    public Employee login(Employee employee);
    //分页模糊查询
    public Page page(int page,int pageSize,String name);
}
