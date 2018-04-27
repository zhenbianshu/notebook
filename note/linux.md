shell命令
---

- od [-abcdfhilovx][-A <字码基数>][-j <字符数目>][-N <字符数目>][-s <字符串字符数>][-t <输出格式>][-w <每列字符数>][–help][–version][文件…]
补充说明：od指令会读取所给予的文件的内容，并将其内容以八进制字码呈现出来。

- ll 命令可以选择显示文件的最后更改（chmod,chown inode节点最后被更改的时间）时间ctime,最后访问(read,cat)时间atime，和最后修改时间(write，在文件内容上的修改)，分别是：
ll -t(默认) 不添加参数 显示最后修改时间
ll -u 显示最后访问时间
ll -c 最后更改时间, linux中的目录是包含目录内文件名和相关inode节点的文件，对目录项的修改会影响到目录文件的ctime
- scp source_file user@ip_str:path/to/dir
- echo会在行尾加换行符 给echo添加-e选项以解释后续参数中的\t \n 等
expr 进行算术运算时，可以将字符串转换为数字进行算术运算；
- tr替换命令，`tr replaced replacer`
- var=value是赋值，var = value是比较操作
- 设置环境变量:`env_var=value;export env_var;`
- 算术计算：`` let var= expression;var=$[expression];var=$((expression));var=`expr expression`;var=`echo expression |bc` ``
- tput sc存储终端光标位置，tput rc恢复存储的光标位置，tput ed清除当前位置到行尾的全部内容
- read -option var 将用户输入存储到$var
-n 限制字符个数；
-s 无回显
-t 时间限制
-p 显示提示信息
- IFS是默认的字符串分隔符，设置它时不需要加引号，如IFS=,
- 普通条件验证放在［］里，而字符串比较或验证最好放在［［］］里
- xargs 命令可以将输出转化为另一个命令的参数，用法为 cat t.log | xargs script
参数有 -n 每n个为一组
-d 符号  以符号为分隔符
-I {} script -n {}将参数替换到大括号的位置
- mktemp 会生成一个随机文件名的文件，并将文件名返回。
-u 只生成随机文件名
-d生成目录
mktemp testXXX 将XXX替换为随机串，XXX最少有三个
- getline命令从输出中获取一行，作为参数。使用后可以通过$n引用变量
- cut  -f -b -c N 读取输出的第N字段，字节，字符， -d 确定字段分隔符
- du -h --max-depth n  显示文件占用内存多少， -h 人性化显示K,M  最大深度为n
- 在.bash_profile中添加`export PATH=/path/to/command:$PATH` 将添加环境变量；
- linux下安装 epel源来更新数据
- `sed -n [a,]b filename` 获取filename中 [第a行到]第b行的数据
- linux内部有对端口的限制，小于1024的端口需要root权限，所以开启监听小端口的进程需要root权限，如nginx;
- awk 'NR==n{print}' 快速打印第n行
- `time command`会在执行命令后返回命令执行的时间；
- `file filename.file` 获取文件编码和格式；
- 使用`lsb_release -a` 获取系统版本信息。
- `date +%s.%N` 获取`秒.纳秒`当前时间戳
- uniq 命令常与 sort 命令共用，因为 uniq 只对相邻行进行处理；

kill 和 kill -9:
---

kill命令向进程发送 SIGTERM 信号，此信息如果被进程捕获，进程可以进行资源释放等操作，如果没有对应的捕获进程逻辑，进程默认会退出结束
kill -9 实际上是向init进程发送SIGKILL信号，init进程会直接结束目标进程，不会给其释放资源的时间，会造成子进程成为孤儿进程，但可以用来杀死hang死，无法响应信号的进程；
`ps -ef|grep PHP|grep -v grep|awk  '{print "kill -9 " $2}' | sh`

crontab
---

- crontab中不会带有环境变量信息，所以crontab的命令要使用绝对命令如 `/usr/local/php/bin/php  test.php`，`/sbin/ifconfig`等
- crontab会在每分钟的开始检测有没有可执行的脚本，它在运行周期内检测是否符合：如 每3分钟执行脚本，crontab会根据当天？的0点开始是否到了三分钟的间隔期，如果到了，就会执行。

shell语法：
---
\#一般指长度  \$#指参数个数 \${#var}指\$var的长度  \${#arr[@]} 指\$arr的项数
@一般指全部  \$@指arv[]
		\$* 与 \$@的区别： ./test.sh arg1 arg2 arg3中   \$@指 [arg1, arg2, arg3] \$\*指 'arg1 arg2 arg3'即\$@全自动用空格将参数串分隔开，而\$\*不会

nohup
---

命令运行由 Command 参数和任何相关的 Arg 参数指定的命令，忽略所有挂断（SIGHUP）信号。在注销后使用 nohup 命令运行后台中的程序。要运行后台中的 nohup 命令，添加 & （ 表示”and”的符号）到命令的尾部。
标准输出和标准错误缺省会被重定向到 nohup.out 文件中。一般我们可在结尾加上"&"来将命令同时放入后台运行，也可用">filename 2>&1"来更改缺省的重定向文件名。
使用nohup命令执行，使其不接收外界的输入和SIGHUP信号；
nohup命令对一个进程进程做了三件事：1.阻止SIGHUP信号发到这个进程。2.关闭标准输入。该进程不再能够接收任何输入，即使运行在前台。3.重定向标准输出和标准错误到文件nohup.out。
1为标准输出，2为标准错误输出 使用 > 覆盖重定向输出 >> 来追回输出重定向
但： commandstring 2>&1 > log.txt 并不能将错误输出输出到标准输出，然后输出到日志，标准错误输出会直接输出到终端
commandstring >log.txt 2>&1 才能达到目的

test命令
---

测试是否正确，语法为 test statement/[statement];echo $?  结果 有true(0) false(1)
常见测试表达式选项有：
	= 判断相等
	-n/-z  是否为null
常见测试文件选项有：
	-d 测试是否目录
	-r 是否可被读取

shell执行方式
---
1. 直接通过文件名执行，文件需要执行权限，通过文件内部的shebang解释文件 shebang 后可以直接附加选项
2. 通过指定解释器来执行脚本，如 bash hello.sh
3. 使用 . ./hello.sh 或 source ./hello.sh命令来执行脚本，不会fork子进程，而是使用当前shell执行，用于 .bashrc或者.bash_profile被修改的时候，我们不必重启shell或者重新登录系统，就能使当前的更改生效。

inode
---
linux内有数据文件区和inode区，每个文件对应一个inode，其内部存储创建时间，访问时间，修改时间，文件字节数，权限，链接数，位置。使用stat 、ls -i 文件 可以查看inode信息；
软硬链接：
- 硬链接指向文件的block内容块 使用 `ln 源文件 hard_link` 创建一个硬链接；硬链接相当于变量的引用计数，两个引用都指向同一个inode，当任何一个引用存在时，文件即存在。硬链接只能在同一文件系统内的文件上使用，不能对目录创建硬链接，且只有root用户才有权限创建；
- 软链接指向文件的文件名， 使用`ln -s 源文文件或目录 soft_link` 创建一个软链接；软链接是另外一个文件，有自己独立的inode，它就相当于一个指向变量的变量，如果源变量不存在了，那么它也会变为null，软链接的创建没有限制；


启动新进程的方式：
---
- `int system (const char *string)`;// 直接传入shell语句执行，且执行方式是阻塞的，即system函数执行结束后，才能继续向下执行；
- 使用exec系列函数，将当前进程替换为目的进程，并继承当前进程的上下文，目的进程执行成功时不会自动返回原进程；函数形如：`int execv (const char *path, char *const argv[])`;
- fork方式：完全继承父进程的上下文，父子进程同时异步执行。其函数原型为：`pid_t fork()`;

IO模型
---
线程在执行中如果遇到磁盘读写或网络通信（统称为I/O 操作），通常要耗费较长的时间，这时操作系统会剥夺这个线程的CPU 控制权，使其暂停执行，同时将资源让给其他的工作线程，这种线程调度方式称为 阻塞。
当I/O 操作完毕时，操作系统将这个线程的阻塞状态解除，恢复其对CPU的控制权，令其继续执行。这种I/O 模式就是通常的同步式I/O（Synchronous I/O）或阻塞式I/O （Blocking I/O）。
相应地，异步式I/O （Asynchronous I/O）或非阻塞式I/O （Non-blocking I/O）则针对所有I/O 操作不采用阻塞的策略。当线程遇到I/O 操作时，不会以阻塞的方式等待I/O 操作的完成或数据的返回，而只是将I/O 请求发送给操作系统，继续执行下一条语句。当操作系统完成I/O 操作时，以事件的形式通知执行I/O 操作的线程，线程会在特定时候处理这个事件。为了处理异步I/O，线程必须有事件循环，不断地检查有没有未处理的事件，依次予以处理。阻塞模式下，一个线程只能处理一项任务，要想提高吞吐量必须通过多线程。

linux的五种IO模型：
---
由初态（IO函数发起）到终态（状态变为“准备好”）；
- 阻塞IO：IO调用时程序一直阻塞，直到完成数据与缓冲区的拷贝，状态变为“准备好”；
- 非阻塞IO：IO调用时通过进程反复调用IO函数，测试数据是否可以开始准备，如果数据开始与缓冲区间进行拷贝，则阻塞到拷贝完成；
- IO复用：使用select/poll/epoll函数阻塞进程查看多个IO是否可读写，直到有一个IO可写，则进行阻塞直到数据与缓冲区拷贝完成；
- 信号驱动IO：检查IO时安装驱动函数，在数据准备好时使用触发驱动函数，使IO阻塞到数据与缓冲区拷贝完成；
- 异步IO：发起IO操作，IO将数据准备好，并完成数据与缓冲区的拷贝时使用信号通知进程进行IO操作；

select/poll/epoll
---
- select:通过设置或者检查存放fd标志位的数据结构来进行下一步处理
	特点是a.fd标志受进程fd数量限制；b.是对socket线性扫描，使用轮询，效率较低；c.需要维护一个存放大量fd的数据结构，结构在传递和复制时开销较大；
- poll:将用户fd数组复制进内存空间，使用链表存储，然后进行select式轮询
	特点是a.由于链表存储，数量不受限制；b.大量的无意义的复制；c.一次触发，如果不处理，下次轮询还能查到；
- epoll:使用事件机制，在fd活跃时回调，传入活跃fd的详细信息；
	特点是a.只关心活跃fd，且能精确定位，效率较高；b.最大限制是（系统）能打开最大文件数，上限较大；c.使用内存映射拷贝，省掉了系统调用时的内存开销； 有边缘触发(Eage Trigger)一个事件只触发一次，水平触发(Level Trigger)一个事件只要不处理一直触发。

epoll的边缘触发(Edge Trigger)为什么要设置为非阻塞：
---
1. 由于边缘触发一个事件只会被触发一次，所以一次读请求要在一个while循环内处理完，不然会造成一些数据永远也不会被读到。
2. 在while循环内如果使用阻塞I/O，需要设置读取数量的量，而在读到足够数据之前不会返回，会影响进程处理下一个请求；而如果用非阻塞I/O，读到没数据后就会返回EAGAIN错误返回。
3. 所以为了一次读光缓冲区内不知量的数据就必须使用非阻塞I/O；



内存映射/文件映射
---
在操作很大的文件时，linux可能无法将其全部装入内存，或装入内存很慢，则可以使用文件映射，将文件地址映射为虚拟内存来操作；
使用函数 `void * mmap(void *start, size_t length, int prot , int flags, int fd, off_t offset);`
- start为内存起始地址，如果映射文件的话传入NULL
- length为映射的长度，文件的话可用fstat(fd)获取长度
- prot为保护模式，有PROT_EXEC, PROT_READ, PROT_WRITE, PROT_NONE，可用与操作符来集合多种保护模式，但不能超出打开模式；
- flag为映射类型，MAP_PRIVATE为写时复制，读时使用同一个，在写操作时复制一份；MP_SHARED是一直共用同一块内存；
- fd为打开的文件描述符
- offset为偏移量，在使用文件时设置

管道：
---
- linux的管道是通过指向同一临时的VFS inode结点实现的，此VFS指向内存中的一个物理页面。
- 管道是半双工的，只能从一端写入，从另一端读出,一般有4096字节的缓冲区，在写入时管道另一端无读取会有SIFPIPE错误号，再写入时将阻塞；
- 要建立管道，需要有相同祖先的两个进程；一个进程在由pipe(int fd[2])创建管道后，一般再fork一个子进程，然后通过管道实现父子进程间的通信;对于fd[2]: fd[0]表示写入端，fd[1]表示输出端；
- 实现父子之间的双向通信：
父进程创建管道A和B，然后fork出一个进程；
父进程关闭管道A的读出端，和管道B的写入端；子进程关闭管道B的读出端和管道A的写入端；
- `nohup command  >/dev/null  2>&1` 将命令输出扔到垃圾筒
- 在脚本中使用`nohup comman >/dev/null 2>&1 &`开启新进程；·

grep 命令：grep needel [-option] file;
---
- -A/B/C N附加显示匹配的后N行/前N行/前N行和后N行；
- -a 将二进制文件按ASCII格式搜索；
- -c 只显示符合要求的行数，在多文件情况下会返回各文件的符合行数；
- -e 按照正则表达式搜索；
- -H 在匹配结果前附加显示文件名（默认不附加文件名）；
- -i 忽略大小写（默认区分大小写）；
- -l/L 只显示 有/没有 匹配结果的文件；
- -m 最多列出符合要求的前m个结果；
- -n 显示符合要求的行号；
- -R 搜索子目录，默认只搜索当前目录下的文件；
- -s 不显示错误信息（如xxx是二进制文件等）；
- -v 反向搜索，即操作不符合的行，一般用于管道过滤；
- -w 严格匹配，即只匹配全词，hello 无法匹配到 helloyou；

awk
---
命令：awk 'pattern' [option] file;
option有
	-F m 或 -F[mn] 使用m作为分隔符或使用多个分隔符，注意 m 需要添加 ‘’ 如 ‘|’
	OFS="\t" 使用tab作为输出分隔符
	ORS='\n' 使用换行作为行分隔符
	NR 第n行
基本规则：
语法：print以，分隔  ""做连接符
`condition {do}`   ：匹配记录{}内每一条执行一次$n ：表示被分隔符分隔行的第n项，$0表示整行;;
`awk -F '|' '{if($1> "2016-11-03 12:00:00" ){print}}' ` 以|为分隔符输出时间第一列的时间大于字符串时间的行
`NR == n` ：表示第n行;
`{printf "%format" $n}` ：对$n项进行格式化输出;
`BEGIN{statement}` ：开始前执行;
`END{statement}` ：结束后执行;
`'/regex/'` ：按照正则搜索;
`{}内的变量取值和赋值不使用$符号，而是类似于C的写法`

互斥量 信号量 条件变量
---
互斥量即锁，必须由加锁的进程来解锁，仅有加锁和解锁两种状态；
条件变量是一个尝试动作，发出信号后没有条件，信号会消失；
信号量是一个全局计数器，并且支持进程对计数条件进行wait；

缓冲机制
---
（全缓冲，行缓冲，不缓冲）：
使用`setvbuf(stream, *buf, type, int size); setvbuf(stream, buf[zize], _IOFBF, size) `设定stream为size大小的全缓冲
- 全缓冲(_IOFBF): 即将输出全部存入缓冲区，直到缓冲区满了或脚本结束；
- 行缓冲(_IOLBF): 将数据存入缓冲区，直到遇到换行符或缓冲区满了或脚本结束；
- 不缓冲(_IONBF): 即不使用缓冲区，输出什么就直接显示，标准错误就是不缓冲；

PS 命令
---
查看linux进程状态：
PS常用参数：
- aux 显示所有进程的详细信息
- -H 树状显示
- -A 显示全部进程
- -u user 显示对应用户的所有状态
- -l 显示本次登陆的详细信息

curl命令
---
一般命令：`curl [options] http://url...`
常见options：
- -o output.txt 将内容写入到一个文件中 `curl -o result.txt http://url`;
- -O 将链接内容以文件形式下载下来；
- -D header.txt 将header写入到文件中；
- -c coockie.txt 将结果coockie写入到文件中；
- -b coockie.txt/coockie  请求带上cookie（文件中或直接字符串）;
- -x proxy_addr 使用代理;
- -d post_data 以post方式请求数据;
- -u name:passwd 在HTTP中使用用户名密码 -U 在ftp中使用用户名密码；
- -H head-line 添加头标识；
- -i/I 显示/只显示 文件头信息；
- -m sec_num  指定超时时间；
- -s 静默模式，不显示下载进度信息
- -# 显示下载进度条；
- `-T filename.txt ftp://uname:passwd@ip:port/path` 通过 ftp上传；
-  -e http://referer  设置来源；
- -A user_agent 设置访问设备信息；
- -v 打印详细的交互信息；
- -F "field=@filename" 上传文件

find命令find dir [options]
---
- -name "name_expression" 名字表达式可以使用正则等；
- -exec command {} -options  对查找到的结果执行command，{}代指查找到的文件；
- -perm permission  根据权限查找，权限为八进制的权限表示法;
- -type 根据文件类型查找文件 d 目录 等
- -size [compare]size_num 文件大小 大于+，小于-，等于  ， 单位有 c 字符数 b约 512字节
- -mtime -ctime [compare]time_num 修改时间，创建时间在某范围内；

timeout命令
---
设置一条shell命令执行的最大时间，在超时时会发送信号杀死进程
用法 `timeout [OPTION] NUMBER[SUFFIX] COMMAND [ARG]`
option: ` -s  SIGNAL`  设置超时时发送给程序什么信号；
NUMBER[SUFFIX] ：设置的超时时间，默认为秒，有s秒 m分钟 h小时 d天
command 为要执行的命令。

flock命令
---
通过文件锁定来执行一条命令。
`flock [options] file [-c]  command`
常见options如下：
- -s 共享锁
- -x 排它锁，独占锁
- -u 解锁
- -n 锁冲突时不阻塞，直接失败
- -w timeout 锁冲突时等待timeout秒
- -o 在执行命令前关闭锁文件的文件描述符，防止命令调起的子进程也持有此描述符
- -c command 执行command命令

tar
---
压缩、解压命令： tar options file
- -f 必备属性 指定要操作的文件
- -c 创建压缩文件
- -x 解压压缩文件
- -z 使用 gzip 属性
- -j  使用bz2属性
- -v 显示过程
- -t 查看内容

strace
---
Strace是一个用来跟踪系统调用的简易工具。
用法是 `strace [options] command`
常用option有：
- `strace -e trace=xxx command` 跟踪command命令执行时调用xxx系统调用的情况，常用有 open/file/process/signal 等
- `strace -p pid command` 跟踪指定pid的系统调用
- `strace -c` 统计各个系统调用的时间并以整洁形式打印
- `strace -t /tt /ttt`  分别在开始处添加时间戳，微秒级的时间戳，UTC的微秒级数
- `strace -r` 以相对时间展示（开始处为000秒）
- `strace -o filename ` 把调用结果写到filename文件里
- `-f ` 除了跟踪原进程外，还跟踪其fork子进程

traceroute
---
原理：Traceroute程序的设计是利用ICMP及IP header的TTL（Time To Live）栏位（field）。首先，traceroute送出一个TTL是1的IP datagram（其实，每次送出的为3个40字节的包，包括源地址，目的地址和包发出的时间标签）到目的地，当路径上的第一个路由器（router）收到这个datagram时，它将TTL减1。此时，TTL变为0了，所以该路由器会将此datagram丢掉，并送回一个「ICMP time exceeded」消息（包括发IP包的源地址，IP包的所有内容及路由器的IP地址），traceroute 收到这个消息后，便知道这个路由器存在于这个路径上，接着traceroute 再送出另一个TTL是2 的datagram，发现第2 个路由器...... traceroute 每次将送出的datagram的TTL 加1来发现另一个路由器，这个重复的动作一直持续到某个datagram 抵达目的地。当datagram到达目的地后，该主机并不会送回ICMP time exceeded消息，因为它已是目的地了，那么traceroute如何得知目的地到达了呢？
Traceroute在送出UDP datagrams到目的地时，它所选择送达的port number 是一个一般应用程序都不会用的号码（30000 以上），所以当此UDP datagram 到达目的地后该主机会送回一个「ICMP port unreachable」的消息，而当traceroute 收到这个消息时，便知道目的地已经到达了。
命令用法：

- -n 不使用DNS解析，直接使用ip;
- -p 指定端口
- -q n 把探测包设置为N个
- -m n 最大ttl设置为n个

rsync
---
服务端配置

```
# general conf
port=873
max connections=500
log file=/var/log/rsyncd.log
pid file=/var/run/rsyncd.pid

[zbs] # module
path = /data1/zbs # aim_dir
read only=no
use chroot=no
uid=root
gid=root
auth users=zbs
secrets file=/etc/rsyncd.scrt # add secret line "zbs:password"
ignore errors
exclude = git/
```

客户端使用 `rsync -avz fileOrDir rsync://{user}@{host}:{port}/{module}/dir`

rsync是一个远程数据同步工具，可以通过LAN/WAN快速同步多台主机间的文件，使用rsync算法可以只传送数据的一部分，因而速度很快；
用法为 `rsync [options]   SRC DEST  ([user@]host::PATH/TO/FILE)`

常用options有：
- -a --archive  归档模式，以递归方式传递文件，并保持文件权限等属性，等同于 rlptgoD
- -z --compress 以文档压缩方式传输
- -v  --verbose 以详细模式传输，会显示传输进度
- -r --recursive  对子目录递归处理
- -p --perms 保持文件权限属性
- -o --owner 保持文件属主信息
- -g --group 保持文件组信息
- -D --devices 保持文件设备信息
- -t --times 保持文件时间信息
- --delete  删除DEST内SRC内没有的文件
- -P --partial 保留传输中断的文件，以便下次加速

CPU负载
---
- 使用`nproc`命令查看系统CPU核数，CPU的核数代码系统能同时进行多少个进程；
- 使用`uptime`命令查看系统CPU的负载，平均负载为 1分钟内 5分钟内 15分钟内；
- 单核系统中 平均负载为1时，即为CPU被充分利用，大于1时有 n-1个进程在等待CPU时间；
- n核系统中 平均负载为n时，CPU被充分利用，大于n时有 (m-n)个进程在等待CPU时间；

守护进程
---
守护进程编程步骤
1.  创建子进程，父进程退出
　所有工作在子进程中进行
　形式上脱离了控制终端
2.  在子进程中创建新会话
　setsid()函数
　使子进程完全独立出来，脱离控制
3.  改变当前目录为根目录
　chdir()函数
　防止占用可卸载的文件系统
　也可以换成其它路径
4.  重设文件权限掩码
　umask()函数
　防止继承的文件创建屏蔽字拒绝某些权限
　增加守护进程灵活性
5.  关闭文件描述符
　继承的打开文件不会用到，浪费系统资源，无法卸载
　getdtablesize()
　返回所在进程的文件描述符表的项数，即该进程打开的文件数目
6.  设置信号处理
　设置信号处理以处理子进程退出、中断等信号


进程运行状态
---
1. TASK_RUNNING( `R`)
    TASK_RUNNING是教科书中两种状态的结合，一种是正在占用CPU事件的RUNNING状态， 一种是RUNNING状态的进程时间片耗尽或者主动让出CPU，或者被更高优先级进程抢占 后，进入的READY状态。处于TASK_RUNNING状态的进程要么正在CPU上运行，要么 随时都可以投入运行，只不过CPU资源有限，调度器暂时没有选中他们。处于TASK_RUNNING状态的进程是调度器的调度对象。
2. TASK_INTERRUPTIBLE (`S`)
     当进程和慢速设备打交道，或者需要等待条件满足时，这种等待时间是不可预估的， 这种情况下，内核会将该进程从CPU的运行队列中移除，从而进程进入睡眠状态。SLEEP状态有 TASK_INTERRUPTIBLE 和TASK_UNINTERRUPTIBLE两种。可中断状态会因等待条件满足或收到信号而中断。
3. TASK_UNINTERRUPTIBLE(`D`)
    不可中断状态只会因条件满足中断，此状态的进程不接受外来的任何信号，因此无法用kill杀掉这些处于此状态的进程。
3. TASK_STOPPED/ TASK_TRACED (`T`)
    SIGSTOP、SIGTSTP、SIGTTIN、SIGTTOUT等信号会将进程暂时停止，进入__TASK_STOPPED 状态。这4种状态不可被忽略，不可被屏蔽，不能安装新的处理函数。在收到SIGCONT 后进程可以恢复执行。
4. EXIT_ZOMBIE / TASK_DEAD (`Z`)
    僵尸状态，进程已经死掉了，只是TASK_ZOMBIE状态中的进程没有被收尸，或者 父进程没有设置SIGCHLD处理函数为SIG_IGN,或者为SIGCHLD设置SA_NOCLDWAIT标志位。
5. TASK_DEAD / EXIT_DEAD (`X`)
退出状态，进程即将被销毁。

VIM
---
- `:set number` 显示行号
- `0` 跳到行首  `$` 跳到行尾
- `gg` 跳到首行   `GG` 跳到尾行
- `()` 在分隔符`(. / \n )`前后跳转
- `dw` 删除当前单词 `d^`删除到行首   `d$` 删除到行尾

VIM编码
---
- encoding：VIM 内部使用的编码包括缓冲区等，默认使用系统的`locale`配置，用户可以在`.vimrc`中改变配置，但改变后再打开文件也会受影响；
- fileencoding：VIM当前编辑文件所使用的编码格式，可以在VIM中使用 `set: fileencoding` 或 `file filename`  获取当前文件的编码；也可以使用`:e ++enc=Xencoding Xfile` 来自定义文件打开的编码；
- termencoding 终端编码格式 ，由终端来配置；

终端交互
---
1. 使用 `w` / `finger ` 查看正在登陆的用户
2. 通过 `USER` 或 `TTY `字段区分用户
3. 使用 `write USER/TTY ` 打开发送信息界面；
4. 写入信息，回车发送，`ctrl+c` 发送结束；
5 . `wall msg` 向所有在登陆用户发送信息；
6. `mesg n` 不接收别人的信息  `mesg y` 接收别人的信息

sort排序
---
用法：`sort [options] filename`
常用选项：
- `-t delimeter` 以delimeter为分隔符
- -kn 对第n行进行排序，可以多选，前面的相同了会判断后面的； 可以在后紧排序方式，只针对此列 如 `-k2n -k3r` 是对第二列按数据升序，对第三列降序
- -b忽略列前的空格
- -o file 将结果写入文件
常用排序方式：
- -f 大小写不敏感 若与 -u 共存，则去除小写的那行
- -u 相同的列只出现一次
- -r 默认为升序，添加此选项使用降序
- 默认为字典序；
- -n 用数字排序；

iptables 防火墙
---
使用: `iptables [-t 表] <-A/I/D/R> 规则链名[规则号] <-i/o 网卡名> -p 协议名 <-s 源IP/子网> --sport 源端口 <-d 目标IP/子网> --dport 目标端口 -j 动作`
- -A 向规则链中添加条目；
- -D 从规则链中删除条目；
- -I 向规则链中插入条目；
- -R 替换规则链中的条目；
- -F 清除规则链中的条目；
- -Z 初始化规则链中的包计数器和字节计数器；
- -L 列出规则链中的条目；
在显示时， -n 以数字方式显示IP，-v*n 显示详情，v越多越详细；

#表名
---
- raw 用以实现网址过滤等高级功能，能在所有链上操作；
- mangle 数据包修改，用于实现服务质量，能在所有链上操作；
- net 地址转换，用于网关路由器，能在 PREROUTING OUTPUT POSTROUTING链上操作；
- filter 包过滤，用于防火墙规则，能在 INPUT OUTPUT FORWORD链上操作；
#规则链包括
---
- INPUT 输入数据包；
- OUTPUT 输出数据包；
- FORWARD 转发数据包；
- PREROUTING 目标地址转换 DNAT；
- POSTROUTING 源地址转换 SNAT；
#动作包括：
---
- ACCEPT 接收数据包；
- DROP 丢弃数据包；
- REDIRECT 重定向、映射、透明代理；
- SNAT 源地址转换；
- DNAT 目标地址转换；
- MASQUERADE IP伪装；
- LOG 日志记录；

内存buffer
---
使用`free -m` 命令查看内存使用率时，真正的内存可用部分是 free 部分+buffer cache的。
但并不是所有的buffer cache都能被重用。
并且，当cache作为文件缓存被释放时会导致IO变高。
其中：
- tmpfs: linux临时文件系统 tmpfs 使用内存作为临时文件系统，在/dev/shm/tmpfs文件夹中，此部分文件存储在内存中，在文件没有被删除掉时内存无法被重用；
- 共享内存：使用 shmget 方式申请的共享内存也会占用内存，在共享内存被显示释放之前，此部分内存不可用；
- 使用 mmap 方式映射文件到内存也会占用cache空间。
- 实际上shmget和mmap的共享内存，在内核层都是使用tmpfs实现的，使用cache存储。

lsof
---
在linux中，万物皆文件，lsof 命令是列出linux当前打开文件的命令。
一般命令格式为 `lsof [options] file`
返回结果字段分别为：
```
COMMAND 进程名
PID  进程号
USER 运行进程的用户
FD 文件描述符 0-2 标准输入输出错误文件 num 文件描述符序号（其后跟 u读取或写入 r只读模式 w写入模式） 0r 对标准输入只读模式
TYPE 文件类型 IPV4 套接字 DIR 文件夹
DEVICE 磁盘名称
SIZE 文件大小
NODE 索引节点
NAME 文件完整名
```
OPTIONS 可选参数为：
```
-u user 用户打开的文件  -g group 用户组打开的文件
-c char 文件名以char开关
-p pid  PID相关的文件
-d 文件号 限制文件号
-i 其他网络相关参数 protocol: @ip :port
```

shell 函数
---
定义为 ：
```shell
[function] funcname(){
    let var1=$1, var2=$2;
    action;
    return int(1~255)
}
```
使用时 `funcname $arg1 $arg2...`
特别注意：传入数组时应该使用  `""` 将参数包括 为 `funcname "$arr" $arg2`

内存映射
---
普通的读取文件需要：
1. 进程调用 read 系统调用进入内核态；
2. 内核将文件读到内核缓冲区；
3. 系统将数据从内核缓冲区拷贝到用户态进程缓冲区；
4. 返回用户态使用数据；

而内存映射在虚拟内存上分配对应的空间，在使用内存时会引起缺页中断，在缺页中断中直接将磁盘上的数据读到用户态的内存，减少了不必要的内存复制。


wget
---
下载文件命令 `wget [option] url`
option有：
- -S 打印服务器响应头
- -O filename 将响应体输出到文件里
- -e command  执行command 如 `-e --http_proxy=1.1.1.1` 使用代理

mtr
---
mtr 能综合ping nslookup tracert的命令，使用很简单 `mtr [option] 域名/ip`;

常用参数有：
- -s 设置ping包的大小;
- -n no-dns，不对ip地址做域名解析；
- -a ip ,设置发送包的IP地址；
- -c 设置ping的次数count；
- -i 设置两次ping的间隔interval；
- -4/-6 指定协议为 ipv4/v6；

结果为：
- Host: tracert经过的ip；
- Loss%: ip丢包率，最后一行为最终丢包率；
- Snt: 发送包数；
- Last/Avg/Best/Worst: 各链路延迟，毫秒为单位；
- StDev: 标准偏差；

watch
---
用于循环监听执行某个命令：`watch [option] command`, option:
- -n N 时间间隔N秒一次
- -d 高亮显示跟上一次执行有变化的地方
- -t 关闭上方显示的时间和命令描述；

perf
---
perf 是一个性能分析工具，它通过tick中断触发采样点，在采样点里判断系统的上下文，这样次数多的话，就可以得到准确的分析数据；

- `perf list` 可以查看支持哪些系统命令；
- `perf stat command` 可以分析执行 command 时 CPU 的真正执行时间和 CPU 的利用率；
    ```
    -p pid 关联到某个正在运行的进程上。
    -r N 连续执行N次
    -a 从所有CPU上收集数据
    ```
- `perf top command` 像top命令一样实时监测系统的运行状况
    ```
    第一列：符号引发的性能事件的比例，默认指占用的cpu周期比例。
    第二列：符号所在的DSO(Dynamic Shared Object)，可以是应用程序、内核、动态链接库、模块。
    第三列：DSO的类型。[.]表示此符号属于用户态的ELF文件，包括可执行文件与动态链接库)。[k]表述此符号属于内核或模块。
    第四列：符号名。有些符号不能解析为函数名，只能用地址表示

    -p pid 关联到某个正在运行的进程上。
    ```

date
---
- date 显示当前时间信息
- `date -r timestamp` 解析时间戳获取时间信息
- `date "+%F %F..."` 格式化输出日期
- `date -s date-time` 设置系统时间

命令行命令操作
---
- ﻿ALT + b/f  移向词头/尾 =》 zsh CTRL + ->/<-
- CTRL + w   删向词头
- ALT + d    删向词尾
- CTRL + u   删向行头
- CTRL + k   删向行尾
- CTRL + -   撤销上步

命令行json格式化命令 jq
---
使用管道加 jq 格式化 json 输出 如  `curl www.example.com | jq ['operation']`

取特定值：
- '.fieldA.fieldB' 从根参数中取出 fieldA 下的 fieldB 字段
- '.list[].field' 从根下取出 list 数组里的所有 field 字段
- '.list | length' 取出根下 list 数组的长度