package com.m2mall.controller.portal.backend;

import com.m2mall.common.Const;
import com.m2mall.common.ServerResponse;
import com.m2mall.pojo.User;
import com.m2mall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manager/user/")
public class UserManagerController {



    @Autowired
    private IUserService iUserService;


    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession httpSession){
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()){
            User curUser = response.getData();
            if (curUser.getRole()== Const.Role.ROLE_ADMIN){
                httpSession.setAttribute(Const.CURRENT_USER,curUser);
            }else{
                return ServerResponse.createByErrorMessage("该用户不是管理员,无法登录");
            }
        }
        return response;
        }
}
