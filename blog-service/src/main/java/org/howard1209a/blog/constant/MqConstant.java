package org.howard1209a.blog.constant;

public class MqConstant {
    /**
     * 交换机
     */
    public static final String BLOG_EXCHANGE = "blog.direct";
    /**
     * 监听新增和修改的队列
     */
    public static final String BLOG_PUT_QUEUE = "blog.put.queue";
    /**
     * 新增或修改的RoutingKey
     */
    public static final String BLOG_PUT_KEY = "blog.put";

    // 双库增删改同步时上的锁
    public static final String MYSQL_ES_SYNC_BLOG = "mysqlElasticsearchSynchronizationBlog:";
}
