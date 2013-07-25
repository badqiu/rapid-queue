package com.google.code.rapid.queue.server.cron;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.google.code.rapid.queue.MessageBroker;
import com.google.code.rapid.queue.MessageBrokerAutoRefresher;
import com.google.code.rapid.queue.MessageBrokerPool;
import com.google.code.rapid.queue.log.LogEntity;
import com.google.code.rapid.queue.log.task.MappedByteBufferSyncExecutor;
import com.google.code.rapid.queue.metastore.model.Queue;
import com.google.code.rapid.queue.metastore.service.QueueService;
import com.google.code.rapid.queue.server.impl.LoseMessageVhostStore;
import com.google.code.rapid.queue.util.RouterKeyUtil;

public class MessageBrokerAutoRefresherJob extends BaseCronJob implements InitializingBean {
	private static Logger logger = LoggerFactory.getLogger(MessageBrokerAutoRefresherJob.class);
	
	private MessageBrokerAutoRefresher messageBrokerAutoRefresher;
	private MessageBrokerPool messageBrokerPool;
	private QueueService queueService;
	
	public void setMessageBrokerPool(MessageBrokerPool messageBrokerPool) {
		this.messageBrokerPool = messageBrokerPool;
	}
	
	public MessageBrokerAutoRefresherJob() {
		super("1/5 * * * * *");
//		super("1/5 1 * * * *");
	}

	public void setMessageBrokerAutoRefresher(
			MessageBrokerAutoRefresher messageBrokerAutoRefresher) {
		this.messageBrokerAutoRefresher = messageBrokerAutoRefresher;
	}
	
	public void setQueueService(QueueService queueService) {
		this.queueService = queueService;
	}

	@Override
	protected void executeInternal() {
		dumpJvm();
		dumpMessageBrokerInfo();
		updateAllQueueSize();
		messageBrokerAutoRefresher.execute();
	}

	private void updateAllQueueSize() {
		for(MessageBroker mb : messageBrokerPool.getAll()) {
			Map<String,Integer> sizeMap = mb.getManager().listQueueSize();
			for(Map.Entry<String, Integer> entry : sizeMap.entrySet()) {
				queueService.updateQueueSize(entry.getKey(), mb.getVhostName(), entry.getValue());
			}
		}
	}

	private void dumpMessageBrokerInfo() {
		for(MessageBroker mb : messageBrokerPool.getAll()) {
			logger.info("---------------------"+mb.getVhostName()+"----------------------");
			logger.info("exchange status:"+mb.getManager().listExchangeSize().toString());
			logger.info("queue status:"+mb.getManager().listQueueSize().toString());
		}
	}

	private void dumpJvm() {
		logger.info("thread.activeCount:"+Thread.activeCount()+" LogEntity.logEntityCache.size():"+LogEntity.logEntityCache.size()+" MappedByteBufferSyncExecutor.size:"+MappedByteBufferSyncExecutor.getInstance().size()+" RouterKeyUtil.cache.size:"+RouterKeyUtil.cache.size()+" LoseMessageVhostStore.status:"+LoseMessageVhostStore.status());;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		Assert.notNull(messageBrokerAutoRefresher,"messageBrokerAutoRefresher must be not null");
		Assert.notNull(queueService,"queueService must be not null");
	}
}
