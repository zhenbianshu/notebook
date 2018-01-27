﻿概述
---
- 消息无边界性，应用程序自己实现消息边界处理
- ARP地址解析协议
- ICMP互联网控制消息协议 用于IP层之间交换差错和其他重要消息 ping 和traceroute命令就使用此协议
- 子网掩码由路由器和主机使用，用以确定一个IP地址的网络/子网部分的结束和主机部分的开始
- TCP的三种传送方式：
单播：在发送者和每一接收者之间需要单独的数据信道
组播，组播是一种允许一个或多个发送者（组播源）发送单一的数据包到多个接收者（一次的，同时的）的网络技术。 组播源把数据包发送到特定组播组，而只有属于该组播组的地址才能接收到数据包。
广播：是指在IP子网内广播数据包，所有在子网内部的主机都将收到这些数据包。只在本地子网内有效，因为路由器会封锁广播通信。
- ARP协议：功能是从逻辑地址IP向硬件地址MAC转换
ARP协议处于网络层，使用 以太网地址帧封装协议，也可以因其功能是获取MAC地址，是网络层的基础，而说其是链路层。
第一种情况，发送方不知道接收方的MAC地址，发送方主机发出广播消息，符合IP的主机主动单播应答发送方主机，并带上自己的MAC地址。
第二种情况，发送方已知道接收方的MAC地址，由于MAC地址会变动和过期，会向接收方发送单播消息来确认一下，然后再发出请求。
- DHCP协议：动态主机设置协议（DHCP）是一种使网络管理员能够集中管理和自动分配IP网络地址的通信协议。在IP网络中，每个连接Internet的设备都需要分配唯一的IP地址。DHCP使网络管理员能从中心结点监控和分配IP地址。当某台计算机移到网络中的其它位置时，能自动收到新的IP地址。
- NAT地址转换：将私有IP重写为一个公共地址
NAT：只进行地址转换，必须要同样数量的IP，意义不是特别大。
NAPT：将出口IP的某个端口与内网私有IP进行转换，能实现节约IP的功能，用途比较广。

- 广播地址：每个子网都有一个本地定向子网广播地址，是地址的网络位不变，主机位置1形成的
组播地址：组播的地址是保留的D类地址从224.0.0.0—239.255.255.255
单播地址：单个子网的IP和MAC地址

TCP重点
---
连接和断开：三次握手、四次挥手；
可靠性：重新发送（超时重传、立即重传）
可用性：滑动窗口

防火墙和代理
---
- 防火墙分为：
包过滤防火墙：能够直接过滤掉一些网络流量，存在于网络层，直接处理IP包，它对用户透明，且效率高，但IP伪造等无法被检测。通过iptables命令来管理其规则.
代理防火墙：通过开放一个代理接口，使所有流量都通过此接口访问服务器，在此接口上进行流量过滤。代理防火墙也被称为应用程序防火墙或网关防火墙。它处于应用层，能监视所有协议层的流量，一般为为HTTP代理防火墙和SOCKS代理防火墙。
HTTP代理
socks代理

- 传输协议，使用端口来帮助分离从IP层进入的数据，因为IP层ipv4头部的“协议”字段或ipv6头部的“下一头部”字段的值将进入的iP数据分离到特定协议，所以端口号在不同的传输协议之间是独立的，两个不同的协议可以使用同一端口号。

- 正向TCP反向UDP应该可以？
- tcpdump用法
sock使用

- TCP给IP的块称为报文段
- ssh使用TCP每个字符发送一个数据包