tips:
---
- redis优先使用ziplist 首先 ziplist 比 hashtable 更节省内存，再者，redis 考虑到 如果数据紧凑的 ziplist 能够放入 CPU 缓存（hashtable 很难，因为它是非线性的），那么查 找算法甚至会比 hashtable 要快！。ziplist 由此有性能和内存空间的优势。
- 对于redis连接假死的情况，可以使用 ping pong 来探测；如果不通，则需要重连；
- 使用 `object idletime key`来获取key的空闲时间（未读取时间）
- redis是单线程模型，一个大命令执行时会导致后续命令都被阻塞，甚至影响到其受理连接。

缓存 redis
---
- 由于程序访问的局部性原理，增加Cache使得要访问的数据绝大多数都可以在Cache中找到，这样才能在性能上使主存的平均读出时间尽可能接近Cache的读出时间。Cache的工作效率通常用“命中率”来表示。
- Cache命中率指的是CPU要访问的信息在Cache中的概率，Cache的命中率越高，CPU访问主存的速度就越接近访问Cache的速度。
同理：缓存的命中率指程序查询缓存时信息正好在缓存中的概率。
- Redis Cache应用：
应用于数据量大，命中率会很高的场景；
使用时每一个Cache层只保留对一种键值的增删改查；
保证redis和db的一致性：在更新db时，先删除cache，然后让其自动补充其值；

redis命令
---
- `ttl key` 获取过期时间  当 key 不存在时，返回 -2 ;当 key 存在但没有设置剩余生存时间时，返回 -1 ; 其余情况获取其过期时间，
- `expire key time` 将key的过期时间设置为time，设置失败返回0，成功返回1；
- `incr key / incrby key step / hincrby hashTable key step` 将某键递增（step）；
注意：注意：ttl get到的过期时间可能为0；incr操作，如果键不存在，先设置其存在，再设置其值；
- `hset key field value;` 当field存在时，会覆盖此field的值，但返回值为0；
			       当field不存在时，设置成功会返回1；
- `rename oldkey newkey` newkey存在会被覆盖，返回1，oldkey不存在或同newkey时返回0；
  `renamenx oldkey newkey` 在newkey已存在时返回失败；

redis和mysql事务对比
---
|redis事务： |        mysql事务                     |                                          步骤说明|
|---|---|---|
|    	|      `SET AUTOCOMMIT=0` (mysql默认立即执行)|  |
|`WATCH & MULTI`		|      `BEGIN/START TRANSACTION`    			|开始事务|
|`EXEC`		|      `COMMIT`								|提交事务|
|`DISCARD`	|      `ROLLBACK`							|取消事务|
需要注意某个(或某些) key 正处于 WATCH 命令的监视之下，且事务块中有和这个(或这些) key 相关的命令，那么 EXEC 命令只在这个(或这些) key 没有被其他命令所改动的情况下执行并生效，否则该事务被打断(abort)，事务被打断返回nil;
另外mysql还能使用`SAVEPOINT pname / ROLLBACK TO SAVEPOINT pname` 命令设置存储点，并返回存储点；
注意：
单纯的MULTI和EXEC命令并没有事务作用；
redis事务并没有回滚功能，只能在事务被DISCARD后得知多少命令被取消，然后再回滚；

缓存
---
缓存的一般操作是 查询时，先检查缓存数据库，如果查到数据，直接返回数据；如果没有数据，则将从DB内查询，并将查到的数据置入缓存。

缓存穿透：
描述：如果一个项目实际上并不存在，那么缓存内会查询不到数据，查询DB时也不会有数据，会出现每次查询都会去查询DB的情况，会对DB造成巨大的压力。
解决：给不存在的数据置一个特殊值，从缓存中获取到此特殊值时，判断是否要请求DB；然后在数据插入DB时，将值放入缓存中，替换掉特殊值。

缓存失效：

描述：某一时刻的大量并发，会造成大量数据进入缓存，然后会在一段时间后同时失效，并对DB造成压力。
解决：给每个缓存设置随机过期时间，防止大量数据同时过期。

缓存雪崩：

描述：系统刚上线或重启时，大量请求的到来，查询DB并将数据写入缓存，可能会造成服务器宕机。特别是在分布式的情况下，一台服务器宕机后，其他服务器的宕机要概率更大，从而形成雪崩似的系统宕机。
解决：采用缓存预热方式，以脚本等程序将数据加载到缓存中。

mongo:
---
- db.setProfilingLevel(0) 设置查询分析日志；
 
redis pipeline
---
redis支持pipeline管道技术，一次请求/响应服务器能实现处理新的请求即使旧的请求还未被响应。这样就可以将多个命令发送到服务器，而不用等待回复，最后在一个步骤中读取该答复。
php中使用 multi() 和 exec() 命令来实现；
```
$pipeline = $redis->multi(Redis::PIPELINE); // 声明一个管道
$pipeline->command1($args1);
$pipeline->command2($args2);
$res = $pipeline->exec(); // 执行所有命令并获取结果
```
其结果类似于` array(0=>res0, 1=>res1)`
使用 `cat file | redis-cli -pipe` 执行文件里的命令

redis键过期策略
---
- 被动删除：读取某键时，发现它已过期，则删除；
- 主动删除：每秒10次，每次读取100个带过期时间的键， 将其删除，如果100个内有超过25个键过期了，则再次取100个键重复删除步骤；

QPS
---
阿里云单机Redis能达到 10W写QPS
60W读QPS
