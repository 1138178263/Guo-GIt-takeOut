package com.guo.reggie.controller;

import com.guo.reggie.common.BaseContext;
import com.guo.reggie.common.R;
import com.guo.reggie.pojo.AddressBook;
import com.guo.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 地址簿管理
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 查询地址簿信息
     * @param httpSession
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(HttpSession httpSession){
        String userId = httpSession.getAttribute("user").toString();
        List<AddressBook> addressBooks = addressBookService.selectAddressBook(userId);
        return R.success(addressBooks);
    }

    /**
     * 新增地址簿信息
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 修改前根据id回显地址簿信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> selectOne(@PathVariable Long id){
        AddressBook byId = addressBookService.getById(id);
        return R.success(byId);
    }

    /**
     * 修改地址簿信息
     * @param addressBook
     * @return
     */
    @PutMapping
    public R<String> updateBook(@RequestBody AddressBook addressBook){
        addressBookService.updateBook(addressBook);
        return R.success("修改信息成功...");
    }

    /**
     * 设置地址簿信息为默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public R<String> deFault(@RequestBody AddressBook addressBook){
        addressBookService.deFault(addressBook.getId());
        return R.success("修改默认成功...");
    }

    /**
     * 结账界面回显默认地址信息
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> isDeFault(){
        Long currentId = BaseContext.getCurrentId();
        AddressBook deFault = addressBookService.isDeFault(currentId);
        return R.success(deFault);
    }

}
