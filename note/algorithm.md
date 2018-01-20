###最近使用算法：
1. 以结点（有key,value,previous,next等属性）来维护“最近使用项”;
2. 以一个链表（有length,head,tail,data_list等属性）来维护最近使用项列表;
3. 使用构造方法，初始化链表属性；
4. 构造链表项的GET，SET（注意数据链表的长度），DEL；注意添加，删除时对链表前后项的连接操作；


###范围不重复随机数获取
1. 把范围数全数置入一个中间数组中；
2. 获取一个小于数组长度的随机数N；
3. 返回中间数组的第N项；
4. 将中间数组最后一个元素的值赋给第N项；（精髓）
5. 将数组长度减一；
代码实现（JS）：
```
function getRandom(numCount) {
    var numList = [];
    var numMin = 100000;
    var numMax = 999999;
    var listLen = numMax - numMin + 1;

    var outPut = [];

    // 将所有数顺序装填到数字列表中
    for (var i = 100000; i < numMax + 1; i++) {
        numList.push(i);
    }

    var randNum;
    for (var j = 0; j < numCount; j++) {
        // 产生一个小于列表长度的随机数randNum
        randNum = Math.floor(Math.random() * listLen);
        // 将列表的第randNum项输出
        outPut.push(numList[randNum]);
        // 将列表的最后一项替换到刚被取出的数的位置
        numList[randNum] = numList[listLen - 1];
        // 列表长度减一,即列表最后一项不会再被取到;
        listLen--;
    }

    return outPut;
}
```

###协同过滤算法
大量数据  ->  两种实体之间多次关联  ->  每种实体之间有相似性  -> 推算某种相似实体与另一种实体的关系
商城购买数据 -> 很多用户购买了很多物品 -> 某些人喜欢某种物品 -> 推测与这些人相似的人会喜欢某些物品
计算方式：计算两种两个实体关系向量的cos值，对比cos值差距
                   计算两种实体关系的相关性