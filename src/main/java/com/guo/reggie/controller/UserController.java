package com.guo.reggie.controller;

import com.guo.reggie.common.R;
import com.guo.reggie.pojo.User;
import com.guo.reggie.service.UserService;
import com.guo.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 根据手机短信获取验证码
     * @param user
     * @param session
     * @return
     */
   @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        //生成随机的4位验证码
        if(StringUtils.isNotEmpty(phone)) {
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}", code);
            //调用阿里云短信服务的API完成短信发送
            //SMSUtils.sendMessage("阿里云短信测试", "SMS_154950909", phone, code);
            //需要将生成的验证码保存到 Session
            session.setAttribute("code", code);
            return R.success("获取验证码成功...");
        }
        return R.error("获取验证码失败...");
    }

    /**
     * 手机端登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从Session中获取保存的验证码
        Object codeInSession = session.getAttribute("code");
        //进行验证码的比对（页面提交的验证码和Session中保存的验证码比对）
        if(codeInSession!=null&&codeInSession.equals(code)) {
            //如果能够比对成功，说明登陆成功
            //判断当前手机号是否为新用户，如果是新用户自动完成注册
            User user = userService.insertPhone(phone);
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登录失败...");
    }


}
