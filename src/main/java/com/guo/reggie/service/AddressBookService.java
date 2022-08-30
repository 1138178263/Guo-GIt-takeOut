package com.guo.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guo.reggie.common.R;
import com.guo.reggie.pojo.AddressBook;

import java.util.List;

public interface AddressBookService extends IService<AddressBook> {
    //查询地址簿信息
    public List<AddressBook> selectAddressBook(String userId);
    //修改地址簿信息
    public void updateBook(AddressBook addressBook);
    //设置地址簿信息为默认
    public void deFault(Long id);
    //结账界面回显默认地址信息
    public AddressBook isDeFault(Long currentId);

}
