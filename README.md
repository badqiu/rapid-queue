rapid-queue
===========

最快速精简的java消息队列,可以比较容易的基于这个项目进行二次开发

解决问题:
1. 解决 rabbitmq 的性能问题, 现rabbitmq可达到1万TPS
2. 解决 rabbitmq 队列大时,删除会卡死的问题
3. embbed使用: 最简代码如下 BlockingQueue queue = new DurableBlockingQueue("/data/deom_queue_dir");
4. 队列权限管理，可以通过后台控制每一个队列的权限，并且设置订阅的 routerKey. 


