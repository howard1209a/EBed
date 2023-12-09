# EBed 简单图床

## 实现功能

### 用户方面

- ✅ 注册登录登出
    - 验证码校验
    - ❌ 邮箱认证
    - cookie保存sessionid
    - 未登录状态请求拦截

### 文件方面

- ✅ 图片批量上传
    - 取消上传
    - 添加描述
- ✅ 我的图库
    - 新建/删除分组
    - 图片详情
        - 外链url
    - 图片编辑
        - 修改信息/分组
    - 图片分组分页显示

### 博客方面

- ✅ 发布博客
    - 博客内容/标签
- ✅ 浏览广场
    - 滚动分页
    - 去重推送
    - 收藏/取消收藏
    - 阅读博客
        - 树状评论结构（对博客评论/回复别人的评论）
        - 点赞/取消点赞评论

### 搜索方面

- ✅ 全局搜索
    - 博客搜索
        - 分词匹配
            - 作者名
            - 标题
            - 内容
        - 限定标签范围
        - 指定排序方式
            - 相似度
            - 收藏数
            - 评论数
            - 新发布
        - 结果匹配分词高亮
        - 输入框补全提示
        - 双库增删改同步（异步及联）
    - ❌ 用户搜索
    - ❌ 商品搜索

### 前端方面

- 网关后的一个微服务

## 技术细节

### Mysql

- 博客表
- 评论表
- 图片表
- 图片分组表
- 标签表
- 博客标签关系表
- 用户博客收藏关系表
- 用户评论点赞关系表
- 用户表

### Redis
- 多次请求间保持状态
  - 一些登录信息（会话状态）
      - 验证码
      - 用户信息状态
      - 缓存用户上一次的上传
  - 已浏览的博客（会话状态）
  - 用户一天内的经验值操作（用户状态）
- 分布式锁
    - 阻塞式锁
        - mysql&ES双库增删改同步
        - 查询并修改有状态信息
        - mysql双表增删改
    - 非阻塞式锁
        - 秒杀业务
- 缓存高频信息
- 作为数据库存储信息（高性能、低一致性的信息）

### Gateway网关

- 根据请求资源路径来路由微服务
- 拦截未登录请求重定向到登录页面
    - 登录接口、注册接口、图片外链放行

### Nacos注册中心

- 提供微服务的注册与信息拉取
- 实现微服务间的调用解耦与负载均衡

### Feign

- 提供接口声明式的 http 微服务调用请求

  