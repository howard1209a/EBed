package org.howard1209a.file.service;

import org.checkerframework.checker.units.qual.A;
import org.howard1209a.file.mapper.ImgMapper;
import org.howard1209a.file.pojo.UserState;
import org.howard1209a.file.pojo.dto.Response;
import org.howard1209a.file.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import static org.howard1209a.file.constant.FileConstant.USER_STATE_KEY;

@Service
public class ImgService {
    @Autowired
    private ImgMapper imgMapper;
    @Autowired
    private RedisUtil redisUtil;

    public Response<Integer> countImgForGroup(String imgFroupName, String session, HttpServletRequest request) {
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        int count = imgMapper.countImgForGroup(userState.getUserId(), imgFroupName);
        return new Response<>(true, count);
    }
}
