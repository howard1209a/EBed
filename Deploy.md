# EBed 简单图床部署指南

## 部署结构

### 分配原因

局域网hyper-v虚拟机服务器存在断电重启的风险，而云服务器2核2GB性能太低跑不了全部的服务。

- 数据库放在云上比较安全，不会出现断电的问题，redis断电丢失数据，mysql断电重启可能会出运维bug。
- EBed应用如果部署在云服务器是访问不了局域网服务器部署的其他服务的，因此只能放在局域网服务器。
- nacos不存数据放在局域网服务器没问题。
- rabbitmq是docker部署的且没做挂载，断电时里面有消息的话可能带来问题。
- elasticsearch也是docker部署的且没做挂载，重启后数据全丢失可以手动同步，同步代码在blog-service服务test的T1类中。

### 实际结构

- 腾讯云服务器
    - mysql
    - redis
- 局域网hyper-v虚拟机服务器
    - nacos
    - rabbitmq
    - elasticsearch
    - Kibana-es
    - EBed

## 部署过程

假设我们现在有一个空的腾讯云服务器和一个空的局域网hyper-v虚拟机服务器。

### 腾讯云服务器

#### mysql

安装 mysql

```shell
apt install mysql-server
```

查看 mysql 状态

```shell
service mysql status
```

启动 mysql

```shell
service mysql start
```

修改账号权限为%

```shell
use mysql;
select host, user, authentication_string, plugin from user;
update user set host = '%' where user = 'root';
GRANT ALL ON *.* TO 'root'@'%';
flush privileges;
```

修改 mysql 配置文件 my.cnf 允许外部 ip 访问

```shell
vim /etc/mysql/my.cnf
# 添加如下
[mysqld]
bind-address = 服务器本地ip
# 重启mysql服务
service mysql restart
```

为 root 用户设置密码

```shell
# 刷新一下
flush privileges;
# 设置密码
ALTER USER 'root'@'%' IDENTIFIED BY '123456';
```

SHA2 认证改为密码认证（mysql 8.0 默认 SHA2 认证 5.7 默认密码认证）

```shell
update user set plugin='mysql_native_password' where user='root';
```

修改操作系统防火墙和云服务器安全组，放行 3306 端口

#### redis

apt 安装 redis

```shell
apt install redis-server
```

修改 redis 配置

```shell
vim /etc/redis/redis.conf
# 注释掉下面这一行，开放远程访问
bind 127.0.0.1
# 解开下面一行注释，设置redis密码
requirepass yourpassword
```

重启 redis

```shell
/etc/init.d/redis-server restart
```

### 局域网hyper-v虚拟机服务器

#### nacos

nacos采用1.4.1版本，nacos依赖jre，先安装jdk1.8

```shell
# 共享文件夹导入tar
cp /mnt/share/nacos-server-1.4.1.tar.gz /usr/local
tar -xvf nacos-server-1.4.1.tar.gz
cp /mnt/share/jdk8.tar.gz /usr/local
tar -xvf jdk8.tar.gz

# 配置环境变量
vim /etc/profile
export JAVA_HOME=/usr/local/jdk1.8.0_144
export PATH=$PATH:$JAVA_HOME/bin
source /etc/profile

cd /usr/local/nacos/bin
./startup.sh -m standalone
```

#### rabbitmq

```shell
# 安装docker
apt install docker.io
## 拉取镜像
docker pull rabbitmq:3-management
## 运行容器
docker run \
 -e RABBITMQ_DEFAULT_USER=howard1209a \
 -e RABBITMQ_DEFAULT_PASS=123456 \
 --name mq \
 --hostname mq1 \
 -p 15672:15672 \
 -p 5672:5672 \
 -d \
 rabbitmq:3-management
```

#### elasticsearch

```shell
# 因为我们还需要部署kibana容器，因此需要让es和kibana容器互联。这里先创建一个网络
docker network create es-net
# 这里我们采用elasticsearch的7.12.1版本的镜像，这个镜像体积非常大，接近1G。不建议自己pull，采用load方式加载。
docker load -i es.tar
# 运行docker命令，部署单点es
docker run -d --name es -e "ES_JAVA_OPTS=-Xms512m -Xmx512m" -e "discovery.type=single-node" -v es-data:/usr/share/elasticsearch/data -v es-plugins:/usr/share/elasticsearch/plugins --privileged --network es-net -p 9200:9200 -p 9300:9300 elasticsearch:7.12.1
```

#### kibana-es

```shell
# load镜像
docker load -i kibana.tar
# 启动容器
docker run -d \
--name kibana \
-e ELASTICSEARCH_HOSTS=http://es:9200 \
--network=es-net \
-p 5601:5601  \
kibana:7.12.1
```

#### 安装ik分词器

```shell
# 安装插件需要知道elasticsearch的plugins目录位置，而我们用了数据卷挂载，因此需要查看elasticsearch的数据卷目录，通过下面命令查看:
docker volume inspect es-plugins
# 将ik分词器拷贝到挂载的数据卷中
cp -r /mnt/share/ik /var/lib/docker/volumes/es-plugins/_data
# 重启容器
docker restart es
```

#### 配置NAT端口转发

```shell
# 查看所有已转发的端口
netsh interface portproxy show all
# 重置转发规则（删除所有规则）
netsh interface portproxy reset
# 增加转发规则
# ssh
netsh interface portproxy add v4tov4 listenport=22 listenaddress=0.0.0.0 connectport=22 connectaddress=172.17.31.147
# nacos
netsh interface portproxy add v4tov4 listenport=8848 listenaddress=0.0.0.0 connectport=8848 connectaddress=172.17.31.147
# rabbitmq web管理
netsh interface portproxy add v4tov4 listenport=15672 listenaddress=0.0.0.0 connectport=15672 connectaddress=172.17.31.147
# rabbitmq 消息通信
netsh interface portproxy add v4tov4 listenport=5672 listenaddress=0.0.0.0 connectport=5672 connectaddress=172.17.31.147
# es外部通信
netsh interface portproxy add v4tov4 listenport=9200 listenaddress=0.0.0.0 connectport=9200 connectaddress=172.17.23.77
# es集群通信
netsh interface portproxy add v4tov4 listenport=9300 listenaddress=0.0.0.0 connectport=9300 connectaddress=172.17.23.77
# Kibana-es可视化工具
netsh interface portproxy add v4tov4 listenport=5601 listenaddress=0.0.0.0 connectport=5601 connectaddress=172.17.23.77
```





