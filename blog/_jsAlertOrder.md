使用回调解决js Alert的执行顺序问题
问题
---
前几天使用 Js 写 HTML 页面时遇到了一个奇怪的问题：

我想实现的功能是通过 confirm 弹窗让用户选择不同的需求，每次选择后都将选择结果暂时输出到页面上，最后一次选择结束后再一次性将选项传到后端处理。
代码类似于：
```
    var step1 = confirm("exec step1?");
    $('#result').html($('#result').html() + "\n"+step1);
    var step2 = confirm("exec step2?");
    $('#result').html($('#result').html() + "\n"+step2);
    var step3 = confirm("exec step3?");
    $('#result').html($('#result').html() + "\n"+step3);

    send(step1, step2, step3);
```

可是实现后却发现：每次在用户confirm之后，页面并没有刷新，step1, step2的结果没有实时刷新到页面上，而是到最后一步跟step3一块显示了出来。

后续尝试了 alert 和 promt 这两个跟 confirm 类似的弹窗函数，情况都与此相同，它们都会在页面渲染之前被执行。

此时，还有更诡异的情况，我们给某一个div里赋值后，立刻alert此div里的内容，会发现alert显示正确的内容，而div里的内容却没有更新，并且会一直阻塞到我们点击确定。

如图：

原因
---
解决这个问题之前先了解一下它是怎么产生的。要了解它需要从Js的线程模型说起。

JavaScript 引擎是单线程运行的,浏览器无论在什么时候都只且只有一个线程在运行JavaScript程序，初衷是为了减少dom等共享资源的冲突。可是单线程永远会面临着一个问题，那就是阻塞。又由于JS经常需要操作页面dom和发起HTTP请求，这些I/O操作耗时一般都比较长，一旦阻塞，就会给用户非常差的使用体验。

于是便有了事件循环（event loop）的产生，js 将一些异步操作或I/O相关的操作全都放到一个事件队列，先顺序执行速度很快的 Js 代码，等到Js 引擎没有同步代码了，有空闲时再读取事件队列的事件来`依次`执行。

这些事件包括：
- setTimeout 的异步事件
- DOM操作相关如布局和绘制事件
- 网络I/O 如AJAX请求事件
- 用户操作事件，如鼠标点击、键盘敲击。

解决
---
明白了原理， 再解决这个问题就有了方向，我们来分析这个问题的冲突：

- 页面渲染由于是DOM操作，会被js引擎放入事件循环
- alert()是 window 的内置函数，被认为是同步js代码
- js引擎会优先执行同步js代码，在空闲时才会执行异步函数

我们无法将页面渲染变成同步操作，那么只好把 alert 变为异步js代码，从而才能在页面渲染之后执行。

关于这个，我们有两种方法可以使用；

替换alert
---
首先我们考虑替换掉alert函数的功能，其实大多数情况下我们替换掉alert并不是它不符合我们期待的执行顺序，而是因为它实在是太丑了，而且也不支持各种美化，可以想像在一个充满了某一类型特色的网站上忽然弹出来一个单调的框框是多么另人跳戏。

这个我们可以考虑bootstrap 的 modal 模块，bootstrap在绝大多数网站上都在应用，而多引入一个modal模块也不会有多大影响。我们使用modal 构造一个弹出对话框的样子，使用modal的 `modal('toggle')/modal('show')/modal('hide')` 方法可以很方便地控制modal的显隐。

改掉它，我们还需要解决后续代码执行的问题，使用alert函数时，我们点击确定后代码还会继续执行，而使用我们自定义的对话框可没有这种功能了，我们需要考虑把后续代码绑定在对话框的点击按钮上，这就需要使用DOM的onclick属性了，我们将后续函数内容抽出一个新的函数，在弹出对话框后将这个函数绑定在按钮的onclick事件上即可。

这里还需要注意，新函数内应该包括关闭对话框的内容。

当然，我们还可以再优化一下，抽象出来一个用来弹出对话框的函数替代alert函数；
```
window.alert = function (message, callbackFunc) {
    $('#alertContent').html(message);
    $('#modal').show();
    $('#confirmButton').onclick(function () {
        $('#modal').hide();
        callbackFunc();
    });
};
```
如此，我们在需要弹出框时调用新的alert函数，并传入 callbackFunc ，在里面做后续的事情就可以了。

setTimeOut函数
---
通过setTimeOut将函数变异步的特性将其变为异步

当然，并不是所有人都愿意使用新的对话框替换alert函数的对话框，总感觉上面的方法不是特别的优雅，对此，我们可以采用另外的方法解决这个问题。

前端的同学应该对setTimeout这个函数不陌生，使用它，可以延迟执行某些代码。对于延迟执行的代码，js引擎总是把这些代码放到事件队列里去，再不停地检查是否已经到了执行时间，再适时执行。代码进入事件队列，就意味着代码变成和页面渲染事件一样异步了。由于事件队列是有序的，我们如果用setTimeout延时执行，就可以实现在页面渲染之后执行alert的功能了。

setTimeout的函数原型为 `setTimeout(code, msec)`，code 是要变为异步的代码或函数，msec是要延时的时间，单位为毫秒。这里我们不需要它延时，只需要它变为异步就行了，所以可以将msec设置为0；

同样，alert之后的代码我们也需要处理，将它们跟alert一块放到setTimeout里异步执行。这样，代码就变为 `setTimeout("alert('msg');doSomething();", 0);`，如果觉得代码不够美观或字符串不好处理的话，可以将后续代码封装成一个函数放到 `doSomething()` 里即可。

小结
---
回调

回调地狱


























