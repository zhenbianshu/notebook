﻿Common
---
- include<> 和"" 的区别：
include<filename>是直接去系统的include文件夹里找filename；
而include "filename"是先在当前文件目录中找filename,找不到再去系统的include文件夹里找；
- 防止整形溢出：注意size_t和int 的转换，int和size_t(unsigned)型运算时会将结果转换为size_t;
- 防止unsigned的下溢，-1 会被转换为2en-1 n为系统位数；
- 和相加和的上溢，防止上溢：int mid = low + (high - low)/2;   比较时：(SIZE_MAX – m < n);
- 线程在任一个时间点上，是可结合的（joinable）或者是可分离的（detached），可结合的线程可被其他的线程回收资源和杀死。
在被其他线程回收之前，它的存储器资源是不释放的。而可分离的线程是不能被其他回收或杀死的，它的存储器资源在它终止时由系统自动释放。默认情况下。线程被创建成可结合的。为了避免内存泄漏，每个可结合的线程都应该要么被显示的回收，即调用pthread_join；要么通过pthread_detach函数分离。
- 结构体中各个成员变量的地址就是相对于结构体实例指针的偏移，偏移量根据前一个成员的类型而定
- .h文件存在的意义：1、共用声明代码 2、代码预处理
- `使用scanf("%[^\n]") `来使用换行符来作为字符串输入结束的标识，原来是空格
- gcc 添加 -std=c99 来指定标准编译
- 可以向函数中传入大结构体的地址来使用这个结构体，以减少内存复制的损耗，为了防止结构体被误改变，可以使用const关键词修饰它；

宏和函数：
---
宏定义格式为 `define MACRO string`
`定义常量时不要加 ；作为定义结尾`
`define HONG(arg) func(arg) `参数严格在括号内，不能有空格，可用，或;分隔语句。
使用 `define FUNC() do{ statement }while(0)`  来消除多语句可能造成的影响。
在定义宏函数的函数体是时 #arg 可以使arg转换为字符串输出；
					`string##arg` 可以连接string和arg成为一个字符串

1.  宏做的是简单的字符串替换(注意是字符串的替换,不是其他类型参数的替换),而函数的参数的传递,参数是有数据类型的,可以是各种各样的类型.
2.  宏的参数替换是不经计算而直接处理的,而函数调用是将实参的值传递给形参,既然说是值,自然是计算得来的.
3.  宏在编译之前进行,即先用宏体替换宏名,然后再编译的,而函数显然是编译之后,在执行时,才调用的.因此,宏占用的是编译的时间,而函数占用的是执行时的时间.
4.  宏的参数是不占内存空间的,因为只是做字符串的替换,而函数调用时的参数传递则是具体变量之间的信息传递,形参作为函数的局部变量,显然是占用内存的.
5.  函数的调用是需要付出一定的时空开销的,因为系统在调用函数时,要保留现场,然后转入被调用函数去执行,调用完,再返回主调函数,此时再恢复现场,这些操作,显然在宏中是没有的.
6.  无法用一个指针来指向一个宏。C语言允许指针指向函数，这一概念在特定的编程条件下非常有用。宏会在预处理过程中被删除，所以不存在类似的“指向宏的指针”；

函数和宏函数的区别就在于，宏函数占用了大量的空间，而函数占用了时间。
函数调用是要使用系统的栈来保存数据的，如果编译器里有栈检查选项，一般在函数的头会嵌入一些汇编语句对当前栈进行检查；同时，CPU也要在函数调用时保存和恢复当前的现场，进行压栈和弹栈操作，所以，函数调用需要一些CPU时间。
而宏函数不存在这个问题。宏函数仅仅作为预先写好的代码嵌入到当前程序，不会产生函数调用，所以仅仅是占用了空间，在频繁调用同一个宏函数的时候，该现象尤其突出。

防止头文件重复加载，结构体、函数被重复定义
---
```
#ifndef _XXX_H
#define _XXX_H
// 定义头文件
#endif
```
这样在加载到后发现_XXX_H已被定义，就不会重复定义了

函数
---
- c中的输入函数有：
`getc(FILE *fp)`;
`fgetc(FILE *fp)`;
`getchar(void)`;
getc可被实现为宏，而fgetc不能实现为宏，这意味着以下几点：
getc的参数不应当是具有副作用的表达式，因为它可能会被计算多次。
因为fgetc一定是个函数，所以可以得到其地址。这就允许将fgetc的地址作为一个参数传送给另一个函数。
调用fgetc所需时间很可能比调用getc要长，因为调用函数所需的时间通常长于调用宏。

- `sscanf(char *str, format, arg[, arg2])` 用来从字符串内读入相应的串，以空格分隔；
- `int atexit(void (*func)(void))` 是注册退出函数的函数，如PHP的`set_shut_down_handler()`一样，将注册的函数压入栈中，最后退出的时候出栈执行；

- C的 `setjmp(jmpbuffer)` 和 `longjmp(jmpbuffer ,err_code)`和PHP的try catch有些类似，longjmp函数用来抛出异常，回到setjmp处并赋予setjmp最后的err_code值。

- 使用dup和dup2复制文件描述符：使得两个文件描述符指向同一个流或文件表项。
`int dup(oldfd)` 返回一个当前可用的最小的文件描述符；
`int dup2(oldfd, newfd)`，指定newfd的数值，如果newfd处于打开状态，则先将其关闭，再重新打开；
使用dup2重定向标准输出：`log = open("log.txt",O_WRONLY|O_CREAT,0644); dup2(log, STDOUT_FILENO);`

C中使用fctnl()实现块锁定。其函数原型为：
---
`int fctnl(int fd, int command, struct flock *flock_info);`
fd为文件句柄;

command参数有如下：
- F_GETLK 获取打开的文件的锁信息，函数调用不成功返回-1；调用进程可以把自己想创建的锁类型信息传递给fcntl，函数调用就会返回将会阻止获取锁的任何信息，即它可以测试你想创建的锁是否能成功被创建；若锁可以创建，返回非-1，其他值不变；若锁已存在或不能创建，则会将已有锁的信息存入*flock_info，flock_info.l_pid为拥有这个锁的进程id；
- F_SETLK命令，这个命令试图对fildes指向的文件的某个区域加锁或解锁，它的功能根据flock结构的l_type的值而定；
- F_SETLKW命令，这个命令与前面的F_SETLK，命令作用相同，但不同的是，它在无法获取锁时，即测试不能加锁时，会一直等待直到可以被加锁为止。

*flock_info 结构体，包括的部分有：
- short l_type;文件锁的类型，对应于F_RDLCK（读锁，也叫共享锁），F_UNLCK（解锁，也叫清除锁），F_WRLCK（写锁，也叫独占锁）中的一个。
- short l_whence;从文件的哪个相对位置开始计算，对应于SEEK_SET（文件头），SEEK_CUR（当前位置），SEEK_END(文件尾）中的一个。
- off_t l_start;从l_whence开始的第l_start个字节开始计算。
- off_t l_len;锁定的区域的长度。
- pid_t l_pid;用来记录参持有锁的进程。

数组和指针
---
数组
```
int nums[] = {1, 2, 3};
int *p = &(nums[0]);   // 数组的名相当于是一个指针
p==nums //成立
p[2] // 可以取到 3
++p; *p==2  // 可以进行自加或自减，以指向数组当前指针的下一个元素
p+=2; *p==3;num[n]==*(p+n); // 可以进行加减运算，以前后移动元素指针
sizeof(p)=指针大小 sizeof(nums)=数组大小 // 但不能进行 sizeof()
```
由上面的定义，函数不能返回一个数组，但可以返回一个指向的指针
```
int *(func(param_list))[10];  // func 是一个函数，param_list 是它的参数, 它尝试返回一个长度为 10，元素类型为 int * 的数组, 而数组是无法返回的

int (*func(param_list))[10];  // 它返回的是一个指针, 这个指针指向了一个长度为 10 元素类型是 int 型的数组

// 返回一个数组指针的函数
type * func(type *param){
    type *res = param; // 保持原指针
    do_something;  // todo 原数组长度发生了变动？？？？
    return res;
}

type *res=func(type *param);
```



文件描述符和流：
---
- 文件描述符是内核为每一个进程所维护的该进程打开文件的记录表。当程序打开一个现有文件或者创建一个新文件时，内核向进程返回一个文件描述符。一般标准输入（standard input）的文件描述符是 0，标准输出（standard output）是 1，标准错误（standard error）是 2；
- 流是文件指针，文件指针指向进程用户区中的一个被称为FILE结构的数据结构，FILE结构包括一个缓冲区和 一个文件描述符；
- 文件描述符就是系统已打开的文件的序号，而流则记录了文件描述符和缓冲区，即流是文件描述符和缓冲区的一个封装；
- 文件描述符和文件流的转化：
`fdopen： FILE * fdopen(int filedes, const char *opentype);` //通过文件描述符和打开类型(只读、读写)来创建一个文件流
`fileno: int fileno(FILE *stream);`通过传入流来获取到对应的文件描述符

进程存储文件结构： 
---
1. 流通过增加缓冲区大小，减少读写系统调用次数来提高读写效率。流通过缓冲区向文件描述符内写入数据；
2. 进程拥有文件描述符表，存储当前进程打开的所有文件数组文件表内文件指针；
3. 同时进程拥有文件表，存储文件状态标志、当前文件偏移量、V节点指针；
4. 系统拥有V节点表，存储当前节点信息，i节点信息，文件长度等；
5. 系统拥有i节点数据块，存储包含文件/目录的几乎全部-适用于放置在硬盘上的信息，如修改时间，大小等；

内核为使当前进程与进程打开的文件建立联系，在进程PCB（一个结构体task\_struct）中使用一个成员来指向关于打开文件列表的结构体struct file\_struct，而该结构体中的`struct file *fd_array[]`是一个指针数组，指向每个打开的文件信息。内核将这个数组中每个成员的下标值（int 型）传递给用户控件来标识该打开的文件，该值即文件描述符。
为了提高执行效率和可移植性，glibc库在用户空间申请了一个FILE *结构体对象，该结构体中的一个成员就是对应打开的文件描述符值，即文件流在文件描述符之上的封装。文件流通过增加缓冲区减少读写系统调用次数来提高读写效率。

声明字符串数组的方法
---
`char * ptr = "string";` // \*ptr 主要声明指针，主体为一个字符串指针
`char ptr[] = "string";` // ptr[] 主要声明数组，一个被ptr指向的数组
对字符串赋值时会出现segment fault错误，因为char * ptr 应该为 const char * ptr,
char代表一个在栈上分配的字符，如果想置空的话，使用`char c = '\0'` 或 `char c = (char) 0`
char* cPtr 是一个指向字符的指针，可以使用 `char* c = new char('\0')` 在堆上分配一个空的字符；
char [n] 是一个长度为n的字符数组；
GCC会把字符串常量放到只读的.rodata段中，字符串常量所在的.rodata和.text（代码）段一样是由操作系统（loader）管理的，程序并不自行分配和销毁，也无权修改，因为标准的不同才支持无const的写法。

结构体struct、联合体union、数组array的区别：
---
|构造体|元素类型|占用内存|访问方式|赋值|其他|
|---|---|---|---|---|---|
|数组|相同类型|元素数量*单个元素|array[index]|直接'='赋值或单个元素赋值|特殊数组字符串会以结尾符'\0'占用一个字节|
|结构体|不同类型|元素类型1+元素类型2+...|struct.key|单个元素赋值或{a,b,c}顺序赋值|字节对齐原则，单个元素的大小一般为4的倍数|
|联合体|不同类型|占用内存最大的类型|union.key|单个元素赋值|各成员占用同一块内存，给一个变量赋值会覆盖其他的变量|


extern和static关键字
---
- extern 关键字 置于变量或者函数前，以标示变量或者函数的定义在别的文件中，提示编译器遇到此变量和函数时在其他模块中寻找其定义，加速了程序编译速度。同时也定义定义的变量或函数为全局量，即在其他文件中也可以使用，另外extern关键字只是声明，而不会定义。
- static 关键字声明了一个静态变量，变量地址在静态变量区，其生命周期一直到程序结束，但其作用域不会变，只是在原函数内部，如果在函数外部声明也只会在同一文件中生效；

一个C程序在系统内的存储地址一般如下：
---
- 高地址：	存储命令行参数和环境变量；
- 栈(stack)：		自上而下，存储函数相关信息，如局部变量，函数调用时返回地址；
- 堆(heap)：		自下而上，通常在堆内使用malloc,realloc等函数动态分配存储，用free释放，如不释放，程序结束后系统自动回收。
- 非初始化数据段(bss,Block Started by Symbol)：未初始化的全局变量和静态变量存放在这里。在程序开始执行之前，内核将此段初始化为0；
- 初始化的数据(data)：存储初始化后的全局变量和静态变量；（像PHP的全局区）
- 正文段(text)：代码段，CPU的机器指令部分，通常是可共享的；而且使用只读来防止程序意外修改了自身程序；

```
int a = 0; //全局初始化区
char *p1; //全局未初始化区
main()
{
    int b; //栈
    char s[] = "abc"; //栈
    char *p2; //栈
    char *p3 = "123456"; //123456\0在常量区，p3在栈上。
    static int c =0； //全局（静态）初始化区
    p1 = (char *)malloc(10); //堆
    p2 = (char *)malloc(20);  //堆
}
```

C进行linux的信号处理：
---
使用`sigaction(int signal, const struct sigaction *act, struct sigaction *oldact);`
即为信号signal设置新的act处理方式，并将原来的oldact处理方式保存起来；
其中struct sigaction *act的结构类似于
```
struct sigaction{
	void (*sa_handler)(int),  //信号处理函数；
	void (*sa_sigaction)(int, siginfo_t *, void *), //在sa_flags为SA_SIGINFO时使用sa_sigaction函数处理，并将siginfo数据存入siginfo_t结构体中；
	sigset_t sa_mask, 在信号处理期间要屏蔽的信号集
	int sa_flags, // 用于指定信号处理的行为
}
```

C中的地址：
---
数组：
char arr[6];
- arr 和 &arr[0] 值都是数组也就是数组第一个元素的地址；
- arr 表示有6个字符元素的数组；
- &arr[0] 表示数组的第一个元素；
方法函数：
void func(){}
- func 和 &func 指的都是函数的地址；
- func 指的是函数的首地址，类型是void()，是一个函数；
- &func 指的是指向函数func的地址，类型是 void(*)()，是一个指针；
- 可以使用 caculate_cb 来存储 &func 指针，直接用来计算 如  `caculate_cb foo = &func; foo();`


C中的跨平台
---
在Linux下的GCC和Windows的VC编译环境是不同的，所以有时候要考虑到C的跨平台使用
通过`__GNUC__`是gcc编译器中定义的宏，而`_MSC_VER`是Windows中VC编译器中的宏， 通过判断宏是否定义来判断是什么平台。
通过
```
#if defined 宏
#defined/include VAR
#elif defined 宏
#defined/include VAR
#endif
```
`#ifdefined` 可以替换成 `#ifdef`

实例：
```
// 判断是windows VC还是Linux GCC环境，分别加载不同的头文件
#if defined __GNUC__
#include <unistd.h>
#elif defined _MSC_VER
#include <windows.h>
#endif

// 定义宏用以实现相同的睡眠等待函数
#if defined __GNUC__
#include <unistd.h>
#define Sleep(x) usleep(x##000)
#elif defined _MSC_VER
#include <windows.h>
#endif
```


结构体和指针的操作符（. / ->）
---
```
结构体`test`是一个特殊的“半指针”，它可以直接 `.` 操作符获取其属性。
但
int main() {
    struct Student stu;
    stu.age = 88;
    funcpr(&stu); // 传递一个结构体的"地址"
    printf("%d", stu.age);
}

// *t 声明接收一个结构体的地址  这里的 stu 就是一个地址，而 *stu 是一个结构体
void funcpr(struct Student * stu){
    stu->age = 30; // -> 符号可以通过地址直接取到结构体的内容
    (*stu).age= 90; // 也可以通过地址符取到结构体再通过 . 取到其内容
}
```
