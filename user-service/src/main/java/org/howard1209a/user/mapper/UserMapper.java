package org.howard1209a.user.mapper;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.howard1209a.user.pojo.User;

public interface UserMapper {
    @Select("select * from user where user_id = #{userId}")
    public User queryUserById(Long userId);

    @Update("update user set profile_photo_type = #{profilePhotoType} where user_id = #{userId}")
    public void updateProfilePhotoType(String profilePhotoType, Long userId);

    @Update("update user set user_name = #{userName} where user_id = #{userId}")
    public void updateUserName(String userName, Long userId);

    @Update("update user set exp = exp + #{num} where user_id = #{userId}")
    public void addExp(Long userId, Integer num);
}
