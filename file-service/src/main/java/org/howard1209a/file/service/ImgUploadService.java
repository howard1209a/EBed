package org.howard1209a.file.service;

import org.howard1209a.file.mapper.ImgGroupMapper;
import org.howard1209a.file.mapper.ImgMapper;
import org.howard1209a.file.pojo.Img;
import org.howard1209a.file.pojo.ImgGroup;
import org.howard1209a.file.pojo.UserState;
import org.howard1209a.file.util.RedisLockUtil;
import org.howard1209a.file.util.RedisUtil;
import org.howard1209a.file.util.SnowflakeIdUtils;
import org.howard1209a.file.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.howard1209a.file.constant.FileConstant.USER_STATE_KEY;

@Service
public class ImgUploadService {
    @Autowired
    private ImgGroupMapper imgGroupMapper;
    @Autowired
    private ImgMapper imgMapper;
    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;
    @Autowired
    private RedisLockUtil redisLockUtil;
    @Autowired
    private RedisUtil redisUtil;

    public void saveUploadedImg(MultipartFile[] images, String session) {
        List<Long> lastUploadedImgId = new ArrayList<>();
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        Long userId = userState.getUserId();
        ImgGroup imgGroup = imgGroupMapper.checkGroupNameForOneUser(new ImgGroup(null, "默认分组", userId));
        Long imgGroupId = imgGroup.getImgGroupId();

        // 处理每个上传的图片
        for (MultipartFile image : images) {
            // 存入mysql
            String fileName = image.getOriginalFilename();
            String[] split = fileName.split("\\.");
            Img img = new Img(snowflakeIdUtils.nextId(), userId, split[0], imgGroupId, null, null, split[1]);
            lastUploadedImgId.add(img.getImgId());
            imgMapper.saveImg(img);
            // 存入磁盘
            File dir = new File(Utils.getImgStoragePath(userId));
            if (!dir.exists()) {
                dir.mkdirs();
            }
            fileName = img.getImgId() + "." + img.getImgType();
            File file = new File(Utils.getImgStoragePath(userId) + fileName);
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try (OutputStream os = new FileOutputStream(file)) {
                os.write(image.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // 把本次上传的所有图片的id缓存到session
        redisLockUtil.blockingGetLock(USER_STATE_KEY + session);
        userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        userState.setLastUploadedImgId(lastUploadedImgId);
        redisUtil.setObject(USER_STATE_KEY + session, userState);
        redisLockUtil.unlock(USER_STATE_KEY + session);
    }

    public void imgDescriptionAllUpload(List<String> descriptions, String session) {
        // 更新每张图片的description
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        List<Long> lastUploadedImgId = userState.getLastUploadedImgId();
        for (int i = 0; i < lastUploadedImgId.size(); i++) {
            imgMapper.updateDescription(lastUploadedImgId.get(i), descriptions.get(i));
        }

        // 清空缓存的descriptions
        redisLockUtil.blockingGetLock(USER_STATE_KEY + session);
        userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        userState.setLastUploadedImgId(null);
        redisUtil.setObject(USER_STATE_KEY + session, userState);
        redisLockUtil.unlock(USER_STATE_KEY + session);
    }
}
