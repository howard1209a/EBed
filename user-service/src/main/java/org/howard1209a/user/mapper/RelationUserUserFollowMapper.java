package org.howard1209a.user.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;

public interface RelationUserUserFollowMapper {
    @Insert("insert into relation_user_user_follow (user_id,followed_user_id) values (#{userId},#{followedUserId})")
    public void insertOneFollowRelation(Long userId, Long followedUserId);

    @Delete("delete from relation_user_user_follow where user_id = #{userId} and followed_user_id = #{unFollowedUserId}")
    public Integer deleteOneFollowRelation(Long userId, Long unFollowedUserId);
}
