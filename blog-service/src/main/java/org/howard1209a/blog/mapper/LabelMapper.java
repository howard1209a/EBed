package org.howard1209a.blog.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.howard1209a.blog.pojo.Label;

import java.util.List;

public interface LabelMapper {
    @Insert("insert into label values (#{labelId},#{labelName})")
    public void insertOneLabel(Label label);

    @Select("select * from label")
    public List<Label> queryAllLabel();

    @Select("select label_id from label where label_name = #{labelName}")
    public Long queryIdByName(String labelName);

    @Select("select label.label_name from relation_blog_label,label where #{blogId} = relation_blog_label.blog_id and relation_blog_label.label_id = label.label_id")
    public List<String> queryLabelsForOneBlog(Long blogId);
}
