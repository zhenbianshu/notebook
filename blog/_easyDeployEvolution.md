一键部署变迁史

#前言

之前的文章说过 由PHP转到Java之后，非常不适应的一点就是代码部署过程耗时长，调试不便，虽然可以使用debug，但有时候还是需要修改代码，整个流程需要：

- 使用命令将项目打成war包（1 min）
- 上传war包（2.5 min），公司内使用无线局域网，上传速度峰值只有1M不到，而且很不稳定，面对100多M的war包，有点力不从心。
- 重启docker进程（1 min）

而且步骤之间不连贯，需要在边上看着进度，并及时操作下一步。解决它们的难点就是这不是在同一台机器上进行的操作，需要自己的机器与服务器配合。

作为一个懒人，非常想一步完成，虽然可能达不到像PHP那种秒上传一个文件立即重启的效果，也要尽量快地部署测试包。本文就介绍了怎么一步步优化测试部署流程的。

#脚本NC时代
刚入职时，对war包什么的一脸懵逼，有同事给了一个脚本，称为“自动部署脚本”，这个脚本的功能是：

1. 服务器运行，在服务器上启动一个nc接收进程；
2. 在客户端上打包完后，使用另一个nc发送进程将包上传覆盖旧包
3. 接收完后 nc进程结束，脚本阻塞完后使用命令重启服务；

这里介绍一下nc命令：

这个脚本虽然比原始版本好了一些，可是时间上并没有缩短，只是帮我少输了两个命令（nc 服务端、重启命令）

命令还可以忍，可是龟速的上传速度真的很急人，这时我开始想着怎么加速上传。


#RSYNC“加速”上传
其实一开始我是想从硬件方面解决这个问题的，使用网线。为此，买了一个网线转接头和一段网线，可是通过同事的设备排除发现转接头和网线都没问题，可是接到一块就不匹配（围笑）。

穷则思变，接着我考虑从软件方面解决这个问题。问了几个同事后，发现有的同事在用rsync 同步文件，可是 `rsync 同步文件的单位不是文件` 吗？看了同事演示的上传后，感觉心态崩塌，不好好读文档的后果啊。

这里介绍一下 rsync 的使用：

###服务端

配置example
###客户端

###同步算法


#添加回调减少一步
使用了 rsync 之后，之前的脚本就不能用了，rsyncd 进程和 shell 进程是两个进程，上传结束后自动重启不好使了，这时我开始打 rsync 的主意了，rsync 是一个开源软件，我考虑帮它加一个参数，让它帮我在系统上传结束后执行一些命令。

rsync 的源码代码量还是挺大的，不过修改它我们不需要通读一遍，我将它分为两个步骤：

- 读取到callback参数的值；
- 上传结束后调用callback参数的值；

首先在 `proto.h` 文件里添加函数声明： `char *lp_callback(int module_id);`

读取参数的相关代码在 `load_param.c` 文件内，首先添加变量声明、设置默认值，最后添加参数调用函数。

服务端文件同步的代码在 `clientserver.c` 文件内，主体是 `sync_module` 函数，前面的一系列操作如用户认证、权限校验等我们可以不必管，找到最后一步，在其调用下一次同步函数前 添加如下代码（解释在注释中）：

```
	char * callback = lp_callback(i); // 读取 callback 参数
    if (callback != NULL && strlen(callback) != 0) {
        char cmd[strlen(callback) + 2];
        strcpy(cmd, callback);
        strcat(cmd, " &"); // system 命令会阻塞，需要在命令上添加 & 让它后台执行
        system(cmd); // 使用 stdlib 的 system 执行 callback 命令
    }
```


#tomcat自动部署
有大神提示可以自动部署，部署文档：https://tomcat.apache.org/tomcat-9.0-doc/deployer-howto.html


1. 配置 server.xml

```
autoDeploy=true

docker:
FROM tomcat:7-jre8
COPY server.xml /usr/local/tomcat/conf/

docker-compose
volumes:
   - ./cluster/server.xml:/usr/local/tomcat/conf/server.xml

```
2. rsync to docker

docker-compose + rsync

#小结
懒 + 不要跟着吃屎

后续添加个提示功能
curl -u "sendUid@password" -d "uid=receiveUid&text=content" "http://i2.api.weibo.com/2/direct_messages/new.json?source=xxxxxx" > /dev/null

git测试系统一键配置

