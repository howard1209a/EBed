# EBed 简单图床详细细节
## 开发小技巧
1. maven在解析依赖失败的时候，可以尝试手动删除本地maven仓库的依赖文件，然后再重新加载maven。
2. mysql两张表的关系：
    - 一对一：任意方存另一方的主键
    - 多对一：多的那方存一的那方的主键
    - 多对多：再建一个关系表
3. 后端的Long类型传到前端js后会超范围，所以一般通过`@JsonSerialize(using = ToStringSerializer.class)`注解让Long类型在序列化成json的时候变成一个字符串。
4. pojo类正好对应数据库表，dto类则用于层间传输，比如前端和后端，controller和service，service和mapper。
5. 