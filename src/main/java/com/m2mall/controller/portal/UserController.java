package com.m2mall.controller.portal;

import com.m2mall.common.Const;
import com.m2mall.common.ResponseCode;
import com.m2mall.common.ServerResponse;
import com.m2mall.pojo.User;
import com.m2mall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;


// 获取用户输入的账号密码,匹配数据库数据,完成不同的业务

@Controller
@RequestMapping("/user/")
public class UserController {


    @Autowired
    IUserService iUserService; //

    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String userName, String password, HttpSession httpSession) {
        //service -> myBratis -> dao
        ServerResponse<User> response = iUserService.login(userName, password);
        if (response.isSuccess()) {
            httpSession.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }


    @RequestMapping(value = "logout.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession httpSession) {
        httpSession.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }


    @RequestMapping(value = "register.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> register(User user) {
        return iUserService.register(user);
    }

    @RequestMapping(value = "check_valid.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type) {
        return iUserService.checkValid(str, type);
    }


    @RequestMapping(value = "get_user_info.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession httpSession) {
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录,无法获取用户信息");
        }
        return ServerResponse.createBySuccess(user);

    }

    // 获取密码提示问题
    @RequestMapping(value = "forget_get_question.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse forgetGetQuestion(String username) {
        return iUserService.selectQuestion(username);
    }


    //校验问题答案
    @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username,question,answer);
    }


    // 重置密码
    @RequestMapping(value = "forget_rest_password.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> forgetRestPassword(String username,String newPassword,String forgetToken){
        return iUserService.forgetResetPassword(username,newPassword,forgetToken);
    }



    @RequestMapping(value = "rest_password.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> restPassword(HttpSession session,String passwordOld,String passwordNew){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
         if (user==null){
            return ServerResponse.createByErrorMessage("用户未登录");
         }
         return iUserService.restPassword(user,passwordNew,passwordOld);

     }

    /**
     *   登录判断
     * @param session
     * @param user
     * @return
     */
    @RequestMapping(value = "update_information.do",method = RequestMethod.GET)
    @ResponseBody
     public ServerResponse<User> update_information(HttpSession session,User user){
        //currentUser 服务器存的登录用户的User信息
         //user   前端给的user信息(没有数据库的userid 值)
         User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
          if (currentUser==null){
              return ServerResponse.createByErrorMessage("用户未登录");
          }
          // 防止横向越权
          user.setId(currentUser.getId());
          user.setUsername(currentUser.getUsername());

         ServerResponse<User> response = iUserService.updateInformation(user);
         if (response.isSuccess()){
             session.setAttribute(Const.CURRENT_USER,response.getData());  //更新session 的信息
         }

         return response;
         }



        // 更新信息之前一般都要拉取用户信息,所以只需要在拉取个人信息的时候,强制登录
    /**
     *   登录判断
     * @param session
     * @return
     */
    @RequestMapping(value = "get_information.do",method = RequestMethod.GET)
    @ResponseBody
     public ServerResponse<User> get_information(HttpSession session){
         User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
         if (currentUser==null){
             return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录,需要强制登录status=10");
         }

         return iUserService.get_information(currentUser.getId());
     }















}
