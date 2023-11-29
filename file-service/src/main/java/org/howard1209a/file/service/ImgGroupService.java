package org.howard1209a.file.service;

import org.howard1209a.file.mapper.ImgGroupMapper;
import org.howard1209a.file.pojo.ImgGroup;
import org.howard1209a.file.pojo.UserState;
import org.howard1209a.file.pojo.dto.Response;
import org.howard1209a.file.util.RedisUtil;
import org.howard1209a.file.util.SnowflakeIdUtils;
import org.howard1209a.file.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;

import java.util.ArrayList;
import java.util.List;

import static org.howard1209a.file.constant.FileConstant.USER_STATE_KEY;

@Service
public class ImgGroupService {
    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;
    @Autowired
    private ImgGroupMapper imgGroupMapper;
    @Autowired
    private RedisUtil redisUtil;

    public Response<String> saveGroup(String imgGroupName, Long userId, String session) {
        if (userId == null) {
            UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
            userId = userState.getUserId();
        }
        ImgGroup imgGroup = new ImgGroup(snowflakeIdUtils.nextId(), imgGroupName, userId);
        ImgGroup res = imgGroupMapper.checkGroupNameForOneUser(imgGroup);
        if (res == null) {
            imgGroupMapper.saveGroup(imgGroup);
            return new Response<>(true, "建立分组成功");
        } else {
            return new Response<>(false, "分组名重复");
        }
    }

    public Response<List<String>> getAllImgGroupName(String session) {
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        List<ImgGroup> imgGroups = imgGroupMapper.queryAllImgGroupByUserId(userState.getUserId());
        List<String> data = new ArrayList<>();
        for (ImgGroup imgGroup : imgGroups) {
            data.add(imgGroup.getImgGroupName());
        }
        return new Response<>(true, data);
    }

    public void deleteGroup(String imgGroupName, String session) {
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        imgGroupMapper.deleteImgGroup(imgGroupName, userState.getUserId());
    }
}
