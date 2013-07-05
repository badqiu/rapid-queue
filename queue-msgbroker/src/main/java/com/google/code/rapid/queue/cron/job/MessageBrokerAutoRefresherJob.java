package com.google.code.rapid.queue.cron.job;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.google.code.rapid.queue.MessageBroker;
import com.google.code.rapid.queue.MessageBrokerAutoRefresher;
import com.google.code.rapid.queue.MessageBrokerPool;
import com.google.code.rapid.queue.log.LogEntity;
import com.google.code.rapid.queue.log.task.MappedByteBufferSyncExecutor;
import com.google.code.rapid.queue.util.Profiler;

public class MessageBrokerAutoRefresherJob extends BaseCronJob implements InitializingBean {
	private static Logger logger = LoggerFactory.getLogger(MessageBrokerAutoRefresherJob.class);
	
	private MessageBrokerAutoRefresher messageBrokerAutoRefresher;
	private MessageBrokerPool messageBrokerPool;
	
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
	
	@Override
	protected void executeInternal() {
		logger.info("--------- profiler ------------\n"+Profiler.dumpAllDur());
		dumpJvm();
		dumpMessageBrokerInfo();
		
		messageBrokerAutoRefresher.execute();
	}

	private void dumpMessageBrokerInfo() {
		for(MessageBroker mb : messageBrokerPool.getAll()) {
			logger.info("---------------------"+mb.getVhostName()+"----------------------");
			logger.info(mb.getManager().listExchangeSize().toString());
			logger.info(mb.getManager().listQueueSize().toString());
		}
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
