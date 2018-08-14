##tips
- 修改 `./bashrc` 中的 `PS1` 环境变量可以修改 bash 每行命令的提示字符串
- 使用 echo "\e[1;**mTEXT" 修改文本颜色， ** 可以代表不同的颜色， 使用 echo "\e[0m" 重置文本颜色；
- 用 (cmd) 声明一个子shell，里面对环境变量的操作不会影响到接下来的代码

### 文件描述符
- 使用 cmd >|>>|<|<< file 创建文件描述符
- 使用 n> n>> 输出文件描述符
- 使用 >&n >>&2 输入到文件描述符

### 数组
- 使用 var=(v1 v2 v3) 定义索引数组
- 使用 ${var[index]} 取索引数组值
- 使用 ${var[*]} 或 ${var[@]} 取所有元素；
- 使用关联数组前必须使用 `declare -A var` 声明关联数组，然后使用 var[index]=val 赋值，或 var=([index1]=val1 [index2]=val2)
- 使用 ${!var[*]} 或 ${!var[@]} 列出数组关联
- 使用 ${#var[*]} 或 ${#var[@]} 获取数组长度

### 终端操作 tput stty
- tput setb n  tput sebf n 设置终端背景色或前景色
- tput cols / tput lines 获取终端列数或行数
- tput ed 清除光标到行尾
- tput sc 存储光标位置  tput rc 恢复存储的光标位置，配合 tput ed 清除到行尾数据以实现原地写
- stty -echo 结果不回显到终端 stty echo 结果开始回显，用于输密码

### 调试 debug
- bash -x shell.sh 启用调试，在执行时显示参数和命令
- bash -v shell.sh 启用调试，命令读取时显示输入
- 使用 shebang： `#!/bin/bash -xv` 调试
- 在某一代码块前后设置  set -x | set +x 只调试某一块