package com.yupi.yupao.service;

import com.yupi.yupao.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author 34234
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-12-01 10:56:33
*/
public interface UserService extends IService<User> {
    /**
     * 用户注册
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return
     */
    long userRegister(String userAccount, String userPassword,String checkPassword,String planetCode);

    /**
     * 用户登录
     * @param userAccount
     * @param userPassword
     * @param request
     * @return
     */
    User doLogin(String userAccount, String userPassword, HttpServletRequest request);


    /**
     * 用户脱敏
     * @param
     * @return
     */
    User getSafetyUser(User user);

    /**
     * 退出登录
     * @param request
     * @return
     */
    boolean userLogOut(HttpServletRequest request);


}
