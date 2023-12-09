# EBed 简单图床详细细节

## 使用说明
### 图片上传
在图片上传栏目中，你可以批量选取一些图片，可以给每个图片添加一些描述，如果选择错了图片也可以删掉它，最后点击上传即可。你所有上传的图片都可以在我的图库中的默认分组中找到。
### 我的图库
在我的图库栏目中，你可以添加或删除一些分组以分类管理你所有的图片，在每个分组中图片都是分页显示的。点击图片编辑按钮，你可以修改图片的名称、描述以及移动它们所属分组。点击查看详情按钮，你可以看到图片的详细信息，其中图片链接展示出来的url你可以直接复制并在需要的地方引用，也可以直接复制到浏览器中下载本张图片。点击最下方的发布按钮，你可以针对本张图片发布一篇博客，你可以填写博客标题、内容以及选择零到多个适合的标签，最后点击发布博客。你发布的博客可能会被其他用户浏览或检索。
### 浏览广场
在浏览广场栏目中，你一直下拉即可随机且不重复地浏览一些其他用户发布的博客。对于喜欢的博客，你可以选择收藏或取消收藏。点击阅读博客按钮，你可以查看博客的详情。在博客详情页的最下方输入栏中，你可以发表关于此篇博客的评论，同时你也可以回复其他用户的评论，对于其他用户的评论你可以点赞或取消赞。
### 全局搜索
在全局搜索栏目中，你可以检索所有的博客。你可以先在搜索框中输入一些关键词，搜索框会根据博客库中的内容对你的输入进行补全提示。在下方的检索条件中，显示了所有存在的标签，如果你选择了一到多个标签，那么结果中的每个博客都必须包含你选择的所有标签。你还可以选择检索结果的排序方式，其中相似度排序将会优先显示与你输入的关键词最接近的结果。最后点击搜索按钮即可检索，最多显示10条搜索结果。
### 用户主页
在用户主页栏目中，你可以上传自己的头像，修改用户名，查看自己的等级和经验值。等级一共分为1-6个等级，执行以下操作将会提高你的经验值：登录经验值加 5（每日一次）、浏览一篇博客经验值加 5（每日一次）、点赞一条评论经验值加 3（每日上限 30）、你的评论被他人点赞经验值加 1（无上限）、你的博客被他人收藏经验值加 1（无上限）、发布一篇博客经验值加 10（每日一次）。

## 项目笔记

1. maven在解析依赖失败的时候，可以尝试手动删除本地maven仓库中对应的依赖文件，然后再重新加载maven。
2. mysql两张表的关系：
    - 一对一：任意方存另一方的主键
    - 多对一：多的那方存一的那方的主键
    - 多对多：再建一个关系表
3. 后端的Long类型传到前端js后会超范围，所以一般通过`@JsonSerialize(using = ToStringSerializer.class)`
   注解让Long类型在序列化成json的时候变成一个字符串，然后前端按照字符串接收。
4. pojo类正好对应数据库表，dto类则用于层间传输，比如前端和后端之间传数据，controller和service之间传数据，service和mapper之间传数据。

## Mysql

### blog

```sql
-- EBed.blog definition

CREATE TABLE `blog`
(
    `blog_id`       bigint      NOT NULL,
    `user_id`       bigint      NOT NULL,
    `title`         varchar(50) NOT NULL,
    `img_id`        bigint      NOT NULL,
    `content`       text        NOT NULL,
    `create_time`   timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `comment_num`   int         NOT NULL DEFAULT '0',
    `favourite_num` int         NOT NULL DEFAULT '0',
    PRIMARY KEY (`blog_id`),
    KEY             `blog_FK` (`user_id`),
    KEY             `blog_FK_1` (`img_id`),
    CONSTRAINT `blog_FK` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
    CONSTRAINT `blog_FK_1` FOREIGN KEY (`img_id`) REFERENCES `img` (`img_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

### comment

```sql
-- EBed.comment definition

CREATE TABLE `comment`
(
    `comment_id`        bigint    NOT NULL,
    `user_id`           bigint    NOT NULL,
    `blog_id`           bigint    NOT NULL,
    `comment_content`   text      NOT NULL,
    `father_comment_id` bigint             DEFAULT NULL,
    `likes_num`         int       NOT NULL DEFAULT '0',
    `create_time`       timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `floor`             int       NOT NULL,
    PRIMARY KEY (`comment_id`),
    KEY                 `comment_FK` (`user_id`),
    KEY                 `comment_FK_1` (`blog_id`),
    CONSTRAINT `comment_FK` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
    CONSTRAINT `comment_FK_1` FOREIGN KEY (`blog_id`) REFERENCES `blog` (`blog_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

### img

```sql
-- EBed.img definition

CREATE TABLE `img`
(
    `img_id`       bigint       NOT NULL,
    `user_id`      bigint       NOT NULL,
    `img_name`     varchar(100) NOT NULL,
    `img_group_id` bigint       NOT NULL,
    `description`  varchar(100) DEFAULT NULL,
    `create_time`  timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `img_type`     varchar(20)  NOT NULL,
    PRIMARY KEY (`img_id`),
    KEY            `img_FK` (`user_id`),
    KEY            `img_FK_1` (`img_group_id`),
    CONSTRAINT `img_FK` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
    CONSTRAINT `img_FK_1` FOREIGN KEY (`img_group_id`) REFERENCES `img_group` (`img_group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

### img_group

```sql
-- EBed.img_group definition

CREATE TABLE `img_group`
(
    `img_group_id`   bigint                                                        NOT NULL,
    `img_group_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
    `user_id`        bigint                                                        NOT NULL,
    PRIMARY KEY (`img_group_id`),
    KEY              `img_group_FK` (`user_id`),
    CONSTRAINT `img_group_FK` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

### label

```sql
-- EBed.label definition

CREATE TABLE `label`
(
    `label_id`   bigint      NOT NULL,
    `label_name` varchar(30) NOT NULL,
    PRIMARY KEY (`label_id`),
    UNIQUE KEY `label_UN` (`label_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

### relation_blog_label

```sql
-- EBed.relation_blog_label definition

CREATE TABLE `relation_blog_label`
(
    `blog_id`  bigint NOT NULL,
    `label_id` bigint NOT NULL,
    KEY        `relation_blog_label_FK` (`blog_id`),
    KEY        `relation_blog_label_FK_1` (`label_id`),
    CONSTRAINT `relation_blog_label_FK` FOREIGN KEY (`blog_id`) REFERENCES `blog` (`blog_id`),
    CONSTRAINT `relation_blog_label_FK_1` FOREIGN KEY (`label_id`) REFERENCES `label` (`label_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

### relation_user_blog_favorite

```sql
-- EBed.relation_user_blog_favorite definition

CREATE TABLE `relation_user_blog_favorite`
(
    `user_id` bigint NOT NULL,
    `blog_id` bigint NOT NULL,
    KEY       `relation_user_blog_favorite_FK` (`user_id`),
    KEY       `relation_user_blog_favorite_FK_1` (`blog_id`),
    CONSTRAINT `relation_user_blog_favorite_FK` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
    CONSTRAINT `relation_user_blog_favorite_FK_1` FOREIGN KEY (`blog_id`) REFERENCES `blog` (`blog_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

### relation_user_comment_like

```sql
-- EBed.relation_user_comment_like definition

CREATE TABLE `relation_user_comment_like`
(
    `user_id`    bigint NOT NULL,
    `comment_id` bigint NOT NULL,
    KEY          `relation_user_comment_like_FK` (`user_id`),
    KEY          `relation_user_comment_like_FK_1` (`comment_id`),
    CONSTRAINT `relation_user_comment_like_FK` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
    CONSTRAINT `relation_user_comment_like_FK_1` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

### user

```sql
-- EBed.`user` definition

CREATE TABLE `user`
(
    `user_id`     bigint                                                       NOT NULL,
    `user_name`   varchar(20)                                                  NOT NULL,
    `password`    varchar(20)                                                  NOT NULL,
    `email`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
    `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `unique_user_name` (`user_name`),
    UNIQUE KEY `unique_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

## Elasticsearch

### blog索引

- 建立索引
   ```json
   PUT /blog
   {
     "mappings": {
       "properties": {
         "comment_num": {
           "type": "integer",
           "index": false
         },
         "content": {
           "type": "text",
           "analyzer": "ik_max_word"
         },
         "create_time": {
           "type": "date",
           "index": false
         },
         "favourite_num": {
           "type": "integer",
           "index": false
         },
         "id": {
           "type": "long",
           "index": false
         },
         "img_id": {
           "type": "long",
           "index": false
         },
         "labels": {
           "type": "keyword"
         },
         "suggestion": {
           "type": "completion",
           "analyzer": "keyword",
           "preserve_separators": true,
           "preserve_position_increments": true,
           "max_input_length": 50
         },
         "title": {
           "type": "text",
           "analyzer": "ik_max_word"
         },
         "user_id": {
           "type": "long",
           "index": false
         },
         "user_name": {
           "type": "text",
           "analyzer": "ik_max_word"
         }
       }
     }
   }
   ```
- 删除索引
  ```json
  DELETE /blog
   ```
- 查询索引中所有文档
   ```json
   GET /blog/_search
   {
   "query": {
   "match_all": {}
   }
   }
   ```
- 测试分词器
   ```json
   GET /_analyze
   {
   "analyzer": "ik_max_word",
   "text": "黑马程序员学习java太棒了"
   }
   ```
