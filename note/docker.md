tips:
---
- Docker使用Host的kernal(bootfs),并在此实现自己的rootfs;
- 后台启动mysql时，可以使用 `-e MYSQL_ROOT_PASSWORD=XXX` 定义mysql的初始root命令；

参数：
---
- `docker run -d` 后台启动一个docker服务
- `-p dPort:hPort` 把宿主机的hPort收到的请求转发到 docker容器的dPort
- `--name dName` 指定docker实例的名字

实例：
---
- `docker run -d -p 6379:6379 redis` 启动一个redis服务器
- `docker run -d -p 3306:3306 mysql -e MYSQL_ROOT_PASSWORD=123456` 启用一个mysql服务器

创建docker镜像
----------


1. 编辑 Dockerfile
2. 使用 docker build 命令创建镜像 `docker build [options] context`
     -t 指定新的镜像名  -f 指定Dockerfile路径
3. docker 将context 目录下的文件注入到docker daemon;
4. 执行Dockerfile 修改临时镜像；
5. 保存镜像；