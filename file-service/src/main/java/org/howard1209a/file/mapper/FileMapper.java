package org.howard1209a.file.mapper;

import org.apache.ibatis.annotations.Insert;
import org.howard1209a.file.pojo.Img;

import java.sql.Timestamp;

public interface FileMapper {
    @Insert("insert into img (img_id,user_id,img_name,group_id,description) values (#{imgId},#{userId},#{imgName},#{groupId},#{description})")
    void saveImg(Img img);
}
