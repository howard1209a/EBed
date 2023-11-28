package org.howard1209a.file.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.howard1209a.file.pojo.ImgGroup;

public interface ImgGroupMapper {
    @Insert("insert into img_group (img_group_id,img_group_name,user_id) values (#{imgGroupId},#{imgGroupName},#{userId})")
    void saveGroup(ImgGroup imgGroup);

    @Select("select * from img_group where img_group_name = #{imgGroupName} and user_id = #{userId}")
    ImgGroup checkGroupNameForOneUser(ImgGroup imgGroup);
}
