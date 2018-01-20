###注意点 tips
- PHP的类属性声明并初始化时值必须是标量。
- PHP的文件开始标签<?是必须的，但在一个纯PHP文件中，其关闭标签 ?>是不必的，并且，如果闭合标签后有空格空行的话，会被原样输出，因为解释器只会解释PHP两个标签间的内容，多余的空格或空行可能会导致HEADER头等信息出错。
- 由于PHP的单进程性，可以不用考虑类变量被两个请求同时使用，所以可以将同一类中多个方法的同一变量声明为类变量，达到共用的目的。
- php的exec/system命令可以执行shell命令：exec/system('shell')(只能在cli模式下);此方法获取命令行输出的最后一行；
- ``shell_exec('')/`command` `` 获取命令行输出的全部内容；
- 另外在cli模式下，预定义变量\$argv指参数数组，\$argv[0]为脚本名；\$argc指参数的个数，因为必有脚本名，所以\$argc最小为1；
- 使用命令行模式php -r 可以执行单行PHP代码，注意单行代码最好用‘’单引号，防止与shell的交互时shell先解析字符串里的内容出错，shell中\$var指的是某一变量，会先把PHP的\$var变量解析出空来。
- $SERVER中的 remote_addr 由服务器获取，可能是客户端也有可能是代理proxy的IP地址
- 头信息中x_forwarded_for是由代理服务器在执行代理时自动添加的，是客户端的IP
- ini_set('display_errors',1) ,用来设置是否将错误进行标准输入
- error_reporting(错误级别),用来设置什么级别的错误会被显示
- 三元运算符 ?: 小窍门， 使用 \$a ?: \$b 表示当$a成立时，结果为\$a ,\$a不存在时运算\$b
- php_sapi_name()函数 或PHP常量 PHP_SAPI 可以判断当前PHP脚本运行的环境，多进程最好在cli模式下处理。
返回值可能是：aolserver、apache、 apache2filter、apache2handler、 caudium、 cgi-fcgi、cli、 cli-server、 continuity、embed、fpm-fcgi、 isapi、litespeed、 milter、nsapi、 phttpd、pi3web、roxen、 thttpd、tux 和 webjames

###函数
- 使用`getcwd()`方法可以获取当前的目录值；
- `var_export(mixed $var, bool $return)`方法可以直接输出PHP变量的string信息，不同于var_dump(),它不会输出string[length]等信息，会如PHP代码内一样输出,如果第二个参数为true，可以将结果字符串赋值给一个变量，这样可以完成带数组的配置文件改写等。
- 使用`disk_total_space(string $directory)`方法获取磁盘总容量； $directory为文件系统目录或磁盘分区
- 使用`disk_free_space(string $directory)`方法获取磁盘空闲容量；
- PHP array_filter函数用法 `array_filter($arr[,$call_back])`;
该函数把输入数组中的每个键值传给回调函数。如果回调函数返回 true，则把输入数组中的当前键值返回结果数组中。数组键名保持不变。
如果没有提供 callback 函数， 将删除 input 中所有等值为 FALSE 的条目。callback可用数组来表示类函数和静态函数，也可以用function(){}来写匿名函数。如果此匿名函数要使用其他外来的值，可以用use
全部用例为  `array_filter($arr, function($val) use ($out){ return process})`;
- `md5($string, bool $raw)`,\$raw表示返回格式规定十六进制或二进制输出格式, 默认值为false，即传输32字符十六进制数；
- `microtime($float)` float 为 false时："0.13510100 1477655492" 8位精度， float为true时：float(1477655555.9244) 四位精度浮点数
- 可以自定义`set_exeption_handler('func')`来全局捕获异常；
- 可以自定义`set_shutdown_function('func')`来执行脚本结束时的函数,此函数即使是在ERROR结束后，也会自动调用。
- 可以自定义`set_error_handler('func')`在PHP发生错误时自动调用，注意，必须在已注册错误函数后才发出的错误才会调用。函数参数应为`($errno, $errstr, $errfile, $errline)`;
- `memory_get_usage()` 获取当前脚本所占用内存大小
- `memory_get_peak_usage()` 获取当前脚本占用内存的峰值大小
- `preg_replace($pattern, $replacement, $subject);`替换中文时：`\u4e00-\u9fa5`是unicode里中文的表示法，但preg_replace不支持`\u`的写法，可以使用 `\x{XXXX}`来替代；最后使用`u`选项来表示用的是utf-8字符集。
- php5.5 之前的版本（由于foreach的复制性），foreach过程中会读取数据后将指针指向下一个数组元素，所以foreach之后指针会指向null，这点在php7被修复了。
- PHP的netsort(natural sort)可以对数组进行自然排序；即1、3、10、14；避免字典排序的 1、10、14、3;


###扩展安装方法
1、找到phpize可执行文件，phpize一般在php/bin下，如果没有安装，可以安装php-cli、php-dev来安装；
2、下载扩展的源代码，里面是没有configure文件的，先源代码目录下运行phpize命令生成configure文件
3、找到php-config文件，在运行congigure时带上参数 --with-php-config=/path/to/php-config
4、make && make install 在命令行最终处产生了 extension.so所在的文件目录
5、使用phpinfo();或php -i |grep ini查找加载的php.ini文件，并向其中添加extension=/path/to/extension.so
6、重启apache或php-fpm 使用kill命令杀掉原父进程，然后运行启动
7、使用php -m查看已安装的php模块有没有成功被加载

###字符表示法转换
- `ord('c') `将字符转为ASCII码值
- `chr(int)` 将数字转换为ASCII码值对应的字符
- `dechex(int)` 将十进制数转换为十六进制
- `hexdec(0xhex) `将十六进制数转换为十进制
- `pack($format,$data)`  将数据转换为二进制格式
- `unpack($format, $data)` 将二进制数据还原为普通数据 同上：以一定的格式，支持 数值型
- `bin2hex($bin_num) `将二进制数据转换为十六进制
- `hex2bin($hex_num)` 将十六进制数据转换为二进制

###header()方法
- `header('Location:url')` 跳转到指定URL，如有多个重定向，以最后一个（前面没有输出的）为准。
- `header('HTTP/VER CODE [INFO]')` 直接HTTP状态 如：header('HTTP/1.1 404') 直接返回404
- `header('header_key:header_val')` coockie之类的头字符串

- 使用header()方法下载
```
header('Content-Type:application/octet-stream'); 统一mime类型，可以将 octet-stream 替换为具体的mime类型
header('Content-Disposition:attachment;filename="file_name"');
[header('Content-Length:int');]
echo 'content';/readfile('path/to/file')(读取文件内容并输出到缓冲区);
```

###生成器 generator
使用`yield` 关键字向函数外部抛出值，但函数本身却不返回；继续执行时，会持续向外产生值；
```
function generator(){
	for($i=0; $i<3; $i++){
		yield $i; // 向外抛出一个值，循环却不停止；
	}
}
$res = generator();
```
\$res是一个生成器，可以用foreach遍历；此生成器，类似数组，但不是数组；
此外，yield还能抛出键值对 yield \$key=>\$val;可以组成关联生成器；在一次循环内yield多次的会顺序插入；
而且`foreach(generator() as $key => $val){}`  时，generator()函数在每个项目执行一次。

###静态变量
- PHP中静态方法和类方法调用要分清，静态方法用self::调用，类方法一定要用$this->调用。
- PHP父类用const声明的常量在子类中重新定义也不会被覆盖，如果需要子类覆盖可以使用静态变量 static；
- PHP static定义的变量，使用 self:: 或者 __CLASS__ 对它引用时，取决于定义当前方法所在的类，最好使用static关键字引用
```
/* class A{
    public static $var = 'aaa';
    public function getSelf(){
        var_dump(self::$var);
    }
    public function getStatic(){
        var_dump(static::$var);
    }
}
class B extends A{
    public static $var = 'bbb';
}

$a = new A();
$a->getSelf(); // aaa
$a->getStatic(); // aaa

$b = new B();
$b->getSelf(); // aaa
$b->getStatic(); // bbb */
```

###测试用例 phpunit
- 测试用例的主要目的是为了以后再程序进行修改的时候一次运行来验证底层逻辑的。
- setUp()来搭建基境，使用tearDown来恢复原来的环境；
- setUpBeforeClass() 和 tearDownAfterClass()
- 在注释中加入$dataProvider来指定一个函数为数据源，该函数直接返回一个二维数组，可以存放多次测试的信息。
- 在注释中使用@depends来标注函数间的依赖，使得一个测试方法依赖另一个方法的结果，可以用@depends clone使用返回值的副本，而不是原结果。
- 使用expectException()来对预期的异常进行处理；
- 使用expectOutputString()来判断预期输出。
- 使用assertTrue，assertEqual来断言结果。

###操作zip
```
$zip = new ZipArchive();
$zip->open($zip_name);
$zip->extractTo($aim_dir, [$keep_file(string,array)]); // 从zip中提取一个或多个，或全部了文件到某个目录
$zip->deleteName($dir/$file Name); // 从zip中删除文件或文件夹
$zip->addFile($file_to_add); // 向zip中添加文件
```
###内存分配-写时复制 COW
- php变量存在一个叫"zval"的变量容器中,"zval"变量容器包括含变量的类型和值，还包括额外的两个字节信息，分别是“is_ref”表示变量是否属于引用，“refcount”指向这个zval变量容器的变量个数。
-is_ref标识是不是用户使用 & 的强制引用；
- ref_count是引用计数，用于标识此zval被多少个变量引用，即COW的自动引用，为0时会被销毁；
- 只有当变量容器在”refcount“变成0时就被销毁.当你unset()一个变量时，想要的“zval”中refcount就会减1.
- PHP在修改一个变量以前，会首先查看这个变量的refcount，如果refcount大于1，PHP就会执行分离，如上，PHP发现\$a指向的zval的refcount大于1，那么PHP就会复制一个新的zval出来，将原zval的refcount减1，并修改symbol_table，使得\$a和\$b分离(Separation)。这个机制就是所谓的copy on write(写时复制)。

###YII的ActiveRecord实现：
1. 使用`describe table_name` 查询语句；
2. 分析查询结果，对每一个字段，有Field(字段名)、Type(数据类型)、Null(是否为空)、Key(索引信息，‘PRI’表示为主键)、Default(默认值)、Extra(附加信息，如auto_increment)
3. 通过主键信息，对表信息进行操作；

###静态变量，define() 和 const 定义常量的区别：
1. 只有静态变量能定义数组；
2. define()能使用表达式来定义一个常量，const不能；
3. define()能在if代码块中定义，const不能；
4. define()不能在类中定义常量，const可以；
5. const在编译期间定义常量，而define()在执行期间定义，const稍快一点；
6. define的定义是全局的，const是类内部的；由4.

###trait
是PHP的一个为了实现函数继承，解决单继承问题的新特性。
```
trait TraitName{methods}
class ClassName extends Base{use TraitName}
```
基类，trait，自定义函数的优先级为  自身>trait>基类

###反射
是PHP为了获取类信息，调用类，反向修改类的一个扩展，它的大部分功能在于实现获取类信息，包括类属性，方法，注释等一系列信息。
用类名实例化一个反射类，通过反射类的一些方法来获取对应信息。如果需要传入不同命名空间的类可以直接传入一个带有命名空间的名字，例如
`$reflector = new Reflection('NameSpace\\ClassName');`注意反斜杠要进行转义。

###使用Soap进行一次HTTP请求:
1. 使用`$header_items[] = new SoapVar($data, format, 'name')`,创建一组数据头
2. 使用`$header = new SoapHeader($namespace, $name, $header_items)`;
3. 创建一个soap连接对象 `$soap = new SoapClient($url, $soap_options)`;
4. 设置soap的请求头 `$sopa->__setSoapHeaders($header)`;
5. 发出soap请求，`$soap->__soapCall($function_name(soap要调用的函数), $arguments(调用函数所传参数), $options, $input_headers(输入头数据), &$output_headers(响应头数据))`;
6. 获取响应信息：`$response = $soap->__getLastResponse()`;

###SPL standard php library
数据结构
- SplDoublyLinkedList 双向链表
- SplStack 栈
- Queue 队列
- Heap 堆
- SplMaxHeap 大顶堆
- SplMinHeap 小顶堆
- SplPriorityQueue 优先队列
- SplFixedArray 定长数组
- SplObjectStorage 对象存储器

###PHP的启动和终止
扩展名为 ext 方法名为func
1. 执行MINIT（Module Initialization）方法，此方法只会在模块加载时执行一次，内核中预置了 PHP_MINIT_FUNCTION(ext)方法；
2. 执行RINIT（Request Initialization）方法，此方法会在每次请求时加载一次，内核中预置了 PHP_RINIT_FUNCTION(ext) 方法；
3. 执行PHP_FUNCTION(func)方法，执行PHP的func方法时会自动执行；
4. 执行RSHUTDOWN（Request Shutdown）方法，此方法会在每次请求执行完毕后加载一次，内核中预置了PHP_RSHUTDOWN_FUNCTION(ext)方法；
5. 执行MSHUTDOWN（Module Shutdown）方法，此方法会在每次模块取消挂载时执行，内核中预置了 PHP_MSHUTDOWN_FUNCTION(ext)方法。

在Cli模式下，每次执行都相当于一次请求，所有方法都只会被执行一遍。
在fpm模式下，M方法会在加载和关闭时被执行一次，R方法会在每次请求时执行一次。