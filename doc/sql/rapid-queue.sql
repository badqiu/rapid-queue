/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50528
Source Host           : localhost:3306
Source Database       : rapid-queue

Target Server Type    : MYSQL
Target Server Version : 50528
File Encoding         : 65001

Date: 2013-07-15 16:06:48
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `rq_binding`
-- ----------------------------
DROP TABLE IF EXISTS `rq_binding`;
CREATE TABLE `rq_binding` (
  `queue_name` varchar(50) NOT NULL COMMENT '队列名称',
  `exchange_name` varchar(50) NOT NULL COMMENT '交换机名称',
  `vhost_name` varchar(50) NOT NULL COMMENT '虚拟host',
  `router_key` varchar(3000) DEFAULT NULL COMMENT '交换机的router_key',
  `remarks` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`queue_name`,`exchange_name`,`vhost_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='队列与交换机的绑定关系';

-- ----------------------------
-- Records of rq_binding
-- ----------------------------
INSERT INTO `rq_binding` VALUES ('queue_demo', 'ex_demo', 'vhost', '*', '');
INSERT INTO `rq_binding` VALUES ('queue_memory', 'ex_memory', 'vhost', '*', '');
INSERT INTO `rq_binding` VALUES ('q_2', 'ex_memory', 'vhost', '', '');

-- ----------------------------
-- Table structure for `rq_exchange`
-- ----------------------------
DROP TABLE IF EXISTS `rq_exchange`;
CREATE TABLE `rq_exchange` (
  `exchange_name` varchar(50) NOT NULL COMMENT '交换机名称',
  `vhost_name` varchar(50) NOT NULL COMMENT '虚拟host',
  `remarks` varchar(200) DEFAULT NULL COMMENT '备注',
  `durable_type` varchar(30) NOT NULL COMMENT '持久模式:memory,durable,haft_durable',
  `auto_delete` tinyint(4) NOT NULL COMMENT '是否自动删除',
  `auto_delete_expires` bigint(20) DEFAULT NULL COMMENT '自动删除的时过期时长，单位毫秒',
  `type` varchar(30) DEFAULT NULL COMMENT '类型: topic,fanout,direct',
  `size` int(11) DEFAULT NULL COMMENT '当前交换机大小',
  `memory_size` int(11) DEFAULT NULL COMMENT '当使用半持久模式,放在内存中的元素大小',
  `max_size` int(11) DEFAULT NULL COMMENT '交换机的大小',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  `operator` varchar(50) DEFAULT NULL COMMENT '操作人员',
  `last_updated_time` datetime DEFAULT NULL COMMENT '最后更新时间',
  `enabled` tinyint(4) DEFAULT '1' COMMENT '是否激活',
  PRIMARY KEY (`exchange_name`,`vhost_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='交换机';

-- ----------------------------
-- Records of rq_exchange
-- ----------------------------
INSERT INTO `rq_exchange` VALUES ('ex_111', 'vhost', '', 'DURABLE', '0', '0', null, '0', '0', '0', '2013-07-05 16:04:55', 'demo_login_user', '2013-07-05 16:04:55', '1');
INSERT INTO `rq_exchange` VALUES ('ex_demo', 'vhost', 'ex_user', 'DURABLE', '1', '1', '1', '0', '100', '100', '2013-05-15 14:55:56', '1', '2013-05-15 14:56:02', '1');
INSERT INTO `rq_exchange` VALUES ('ex_memory', 'vhost', 'ex_memory', 'MEMORY', '1', '1', '1', '0', '1000000', '1000000', '2013-06-03 15:19:26', '1', '2013-06-03 15:19:31', '1');

-- ----------------------------
-- Table structure for `rq_queue`
-- ----------------------------
DROP TABLE IF EXISTS `rq_queue`;
CREATE TABLE `rq_queue` (
  `queue_name` varchar(50) NOT NULL COMMENT '队列名称',
  `vhost_name` varchar(50) NOT NULL COMMENT '虚拟host',
  `remarks` varchar(200) DEFAULT NULL COMMENT '备注',
  `durable_type` varchar(30) NOT NULL COMMENT '持久模式:memory,durable,haft_durable',
  `auto_delete` tinyint(4) NOT NULL COMMENT '是否自动删除',
  `auto_delete_expires` bigint(20) DEFAULT NULL COMMENT '自动删除的时过期时长，单位毫秒',
  `exclusive` tinyint(4) NOT NULL COMMENT '是否互斥，即该队列只能有一个客户端连接',
  `size` int(11) NOT NULL COMMENT '队列当前大小',
  `memory_size` int(11) DEFAULT NULL COMMENT '当使用半持久模式,放在内存中的元素大小',
  `max_size` int(11) NOT NULL COMMENT '队列最大大小',
  `ttl` bigint(20) DEFAULT NULL COMMENT 'time to live in queue,发送至这个队列的数据多久过期',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  `operator` varchar(50) NOT NULL COMMENT '创建人',
  `last_updated_time` datetime NOT NULL COMMENT '最后更新时间',
  `enabled` tinyint(4) DEFAULT '1' COMMENT '是否激活',
  PRIMARY KEY (`queue_name`,`vhost_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rq_queue
-- ----------------------------
INSERT INTO `rq_queue` VALUES ('queue_demo', 'vhost', 'q1', 'DURABLE', '1', '1', '1', '1', '100', '10000', '100', '2013-05-15 14:56:34', '1', '2013-05-15 14:56:38', '1');
INSERT INTO `rq_queue` VALUES ('queue_memory', 'vhost', 'q1', 'MEMORY', '1', '1', '1', '0', '100000', '1000000', '100', '2013-06-03 15:20:11', '1', '2013-06-03 15:20:17', '1');
INSERT INTO `rq_queue` VALUES ('q_2', 'vhost', '', 'DURABLE', '0', '0', '0', '0', '0', '1000000000', '0', '2013-07-05 16:05:17', 'demo_login_user', '2013-07-05 16:05:17', '1');

-- ----------------------------
-- Table structure for `rq_user`
-- ----------------------------
DROP TABLE IF EXISTS `rq_user`;
CREATE TABLE `rq_user` (
  `username` varchar(30) NOT NULL,
  `password` varchar(32) DEFAULT NULL,
  `remarks` varchar(200) DEFAULT NULL,
  `email` varchar(30) DEFAULT NULL,
  `mobile` varchar(20) DEFAULT NULL,
  `allow_webadmin_login` tinyint(4) NOT NULL DEFAULT '0' COMMENT '允许管理后台登录',
  `queue_permission_list` varchar(4000) DEFAULT NULL COMMENT '拥有的queue权限,*代表所有权限',
  `exchange_permission_list` varchar(4000) DEFAULT NULL COMMENT '拥有的exchange权限,*代表所有权限',
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rq_user
-- ----------------------------
INSERT INTO `rq_user` VALUES ('admin', 'f6fdffe48c908deb0f4c3bd36c032e72', 'admin user', 'admin@qq.com', '183838383', '1', '*', 'ex_memory\r\nex_demo');

-- ----------------------------
-- Table structure for `rq_vhost`
-- ----------------------------
DROP TABLE IF EXISTS `rq_vhost`;
CREATE TABLE `rq_vhost` (
  `vhost_name` varchar(50) NOT NULL COMMENT '虚拟host',
  `remarks` varchar(200) DEFAULT NULL COMMENT '备注',
  `host` varchar(50) DEFAULT NULL COMMENT '实际部署的主机',
  PRIMARY KEY (`vhost_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rq_vhost
-- ----------------------------
INSERT INTO `rq_vhost` VALUES ('vhost', 'vhost', 'localhost');
