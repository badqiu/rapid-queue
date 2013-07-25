// thrift --gen java  <xxxx.thrift>

namespace java com.google.code.rapid.queue.thrift.api

struct MessageProperties {
	1: string messageId, // 消息ID
	2: i32 priority, // 优先级
	3: string contentType, // json,xml,text,
	4: string contentEncoding, // content encoding,value=UTF8,GBK
	5: i64 contentLength,
	6: i64 expiration, // 过期时间，单位毫秒
	7: i64 timestamp, // 消息生成时间

	8: i32 deliveryMode, // 消息投递模式

	9: string sourceIp, // 发送消息的来源IP
	10: string sourceApp, // 发送消息的来源App
	11: string sourceUser, // 发送消息的来源User
	
}

struct Message {
	1: string exchange, // exchange
	2: string routerKey, // 路由key
	3: binary body, // 消息体
	
	4: MessageProperties messageProperties, // 消息附加属性
}

exception MessageBrokerException {
  1: string errorCode,
  2: string message,
}


service MessageBrokerService {
	/**
	 * 发送消息
	 * @param msg
	 */
	void send(1:Message msg) throws (1:MessageBrokerException e),
	
	/**
	 * 批量发送消息
	 * @param msg
	 */	
	void sendBatch(1:list<Message> msgList) throws (1:MessageBrokerException e),
	
	/**
	 * 接收消息
	 * @param queue 队列名称
	 * @param timeout 等待超时时间,单位(毫秒)
	 */	
	Message receive(1:string queueName,2:i32 timeout) throws (1:MessageBrokerException e),
	
	/**
	 * 批量接收消息
	 * @param queue 队列名称
	 * @param timeout 等待超时时间,单位(毫秒)
	 * @param batchSize 批量接收的大小
	 */		
	list<Message> receiveBatch(1:string queueName,2:i32 timeout,3:i32 batchSize) throws (1:MessageBrokerException e),

	/**
	 * 登陆
	 */
	void login(1:string username,2:string password,3:string vhost) throws (1:MessageBrokerException e),

	/**
	 * 心跳检查,返回字符串: PONG
	 */
	string ping() throws (1:MessageBrokerException e),			
}

/**
 * MessageBroker管理接口
 */
service MessageBrokerManagerService {
	void queueAdd(1:string queueName),
	
	void queueDelete(1:string queueName),

	void queueClear(1:string queueName),
	
	void queueUnbindAllExchange(1:string queueName),
	
	void queueBind(1:string exchangeName,2:string queueName,3:string routerKey),

	void queueUnbindByRouterKey(1:string exchangeName,2:string queueName,3:string routerKey),

	void queueUnbind(1:string exchangeName,2:string queueName),
	
	void exchangeAdd(1:string exchange),
	
	void exchangeDelete(1:string exchangeName),
	
	void selectVhost(1:string vhostName),
	/**
	 * 返回所有binding
	 * 
	 * @return Map<exchangeName,List<queueName>>
	 */
	/*
	Map<string,List<string>> getAllBinding(),
	
	Set<string> getQueueNames(),

	Set<string> getExchangeNames(),
	
	Map<string,Integer> listExchangeSize(),

	Map<string,Integer> listQueueSize()
	*/
}