package com.neuedu.service.impl;

import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.UserInfoMapper;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.IUserService;
import com.neuedu.utils.MD5Utils;
import com.neuedu.utils.TokenCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {
    
    @Autowired
    UserInfoMapper userInfoMapper;

    @Override
    public ServerResponse login(String username, String password) {
        //1.参数的非空校验
        if(username==null||username.equals("")){
            return ServerResponse.createServerResponseByError("用户名不能为空");
        }
        if(password==null||password.equals("")){
            return ServerResponse.createServerResponseByError("密码不能为空");
        }
        //2.检查用户名是否存在
        int result = userInfoMapper.checkUsername(username);
        if (result==0){
            return ServerResponse.createServerResponseByError("用户名不存在");
        }
        //3.根据用户名和密码查找用户信息
        UserInfo userInfo = userInfoMapper.selectUserInfoByUsernameAndPassword(username, MD5Utils.getMD5Code(password));
        //4.返回结果
        userInfo.setPassword("");
        return ServerResponse.createServerResponseBySuccess(userInfo);
    }

    @Override
    public ServerResponse register(UserInfo userInfo) {

        //1.参数的非空校验
        if(userInfo==null)
        {
            return ServerResponse.createServerResponseByError("参数必需");
        }
        //2.校验用户名
        int result = userInfoMapper.checkUsername(userInfo.getUsername());
        if (result>0){
            return ServerResponse.createServerResponseByError("用户名已存在");
        }
        //3.校验邮箱
        int result_email= userInfoMapper.checkEmail(userInfo.getEmail());
        if(result_email>0){//邮箱存在
            return ServerResponse.createServerResponseByError("邮箱已存在");
        }
        //4.注册
        userInfo.setRole(Const.RoleEnum.ROLE_CUSTOMER.getCode());
        userInfo.setPassword(MD5Utils.getMD5Code(userInfo.getPassword()));
        int count = userInfoMapper.insert(userInfo);
        if (count>0){
            return ServerResponse.createServerResponseBySuccess("注册成功");
        }
        //.返回结果
        return ServerResponse.createServerResponseByError("注册失败");







    }

    @Override
    public ServerResponse forgrt_get_question(String username) {
        //1.参数校验
        if (username==null||username.equals("")){
            return ServerResponse.createServerResponseByError("用户名不能为空");
        }
        //2.校验username
        int result = userInfoMapper.checkUsername(username);
        if(result==0){
            //用户名不存在
            return  ServerResponse.createServerResponseByError("用户名不存在，请重新输入");
        }
        //3.查找密保问题
        String question = userInfoMapper.selectQuestionByUsername(username);
        if (question==null||question.equals("")){
            return ServerResponse.createServerResponseByError("密保问题为空");
        }
        return ServerResponse.createServerResponseBySuccess(question);
    }

    @Override
    public ServerResponse forgrt_check_answer(String username, String question, String answer) {
       //1.参数的校验
        if (username==null||username.equals("")){
            return ServerResponse.createServerResponseByError("用户名不能为空");
        }
        if (question==null||question.equals("")){
            return ServerResponse.createServerResponseByError("问题不能为空");
        }
        if (answer==null||answer.equals("")){
            return ServerResponse.createServerResponseByError("答案不能为空");
        }
        //2.根据username，question，answer查询
        int result = userInfoMapper.selectUsernameAndQuestionAndAnswer(username, question, answer);
        if (result==0){
            //答案错误
            return ServerResponse.createServerResponseByError("答案错误");
        }
        //3.服务端生成一个token保存，并将token返回给客户端
        String forgetToken= UUID.randomUUID().toString();
        //guava cache
        TokenCache.set(username,forgetToken);
        return ServerResponse.createServerResponseBySuccess(forgetToken);
    }

    @Override
    public ServerResponse forgrt_reset_password(String username, String passwordNew, String forgetToken) {
        //1.参数校验
        if (username==null||username.equals("")){
            return ServerResponse.createServerResponseByError("用户名不能为空");
        }
        if (passwordNew==null||passwordNew.equals("")){
            return ServerResponse.createServerResponseByError("密码不能为空");
        }
        if (forgetToken==null||forgetToken.equals("")){
            return ServerResponse.createServerResponseByError("token不能为空");
        }
        //2.token校验
        String token=TokenCache.get(username);
        if (token==null){
            return ServerResponse.createServerResponseByError("token过期");
        }
        if (!token.equals(forgetToken)){
            return ServerResponse.createServerResponseByError("无效的token");
        }
        //3.修改密码
        int result = userInfoMapper.updateUserPassword(username, MD5Utils.getMD5Code(passwordNew));
        if (result>0){
            return ServerResponse.createServerResponseBySuccess();
        }
        return ServerResponse.createServerResponseByError("密码修改失败");
    }

    @Override
    public ServerResponse check_valid(String str, String type) {
        //1.参数非空校验
        if (str==null||str.equals("")){
            return ServerResponse.createServerResponseByError("用户名或者邮箱不能为空");
        }
        if (type==null||type.equals("")){
            return ServerResponse.createServerResponseByError("校验的类型参数不能为空");
        }
        //2.type:username--->校验用户名str
        //      :email  ---->校验邮箱  str
        if(type.equals("username")){
            int result = userInfoMapper.checkUsername(str);
            if (result>0){
                //用户已存在
                return ServerResponse.createServerResponseByError("用户名已存在");
            }else {
                return ServerResponse.createServerResponseBySuccess();
            }
        }else if(type.equals("email")){
            int result = userInfoMapper.checkEmail(str);
            if (result>0){
                //邮箱已存在
                return ServerResponse.createServerResponseByError("邮箱已存在");
            }else {
                return ServerResponse.createServerResponseBySuccess();
            }
        }else{
            return ServerResponse.createServerResponseByError("参数类型错误");
        }
        //3.返回结果
    }

    @Override
    public ServerResponse reset_password(String username,String passwordOld, String passwordNew) {
        //1.参数的非空校验
        if (passwordOld==null||passwordOld.equals("")){
            return ServerResponse.createServerResponseByError("用户名的旧密码不能为空");
        }
        if (passwordNew==null||passwordNew.equals("")){
            return ServerResponse.createServerResponseByError("用户的新密码不能为空");
        }
        //2.根据username和passwordOld
        UserInfo userInfo = userInfoMapper.selectUserInfoByUsernameAndPassword(username, MD5Utils.getMD5Code(passwordOld));
        if (userInfo==null){
            return ServerResponse.createServerResponseByError("就密码错误");
        }
        //3.修改密码
        userInfo.setPassword(MD5Utils.getMD5Code(passwordNew));
        int result = userInfoMapper.updateByPrimaryKey(userInfo);
        if (result>0){
            return ServerResponse.createServerResponseBySuccess();
        }else {
            return ServerResponse.createServerResponseByError("密码修改失败");
        }
    }

    @Override
    public ServerResponse update_information(UserInfo user) {
        //1.参数校验
        if (user==null){
            return ServerResponse.createServerResponseByError("参数不能为空");
        }
        //2.更新用户信息
        int result = userInfoMapper.updateUserBySelectActive(user);
        if (result>0){
            return ServerResponse.createServerResponseBySuccess();
        }else {
            return ServerResponse.createServerResponseByError("更新个人信息失败");
        }
    }

    @Override
    public UserInfo findUserInfoByUserid(Integer userId) {
        return  userInfoMapper.selectByPrimaryKey(userId);
    }
}
