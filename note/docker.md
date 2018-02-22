tips:
---
- Docker使用Host的kernal(bootfs),并在此实现自己的rootfs;
- 后台启动mysql时，可以使用 `-e MYSQL_ROOT_PASSWORD=XXX` 定义mysql的初始root命令；

参数：
---
- `docker run -d` 后台启动一个docker服务 detached,也可以使用 `docker attach` 重新回到该会话；
- `-p dPort:hPort` 把宿主机的hPort收到的请求转发到 docker容器的dPort
- `--name dName` 指定docker实例的名字
- `docker exec -it containerSHA /bin/bash` 执行与容器的交互操作；
- `-v oriDir:containerDir` 映射宿主机的目录到容器中；


实例：
---
- `docker run -d -p 6379:6379 redis` 启动一个redis服务器
- `docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=123456 mysql` 启用一个mysql服务器

创建docker镜像
----------


1. 编辑 Dockerfile
2. 使用 docker build 命令创建镜像 `docker build [options] context`
     -t 指定新的镜像名  -f 指定Dockerfile路径
3. docker 将context 目录下的文件注入到docker daemon;
4. 执行Dockerfile 修改临时镜像；
5. 保存镜像；

启动nginx
-------
默认地：
- 启动配置文件在 `/etc/nginx/conf.d` 目录下；
- 网站目录在 `/usr/share/nginx/html` 下；
- 日志目录下 `/var/log/nginx/` 下；

启动时可以使用 -v 选项使用宿主机的目录替代原容器目录；

所以一个标准的nginx启动应该是 `docker run -p 80:8888 -v /data1/dir/myConfDir:/etc/nginx/conf.d -d nginx`