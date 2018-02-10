package com.m2mall.dao;

import com.m2mall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUserName(String username);   //跟sql 字段一致
    // 声明多个参数时候需要使用@Param 注解,sql xml 里面对应 注解的value
    User selectLogin(@Param("username")String username,@Param("password")String password);


    int checkEmail(String email);

   // 通过登录名 获取 安全问题
    String selectQuestionByUsername(String username);
    // 校验答案...
    int checkAnswer(@Param("username")String username,
                    @Param("question") String question,
                    @Param("answer")String answer
                    );

    // 更新密码

    int updatePasswordByUserName(@Param("username") String username,@Param("password") String password);



    int checkPassword(@Param(value = "userid") Integer userid,@Param(value = "password") String password);



    int checkEmailByUserId(@Param("userid") Integer userid,@Param("email") String email);



    
}



