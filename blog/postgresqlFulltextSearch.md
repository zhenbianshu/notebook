使用PostgreSQL进行全文检索

全文检索应该算是一个挺普遍的需求了，虽然有 ES 这种搜索引擎，但为了数据的灵活性，关系型数据库更为适合。本文记录了 PostgreSQL 进行全文搜索时的配置步骤，总结了几个优化点，同时填了几个经常遇到的坑。

#前言
PostgreSQL 被称为是“最高级的开源数据库”，它的数据类型非常丰富，用它来解决一些比较偏门的需求非常适合。

前些天将 POI 点关键词查询的功能迁到了 PgSQL，总算对前文 [空间索引 - 各数据库空间索引使用报告](http://www.cnblogs.com/zhenbianshu/p/6817569.html) 有了一个交代。

由于 PgSQL 国内的资料较少，迁移过程踩了不少坑，这里总结记录一下，帮助后来的同学能顺利使用 PgSQL。而且目前在灰度测试刚布了一台机器，后续可能还要添加机器，整理一下流程总是好的。

文章经常被人爬，而且还不注明原地址，我在这里的更新和纠错没法同步，这里注明一下原文地址：http://www.cnblogs.com/zhenbianshu/p/7773930.html

---
#开始
###安装
首先是安装 PgSQL，这里我使用的是 PgSQL 9.6，PgSQL 10 也刚发布了，有兴趣的可以尝下鲜。

PgSQL 的安装可以说非常复杂了，除了要安装 Server 和 Client 外，还需要安装 devel 包。为了实现空间索引功能，我们还要安装最重要的 PostGIS 插件，此插件需要很多依赖，自己手动安装非常复杂而且很可能出错。

推荐自动化方式安装，Yum 一定要配合 epel 这样的 Yum 源，保障能将依赖一网打尽。当然最好的还是使用 docker 来运行，找个镜像就行了。

###插件
由于 PgSQL 的很多功能都由插件实现，所以还要安装一些常用的插件，如:

```
postgis_topology（管理面、边、点等拓扑对象）
pgrouting（路径规划）
postgis_sfcgal（实现3D相关算法）
fuzzystrmatch（字符串相似度计算）
address_standardizer/address_standardizer_data_us（地址标准化）
pg_trgm（分词索引）
```

这些插件在安装目录 `/path/extensions` 下编译完毕后，在数据库中使用前要先使用 `create extension xxx` 启用。

###启动
1. 切换到非 root 用户。（PgSQL 在安装完毕后会创建一个名为 `postgres` 的超级用户，我们可以使用这个超级用户来操作 PgSQL，后期建议重新创建一个普通用户用来管理数据）；
2. 切换到 `/installPath/bin/` 目录下，PgSQL 在此目录下提供了很多命令，如 `createdb、createuser、dropdb、pg_dump` 等；
3. 使用 `createdb` 命令初始化一个文件夹 `dir_db` (此目录不能已存在)存放数据库物理数据，使用 `-E UTF8` 参数指定数据库字符集为 utf-8；
4. 使用 `pg_ctl -D dir_db` 指定数据库启动后台服务；
5. 使用 `psql -d db` 在命令行登陆 PgSQL;

---
#配置
安装完毕后还要配置一些比较基本的参数才能正常使用。
###Host权限
PgSQL需要在 `pg_hba.conf` 文件中配置数据库 Host 权限，才能被其他机器访问。

```
# TYPE  DATABASE        USER            ADDRESS                 METHOD
local   all             all                                     trust
host    all             all             127.0.0.1/32            md5
host    all             all             172.16.0.1/16            md5
```

文件中注释部分对这几个字段介绍得比较详细， 我们很可能需要添加 `host(IP)` 访问项， ADDRESS 是普通的网段表示法，METHOD 推荐使用 `md5`，表示使用 md5 加密传输密码。
###服务器配置
服务器配置在 `postgresql.conf`中，修改配置后需要 使用 `pg_ctl restart -D dir_db` 命令重启数据库；

此外，我们也可以在登陆数据库后修改配置项：使用 `SELECT * FROM pg_settings WHERE name = 'config';` 查询当前配置项，再使用 UPDATE 语句更新配置。但有些配置如内存分配策略是只在当前 session 生效的，全局生效需要在配置文件中修改，再重启服务器。

我们可以修改配置并用客户端验证 SQL 语句的优化，使用 `\timing on` 开启查询计时，使用 `EXPLAIN ANALYSE` 语句 分析查询语句效率。
下面介绍两个已实践过的配置参数：

- shared_buffers：用于指定共享内存缓冲区所占用的内存量。它应该足够大来存储常使用的查询结果，以减少物理I/O。但它也不能太大，以避免系统 内存swap 的发生， 一般设置为系统内存的 20%。
- work_mem：一个连接的工作内存，在查询结果数据量较大时，此值如果较小的话，会导致大量系统 I/O，导致查询速度急剧下降，如果你的 explain 语句内 `buffer 部分 read`数值过大，则表示工作内存不足，需要调整加此参数。但此值也不能太大，需要保证 `work_mem * max_connections + shared_buffers + 系统内存 < RAM`，不然同样可能会导致系统 内存swap。

这样，PgSQL 就能作为一个正常的关系型数据使用了。

---
#分词
全文索引的实现要靠 PgSQL 的 gin 索引。分词功能 PgSQL 内置了英文、西班牙文等，但中文分词需要借助开源插件 `zhparser`；
###SCWS 
要使用 zhparser，我们首先要安装 SCWS 分词库，SCWS 是 Simple Chinese Word Segmentation 的首字母缩写（即：简易中文分词系统），其 GitHub 项目地址为 [hightman-scws](https://github.com/hightman/scws)，我们下载之后可以直接安装。

安装完后，就可以在命令行中使用 `scws` 命令进行测试分词了， 其参数主要有：

- -c utf8 指定字符集
- -d dict 指定字典  可以是 xdb 或 txt 格式
- -M 复合分词的级别， 1~15，按位异或的 `1|2|4|8` 依次表示 `短词|二元|主要字|全部字`，默认不复合分词，这个参数可以帮助调整到最想要的分词效果。

###zhpaser
1. 下载 zhparser 源码 `git clone https:github.com/amutu/zhparser.git`；
2. 安装前需要先配置环境变量：`export PATH=$PATH:/path/to/pgsql`；
3. `make && make install`编译 zhparser；
4. 登陆 PgSQL 使用 `CREATE EXTENSION zhparser;` 启用插件；
5. 添加分词配置
 
    ```
	CREATE TEXT SEARCH CONFIGURATION parser_name (PARSER = zhparser); // 添加配置
	ALTER TEXT SEARCH CONFIGURATION parser_name ADD MAPPING FOR n,v,a,i,e,l,j WITH simple; // 设置分词规则 （n 名词 v 动词等，详情阅读下面的文档）
	```
6. 给某一列的分词结果添加 gin 索引 `create index idx_name on table using gin(to_tsvector('parser_name', field));`
7. 在命令行中使用上一节中介绍的 scws 命令测试分词配置，如我认为复合等级为 `7` 时分词结果最好，则我在 `postgresql.conf`添加配置
	
	```
	zhparser.multi_short = true #短词复合: 1
zhparser.multi_duality = true  #散字二元复合: 2
zhparser.multi_zmain = true  #重要单字复合: 4
zhparser.multi_zall = false  #全部单字复合: 8
	```

###SQL
查询中我们可以使用最简单的 `SELECT * FROM table WHERE to_tsvector('parser_name', field) @@ 'word'` 来查询 field 字段分词中带有 word 一词的数据；

使用 `to_tsquery()` 方法将句子解析成各个词的组合向量，如 `国家大剧院` 的返回结果为 `'国家' & '大剧院' & '大剧' & '剧院'` ，当然我们也可以使用 `& |` 符号拼接自己需要的向量；在查询 长句 时，可以使用 `SELECT * FROM table WHERE to_tsvector('parser_name', field) @@ to_tsquery('parser_name','words')`；

有时候我们想像 MySQL 的 `SQL_CALC_FOUND_ROWS` 语句一样同步返回结果条数，则可以使用
`SELECT COUNT(*) OVER() AS score FROM table WHERE ...`，PgSQL 会在每一行数据添加 score 字段存储查询到的总结果条数；

到这里，普通的全文检索需求已经实现了。

#优化
我们接着对分词效果和效率进行优化：
###存储分词结果
我们可以使用一个字段来存储分词向量，并在此字段上创建索引来更优地使用分词索引：

```
ALTER TABLE table ADD COLUMN tsv_column tsvector; 			// 添加一个分词字段
UPDATE table SET tsv_column = to_tsvector('parser_name', coalesce(field,'')); 	// 将字段的分词向量更新到新字段中
CREATE INDEX idx_gin_zhcn ON table USING GIN(tsv_column); 	// 在新字段上创建索引
CREATE TRIGGER trigger_name BEFORE INSERT OR UPDATE  ON table FOR EACH ROW EXECUTE PROCEDURE
tsvector_update_trigger(tsv_column, 'parser_name', field); // 创建一个更新分词触发器
```
这样，再进行查询时就可以直接使用 `SELECT * FROM table WHERE tsv_column @@ 'keyword'` 了。

这里需要注意，这时候在往表内插入数据的时候，可能会报错，提示指定 parser_name 的 schema， 这时候可以使用 `\dF` 命令查看所有 text search configuration 的参数：

```
               List of text search configurations
   Schema   |    Name    |              Description
------------+------------+---------------------------------------
 pg_catalog | english    | configuration for english language
 public     | myparser   |
```
注意 schema 参数，在创建 trigger 时需要指定 schema， 如上面，就需要使用 `public.myparser`。

###添加自定义词典
我们可以在网上下载 xdb 格式的词库来替代默认词典，词库放在 `share/tsearch_data/` 文件夹下才能被 PgSQL 读取到，默认使用的词库是 `dict.utf8.xdb`。要使用自定义词库，可以将词库放在词库文件夹后，在 `postgresql.conf` 配置 `zhparser.extra_dict="mydict.xdb"` 参数；

当我们只有 txt 的词库，想把这个词库作为默认词库该怎么办呢？使用 scws 带的`scwe-gen-dict` 工具或网上找的脚本生成 xdb 后放入词库文件夹后，在 PgSQL 中分词一直报错，读取词库文件失败。我经过多次实验，总结出了一套制作一个词典文件的方法：

1. 准备词库源文件 mydict.txt：词库文件的内容每一行的格式为`词  TF  IDF 词性`，词是必须的，而 `TF 词频(Term Frequency)、IDF 反文档频率(Inverse Document Frequency) 和 词性` 都是可选的，除非确定自己的词典资料是对的且符合 scws 的配置，不然最好还是留空，让 scws 自已确定；
2. 在 `postgresql.conf` 中设置 `zhparser.extra_dicts = "mydict.txt"` 同时设置 `zhparser.dict_in_memory = true`；
3. 命令行进入 PgSQL，执行一条分词语句 `select to_tsquery('parser', '随便一个词')` ，分词会极慢，请耐心(请保证此时只有一个分词语句在执行)；
4. 分词成功后，在/tmp/目录下找到生成的 `scws-xxxx.xdb` 替换掉 `share/tsearch_data/dict.utf8.xdb`；
5. 删除刚加入的 `extra_dicts dict_in_memory` 配置，重启服务器。

###扩展
由于查询的是 POI 的名称，一般较短，且很多词并无语义，又考虑到用户的输入习惯，一般会输入 POI 名称的前几个字符，而且 scws 的分词准确率也不能达到100%，于是我添加了名称的前缀查询来提高查询的准确率，即使用 B树索引 实现 `LIKE '关键词%'` 的查询。这里需

这里要注意的是，创建索引时要根据字段类型配置 `操作符类`，不然索引可能会不生效，如在 字段类型为 varchar 的字段上创建索引需要使用语句`CREATE INDEX idx_name ON table(COLUMN varchar_pattern_ops)`，这里的 varchar_pattern_ops 就是操作符类，操作符类的介绍和选择可以查看文档：[11.9. 操作符类和操作符族](http://www.postgres.cn/docs/9.4/indexes-opclass.html)。

自此，一个良好的全文检索系统就完成了。

---
#总结

简单的数据迁移并不是终点，后续要做的还有很多，如整个系统的数据同步、查询效率优化、查询功能优化（添加拼音搜索、模糊搜索）等。特别是查询效率，不知道是不是我配置有问题，完全达不到那种 E级毫秒 的速度，1kw 的数据效率在进行大结果返回时就大幅下降（200ms），只好老老实实地提前进行了分表，目前百万级查询速度在 20ms 以内，优化还有一段路要走。


不过这次倒是对 技术的“生态”有了个更深的体会，这方面 PgSQL 确实和 MySQL 差远了，使用 MySQL 时再奇葩的问题都能在网上快速找到答案，而 PgSQL 就尴尬了，入门级的问题搜索 stackoverflow 来来回回就那么几个对不上的回答。虽然也有阿里的“德哥”一样的大神在辛苦布道，但用户的数量才是根本。不过，随着 PgSQL 越来越完善，使用它的人一定会越来越多的，我这篇文章也算是为 PgSQL 加温了吧，哈哈~希望能帮到后来的使用者。

关于本文有什么问题可以在下面留言交流，如果您觉得本文对您有帮助，可以点击下面的 **`推荐`** 支持一下我，博客一直在更新，欢迎 **`关注`** 。

参考：

[PostgreSQL系统配置优化](http://aigo.iteye.com/blog/2064136)

[[PG]使用 zhparser 进行中文分词全文检索](http://www.jianshu.com/p/80e5d003b62c)

[SCWS 中文分词](http://www.xunsearch.com/scws/docs.php#libscws) 

[Fast Search Using PostgreSQL Trigram Indexes](https://about.gitlab.com/2016/03/18/fast-search-using-postgresql-trigram-indexes/)

[使用阿里云PostgreSQL zhparser时不可不知的几个参数](https://yq.aliyun.com/articles/7730)

[德哥的PostgreSQL私房菜 - 史上最屌PG资料合集](https://yq.aliyun.com/articles/59251)