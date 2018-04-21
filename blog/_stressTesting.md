﻿#压测

1. 使用同样的QPS，对比耗时，分阶段对比；
2. A耗时 X AQPS <-> B耗时 X BQPS；
3. 网卡 iperf iftop；
4. cpu top us% load；
5. 考虑QPS陡降的可能性；
6. tomcat 线程数，hystrix 线程数，各任务线程池；
7. 磁盘I/O
8. 锁
9. 中断
10. 连接数