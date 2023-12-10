package org.howard1209a.user.service;

import org.howard1209a.user.mapper.RelationUserUserFollowMapper;
import org.howard1209a.user.mapper.UserMapper;
import org.howard1209a.user.pojo.User;
import org.howard1209a.user.pojo.UserState;
import org.howard1209a.user.pojo.dto.ExpDto;
import org.howard1209a.user.pojo.dto.Response;
import org.howard1209a.user.util.RedisUtil;
import org.howard1209a.user.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import static org.howard1209a.user.constant.RegisterConstant.USER_STATE_KEY;
import static org.howard1209a.user.constant.UserConstant.*;

@Service
public class UserService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RelationUserUserFollowMapper relationUserUserFollowMapper;

    public void saveProfilePhoto(String session, MultipartFile multipartFile) {
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        String fileName = multipartFile.getOriginalFilename();
        String profilePhotoType = fileName.split("\\.")[1];
        fileName = userState.getUserId() + "." + profilePhotoType;
        File file = new File(USER_PROFILE_PHOTO_DIR + fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (OutputStream os = new FileOutputStream(file)) {
            os.write(multipartFile.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        userMapper.updateProfilePhotoType(profilePhotoType, userState.getUserId());
    }

    public void downloadProfilePhoto(Long userId, String session, HttpServletRequest request, HttpServletResponse response) {
        if (userId == null) {
            UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
            userId = userState.getNextProfileUserId();
        }
        User user = userMapper.queryUserById(userId);

        String path = USER_PROFILE_PHOTO_DIR;
        if (Utils.isEmpty(user.getProfilePhotoType())) {
            path += "default.jpeg";
        } else {
            path += userId + "." + user.getProfilePhotoType();
        }
        File file = new File(path);
        try (FileInputStream is = new FileInputStream(file);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateUserName(String session, String userName) {
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        userMapper.updateUserName(userName, userState.getUserId());
    }

    public Response<ExpDto> queryExpByNextProfileUserId(String session) {
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        User user = userMapper.queryUserById(userState.getNextProfileUserId());
        return new Response<>(true, new ExpDto(user.getExp()));
    }

    public User queryUserById(Long userId) {
        return userMapper.queryUserById(userId);
    }

    public void addExp(Long userId, String addExpType) {
        if (EXP_LIMIT_LOGIN_LIMIT_HASHKEY.equals(addExpType)) {
            if (canAddExp(userId, addExpType, 1)) {
                userMapper.addExp(userId, 5);
            }
        } else if (EXP_LIMIT_READBLOG_LIMIT_HASHKEY.equals(addExpType)) {
            if (canAddExp(userId, addExpType, 1)) {
                userMapper.addExp(userId, 5);
            }
        } else if (EXP_LIMIT_LIKECOMMENT_LIMIT_HASHKEY.equals(addExpType)) {
            if (canAddExp(userId, addExpType, 10)) {
                userMapper.addExp(userId, 3);
            }
        } else if (EXP_LIMIT_PUBLISHBLOG_LIMIT_HASHKEY.equals(addExpType)) {
            if (canAddExp(userId, addExpType, 2)) {
                userMapper.addExp(userId, 10);
            }
        } else if (EXP_COMMENT_LIKE_BY_OTHER.equals(addExpType)) {
            userMapper.addExp(userId, 1); // 无上限直接加
        } else {
            userMapper.addExp(userId, 1); // 无上限直接加
        }
    }

    private boolean canAddExp(Long userId, String addExpType, Integer maxNum) {
        // 两次判断保证数据一致性，hashIncrement方法满足原子性
        if (redisUtil.getHashObject(EXP_LIMIT_KEY + userId, addExpType, Integer.class) < maxNum) {
            long limit = redisUtil.hashIncrement(EXP_LIMIT_KEY + userId, addExpType, 1);
            if (limit > maxNum) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public void updateSelfIntroduction(String session, String selfIntroduction) {
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        userMapper.updateSelfIntroduction(userState.getUserId(), selfIntroduction);
    }

    public Response<User> queryOneUserByNextProfileUserId(String session) {
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        User user = userMapper.queryUserById(userState.getNextProfileUserId());
        return new Response<>(true, user);
    }

    public void follow(String session, Long followedUserId) { // TODO 后续同步es
        // 保证幂等性和数据一致性
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        try {
            relationUserUserFollowMapper.insertOneFollowRelation(userState.getUserId(), followedUserId);
        } catch (DuplicateKeyException e) {
            return;
        }
        userMapper.addOneFollowNum(userState.getUserId());
        userMapper.addOneFunNum(followedUserId);
    }

    public void unFollow(String session, Long unFollowedUserId) { // TODO 后续同步es
        // 保证幂等性和数据一致性
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        if (relationUserUserFollowMapper.deleteOneFollowRelation(userState.getUserId(), unFollowedUserId) == 0) {
            return;
        }
        userMapper.subtractOneFollowNum(userState.getUserId());
        userMapper.subtractOneFunNum(unFollowedUserId);
    }

    public void setNextProfileUserId(String session, Long nextProfileUserId) {
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        userState.setNextProfileUserId(nextProfileUserId);
        redisUtil.setObject(USER_STATE_KEY + session, userState);
    }

    public Response<User> queryLoginUser(String session) {
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        User user = userMapper.queryUserById(userState.getUserId());
        return new Response<>(true, user);
    }
}
