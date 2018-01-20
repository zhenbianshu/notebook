空间索引
===
索引我们都用过，它是一种特殊的存储结构，就像图书馆里书的分类存放策略或是现代化图书馆里的图书查询系统，能帮助我们快速找到自己需要的书。
数据库中，索引的存储一般使用 B树 或 B+树 来实现，通过二分法来查找法来快速定位到数据位置。

普通索引对于一维数据(key->data)是无往不利，可是面对空间数据(lon,lat -> data)就有些无能为力了，如果查询(116.27636, 40.041285)附近的点：

- 我们在 lon 或 lat 列上创建普通索引，假设是 lon 列，那么通过 lon 列查找到同一经度的数据后，还要在此基础上过滤掉纬度差异过大的数据。

- 如果在 lon,lat 上创建多列索引，查询到同一经度、纬度相近的数据固然快，但附近的点并不只是经度相同。

如此下来，就要用到空间索引了。空间索引通过 四叉树、R 树等数据结构，还有 GeoHash 算法将二维数据转化为一维使用普通B树索引 来实现，它们都能实现对空间范围内的快速搜索。

可是，今天的主题不在这里，我们的首要目标是要解决问题，这些空间索引的实现改日专门写文章来实现。本文来说一说现有的数据库中对空间索引的支持情况，希望能帮助跟我一样的 GIS 小白进行技术选型。

组内准备切换 poi 数据的存储数据库，花了一周时间安装配置各种数据库来测试空间索引的效率，测试了 Redis, Mongo, PostgreSQL, Mysql 这几个知名的支持空间索引的数据库，技术选型基本完毕，可是中间踩过的坑和配置经验不能丢，详情如下：

-----
Redis
===
介绍
---
redis，一个功能强大、效率极高的缓存数据库（或许已经不仅仅是缓存数据库了），已经成为类似于关系存储型数据库在各个项目中不可或缺的组件了。首先考虑它是因为它的效率有保障，而且项目中几乎必备，运维代价很低。Redis 的 空间索引采用 GeoHash 原理，配合集合存储，查询效率接近 log(N)。

Redis 3.0 以上版本支持空间索引，新项目不必考虑这些，而一般的老项目可能就需要升级 Redis 了，另外 PHP 中可能还要升级 Redis 的扩展，以支持 Redis 的空间索引函数。

使用
---
Redis 的安装配置这里就不再多提了，这里简单地介绍一下 Redis 的 GEO 系列函数。


- `GEOADD key longitude latitude member [longitude latitude member ...]` 

	GEOADD 将元素添加到集合中，可一次添加多个元素，其对应的 php 函数原型为： `geoadd($key, $lon, $lat, $member)`；
- `GEORADIUS key longitude latitude radius m|km|ft|mi [WITHCOORD] [WITHDIST] [WITHHASH] [ASC|DESC] [COUNT count]`

	GEORADIUS 查询集合内 以目标点为圆心，半径为radius的圆内 的元素。其 php 函数原型为 `georadius($key, $lon, $lat, $radius, $unit, $options)`;
	其 $options 类似于 `array('count' => $count, 'WITHDIST' ...);`

结论
---
Redis 确实效率高，使用方便，但有一个无法克服的问题，即无法实现多条件查询。仅仅查询附近的点，Redis 是无懈可击，但是如果需求是查询附近的饭店呢？或是需求查询附近的 '万达' 呢？ 

不是不可以实现：

- 在关系型数据库内存储每个地点的详细信息，Redis 内的 member 存储每个地点在关系型数据库中的主键 ID，查询到地点的 ID 后，再去取地点的详细信息来过滤。

	多余的库访问，会造成额外的网络开销和 IO 开销。
- 在以一定规则拼接 member 的值，如 `$memeber = $name.','.$category;`，在查询到地点后解析 member 后进行过滤。
	
	较上面方法，省了网络开销，但不够灵活，如果再加上'城市'的限制，那么整个库的数据都要被清洗。
	
参考：
[Redis 命令参考 » GEO（地理位置）](http://redisdoc.com/geo/index.html)


-----
MongoDB
===
介绍
---

MongoDB 是老牌的支持空间索引的数据库，作为一个文档型数据库，它在存储日志或静态数据时效果不错。 它提供两种类型的空间索引：

- 2d 索引支持平台普通坐标的索引，适用于 2.4 版本之前；我们就不再考虑了，在大范围上存储和计算时，效率会有较大误差。
- 2dsphere 索引支持查询在一个类地球的球面上进行几何计算，以GeoJSON对象或者普通坐标对的方式存储数据。

2d 索引 和2dsphere 索引都是使用 GeoHash 算法用 B+ 树来实现。

使用
---

Mongo 创建空间索引的方法很简单：`db.collection.createIndex( { field : "2dsphere" } );`。

查询语句类似（下面是查询距目标点 3000米 内的地点）：

```
db.poi.find( { loc :
            	{ $near :
              		{ $geometry :
                      	{ type : "Point" ,
                           coordinates : [ 113.965355, 23.782865] 
                         } ,
                           $maxDistance : 3000
                      }
                  } 
                } )
```

Mongo 的使用需要注意如下：

- Mongo 的 PHP 扩展已经更新了，旧的扩展已被废弃，操作要使用 MongoDB\Driver\XXX 等类来进行，具体方法还是官方文档比较清晰。

- Mongo 的 2dsphere 索引需要建立索引的字段存储的数据为 geoJSON 对象，在 PHP 中的构造形式类似：

```
	$document = [
        	'loc' => [
            	'type' => 'Point',
            	'coordinates' => [$lon, $lat],
        	],
        	'name' => $name
    	];
```
- Mongo在查询返回距离时需要使用 runCommand 命令，其语法类似于 `db.runCommand({"geoNear":"collection", "near":[lon, lat], "num":count, query:{other condition}})`;

结论
---
mongo 的空间索引还是比较灵活的，GeoJSON 对象有点、线、多边形、多条线段、多点、多个多边形。支持 包含、相交、临近的查询，同时它也解决了 Redis 的多条件查询问题。

但是测试发现，mongo 有以下问题：

- 在进行大量数据时，性能会急剧下降，特别在符合条件的结果很多时，查询时间简直没法看。
- Mongo 对分词模糊查询的支持不太好，要进行按地点名字模糊查询还需要想办法。
- Mongo 的安全性配置是个问题。

参考：[Mongodb地理空间索引和查询(Geospatial Indexes)](http://coderschool.cn/1780.html)

[MongoDB » GeoJSON](https://docs.mongodb.com/manual/reference/geojson/)


-----
PostgreSQL
===
介绍
---
postgreSQL 是一个知名的关系型数据库，构建在其上的空间对象扩展模块 PostGIS 使得其成为一个真正的大型空间数据库。它通过 R树 或 GIST 树索引来实现共空间索引，查询效率极高。同时它对分词模糊查询支持很好，也能解决以地点名查询的需求。

>PostGIS 是一个开源程序，它为对象－关系型数据库PostgreSQL提供了存储空间地理数据的支持，使 PostgreSQL 成为了一个空间数据库，能够进行空间数据管理、数量测量与几何拓扑分析。PostGIS 实现了 Open Geospatial Consortium 所提出的基本要素类（点、线、面、多点、多线、多面等）的 SQL 实现参考。

使用
---
postgreSQL 的使用，对比其他数据库来说，较繁琐。

1. 要使用 postgreSQL 的空间索引，需要安装 postgis，由于它依赖多而复杂，能使用 yum，apt-get，homebrew 等工具的优先使用；
2. 数据库完毕后使用 initdb 命令初始化一个数据库；
3. 使用非root用户 `postgres -D datadir` 开启服务； 
4. 使用 `CREATE EXTENSION postgis;` 安装扩展；
5. 使用 `CREATE INDEX idx_name ON table USING gist(field);` ；

然后就可以建表建索引导数据了。

以下是一个典型的查询语句（查询跟目标点 3000米 内的地点名称和距离）：

```
SELECT id, name, st_astext(loc), 
	ST_Distance(loc, ST_GeographyFromText('SRID=4326;POINT(118.08688 33.64843)')) as dist
 FROM test WHERE 
 	ST_DWithin(loc, ST_GeographyFromText('SRID=4326;POINT(118.08688 33.64843)'), 3000) 
order by dist ASC limit 200;
```

使用时还需要注意：

- 如果需要进行中文分词查询的话，初始化数据库时要添加 `-E UTF8` 选项来指定字符集;
- postgreSQL 不能使用 root 用户登陆，对于权限的控制也比较严，动辙需要赋予权限；
- 注意将数据保存为 geography 对象，默认使用 m 为单位。在使用 geometry 对象时，默认使用 笛卡尔度 为单位，虽然可以互相转换，但麻烦是少不了的。
- 建表时要指定其 SRID （空间参考标识符， 是与特定坐标系、容差和分辨率关联的唯一标识符） 值，以经纬度存储用 4326; 例如 `loc geography(point, 4326)`，另外将数据转为 geography 点时也要注意使用 SRID:4326；
- 使用`ST_ASText(field)` 和 `ST_GeographyFromText('SRID=4326;POINT(lon lat)'))` 进行geography 和 字符串点进行转换；
- 主键索引数据类型可指定为 `serial`，类型于mysql的 int auto increment;
- 使用 `\timing on` 和 `\timing off`来切换是否显示命令执行时间；


结论
---
postgreSQL 对空间查询的支持非常灵活，足以支持多种复杂的空间查询，PostGIS 能计算不同投影坐标系下的真实空间距离，且查询效率极高，在大量数据时也不会像 mongo 一样性能急剧下降。

同时它关系型数据库的特性支持我们进行多条件查询，最后它也可以使用 `zhparser` 扩展来进行中文分词，以支持对地点名模糊查询。

虽然它在存在着复杂索引时写入较慢的问题，但对于存储不常变动的地点信息来说，是无关大碍的。

参考：
[PgSQL · 功能分析 · PostGIS 在 O2O应用中的优势](http://mysql.taobao.org/monthly/2015/07/04/)

[PostgreSQL 全表 全字段 模糊查询的毫秒级高效实现](https://yq.aliyun.com/articles/68424)

[postgis - 函数介绍](https://postgis.net/docs/using_postgis_dbmanagement.html#PostGIS_Geography)

-----
MySQL
===
介绍
---
Mysql 的重要性和强大不必多言，它的存储引擎 MyISAM 很早就支持空间索引。而 InnoDB 则在5.7.4 labs版本中才添加对空间索引的支持。

它们都是通过 R 树来实现空间索引。

使用
---
Mysql 中空间索引使用时要注意：

- 对空间索引的字段首先要设置为`field geometry NOT NULL`；
- 使用建立空间索引 ``SPATIAL KEY `idx_fld` (`geom`)``来创建一列空间索引；
- SQL语句中字符串与geometry的转换函数 `POINTFROMTEXT('POINT(lon lat)')`；
- 进行范围查询时要先构造空间区域：`GEOMFROMTEXT('Polygon((lon1 lat1,lon2 lat2,lon3 lat3,lon4 lat4 ...))'`

以下是一个典型的空间查询语句(查询距目标点3km以内的点)：

```
SELECT id, ST_Distance_Sphere(Point(-73.951368, 40.716743), geom) as dist, tags, ST_AsText(loc)
FROM nodes
WHERE ST_Contains( ST_MakeEnvelope(
                    Point((-73.951368+(3/111)), (40.716743+(3/111))),
                    Point((-73.951368-(3/111)), (40.716743-(3/111)))
                 ), loc )
      
ORDER BY dist LIMIT 10
```

结论
---
由于 Innodb 的功能比 MyISAM 强大太多，且事务、行锁、B+树索引等功能的不可替代性，这里不再讨论 MyISAM。

Mysql 的空间索引查询效率不低。作为传统的关系型数据库，其多条件支持、分词也都被很好地支持。

虽然对 InnoDB 的空间索引有信心，也略期待，可是对一个长时间存在的系统来说，数据库版本的升级真正不是一个简单的事。

参考：[MySQL Blog - mysql对GIS空间数据的支持](http://mysqlserverteam.com/mysql-5-7-and-gis-an-example/)

-----
总结
===

我以 126万 poi 数据进行了测试，查询范围 3km 内的点（最多取200条）。
系统信息： macos10.12 (x86_64)； 内核： 2 GHz Intel Core i5； 内存： 8 GB 1867 MHz LPDDR3；

以下是各数据库的对比情况：

|数据库|耗时|区域查询|多条件支持|分词支持|运维复杂度|备注|
|---|---|---|---|---|---|---|---|
|redis(3.2.8)|1-10ms|不支持|不支持|不支持|低|简单但功能单一|
|mongo(3.4.4)|10-50ms|支持|支持|不支持|中|结果数据量大时性能下降明显|
|postgreSQL(9.6.2)|3-8ms|支持|支持|支持|中|数据写入较慢|
|mysql（5.7.18 Innodb）|8-15ms|支持|支持|支持|低|版本升级太困难|

可能测试操作时有些误差，有熟练使用这些数据库的可以评论交流一下。

数据库没有哪个一定好，只要适合场景即可。

如果您觉得本文对您有帮助，可以点击下面的 **推荐** 支持一下我。博客一直在更新，欢迎 **关注** 。