/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2013-5-9 15:26:26                            */
/*==============================================================*/


drop table if exists rq_binding;

drop table if exists rq_exchange;

drop table if exists rq_queue;

drop table if exists rq_user;

drop table if exists rq_vhost;

/*==============================================================*/
/* Table: rq_binding                                            */
/*==============================================================*/
create table rq_binding
(
   queue_name           varchar(50) not null comment '队列名称',
   exchange_name        varchar(50) not null comment '交换机名称',
   vhost_name           varchar(50) not null comment '虚拟host',
   router_key           varchar(3000) comment '交换机的router_key',
   remarks              varchar(200) comment '备注',
   primary key (queue_name, exchange_name, vhost_name)
);

alter table rq_binding comment '队列与交换机的绑定关系';

/*==============================================================*/
/* Table: rq_exchange                                           */
/*==============================================================*/
create table rq_exchange
(
   exchange_name        varchar(50) not null comment '交换机名称',
   vhost_name           varchar(50) not null comment '虚拟host',
   remarks              varchar(200) comment '备注',
   durable_type         varchar(30) not null comment '持久模式:memory,durable,haft_durable',
   auto_delete          tinyint not null comment '是否自动删除',
   auto_delete_expires  bigint comment '自动删除的时过期时长，单位毫秒',
   type                 varchar(30) comment '类型: topic,fanout,direct',
   size                 int comment '当前交换机大小',
   memory_size          int comment '当使用半持久模式,放在内存中的元素大小',
   max_size             int comment '交换机的大小',
   created_time         datetime not null comment '创建时间',
   operator             varchar(50) comment '操作人员',
   last_updated_time    datetime comment '最后更新时间',
   primary key (exchange_name, vhost_name)
);

alter table rq_exchange comment '交换机';

/*==============================================================*/
/* Table: rq_queue                                              */
/*==============================================================*/
create table rq_queue
(
   queue_name           varchar(50) not null comment '队列名称',
   vhost_name           varchar(50) not null comment '虚拟host',
   remarks              varchar(200) comment '备注',
   durable_type         varchar(30) not null comment '持久模式:memory,durable,haft_durable',
   auto_delete          tinyint not null comment '是否自动删除',
   auto_delete_expires  bigint comment '自动删除的时过期时长，单位毫秒',
   exclusive            tinyint not null comment '是否互斥，即该队列只能有一个客户端连接',
   size                 int not null comment '队列当前大小',
   memory_size          int comment '当使用半持久模式,放在内存中的元素大小',
   max_size             int not null comment '队列最大大小',
   ttl                  bigint comment 'time to live in queue,发送至这个队列的数据多久过期',
   created_time         datetime not null comment '创建时间',
   operator             varchar(50) not null comment '创建人',
   last_updated_time    datetime not null comment '最后更新时间',
   primary key (queue_name, vhost_name)
);

/*==============================================================*/
/* Table: rq_user                                               */
/*==============================================================*/
create table rq_user
(
   username             varchar(30) not null,
   password             varchar(30),
   remarks              varchar(200),
   email                varchar(30),
   mobile               varchar(20),
   primary key (username)
);

/*==============================================================*/
/* Table: rq_vhost                                              */
/*==============================================================*/
create table rq_vhost
(
   vhost_name           varchar(50) not null comment '虚拟host',
   remarks              varchar(200) comment '备注',
   host                 varchar(50) comment '实际部署的主机',
   primary key (vhost_name)
);

alter table rq_binding add constraint FK_Reference_1 foreign key (queue_name, vhost_name)
      references rq_queue (queue_name, vhost_name) on delete restrict on update restrict;

alter table rq_binding add constraint FK_Reference_2 foreign key (exchange_name, vhost_name)
      references rq_exchange (exchange_name, vhost_name) on delete restrict on update restrict;

alter table rq_binding add constraint FK_Reference_5 foreign key (vhost_name)
      references rq_vhost (vhost_name) on delete restrict on update restrict;

alter table rq_exchange add constraint FK_Reference_4 foreign key (vhost_name)
      references rq_vhost (vhost_name) on delete restrict on update restrict;

alter table rq_queue add constraint FK_Reference_3 foreign key (vhost_name)
      references rq_vhost (vhost_name) on delete restrict on update restrict;

