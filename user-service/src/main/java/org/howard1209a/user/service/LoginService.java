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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static org.howard1209a.user.constant.RegisterConstant.USER_STATE_KEY;
import static org.howard1209a.user.constant.UserConstant.*;

@Service
public class LoginService {
    @Autowired
    private RegisterMapper registerMapper;
    @Autowired
    private LoginMapper loginMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserService userService;

    public Response<String> loginCheck(LoginDto loginDto, String session) {
        User user = registerMapper.findByUserName(loginDto.getUserName());
        if (user == null) {
            return new Response<>(false, "用户名不存在");
        }
        if (!user.getPassword().equals(loginDto.getPassword())) {
            return new Response<>(false, "密码错误");
        }
        // 更新session
        redisUtil.setObject(USER_STATE_KEY + session, new UserState(null, true, user.getUserId(), null, null));

        // 如果是今天第一次登录则初始化今日获取经验限制信息（用户状态）
        String key = EXP_LIMIT_KEY + user.getUserId();
        if (!redisUtil.hasKey(key)) {
            redisUtil.setHashObject(key, EXP_LIMIT_LOGIN_LIMIT_HASHKEY, 0);
            redisUtil.setHashObject(key, EXP_LIMIT_READBLOG_LIMIT_HASHKEY, 0);
            redisUtil.setHashObject(key, EXP_LIMIT_LIKECOMMENT_LIMIT_HASHKEY, 0);
            redisUtil.setHashObject(key, EXP_LIMIT_PUBLISHBLOG_LIMIT_HASHKEY, 0);

            LocalDateTime now = LocalDateTime.now(); // 获取当前时间
            LocalDateTime midnight = LocalDateTime.of(now.toLocalDate(), LocalTime.MAX); // 获取今天的 23:59:59
            long secondsUntilMidnight = now.until(midnight, ChronoUnit.SECONDS); // 计算当前时间到今天 23:59:59 的时间差
            redisUtil.setExpireTime(key, secondsUntilMidnight, TimeUnit.SECONDS); // 23:59:59自动过期
        }

        // 登录尝试加一下经验值
        userService.addExp(user.getUserId(), EXP_LIMIT_LOGIN_LIMIT_HASHKEY);
        return new Response<>(true, "登陆成功");
    }

    public Response<String> getUserInfo(String session) {
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        User user = loginMapper.findUserById(userState.getUserId());
        return new Response<>(true, user.getUserName());
    }
}
