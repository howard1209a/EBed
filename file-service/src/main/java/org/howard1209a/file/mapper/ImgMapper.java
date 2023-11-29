package org.howard1209a.file.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.howard1209a.file.pojo.Img;

public interface ImgMapper {
    @Insert("insert into img (img_id,user_id,img_name,img_group_id) values (#{imgId},#{userId},#{imgName},#{imgGroupId})")
    void saveImg(Img img);

    @Update("update img set description = #{description} where img_id = #{imgId}")
    void updateDescription(Long imgId, String description);
}
