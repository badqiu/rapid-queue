package com.google.code.rapid.queue.cron.job;

import java.nio.MappedByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.google.code.rapid.queue.MessageBrokerAutoRefresher;
import com.google.code.rapid.queue.log.LogEntity;
import com.google.code.rapid.queue.log.task.MappedByteBufferSyncExecutor;

public class MessageBrokerAutoRefresherJob extends BaseCronJob implements InitializingBean {
	private static Logger logger = LoggerFactory.getLogger(MessageBrokerAutoRefresherJob.class);
	
	private MessageBrokerAutoRefresher messageBrokerAutoRefresher;
	public MessageBrokerAutoRefresherJob() {
		super("1/5 * * * * *");
	}

	public void setMessageBrokerAutoRefresher(
			MessageBrokerAutoRefresher messageBrokerAutoRefresher) {
		this.messageBrokerAutoRefresher = messageBrokerAutoRefresher;
	}
	
	@Override
	protected void executeInternal() {
		messageBrokerAutoRefresher.execute();
		dumpJvm();
	}

	private void dumpJvm() {
		logger.info("thread.activeCount:"+Thread.activeCount()+" LogEntity.logEntityCache.size():"+LogEntity.logEntityCache.size()+" MappedByteBufferSyncExecutor.size:"+MappedByteBufferSyncExecutor.getInstance().size());;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		Assert.notNull(messageBrokerAutoRefresher,"messageBrokerAutoRefresher must be not null");
	}
}
