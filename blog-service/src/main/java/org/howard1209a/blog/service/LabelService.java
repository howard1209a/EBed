package org.howard1209a.blog.service;

import org.howard1209a.blog.mapper.LabelMapper;
import org.howard1209a.blog.mapper.RelationBlogLabelMapper;
import org.howard1209a.blog.pojo.Label;
import org.howard1209a.blog.pojo.RelationBlogLabel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LabelService {
    @Autowired
    private LabelMapper labelMapper;
    @Autowired
    private RelationBlogLabelMapper relationBlogLabelMapper;

    public List<String> queryAllLabel() {
        List<Label> labels = labelMapper.queryAllLabel();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < labels.size(); i++) {
            list.add(labels.get(i).getLabelName());
        }
        return list;
    }

    public void saveRelation(List<String> labels, Long blogId) {
        for (String labelName : labels) {
            Long labelId = labelMapper.queryIdByName(labelName);
            relationBlogLabelMapper.insertRelation(new RelationBlogLabel(blogId, labelId));
        }
    }
}
