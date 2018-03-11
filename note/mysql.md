tips:
---
- 数据库类型的值一定要规划好，int的限制为 4 294 967 295；
- 使用union [all] 可以将两查询的两组数据合并取值；对于分了一次表的数据很有用
åΩ- 用distinct关键字只能过滤查询字段中所有记录相同的（记录集相同），而如果要指定一个字段却没有效果，另外distinct关键字会排序，效率很低.
- MySQL有配置参数lower_case_table_names,linux系统默认为0，即库表名以实际情况存储，大小写敏感。如果是1，以小写存储，大小写不敏感。如果是2，以实际情况存储，但以小写比较。
-  由一个数据库导出到另一个数据库
在源数据库：mysqldump database -uuser -ppassword --add-drop-table | mysql (-h 目标ip) database -uuser -ppassword
- mysql my.cnf中的wait_timeout 字段是指mysql服务器在连接处理不活跃状态时的等待时间，超过等待时间连接
- mysql 配置文件读取顺序为 /etc/my.cnf  /bin/mysql/my.cnf   /usr/local/mysql/my.cnf  /my.cnf 以 “最后一个” 为准
- int(M) 中M的理解，int型数据无论是int(4)还是int(11)，都已经占用了 4 bytes 存储空间，M表示的只是显示宽度(display width, max value 255)，并不是定义int的长度。
- decimal类型为二进制存储，M是数字最大位数（精度precision），范围1-65；D是小数点右侧数字个数（标度scale），范围0-30，但不得超过M。
占用字节数计算方法 —— 小数和整数分别计算，每9位数占4字节，1-2位 1字节  3-4位 2字节 5-6位 3字节 7-9位四字节；
- mysql varchar(n)类型存储时，尽量选用较小的n值，虽然mysql在存储方式上，n的值为1和255没什么区别；但在查询时，临时表会为varchar存储尽量大的空间，在字符集为utf-8时，内存表分配的空间为 3*n + 长度；另外还要考虑别的存储引擎的存储方式；
- replace into 会使用当前参数替换掉与之唯一主键冲突的数据，流程是删除原有记录，新建一条记录；
- set password = password('new password') 设置当前登陆的用户密码；
- `show status like '%item%'` 查看mysql状态,如client

- 使用`POINTFROMTEXT('POINT({$lon} {$lat})')`插入点数据

- 关于唯一索引和软删除的冲突问题，可以将软删除的是否已删除 id_deteted 改为 int 字段，存放主键ID值，并添加到唯一索引中；

- `mysql -uLBS_r -pEFN0uQq6nqdD -hs5000c.randa.grid.sina.com.cn -P5000 LBS  -e "SELECT poiid,lon,lat,category,city,district from lbs_poi_a" >> /tmp/lbs_poi_a.txt`  登陆数据库并将查询结果写入到某个文件

优化：
---
- 针对不同的数据需求：数据分层，放在本地cache/远程cache/远程数据库等。
- 减少数据库操作：在必要逻辑保证的情况下：加缓存和数据冗余。
- 不用存储过程：不利于迁移，各个数据库不兼容
- 不用外键：存储时查询效率低
- 查询只有一条数据时尽量使用 limit 1；因为这样mysql引擎会在找到一条数据后停止向后搜索，而且不用去检查返回条数；
- join时，最好对join的两个字段加上索引，并且最好能确认两个字段类型相同，不然mysql不会去使用索引；
- null字段会消耗额外的空间，所以尽量使用not null，即使加上’‘或0，都会比null要省空间；
- 固定长度的值会更快点，因为mysql容易计算偏移量；
- delete 和 insert 操作会进行表级锁，太慢的delete和insert会占用表大量时间，容易造成雪崩

sql查询处理的每一个阶段
---
1.  FORM: 对FROM的左边的表和右边的表计算笛卡尔积。产生虚表VT1
2.  ON: 对虚表VT1进行ON筛选，只有那些符合<join-condition>的行才会被记录在虚表VT2中。
3.  JOIN： 如果指定了OUTER JOIN（比如left join、 right join），那么保留表中未匹配的行就会作为外部行添加到虚拟表VT2中，产生虚拟表VT3, rug from子句中包含两个以上的表的话，那么就会对上一个join连接产生的结果VT3和下一个表重复执行步骤1~3这三个步骤，一直到处理完所有的表为止。
4.  WHERE： 对虚拟表VT3进行WHERE条件过滤。只有符合<where-condition>的记录才会被插入到虚拟表VT4中。
5.  GROUP BY: 根据group by子句中的列，对VT4中的记录进行分组操作，产生VT5.
6.  CUBE | ROLLUP: 对表VT5进行cube或者rollup操作，产生表VT6.
7.  HAVING： 对虚拟表VT6应用having过滤，只有符合<having-condition>的记录才会被 插入到虚拟表VT7中。
8.  SELECT： 执行select操作，选择指定的列，插入到虚拟表VT8中。
9.  DISTINCT： 对VT8中的记录进行去重。产生虚拟表VT9.
10.  ORDER BY: 将虚拟表VT9中的记录按照<order_by_list>进行排序操作，产生虚拟表VT10.
11.  LIMIT：取出指定行的记录，产生虚拟表VT11, 并将结果返回。

left/right join 和 inner join ：
---
查询对象：
	left/right join 是【依赖左/右表】为主表而生成记录，主表有数据就会有一条数据，副表的数据可以为空
  	inner join查询【两个表都存在数据】而结合起的数据
索引的使用 ：
	left/right join where table.cond 语句会使用对应table的索引，同时 on语句会使用【主表的索引】
	inner join on语句会使用【两个表的索引】

mysql for update 语句  
---
在innodb里加了行悲观锁，使用此语句防止出现并发时，查询出来判断后修改数值，但数值已被其他进程修改过的情况；
使用：
```
set autocommit=0; // 关闭自动提交
select * from xxx for update; // 查询加上悲观锁锁
update ... 更新
commit; // 提交事务，解除悲观锁；
```
字符集：
---
uft8和utf8mb3存储每字符占用1-3个字节，最多能存储65535个字符，包括了，中韩日越 四种语言
utf32能存储所有字符，每个字符固定占用4字节。
utf8mb4也能存储所有字符，但它每个字符占用1-4个字节，而且能存储普遍的emj表情符

limit 分页优化
---
普通的limit m,n 分页方式，当m值特别大的时候会造成大量的IO浪费，推荐分页方式：
```
SELECT * FROM table WHERE TIME<last_TIME ORDER BY TIME DESC LIMIT 10.
SELECT * FROM table inner JOIN(SELECT id FROM table ORDER BY TIME LIMIT 10000,10) as t USING(id)
```

索引
---
- 聚集索引：由于B+树最底层包含了所有数据表的信息，其子结点中包含了每一个页的信息，所以可以通过主键索引查找到所有数据。而非聚集索引则需要拿到主键ID后再通过主键ID拿到数据信息；
- 联合索引：指多个字段结合在一块形成的索引，其索引是按照 第一字段排序，在第一字段相同的情况下按第二字段排序，所以会有使用索引必须在第一字段上添加where的限制；
- 覆盖索引：索引里的内容已经包括了查找所需要的所有字段，不需要再通过主键查找数据。通过主键查找时会使用聚集索引作为特殊的覆盖索引；
- 哈希索引：通过计算哈希值直接定位到存储位置的索引，时间复杂度一般为O（1），但由于通过哈希值来定位，只支持等值查询，不支持范围查询。
- 全文索引：mysql通过建立倒排索引列来实现全文索引，只支持有明显字符界定符的语言，不支持中文。
- 建议mysql使用索引 `SELECT  * FROM USE INDEX (index_name) WHERE ...`;
- 强制mysql使用索引 `SELECT  * FROM FORCE INDEX (index_name) WHERE ...`;

隔离级别
---
|READ UNCOMMITED（未提交读）|READ COMMITED（已提交读）|READ REPEATABLE（可重复读）|SEARIALIZABLE（串行化）|
|---|---|---|
|脏读（读到另一个未提交的事务）|不可重复读（读到另一个已提交事务）|幻读（修改数据被另一事务重写）|丢失更新（读到的数据立刻被更新）|
解决丢失更新：使用`for update` 语句；
mysql的 for update 语句 ，使用 Next-Key Lock算法，会给范围索引加锁，如 `select * from t where id <5 for update` 会给id小于5的数据索引加排它锁；

数据备份
---
1. 主服务器更改，并写入到二进志日志binlog
2. 推送到从服务器的IO线程，写入到中继日志
3. SQL线程读取中继日志更新到从库

主从复制，读写分离
---
- 主从读写分离中，事务都由主库来处理。主库处理后会给slave发送事务信号，slave处理后事务才会真正的提交；

QPS
---
对于普通的SSD数据库服务器，一般查询（读写混合）可以达到 3000~5000
如果是基于主键的查询，QPS能达到6、7000
但对于复杂的查询，如group order by 的查询，可能只有 1000左右

硬盘容量估算
------
- varchar(n) 以 `2n/3` 计算；
- timestamp 为 4 字节；
- int 为 4 字节；
- char(n) 为 n 字节；
...

由以上计算出每行所需要的字节数，再估算行数和增长量： `dbSize = rows * rowSize`。

此外，还需要考虑索引大小，索引一般可以达到 dbSize 的 `1/3`，为保证索引扩充，可定为 dbSize 的 `2/3`。