package org.howard1209a.file.service;

import org.checkerframework.checker.units.qual.A;
import org.howard1209a.file.mapper.ImgGroupMapper;
import org.howard1209a.file.mapper.ImgMapper;
import org.howard1209a.file.pojo.Img;
import org.howard1209a.file.pojo.ImgGroup;
import org.howard1209a.file.pojo.UserState;
import org.howard1209a.file.pojo.dto.ImgDto;
import org.howard1209a.file.pojo.dto.ImgModifyDto;
import org.howard1209a.file.pojo.dto.ImgPaginationDto;
import org.howard1209a.file.pojo.dto.Response;
import org.howard1209a.file.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.howard1209a.file.constant.FileConstant.USER_IMG_STORAGE_DIR;
import static org.howard1209a.file.constant.FileConstant.USER_STATE_KEY;

@Service
public class ImgService {
    @Autowired
    private ImgMapper imgMapper;
    @Autowired
    private ImgGroupMapper imgGroupMapper;
    @Autowired
    private RedisUtil redisUtil;

    public Response<Integer> countImgForGroup(String imgFroupName, String session) {
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        int count = imgMapper.countImgForGroup(userState.getUserId(), imgFroupName);
        return new Response<>(true, count);
    }

    public Response<List<ImgDto>> imgPaginationForGroup(ImgPaginationDto imgPaginationDto, String session) {
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        Integer startIndex = imgPaginationDto.getStartIndex();
        Integer length = imgPaginationDto.getLength();
        List<Img> imgs = imgMapper.paginationImgForGroup(userState.getUserId(), imgPaginationDto.getImgGroupName(), startIndex, length);
        List<ImgDto> res = new ArrayList<>();
        for (Img img : imgs) {
            String url = "http://localhost:10010/file/img/download/" + img.getImgId() + "." + img.getImgType();
            ImgDto imgDto = new ImgDto(img.getImgName(), url, img.getDescription(), img.getCreateTime(), null, img.getImgId().toString());
            res.add(imgDto);
        }
        return new Response<>(true, res);
    }

    public void download(HttpServletResponse response, String imgFileName, String session) {
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        String path = USER_IMG_STORAGE_DIR + userState.getUserId() + "/" + imgFileName;
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

    public void modifyImg(ImgModifyDto imgModifyDto, String session) {
        UserState userState = redisUtil.getObject(USER_STATE_KEY + session, UserState.class);
        ImgGroup imgGroup = imgGroupMapper.checkGroupNameForOneUser(new ImgGroup(null, imgModifyDto.getNewGroupName(), userState.getUserId()));
        imgMapper.updateImg(imgModifyDto, imgGroup.getImgGroupId());
    }
}
