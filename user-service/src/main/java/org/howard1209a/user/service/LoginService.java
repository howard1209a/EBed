package org.howard1209a.user.service;

import org.howard1209a.user.mapper.LoginMapper;
import org.howard1209a.user.mapper.RegisterMapper;
import org.howard1209a.user.pojo.User;
import org.howard1209a.user.pojo.UserState;
import org.howard1209a.user.pojo.dto.LoginDto;
import org.howard1209a.user.pojo.dto.Response;
import org.howard1209a.user.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.File;

import static org.howard1209a.user.constant.RegisterConstant.USER_STATE_KEY;

@Service
public class LoginService {
    @Autowired
    private RegisterMapper registerMapper;
    @Autowired
    private LoginMapper loginMapper;
    @Autowired
    private RedisUtil redisUtil;

    public Response<String> loginCheck(LoginDto loginDto, String session) {
        User user = registerMapper.findByUserName(loginDto.getUserName());
        if (user == null) {
            return new Response<>(false, "用户名不存在");
        }
        if (!user.getPassword().equals(loginDto.getPassword())) {
            return new Response<>(false, "密码错误");
        }
        // 更新session
        redisUtil.setObject(USER_STATE_KEY + session, new UserState(null, true, user.getUserId(), null));
        return new Response<>(true, "登陆成功");
    }

    public Response<String> getUserInfo(String session) {
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        User user = loginMapper.findUserById(userState.getUserId());
        return new Response<>(true, user.getUserName());
    }
}
