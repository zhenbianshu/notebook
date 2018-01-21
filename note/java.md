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