package com.google.code.rapid.queue.util;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;

import cn.org.rapid_framework.util.Profiler.Step;

public class Profiler {
	
	static ConcurrentHashMap<String, Dur> all = new ConcurrentHashMap<String, Dur>();
	
	public static void enter(String messageId) {
		Step step = cn.org.rapid_framework.util.Profiler.getStep();
		if(step == null) {
			cn.org.rapid_framework.util.Profiler.start(messageId);
		}else {
			cn.org.rapid_framework.util.Profiler.enter(messageId);
		}
	}

	public static void release(){
		Step step = cn.org.rapid_framework.util.Profiler.release();
		if(step != null) {
			merge2AllDur(new Dur(step.getMessage(),step.getDuration()));
		}
	}

	public static String dumpAllDur() {
		return StringUtils.join(all.values(),"\n");
	}
	
	public static ConcurrentHashMap<String, Dur> getAll() {
		return all;
	}

	private static void merge2AllDur(Dur dur) {
		Dur allDur = all.get(dur.messageId);
		if(allDur == null) {
			allDur = new Dur(dur.messageId,0);
			all.put(dur.messageId, allDur);
		}
		allDur.merge(dur);
	}
	
	public static class Dur {
		private String messageId;
		private long startTime;
		private long count;
		private long cost;
		
		public Dur(String messageId, long cost) {
			super();
			this.messageId = messageId;
			this.cost = cost;
		}

		public void merge(Dur dur) {
			this.cost += dur.cost;
			this.count++;
		}
		
		public String getMessageId() {
			return messageId;
		}

		public long getCount() {
			return count;
		}

		public long getCost() {
			return cost;
		}

		public int getTps() {
			int tps = (int)(count * 1000.0 / cost);
			return tps;
		}
		
		public String toString() {
			int tps = getTps();
			long avgCost = cost / count;
			return String.format("count:%,d \t cost:%,d \t tps:%s \t avgCost:%s \t %s",count,cost,tps,avgCost,messageId);
//			return messageId+" count:"+count+" cost:"+cost+" tps:"+tps;
		}
	}
}
