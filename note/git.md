Git
---
- Git保存的不是文件的变动，而是保存了每次`修改前后文件`的整体内容为一个 `blob`；
- Git通过`指针`的形式，指向每次修改的文件，表示一个版本。
- 每一个文件的不同版本，都有一个40位的sha1校验和。

回退本地代码
---
git reset 三种方式：

- git reset --soft: 撤销commit的代码
- git reset --mixed: 撤销commit的代码，并将 add 的文件也恢复； 默认使用此选项
- git reset --hard:撤销commit的代码，并将工作区的代码的改动也撤销；

回退的目标可以是：

- HEAD^ 上一个commit
- 057d 上一次提交的摘要
- origin/master 根据分支恢复
- HEAD~n 恢复上面第n个版本
- 后面添加文件名可恢复指定文件