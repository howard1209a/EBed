package org.howard1209a.user.service;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.howard1209a.user.feign.FileClient;
import org.howard1209a.user.mapper.RegisterMapper;
import org.howard1209a.user.pojo.User;
import org.howard1209a.user.pojo.UserState;
import org.howard1209a.user.pojo.dto.RegisterDto;
import org.howard1209a.user.pojo.dto.Response;
import org.howard1209a.user.util.RedisUtil;
import org.howard1209a.user.util.SnowflakeIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import static org.howard1209a.user.constant.RegisterConstant.USER_STATE_KEY;

@Slf4j
@Service
public class RegisterService {
    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;
    @Autowired
    private RegisterMapper registerMapper;
    @Autowired
    private FileClient fileClient;
    @Autowired
    private RedisUtil redisUtil;

    public void identifyImage(HttpServletResponse response, String session) {
        //定义图形验证码的长、宽、验证码字符数、干扰元素个数
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 90, 4, 100);
        // 验证码值
        String code = lineCaptcha.getCode();
        redisUtil.setObject(USER_STATE_KEY + session, new UserState(code, false, null, null));
        // 输出到客户端
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            // 图形验证码写出，写出到流
            lineCaptcha.write(outputStream);
        } catch (Exception e) {
            log.error("图形验证码输出错误");
        }
    }

    public void createSession(HttpServletResponse response) {
        int id = 12345;
        Cookie cookie = new Cookie("session", id + "");
        cookie.setPath("/"); // cookie的path设置为/，意味着此cookie只要不跨域就可以随便共享
        response.addCookie(cookie);
        redisUtil.setObject(USER_STATE_KEY + id, new UserState(null, false, null, null));
    }

    public Response<String> checkAndRegister(RegisterDto registerDto, String session) {
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        if (!registerDto.getIdentifyImageNumber().equals(userState.getIdentifyImageNumber())) {
            return new Response<>(false, "验证码错误");
        }
        if (registerMapper.findByUserName(registerDto.getUserName()) != null) {
            return new Response<>(false, "用户名重复");
        }
        if (registerMapper.findByEmail(registerDto.getEmail()) != null) {
            return new Response<>(false, "此邮箱已被注册");
        }
        registerDto.setUserId(snowflakeIdUtils.nextId());
        registerMapper.saveUser(new User(registerDto));
        fileClient.createImgGroup("默认分组", registerDto.getUserId());
        return new Response<>(true, "注册成功");
    }
}
