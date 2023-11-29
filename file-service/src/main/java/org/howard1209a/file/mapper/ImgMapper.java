package org.howard1209a.file.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.howard1209a.file.pojo.Img;

import java.util.List;

public interface ImgMapper {
    @Insert("insert into img (img_id,user_id,img_name,img_group_id) values (#{imgId},#{userId},#{imgName},#{imgGroupId})")
    void saveImg(Img img);

    @Update("update img set description = #{description} where img_id = #{imgId}")
    void updateDescription(Long imgId, String description);

    @Select("select count(*) from img where user_id = #{userId} and img_group_id = (select img_group_id from img_group where user_id = #{userId} and img_group_name = #{imgGroupName})")
    Integer countImgForGroup(Long userId, String imgGroupName);
}
