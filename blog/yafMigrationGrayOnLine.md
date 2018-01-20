代码迁移之旅（二）- 渐进式迁移方案

本文介绍了如何使用 Nginx 路由重写功能搭配 Linux 链接，再加上项目内部路由达到代码的渐进式迁移，在新旧项目之间实现无缝切换。

说在前面
===
这是代码迁移的第二篇文章，也是最后一篇了，由于个人原因，原来的迁移我无法继续参与了，但完整的方案我已经准备好了，在测试环境也已经可以正常进行了。
上篇文章 [代码重构之旅（一） 项目结构](http://www.cnblogs.com/zhenbianshu/p/7773930.html) 介绍了迁移代码的前期准备和项目结构的设计，本篇文章来介绍一下可实施的迁移方案。

在迁移之前，代码的可用性我们只能画一个问号，使代码的迁移过程更简单、更安全是我们要追求的目标。下面讲如何一步一步将代码代码迁移到正式环境。

文章欢迎转载，但请注明来源：http://www.cnblogs.com/zhenbianshu/p/7978835.html， 谢谢。

---
问题抽象分析
===
首先要看一下一次完整的迁移需要满足什么要求：

- 灰度发布，谁也无法保证一次将整个系统迁移到另一个系统不会发生问题，而以接口或接口部分流量为单位进行迁移则可以大大提升可控性。
- 客户端无感知，即迁移平滑，长时间的系统不可用是完全无法接受的。
- 可回滚，一旦出现异常问题可以快速回滚，避免造成较大影响。
- 易实现，尽量避免大量地操作，操作多意味着犯错的可能性更大，回滚的难度也大。

只有实现了以上要求，才算是一次成功的迁移。那么我们先分析一下目前的情况：

我是migrant图！！！！

如上图是我们两个系统的目前状态：

- 两个系统共享一个 Nginx 服务器，而且在 Nginx 中，由于新老系统的 `Host::Ip` 也不需要变动，所以新老系统还共享一个同一个 Server。
- 新旧两个模块分别对应着两个版本控制目录，旧模块将 Http 请求进行 url 重写后直接分发到各 PHP 脚本，例如：`rewrite ^/api/common/test.json?(.*)$ /api_test.php?$1;`
- 新模块将 Http 请求直接分发到 index.php 后，由 index.php 进行内部路由转发。

两个模块初始状态相安无事，现在的问题是如何将旧模块的接口逐渐过渡到新模块中。由于旧模块的分发入口在 Nginx 中，最简单的办法自然是修改其原来的重定向规则。

---
Nginx重定向
===

先看一个典型的nginx http服务器配置：

```nginx
http {
    upstream stream_name{
    }
    
    server {
        listen port;
        server_name domain_name host_name;
        
        rewrite ori destA;

        location pathA {
            rewrite ori destB type;
        }
        
        location pathB {
        	if(match){
        		rewrite ori destC type;
        	}
        		rewrite ori destD type; 
        }
    }
}
```

我们要使用的就是 Nginx 强大的路由重定向功能。

location
---
location 是一个 URI 捕获语句，它被定义在 server 模块内，会对 server 内的所有请求进行 URI 匹配，一旦匹配，则进入 location 模块内部执行。

location 常见的使用形式是：

```nginx
location path_pattern {
	operation;
}
```

它的 `path_pattern` 有以下几种形式，优先级从高到低为：

1. 完全相等匹配 `location = uri {}`
2. 前缀匹配  `location prefix {}` 或 `location ^prefix ~ {}`
3. 正则匹配 `location ~ regex {}` 或不区分大小写正则匹配 `location *~ regex {}`
4. 通用匹配 `location / {}`

不同的 pattern 类型匹配顺序与定义顺序无关，而是由优先级从高到低进行匹配，同一类型的，优先使用 pattern 串更长的进行匹配，因为长串会更精确。

它的 operation 一般是 rewrite 或 proxy\_pass 语句，对捕获到的请求进行重写或转发。用于转发的 proxy_pass 语句很简单， `proxy_pass proxy_name;` 即可，下面具体说一下路由重写功能。

if
---
if 语句可以对 uri 进行更加灵活的判断和操作，它的常见使用形式是：

```nginx
if (match) {
	rewrite ori destA type;
}
	rewrite ori destB type;
```
在 `match` 语句中，可以使用如 `$request_uri` 等全局变量，常见的还有 `$query_string,$uri,$remote_addr`等。

但是需要注意使用 if 语句是十分低效的行为，它就像普通的代码一样，每个 Http 请求碰到 if 语句都会进行一次 `match` 计算并判断，虽然写在 location 内部会好一些，但最好还是极力避免此语句。

rewrite
---
rewrite 是对匹配到的请求进行 uri 重写，它可以被写在 `server/location/if` 模块中，使用方式 是 `rewrite ori dest type;`。在 server 模块中，rewrite 和 location 的执行顺序为：`server中的rewrite -> location -> location中的rewrite`

我们可以使用正则或全相等来匹配 `ori`，并将正则结果应用于 `dest` 上，如 `rewrite ^/api/common/test.json?(.*)$ /api_test.php?$1;` 则将 ori 内部的 query\_string 匹配出来并使用 `$1` 赋值给 dest。

rewrite 默认将 uri 重写后并不直接将请求分发到 CGI，而是将结果 uri 作为一个新的请求再次进行 server 模块内处理，如果循环重入超 10 次 nginx 会直接返回 `500 internal server error`，而控制 rewrite 匹配后的行为 主要依靠其 type 参数：

- last 结束此模块(server/location) 匹配，并重入 server 模块处理，rewrite 默认使用此项;
- break 结束所有模块匹配，直接将请求分发到 CGI；
- redirect 直接分发请求，返回 Http 状态码 302 临时重定向；
- permanment 直接分发请求，返回 Http 状态码 301 永久重定向；

应用
---
介绍完了 Nginx 的重定向功能，还需要考虑怎么使用此功能进行代码的过渡。

1. 使用 location 捕获对应接口；
2. 使用 if 进行部分流量分发（可选）；
3. 将请求 rewrite 到新模块。

如：

```nginx
location ~ /api/test.json {  # 匹配到 test 接口
	if ($remote_addr ~* 1$) {  # 分流 IP 末位为 1 的请求
		root new_dir/public; # 设置新项目的目录为根目录
		rewrite ^(.*)$ /index.php$1 break; # 将请求分发到新项目的 index.php 入口文件
	}
	rewrite ^/api/test.json?(.*)$ /api_test.php?$1; # IP 末位不为 1 的请求继续访问旧项目
}
```

---
Linux链接
===
如上，我们发现如果针对每个接口进行一次 location 重定向，都需要写 7 行代码，即使不用 if 语句（多数情况如此），每次也需要 4 行代码。

```nginx
location ~ /api/test.json {  # 匹配到 test 接口
	root new_dir/public; # 设置新项目的目录为根目录
	rewrite ^(.*)$ /index.php$1 break; # 将请求分发到新项目的 index.php 入口文件
}
```
如此下来，项目如果有 100 个接口，那么维护这100个 location 模块也颇为废劲。其实更多时刻，我们并不需要使用 location 语句，直接在 server 模块内部使用 rewrite 即可，而阻止我们直接使用 rewrite 的罪魁祸首就是由于新旧模块不在同一文件夹下，我们必须使用 root 语句将根目录定义到新项目下，至于为什么不将新旧项目的父文件夹定义为 root，是因为项目中有一些路径可能会有深坑。

这里我们可以使用 linux 的 `软链接` 来 把新项目“放置”在旧项目下：linux 中软链接的功能就像 windows 中的`快捷方式`一样，是一个指向文件或真实目录的符号。至于其实现，就要说到 linux 文件结构中的重要概念 `inode` 了，这里不再多提。 

使用`ln -s /path/to/dir_new /path/to/dir_old/yaf` 在旧项目目录下创建一个 `yaf` 软链接指向新项目目录；

这样，就可以以旧项目目录为根目录，找到新项目目录下的文件了，使用单行命令 `rewrite ^(/api/test.json(.*)$) /yaf/public/index.php$1 break;`  即可。

---
框架内URL重写
===
通过上面 Nginx 的重定向，所有的请求都会被分发到 index.php 中， 接下来就需要在 yaf 内对 index.php 接收到的 Http 请求进行内部分发。

yaf 提供了 `Yaf_Route_Static、Yaf_Route_Simple、Yaf_Route_Supervar、Yaf_Route_Map、Yaf_Route_Rewrite、Yaf_Route_Regex ` 六种路由方式，各有其适合的场景，需要在 `/conf/application.ini` 中配置 `application.dispatcher.defaultRoute.type="type"`。

我们的内部接口名完全不规则，有改写为 `.json` 后缀的，也有保持 `.php` 的，有带下划线的，也有大小写敏感的，找不到什么规律，于是使用了 `map` 类型，直接匹配 `uri` 然后映射向 controller 类。

我们将 uri 和controller的映射统一保存在一个文件内，形如：

```php
return array(
  	// 接口作用
    'key' =>  
    	array(
    		'type' => 'rewrite',  
    		'match' => '/api/test.json', 
    		'route' => 
    			array(
    				'controller' => 'Api_Test'
    			),
    	),
    	...
    );
```

然后在 `Bootstrap.php` 内加载此配置文件：

```php
	public function _initRouter() {
        $router = \Yaf\Dispatcher::getInstance()->getRouter();
        $config = getConfig('rewrite_file_name');
        $router->addConfig($config);
    }
```

自此，关于迁移的配置就完成了。

---
测试
===
一次安全的迁移，完整的测试当然必不可少。在保证技术方案没问题的前提下，还要进行完整的业务逻辑测试。在  测试之前，开发首先要通过尽可能完整的测试，将 BUG 率降到最低。

我们的系统对外提供服务都是通过接口，这也方便了我们进行测试。为了保证测试的完整性，我们可以将线上流量引入到新代码中进行测试，而实行请求导流的最好媒介就是日志。

一般来说，服务器都有完整的线上请求日志，如果有必要，在给特定接口添加特定日志以配合测试也是可以的。接入线上日志，构造跟线上一样的请求到测试服务器，再对比原始服务器的响应内容，将异常响应记录下来由开发分析并查找原因，直到最后新旧项目对所有请求的响应完全一致。

---
小结
===
项目的重构不是一个小事，特别是大规模的项目代码迁移，执行它必须胆大心细，但每一次重构，无论是对自己的技术能力还是项目的生命周期都是很大的提升。

虽然不鼓励没事就瞎折腾代码，但一定要时刻警惕，走出代码的舒适区，一定要提前预防根治代码疾病，不要在代码已经无可救药时才想到重构，对代码有目的有计划的优化是件非常有意义的事。

关于本文有什么问题可以在下面留言交流，如果您觉得本文对您有帮助，可以点击下面的 **`推荐`** 支持一下我，博客一直在更新，欢迎 **`关注`** 。



