package com.guo.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guo.reggie.common.R;
import com.guo.reggie.mapper.AddressBookMapper;
import com.guo.reggie.pojo.AddressBook;
import com.guo.reggie.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    /**
     * 查询地址簿信息
     * @param userId
     * @return
     */
    @Override
    public List<AddressBook> selectAddressBook(String userId) {
        LambdaQueryWrapper<AddressBook> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AddressBook::getUserId,userId);
        lambdaQueryWrapper.orderByDesc(AddressBook::getIsDefault);
        List<AddressBook> addressBooks = addressBookMapper.selectList(lambdaQueryWrapper);
        return addressBooks;
    }

    /**
     * 修改地址簿信息
     * @param addressBook
     */
    @Override
    public void updateBook(AddressBook addressBook) {
        LambdaQueryWrapper<AddressBook> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AddressBook::getId,addressBook.getId());
        addressBookMapper.update(addressBook,lambdaQueryWrapper);
    }

    /**
     * 设置地址簿信息为默认
     * @param id
     */
    @Override
    public void deFault(Long id) {
        //查询以前的默认的信息
        LambdaQueryWrapper<AddressBook> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AddressBook::getIsDefault,1);
        AddressBook addressBook = addressBookMapper.selectOne(lambdaQueryWrapper);
        //更改以前的为不默认
        if(addressBook!=null){
            LambdaUpdateWrapper<AddressBook> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.set(AddressBook::getIsDefault,0);
            this.update(lambdaUpdateWrapper);
        }
        //根据id更改现在的
        LambdaUpdateWrapper<AddressBook> lambdaUpdateWrapper1 = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper1.eq(AddressBook::getId,id);
        lambdaUpdateWrapper1.set(AddressBook::getIsDefault,1);
        this.update(lambdaUpdateWrapper1);
    }

    /**
     * 结账界面回显默认地址信息
     * @param currentId
     * @return
     */
    @Override
    public AddressBook isDeFault(Long currentId) {
        LambdaQueryWrapper<AddressBook> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AddressBook::getUserId,currentId);
        lambdaQueryWrapper.eq(AddressBook::getIsDefault,1);
        AddressBook addressBook = addressBookMapper.selectOne(lambdaQueryWrapper);
        return addressBook;
    }
}
