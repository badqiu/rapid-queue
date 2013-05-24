delete from rq_exchange where exchange_name = 'ex_demo2';
INSERT INTO `rq_exchange` VALUES ('ex_demo2', 'vhost', 'demo2', 'DURABLE', '1', '1', '1', null, '100', '100', '2013-05-24 17:33:27', '1', '2013-05-24 17:33:30');

delete from rq_queue where queue_name = 'queue_demo2';
INSERT INTO `rq_queue` VALUES ('queue_demo2', 'vhost', 'q1', 'DURABLE', '1', '1', '1', '1', '100', '10000', '100', '2013-05-15 14:56:34', '1', '2013-05-15 14:56:38');

delete from rq_binding where exchange_name = 'ex_demo2' and queue_name = 'queue_demo2' and vhost_name = 'vhost';
INSERT INTO `rq_binding` VALUES ('queue_demo2', 'ex_demo2', 'vhost', '*', 'ex_user_*');


