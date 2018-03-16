TIPS
----
- 每个实现了 serializable 接口的类在序列化时 Java 会自动添加其摘要算法生成的一个 serialVesionUID,如果不显示指定，一旦对类进行了小改动就会导致重新生成的UID不一样，反序列化失败，所以可以自己指定一个不再变动。

spring bean 的使用
---------------
1. 在 `src/main/resources/spring` 目录下添加 `configxx.xml` 文件。
2. 在文件中配置如下以添加一个 bean；
```
<bean id="xxx" class="pack.class">
    <property name="testName", value="testValue"/>
    <property name="otherName">
        <list>
            <ref bean="testBeanId"/>
        </list>
    </property>
</bean>
```
3. 添加 `pack.class` 类，并确认类有 testName、otherName 项，并设置了 setter 方法。

线程同步
---
- synchronized 关键字
```
function(){
    // 用于 synchronized 加锁的实例(this)必须是线程共享实例
    // 此段代码同时只能被一个线程执行
    synchronized(this){
        dosomething;
    }
}

// 也可以用 synchronized 关键字锁住方法
// 此对象的函数同时只能被一个线程执行
public synchronized void function(){}

// synchronized 限制的静态方法，同时只能被一个线程执行
public synchronized static void function(){}
```


对象包装器
---
ArrayList里只能存放 Object 对象，而基本类型不是对象，所以需要包装器
Integer,Long,Float,Double,Short,Byte,Character,Void,Boolean分别包装一些基本数据类型。
但需要注意：
- 对象包装器不可变，一旦构造了包装器，就不允许再更改其中的值。
- 包装器会在使用时自动拆箱和装箱
- boolean, byte, char<=127, -128<=short,int <=127，在java内已经构造好了同一个对象，所以new的前后两个对象实际上是一个
- 包装器在混用时，自动拆箱和装箱时也会像类型自动转换一样，转换为范围更大的类型
- 由于包装器是对象类型，所以使用 equals 方法时要小心空指针异常

动态实例化对象
---
```
Class cls = Class.forName(m.getClass().getName()); // 使用forName获取类对象
Class[] params = {String.class, Integer.TYPE}; // 创建一个构造函数方法所需要的类型对象数组
Object[] values = {"ming", 10}; // 创建一个构造函数方法所需要的值对象数组
Constructor constructor = cls.getDeclaredConstructor(params); // 获取构造函数方法
Object object = constructor.newInstance(values); // 使用newInstance方法获取对象
Method method = cls.getMethod("test"); // 获取要使用方法名
method.invoke(object); // 调用方法名
```

异常
---
异常分为 IOException 和 RuntimeException.
- RuntimeException: 由程序错误导致的异常，如数组访问越界、访问 null 指针。非受查异常，程序应该处理好此类异常。
- IOException: 由 I/O 错误导致的异常，如 文件不存在，在文件尾读取数据。受查异常，编译器会检查是否为所有的此类异常提供处理机制。此类异常可在函数声明中指定，并抛给上层调用者。
- finally 块无论如何都会执行，即使 try 块内有 return。
- 可以在 catch 块中使用 `ne = new Exception; ne.initCase(e)` 来将原异常包装到新异常内抛出。
- 可在 catch 块中使用 `e.printStackTrace()` 来打印异常的栈信息排查错误。

泛型
---
泛型是为了使用方法更具有通用性，Java 是强类型语言，如 sort() 方法，需要对不同类型的参数分别构造不同的函数。而使用泛型类型，仅一个方法 `sort(T TYPE)` 就可以了。

泛型有多种替代符，如 `T/U/S(任意类型) E(集合类型) K/V(关键字和值对) `；使用：`<>` 声明泛型如 `<T>`, 使用准确类型替代 T 来实例化泛型类型，如 <String>;


使用：
- 泛型类：
```
class Test<T>{ // 顶部声明泛型
    private T field; // 类定义中使用泛型
    Test(T f){
        this.field=f;
    }
}

Test<String> cls = new Test<>("test"); // 实例化时指定类型
```
- 泛型方法
```
class Test{
    // 在限定符后声明泛型类型，在后续使用
    public static <T> test(T arg){
        System.out.println(arg);
    }
}
Test.test("hello");
```
- 对泛型添加类型限定，可以在声明时 使用`<T extends Interface>` 来限定传入类型是 Interface(接口或父类) 类型的数据；

注意：
- 实例化泛型类型不能用基本类型，可以使用包装器类型代替。如不能声明 `<integer>`，使用 `<Integer>` 代替 ;
- 泛型 `<Child>` 不是泛型 `<Parent>` 的子类型，所以 `<Parent>` 限定的参数不能使用 `<Child>` 传入；
- 可以使用通配符来表达泛型之间的继承关系，如 `<? extends Parent>` 限定的类型就可以传入 `<Child>`; 而且 `<? super Child>` 限定的类型可以传入 `<Parent>` 类型。

集合
---
一些常用集合类型及描述：

类型|特点
---|---
List|有序，可重复，可通过索引访问
Set|无序，不可重复
Map|键值对

Java中各种集合的实现：

类型|描述|
---|---|
ArrayList|可以`动态增长和缩减`的`索引`序列
LinkedList|可以在任何位置进行高效插入和删除的`有序`序列
ArrayDeque|用循环数组实现的双端队列
HashSet|没有重复元素有无序集合
TreeSet|有序集合
EnumSet|包含枚举类型的集合
LinkedHashSet|可以记住元素插入次序的集合
PriorityQueue|可以高效删除最小元素的集合
HashMap|存储键值关联的映射表
TreeMap|键值有序排列的映射表
EnumMap|键值属于枚举类型的映射表
LinkedHashMap|可以记住键值项添加次序的映射表
WeakHashMap|值无用后可以被垃圾回收器回收的映射表
IdentityHashMap|使用 `==` 而不用 `equals` 比较键值的映射表

注意：
- LinkedList，便于在 List 中间添加或删除元素，但使用索引访问效率是 O(n); 而 ArrayList 支持随机访问。
- TreeSet 使用红黑树支持对元素的排序，但元素类需要实现 `Comparable` 接口;
- PriorityQueue 使用大顶堆排序数据，每次 `remove()` 都能取到最小的元素，元素同样需要实现 `Comparable` 接口；

注解
---
- @GeneratedValue(strategy=GenerationType.xxx) ：用于与 @Id 注解搭配，表示生成数据的策略, 取值有 IDENTITY(数据库自动生成)、 AUTO(程序生成)、TABLE(由一个数据表存储)、SEQUENCE(数据库序列生成)。
- @Column(name = "xxx") 使用 xxx 表示字段名
- @JSONField(name = "xxx") 序列化和反序列化为 json 时的字段名
- @AutoWired 自动获取 BEAN 对象注入，使用时要主动声明字段和setter函数，在Bean中还有 @AutoWired 注解时需要给此bean添加 property 字段并新引入的bean定义好。
- @Service 自动生成 service bean 对象
- @Data 提供所有属性的 getter 和 setter 方法；还有功能简化的 @Setter 和 @Getter;

stream
------
stream 使用 map/reduce 的思想将数据分组处理后再汇总，在产生一个stream流后，使用`中间符`和`终结符`进行数据操作。

产生stream流：
- collection 可以直接使用 `collection.stream()`;
- 数组可以使用 `Stream.of(arr)` 或 `Array.stream(arr)` 来产生开始流。

中间符处理流，生成新的流：
- `map(item->{object})` 遍历流中元素；
- `filter(item->{bool})` 过滤流元素；
- `flatMap(item->拆散元素方式)` 将 `[[a,b],[c,d]]` 拆成 `a,b,c,d`;
- `limit(count)` 限制流元素数量；
- `skip(count)` 跳过开始n个元素；
- `distinct()` 元素去重；
- `sorted(sort_method)` 元素排序；

终结符处理流，生成最终结果：
- `reduce((x,y)->x op y)` ,操作前后两个元素，最终生成一个结果
- `collect(Collectors.toList())`， 将所有值处理，生成一个集合结果
- `foreach(item->operate)` 遍历结果操作
- `toArray() / toMap(key,value)` 生成数组/MAP
- `sum()` 用来 `mapToLong()/mapToInt` 之后求和

hashCode 和 equals
-----------------
- 默认 Object 类的 equals 方法是同一个对象才会返回 true;
- String 类的 equals 方法以判断字符串内容返回结果；
- 如果两个对象 equals 为 true，则其 hashCode 必须要相等；
- 在 hashSet 中，通过 hashCode 建立索引查找到存储位置，通过 equals 方法判断是否同一个对象，如果不是同一个对象，用链表或红黑树存储 hashCode 相同的两个对象；

spring 参数注解
-----------
- `@PathVariable String id` id 需要在 `@RequestMapping("/test/{id}")` 里定义；
- @RequestHeader("header field") 获取 HTTP 头参数；
- @CookieValue("cookie field") 获取 cookie 参数；
- @RequestParam(value = "REQUEST field", required = false) 从 REQUEST 里获取参数， 可以设置是否必传；
- `@RequestBody String data` 获取整个request body， 适用于 content-type 为 `application/json` 等类型的。

处理参数过长的情况
---
- 封装参数对象
- 使用子类 classBuilder
- 重载构造函数、使用得命名后的静态工厂函数代替重构的构造函数；