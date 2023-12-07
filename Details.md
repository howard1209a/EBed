# EBed 简单图床详细细节

## 项目笔记

1. maven在解析依赖失败的时候，可以尝试手动删除本地maven仓库的依赖文件，然后再重新加载maven。
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
