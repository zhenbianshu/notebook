tips
---
nginx 可配置CPU亲合力，将其核心数与其工作进程数搭配，以达到效率的最大优化；
如：
```
worker_processes 8;
worker_cpu_affinity 00000001 00000010 00000100 00001000 00010000 00100000 01000000 10000000;
```
- nginx配置中使用 try_files A B C 来依次尝试文件，以替代if 语句
- 在url重写中，使用 `$request_uri` 来代替 `^(.*)$` 作为请求行

配置：
---
- Fast CGI(Fast Common Gateway Interface):是普通CGI的升级版本，普通CGI是在请求来时服务器临时FORK一下进程加载环境变量等，然后解释脚本执行，然后将标准错误和标准输出返回给服务器，此进程关闭，这样每次有请求临时FORK一个进程会造成大量的内存浪费。
Fast CGI是在服务器开启时初始化几个CGI解释器，在像普通CGI处理完请求后，不会自动关闭，而是接着处理后续请求。

- Nginx 是非阻塞IO & IO复用模型，通过操作系统提供的类似 epoll 的功能，可以在一个线程里处理多个客户端的请求。Nginx 的进程就是线程，即每个进程里只有一个线程，但这一个线程可以服务多个客户端。

- PHP-FPM 是阻塞的单线程模型，pm.max_children 指定的是最大的进程数量，pm.max_requests 指定的是每个进程处理多少个请求后重启(因为 PHP 偶尔会有内存泄漏，所以需要重启).PHP-FPM 的每个进程也只有一个线程，但是一个进程同时只能服务一个客户端。
大多数的 Linux 程序都倾向于使用进程而不是线程，因为 Linux 下相对来说创建进程的开销比较小，而 Linux 的线程功能又不是很强大。

- 反向代理：客户端请求nginx服务器，nginx自动转发请求到上游服务器，然后将上游服务器的响应再返回给客户端，反向代理隐藏了真正的服务器，代理的对象是服务端
- 正向代理：客户端配置代理服务器为nginx服务器，nginx作为代理服务器去请求上游服务器，正向代理隐藏了真正的客户端，代理的对象是客户端，

http状态码
---
- 301，永久重定向，浏览器获取到此状态码后，以后的请求就会直接请求重定向地址。
- 302，临时重定向，浏览器获取到此状态码后，再去请求重定向地址，可以保证浏览器每次都会请求源地址。
- 500错误，服务器内部错误，接受到请求无法正常响应。
- 502错误，代理或网关获取到服务器返回的报文无效。
- 504错误，服务器响应超时。


nginx配置
---
```
http {
    upstream stream_name{
        [ip_hash]; ##以ip哈希
        serverA ip:port [weight=num1] [down ##不支持负载] [backup##其他非备份机出错时访问此机器];
        serverB ip:port [weight=num2] [max_fail##最大失败次数后,返回错误] [fail_timeout ##最大失败次数后,暂停时间];
        [fair]; ##以响应时间分配
        [url_hash] ##以url哈希
    }
    server {
        listen port;
        server_name domain_name host_name;
        root root_path;

        access_log log_path;
        error_log log_path;

        location path {
            proxy_pass http://stream_name;
        }
    }
}
```

location 配置
---
优先级从高到低：
1. 完全相等匹配 `location = xxx {}`
2. 前缀匹配  `location xxx {}` 或 `location ^~ {}`
3. 正则匹配 `location ~ xxx {}` 或不区分大小写正则匹配 `location *~ xxx {}`
4. 通用匹配 `location / {}`
由优先级从高到低进行匹配，同一类型的，优先使用长串匹配（长串会更精确），直到匹配到一个`没有 rewrite 的location 段`；

rewrite 配置
---
执行顺序：`server中的rewrite -> location -> location中的rewrite`
有rewrite 字段，且没有 break，则从最开始的server重新匹配，循环超10次会返回 `500 internal server error`
有四种操作：
- last 结束此次 模块(server/location) 匹配;
- break 结束所有 模块 匹配；
- redirect 客户端返回 302 临时重定向；
- permanment 客户端返回 301 永久重定向；

全局变量
---
- \$args: 请求行内的参数 同 $query_string
- \$content_length/\$content_type/\$http_user_agent/\$http_cookie: 请求行内 content_length/content_type/user_agent/cookie 字段
- \$document_root: 当前请求的 root 字段值；
- $host: 请求主机头字段或为服务器名
- $limit_rate: 请求速率
- $request_method: 请求方式 GET/POST
- $remote_addr: 客户端IP地址
- $remote_port: 客户端端口
- $remote_user: auth user字段
- $request_filename: 请求文件的路径，由root或alias与URI请求生成
- $scheme: HTTP方法，http/https
- $server_protocol: 请求协议 http/1.0或http/2.0等
- $server_addr：服务器IP
- $server_port: 服务器端口
- $server_name: 服务器名称
- $request_uri: 原始请求的uri /test.php?arg=hello
- $uri: 不带请求参数的uri /test.php
- $document_uri:同uri

Nginx转发
---
nginx可以在Location模块中配置转发：
```
proxy_pass  http://xxxx;  把流量转发到 xxxx；
proxy_set_header  DESTFIELD $sourcefield; // 转发时将 $sourcefield 信息置入目标 HTTP头

注意 DESTFIELD 中使用大写，不能使用 '-_' 等字符，如使用，需要配置 'underscores_in_headers on'（off default）
在日志项中可以添加  "$http_destfield" 将某一header添加到日志中 , 添加 '$http_' 前缀，后接小写目标字段名
```












