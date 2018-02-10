package com.m2mall.service.impl;

import com.m2mall.common.Const;
import com.m2mall.common.ServerResponse;
import com.m2mall.common.TokenCache;
import com.m2mall.dao.UserMapper;
import com.m2mall.pojo.User;
import com.m2mall.service.IUserService;
import com.m2mall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String userName, String password) {

        int resultCount = userMapper.checkUserName(userName);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("用户名不存在!");
        }
        password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(userName, password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("密码错误!");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功!", user);
    }

    @Override
    public ServerResponse<String> register(User user) {


        ServerResponse<String> validResponse = this.checkValid(user.getUsername(), Const.CURRENT_USER);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }
        validResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }


        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("注册失败");//数据库插入异常
        }

        return ServerResponse.createBySuccessMessage("注册成功");
    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNoneBlank(type)) {
            if (type.equals(Const.USERNAME)) {
                int resultCount = userMapper.checkUserName(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("注册的用户名已存在");
                }
            }
            if (type.equals(Const.EMAIL)) {
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("注册邮箱已存在");
                }
            }
            return ServerResponse.createBySuccessMessage("校验成功");
        } else {
            return ServerResponse.createByErrorMessage("参数错误");
        }


    }


    // 密码提示问题的获取 , 校验用户名是否存在
    public ServerResponse selectQuestion(String username) {
        ServerResponse<String> validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {     // 校验成功,说明用户名不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isBlank(question)) {
            return ServerResponse.createByErrorMessage("找回密码的问题是空的");
        }

        return ServerResponse.createBySuccess(question);
    }

    // 校验问题答案
    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount > 0) {  // 用户名,问题 答案跟数据库一致....说明 ..通过
            String forgetToken = UUID.randomUUID().toString();
            //吧token 放到本地设置有效期(放入缓存)
            TokenCache.put(TokenCache.TOKEN_PREFIX + username, forgetToken);
            return ServerResponse.createBySuccess(forgetToken);  //forgettoken 返回给上一层,用户根据这个forgettoken,从新设置密码.
        }
        return ServerResponse.createByErrorMessage("答案错误");
    }

    // 从内存根据 token_#{username} 取出对应的value(forgetToken) , 校验,
    // 验证通过插入新的密码
    @Override
    public ServerResponse<String> forgetResetPassword(String username, String newpassword, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误,token需要传递");
        }
        if (StringUtils.isBlank(username)){
            return ServerResponse.createByErrorMessage("用户不存在");
        }


        String forgetTokenFromCache = TokenCache.getValue(TokenCache.TOKEN_PREFIX + username);


        if (StringUtils.isBlank(forgetToken)) {
            return ServerResponse.createByErrorMessage("token无效或过期!");
        }
        if (forgetToken.equals(forgetTokenFromCache)){
            String md5Password = MD5Util.MD5EncodeUtf8(newpassword);
            int resultCount = userMapper.updatePasswordByUserName(username, md5Password);

            if (resultCount>0){
                return ServerResponse.createBySuccess("重置密码成功");
            }else{
                return ServerResponse.createByErrorMessage("token错误,请重新获取重置密码token");
            }
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    @Override
    public ServerResponse<String> restPassword(User user, String passwordNew, String passwordOld) {
        int resultCount = userMapper.checkPassword(user.getId(), MD5Util.MD5EncodeUtf8(passwordOld));
        if (resultCount==0) {
        return ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(passwordNew);
        resultCount = userMapper.updateByPrimaryKeySelective(user);
        if (resultCount>0){
            return ServerResponse.createBySuccessMessage("更新密码成功");
        }

        return ServerResponse.createByErrorMessage("更新密码失败");
    }


    /**
     *
     * @param user  用户输入的user
     * @return
     */
    @Override
    public ServerResponse<User> updateInformation(User user){
        //username 是不能被更新
        int resultCount = userMapper.checkEmailByUserId(user.getId(), user.getEmail());
        if (resultCount>0){
            return ServerResponse.createByErrorMessage("email已存在,请尝试更换email在尝试更新");

        }
        User updateUser=new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        resultCount = userMapper.updateByPrimaryKeySelective(user);
        if (resultCount>0){
            return ServerResponse.createBySuccessMessage("更新个人信息成功");
        }


        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    /**
     *
     * @param userid  httpSession 的 userid
     * @return
     */

    @Override
    public ServerResponse<User> get_information(Integer userid){
        User user = userMapper.selectByPrimaryKey(userid);
          if (user==null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
          }

          return ServerResponse.createBySuccess(user);
    }



}
