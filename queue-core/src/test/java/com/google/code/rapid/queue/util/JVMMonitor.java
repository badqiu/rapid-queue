/*
 *  Copyright 2011 badqiu [badqiu1223@gmail.com][weibo.com@badqiu1223]
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.google.code.rapid.queue.util;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

/**
 * 对JVM的状态监控
 * 
 * @author badqiu
 * @date 2010-8-13
 * @version $Id: JVMMonitor.java 42 2012-08-16 03:00:00Z badqiu1223@gmail.com $
 */
public class JVMMonitor {
    private static final OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();
    private static final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
    private static final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    private static final long maxMemory = Runtime.getRuntime().maxMemory();
    private static final ClassLoadingMXBean classLoadingBean = ManagementFactory.getClassLoadingMXBean();
    private static final List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory
            .getGarbageCollectorMXBeans();
    private static final List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
    private static final Set<String> edenSpace = new HashSet<String>();
    private static final Set<String> survivorSpace = new HashSet<String>();
    private static final Set<String> oldSpace = new HashSet<String>();
    private static final Set<String> permSpace = new HashSet<String>();
    private static final Set<String> codeCacheSpace = new HashSet<String>();
    private static final List<String> youngGenCollectorNames = new ArrayList<String>();
    private static final List<String> fullGenCollectorNames = new ArrayList<String>();
    static {
        // 各种GC下的eden名字
        edenSpace.add("Eden Space");// -XX:+UseSerialGC
        edenSpace.add("PS Eden Space");// –XX:+UseParallelGC
        edenSpace.add("Par Eden Space");// -XX:+UseConcMarkSweepGC
        edenSpace.add("Par Eden Space");// -XX:+UseParNewGC
        edenSpace.add("PS Eden Space");// -XX:+UseParallelOldGC
        // 各种gc下survivorSpace的名字
        survivorSpace.add("Survivor Space");// -XX:+UseSerialGC
        survivorSpace.add("PS Survivor Space");// –XX:+UseParallelGC
        survivorSpace.add("Par Survivor Space");// -XX:+UseConcMarkSweepGC
        survivorSpace.add("Par survivor Space");// -XX:+UseParNewGC
        survivorSpace.add("PS Survivor Space");// -XX:+UseParallelOldGC
        // 各种gc下oldspace的名字
        oldSpace.add("Tenured Gen");// -XX:+UseSerialGC
        oldSpace.add("PS Old Gen");// –XX:+UseParallelGC
        oldSpace.add("CMS Old Gen");// -XX:+UseConcMarkSweepGC
        oldSpace.add("Tenured Gen  Gen");// Tenured Gen Gen
        oldSpace.add("PS Old Gen");// -XX:+UseParallelOldGC

        // 各种gc下持久代的名字
        permSpace.add("Perm Gen");// -XX:+UseSerialGC
        permSpace.add("PS Perm Gen");// –XX:+UseParallelGC
        permSpace.add("CMS Perm Gen");// -XX:+UseConcMarkSweepGC
        permSpace.add("Perm Gen");// -XX:+UseParNewGC
        permSpace.add("PS Perm Gen");// -XX:+UseParallelOldGC
        // codeCache的名字
        codeCacheSpace.add("Code Cache");
        // Oracle (Sun) HotSpot
        // -XX:+UseSerialGC
        youngGenCollectorNames.add("Copy");
        // -XX:+UseParNewGC
        youngGenCollectorNames.add("ParNew");
        // -XX:+UseParallelGC
        youngGenCollectorNames.add("PS Scavenge");
        // Oracle (Sun) HotSpot
        // -XX:+UseSerialGC
        fullGenCollectorNames.add("MarkSweepCompact");
        // -XX:+UseParallelGC and (-XX:+UseParallelOldGC or -XX:+UseParallelOldGCCompacting)
        fullGenCollectorNames.add("PS MarkSweep");
        // -XX:+UseConcMarkSweepGC
        fullGenCollectorNames.add("ConcurrentMarkSweep");

    }

    /**
     * @return MBeanServer
     */
    static MBeanServer getPlatformMBeanServer() {
        return ManagementFactory.getPlatformMBeanServer();
    }

    /**
     * 获取系统负载
     * 
     * @return
     */
    public static double getSystemLoad() {
        if (!(bean instanceof com.sun.management.OperatingSystemMXBean))
            return 0L;
        return ((com.sun.management.OperatingSystemMXBean) bean).getSystemLoadAverage();
    }

    /**
     * 获取CPU个数
     * 
     * @return
     */
    public static int getAvailableProcessors() {
        if (!(bean instanceof com.sun.management.OperatingSystemMXBean))
            return 0;
        return ((com.sun.management.OperatingSystemMXBean) bean).getAvailableProcessors();
    }

    /**
     * 返回文件描述符数
     * 
     * @return
     */
    public static String getFileDescriptor() {
        try {
            String[] attributeNames = new String[] { "MaxFileDescriptorCount", "OpenFileDescriptorCount" };
            ObjectName name;
            name = new ObjectName("java.lang:type=OperatingSystem");
            AttributeList attributes = getPlatformMBeanServer().getAttributes(name, attributeNames);
            StringBuilder sb = new StringBuilder();
            for (int i = 0, len = attributes.size(); i < len; i++) {
                if (sb.length() > 0) {
                    sb.append("\r\n");
                }
                sb.append(attributes.get(i).toString().replace(" = ", ":"));
            }
            return sb.toString();
        } catch (MalformedObjectNameException e) {
            return "MaxFileDescriptorCount:0\r\nOpenFileDescriptorCount:0";
        } catch (NullPointerException e) {
            return "MaxFileDescriptorCount:0\r\nOpenFileDescriptorCount:0";
        } catch (InstanceNotFoundException e) {
            return "MaxFileDescriptorCount:0\r\nOpenFileDescriptorCount:0";
        } catch (ReflectionException e) {
            return "MaxFileDescriptorCount:0\r\nOpenFileDescriptorCount:0";
        }
    }

    /**
     * 获取所有的线程数
     * 
     * @return
     */
    public static int getAllThreadsCount() {
        return threadBean.getThreadCount();
    }

    /**
     * 获取峰值线程数
     * 
     * @return
     */
    public static int getPeakThreadCount() {
        return threadBean.getPeakThreadCount();
    }

    /**
     * Returns the current number of live daemon threads.
     * 
     * @return the current number of live daemon threads.
     */
    public static int getDaemonThreadCount() {
        return threadBean.getDaemonThreadCount();
    }

    /**
     * 获取启动以来创建的线程数
     * 
     * @return
     */
    public static long getTotalStartedThreadCount() {
        return threadBean.getTotalStartedThreadCount();
    }

    /**
     * 获取死锁数
     * 
     * @return 死锁数
     */
    public static int getDeadLockCount() {
        ThreadMXBean th = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        long[] deadLockIds = th.findMonitorDeadlockedThreads();
        if (deadLockIds == null) {
            return 0;
        } else {
            return deadLockIds.length;
        }

    }

    /**
     * 获取虚拟机的heap内存使用情况
     * 
     * @return
     */
    public static MemoryUsage getJvmHeapMemory() {
        return memoryMXBean.getHeapMemoryUsage();

    }

    /**
     * 获取虚拟机的noheap内存使用情况
     * 
     * @return
     */
    public static MemoryUsage getJvmNoHeapMemory() {
        return memoryMXBean.getNonHeapMemoryUsage();

    }

    /**
     * 获取当前JVM占用的总内存
     * 
     * @return
     */
    public static long getTotolMemory() {
        long totalMemory = Runtime.getRuntime().totalMemory();

        return totalMemory;
    }

    /**
     * 获取当前JVM给应用分配的内存
     * 
     * @return
     */
    public static long getUsedMemory() {
        long totalMemory = Runtime.getRuntime().totalMemory();
        long usedMemory = totalMemory - Runtime.getRuntime().freeMemory();
        return usedMemory;
    }

    /**
     * 获取JVM能使用到的最大内存
     * 
     * @return
     */
    public static long getMaxUsedMemory() {
        return maxMemory;
    }

    /**
     * 获取启动以来加载的总的class数
     * 
     * @return
     */
    public static long getTotalLoadedClassCount() {
        return classLoadingBean.getTotalLoadedClassCount();
    }

    /**
     * 获取当前JVM加载的class数
     * 
     * @return
     */
    public static int getLoadedClassCount() {
        return classLoadingBean.getLoadedClassCount();
    }

    /**
     * 获取JVM被启动以来unload的class数
     * 
     * @return
     */
    public static long getUnloadedClassCount() {

        return classLoadingBean.getUnloadedClassCount();
    }

    /**
     * 获取GC的时间
     * 
     * @return
     */
    public static String getGcTime() {
        StringBuilder sb = new StringBuilder();
        for (GarbageCollectorMXBean bean : garbageCollectorMXBeans) {
            if (sb.length() > 0) {
                sb.append("\r\n");
            }
            if (youngGenCollectorNames.contains(bean.getName())) {
                sb.append("youngGCCount:");
                sb.append(bean.getCollectionCount());
                sb.append("\r\n");
                sb.append("youngGCTime:");
                sb.append(bean.getCollectionTime());
            } else if (fullGenCollectorNames.contains(bean.getName())) {
                sb.append("fullGCCount:");
                sb.append(bean.getCollectionCount());
                sb.append("\r\n");
                sb.append("fullGCTime:");
                sb.append(bean.getCollectionTime());
            }

        }

        return sb.toString();
    }

    public static Map<String, MemoryUsage> getMemoryPoolCollectionUsage() {
        Map<String, MemoryUsage> gcMemory = new HashMap<String, MemoryUsage>();
        for (MemoryPoolMXBean memoryPoolMXBean : memoryPoolMXBeans) {
            String name = memoryPoolMXBean.getName();
            if (edenSpace.contains(name)) {
                gcMemory.put("eden", memoryPoolMXBean.getCollectionUsage());
            } else if (survivorSpace.contains(name)) {
                gcMemory.put("survivor", memoryPoolMXBean.getCollectionUsage());
            } else if (oldSpace.contains(name)) {
                gcMemory.put("old", memoryPoolMXBean.getCollectionUsage());
            } else if (permSpace.contains(name)) {
                gcMemory.put("perm", memoryPoolMXBean.getCollectionUsage());
            } else if (codeCacheSpace.contains(name)) {
                gcMemory.put("codeCache", memoryPoolMXBean.getCollectionUsage());
            }

        }
        return gcMemory;
    }

    public static Map<String, MemoryUsage> getMemoryPoolUsage() {
        Map<String, MemoryUsage> gcMemory = new HashMap<String, MemoryUsage>();
        for (MemoryPoolMXBean memoryPoolMXBean : memoryPoolMXBeans) {
            String name = memoryPoolMXBean.getName();
            if (edenSpace.contains(name)) {
                gcMemory.put("eden", memoryPoolMXBean.getUsage());
            } else if (survivorSpace.contains(name)) {
                gcMemory.put("survivor", memoryPoolMXBean.getUsage());
            } else if (oldSpace.contains(name)) {
                gcMemory.put("old", memoryPoolMXBean.getUsage());
            } else if (permSpace.contains(name)) {
                gcMemory.put("perm", memoryPoolMXBean.getUsage());
            } else if (codeCacheSpace.contains(name)) {
                gcMemory.put("codeCache", memoryPoolMXBean.getUsage());
            }

        }
        return gcMemory;
    }

    public static Map<String, MemoryUsage> getMemoryPoolPeakUsage() {
        Map<String, MemoryUsage> gcMemory = new HashMap<String, MemoryUsage>();
        for (MemoryPoolMXBean memoryPoolMXBean : memoryPoolMXBeans) {
            String name = memoryPoolMXBean.getName();
            if (edenSpace.contains(name)) {
                gcMemory.put("eden", memoryPoolMXBean.getPeakUsage());
            } else if (survivorSpace.contains(name)) {
                gcMemory.put("survivor", memoryPoolMXBean.getPeakUsage());
            } else if (oldSpace.contains(name)) {
                gcMemory.put("old", memoryPoolMXBean.getPeakUsage());
            } else if (permSpace.contains(name)) {
                gcMemory.put("perm", memoryPoolMXBean.getPeakUsage());
            } else if (codeCacheSpace.contains(name)) {
                gcMemory.put("codeCache", memoryPoolMXBean.getPeakUsage());
            }

        }
        return gcMemory;
    }

    public static String getMonitorStats(String item) {
        if ("load".equals(item)) {
            return "load:" + JVMMonitor.getSystemLoad();
        } else if ("allThreadsCount".equals(item)) {
            return "allThreadsCount:" + JVMMonitor.getAllThreadsCount();
        } else if ("peakThreadCount".equals(item)) {
            return "peakThreadCount:" + JVMMonitor.getPeakThreadCount();
        } else if ("daemonThreadCount".equals(item)) {
            return "daemonThreadCount:" + JVMMonitor.getDaemonThreadCount();
        } else if ("totalStartedThreadCount".equals(item)) {
            return "totalStartedThreadCount:" + JVMMonitor.getTotalLoadedClassCount();
        } else if ("deadLockCount".equals(item)) {
            return "deadLockCount:" + JVMMonitor.getDeadLockCount();
        } else if ("heapMemory".equals(item)) {
            MemoryUsage memoryUsage = JVMMonitor.getJvmHeapMemory();
            return "used:" + memoryUsage.getUsed() + "\r\ncommitted:" + memoryUsage.getCommitted() + "\r\nmax:"
                    + memoryUsage.getMax();
        } else if ("noHeapMemory".equals(item)) {
            MemoryUsage memoryUsage = JVMMonitor.getJvmNoHeapMemory();
            return "used:" + memoryUsage.getUsed() + "\r\ncommitted:" + memoryUsage.getCommitted() + "\r\nmax:"
                    + memoryUsage.getMax();
        } else if ("memory".equals(item)) {
            return "totolMemory:" + JVMMonitor.getTotolMemory() + "\r\nused:" + JVMMonitor.getUsedMemory()
                    + "\r\nmaxUsedMemory:" + JVMMonitor.getMaxUsedMemory();
        } else if ("classCount".equals(item)) {
            return "totalLoadedClassCount:" + JVMMonitor.getTotalLoadedClassCount() + "\r\nloadedClassCount:"
                    + JVMMonitor.getLoadedClassCount() + "\r\nunloadedClassCount:" + JVMMonitor.getUnloadedClassCount();
        } else if ("GCTime".equals(item)) {
            return JVMMonitor.getGcTime();
        } else if ("memoryPoolCollectionUsage".equals(item)) {
            Map<String, MemoryUsage> gcMap = JVMMonitor.getMemoryPoolCollectionUsage();
            StringBuilder sb = new StringBuilder();
            for (String key : gcMap.keySet()) {
                if (sb.length() > 0) {
                    sb.append("\r\n");
                }
                MemoryUsage memoryUsage = gcMap.get(key);
                if (memoryUsage == null) {
                    memoryUsage = new MemoryUsage(0, 0, 0, 0);
                }
                if (memoryUsage != null) {
                    sb.append(key);
                    sb.append("Init:");
                    sb.append(memoryUsage.getInit());
                    sb.append("\r\n");
                    sb.append(key);
                    sb.append("Used:");
                    sb.append(memoryUsage.getUsed());
                    sb.append("\r\n");
                    sb.append(key);
                    sb.append("Committed:");
                    sb.append(memoryUsage.getCommitted());
                    sb.append("\r\n");
                    sb.append(key);
                    sb.append("Max:");
                    sb.append(memoryUsage.getMax());
                }

            }
            return sb.toString();
        } else if ("memoryPoolUsage".equals(item)) {
            Map<String, MemoryUsage> gcMap = JVMMonitor.getMemoryPoolUsage();
            StringBuilder sb = new StringBuilder();
            for (String key : gcMap.keySet()) {
                if (sb.length() > 0) {
                    sb.append("\r\n");
                }
                MemoryUsage memoryUsage = gcMap.get(key);
                if (memoryUsage == null) {
                    memoryUsage = new MemoryUsage(0, 0, 0, 0);
                }
                if (memoryUsage != null) {
                    sb.append(key);
                    sb.append("Init:");
                    sb.append(memoryUsage.getInit());
                    sb.append("\r\n");
                    sb.append(key);
                    sb.append("Used:");
                    sb.append(memoryUsage.getUsed());
                    sb.append("\r\n");
                    sb.append(key);
                    sb.append("Committed:");
                    sb.append(memoryUsage.getCommitted());
                    sb.append("\r\n");
                    sb.append(key);
                    sb.append("Max:");
                    sb.append(memoryUsage.getMax());
                }
            }
            return sb.toString();
        } else if ("memoryPoolPeakUsage".equals(item)) {
            Map<String, MemoryUsage> gcMap = JVMMonitor.getMemoryPoolPeakUsage();
            StringBuilder sb = new StringBuilder();
            for (String key : gcMap.keySet()) {
                if (sb.length() > 0) {
                    sb.append("\r\n");
                }
                MemoryUsage memoryUsage = gcMap.get(key);
                if (memoryUsage == null) {
                    memoryUsage = new MemoryUsage(0, 0, 0, 0);
                }
                if (memoryUsage != null) {
                    sb.append(key);
                    sb.append("Init:");
                    sb.append(memoryUsage.getInit());
                    sb.append("\r\n");
                    sb.append(key);
                    sb.append("Used:");
                    sb.append(memoryUsage.getUsed());
                    sb.append("\r\n");
                    sb.append(key);
                    sb.append("Committed:");
                    sb.append(memoryUsage.getCommitted());
                    sb.append("\r\n");
                    sb.append(key);
                    sb.append("Max:");
                    sb.append(memoryUsage.getMax());
                }

            }
            return sb.toString();
        } else if ("fileDescriptor".equals(item)) {
            return getFileDescriptor();
        }
        return item + " command not found";
    }
}
