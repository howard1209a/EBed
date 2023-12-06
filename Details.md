# EBed 简单图床详细细节

## 开发小技巧

1. maven在解析依赖失败的时候，可以尝试手动删除本地maven仓库的依赖文件，然后再重新加载maven。
2. mysql两张表的关系：
    - 一对一：任意方存另一方的主键
    - 多对一：多的那方存一的那方的主键
    - 多对多：再建一个关系表
3. 后端的Long类型传到前端js后会超范围，所以一般通过`@JsonSerialize(using = ToStringSerializer.class)`
   注解让Long类型在序列化成json的时候变成一个字符串。
4. pojo类正好对应数据库表，dto类则用于层间传输，比如前端和后端，controller和service，service和mapper。

PUT /blog
{
"mappings" : {
"properties" : {
"commentNum" : {
"type" : "long"
},
"comment_num" : {
"type" : "integer",
"index" : false
},
"content" : {
"type" : "text",
"analyzer" : "ik_max_word"
},
"createTime" : {
"type" : "long"
},
"create_time" : {
"type" : "date",
"index" : false
},
"favouriteNum" : {
"type" : "long"
},
"favourite_num" : {
"type" : "integer",
"index" : false
},
"id" : {
"type" : "long",
"index" : false
},
"imgId" : {
"type" : "long"
},
"img_id" : {
"type" : "long",
"index" : false
},
"labels" : {
"type" : "keyword"
},
"suggestion" : {
"type" : "completion",
"analyzer" : "keyword",
"preserve_separators" : true,
"preserve_position_increments" : true,
"max_input_length" : 50
},
"title" : {
"type" : "text",
"analyzer" : "ik_max_word"
},
"userId" : {
"type" : "long"
},
"userName" : {
"type" : "text",
"fields" : {
"keyword" : {
"type" : "keyword",
"ignore_above" : 256
}
}
},
"user_id" : {
"type" : "long",
"index" : false
},
"user_name" : {
"type" : "text",
"analyzer" : "ik_max_word"
}
}
}
}
