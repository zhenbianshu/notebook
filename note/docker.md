###tips:
- Docker使用Host的kernal(bootfs),并在此实现自己的rootfs;

###创建docker镜像

1. 编辑 Dockerfile
2. 使用 docker build 命令创建镜像 `docker build [options] context`
     -t 指定新的镜像名  -f 指定Dockerfile路径
3. docker 将context 目录下的文件注入到docker daemon;
4. 执行Dockerfile 修改临时镜像；
5. 保存镜像；