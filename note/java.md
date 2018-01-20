###线程同步
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


###对象包装器
ArrayList里只能存放 Object 对象，而基本类型不是对象，所以需要包装器
Integer,Long,Float,Double,Short,Byte,Character,Void,Boolean分别包装一些基本数据类型。
但需要注意：
- 对象包装器不可变，一旦构造了包装器，就不允许再更改其中的值。
- 包装器会在使用时自动拆箱和装箱
- boolean, byte, char<=127, -128<=short,int <=127，在java内已经构造好了同一个对象，所以new的前后两个对象实际上是一个
- 包装器在混用时，自动拆箱和装箱时也会像类型自动转换一样，转换为范围更大的类型
- 由于包装器是对象类型，所以使用 equals 方法时要小心空指针异常

###动态实例化对象
```
Class cls = Class.forName(m.getClass().getName()); // 使用forName获取类对象
Class[] params = {String.class, Integer.TYPE}; // 创建一个构造函数方法所需要的类型对象数组
Object[] values = {"ming", 10}; // 创建一个构造函数方法所需要的值对象数组
Constructor constructor = cls.getDeclaredConstructor(params); // 获取构造函数方法
Object object = constructor.newInstance(values); // 使用newInstance方法获取对象
Method method = cls.getMethod("test"); // 获取要使用方法名
method.invoke(object); // 调用方法名
```