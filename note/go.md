###tips


###interface
- 使用`var item InterfaceName `声明变量是 InterfaceName 类型的，用它可以给变量赋一个默认值；
- `interface{}` 是一个万能结构，使用它修饰参数，可以传入任意类型的参数；用它修饰返回值，可以返回任意类型的值；


###判断某对象是否是某个struct类型的
```
_,ok := interface{}(Xobject).(Xstruct)
if ok {
   fmt.Println("yes")
}else{
   fmt.Println("no")
}
```
判断类型的语法为  `error, match := Xinterface.(Xstruct)`，所以一开始要使用 `interface{}(Xobject)` 将对象转为接口类型；

### GO中的 new 和 make
- new和make都用来分配内存；
- new 适用于`值类型或用户自定义类型`，make用于`内置引用类型`，如slice/map/channel;
- new 分配类型的零值并返回`地址`，即指向某类型的指针； make返回某类型初始化后的值；

###包引入 Import
- `. "fmt"` 将pack里的函数引入到此包中直接使用 `Println("")`
- `f "fmt"` 给包起一个别名使用 `f.Println("")`
- `_ "fmt"` 只调用fmt包的init函数，不使用其他函数