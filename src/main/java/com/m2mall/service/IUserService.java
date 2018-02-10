package com.m2mall.service;

import com.m2mall.common.ServerResponse;
import com.m2mall.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;

public interface IUserService {


    //登录
    ServerResponse<User> login(String userName, String password);


    //注册
    ServerResponse<String> register(User user);


    // 校验
    ServerResponse<String> checkValid(String str, String type);

    //得到密码提示问题
    ServerResponse selectQuestion(String username);

    // 校验答案...
    ServerResponse<String> checkAnswer(String username, String question, String answer);


    // 根据安全问题重置密码

    ServerResponse<String> forgetResetPassword(String username,String newpassword,String forgetToken);


    // 更具旧密码重置密码
    ServerResponse<String> restPassword(User user,String passwordNew,String passwordOld);

    ServerResponse<User> updateInformation(User user);



    ServerResponse<User> get_information(Integer userid);
}