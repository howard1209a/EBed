package org.howard1209a.file.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.howard1209a.file.pojo.Img;
import org.howard1209a.file.pojo.dto.ImgModifyDto;

import java.util.List;

public interface ImgMapper {
    @Insert("insert into img (img_id,user_id,img_name,img_group_id,img_type) values (#{imgId},#{userId},#{imgName},#{imgGroupId},#{imgType})")
    void saveImg(Img img);

    @Update("update img set description = #{description} where img_id = #{imgId}")
    void updateDescription(Long imgId, String description);

    @Select("select count(*) from img where user_id = #{userId} and img_group_id = (select img_group_id from img_group where user_id = #{userId} and img_group_name = #{imgGroupName})")
    Integer countImgForGroup(Long userId, String imgGroupName);

    @Select("select * from img where user_id = #{userId} and img_group_id = (select img_group_id from img_group where user_id = #{userId} and img_group_name = #{imgGroupName}) order by create_time desc limit #{length} offset #{startIndex}")
    List<Img> paginationImgForGroup(Long userId, String imgGroupName, Integer startIndex, Integer length);

    @Update("update img set img_name = #{imgModifyDto.newName}, img_group_id = #{imgGroupId}, description = #{imgModifyDto.newDescription} where img_id = #{imgModifyDto.imgId}")
    void updateImg(@Param("imgModifyDto") ImgModifyDto imgModifyDto, @Param("imgGroupId") Long imgGroupId);
}
