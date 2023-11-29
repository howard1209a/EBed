package org.howard1209a.file.mapper;

import org.apache.ibatis.annotations.Insert;
import org.howard1209a.file.pojo.Img;

public interface ImgMapper {
    @Insert("insert into img (img_id,user_id,img_name,img_group_id) values (#{imgId},#{userId},#{imgName},#{imgGroupId})")
    void saveImg(Img img);
}
