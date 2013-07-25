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
package com.google.code.rapid.queue.log.task;

import java.io.File;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.rapid.queue.FileQueue;
import com.google.code.rapid.queue.log.LogEntity;
import com.google.code.rapid.queue.util.FileMappedByteBuffer;

/**
 * @author badqiu
 * @date 2012-5-18
 */
public class FileRunner {
    private final Logger log = LoggerFactory.getLogger(FileRunner.class);
    // 删除队列
    private final BlockingQueue<String> deleteQueue = new LinkedBlockingQueue<String>();
    // 新创建队列
    private final BlockingQueue<FilePreCreateAttr> createQueue = new LinkedBlockingQueue<FilePreCreateAttr>();
    
    private final Executor executor = Executors.newFixedThreadPool(2);
    
    private static FileRunner instance;
    
    private FileRunner() {
		Runnable deleteFileTask = new Runnable() {
			@Override
			public void run() {
				log.info("FileRunner delete file thread stoped");
				try {
					while (true) {
						try {
				        	String deleteFilePath = deleteQueue.take();
				            
				            if (deleteFilePath != null) {
				                File delFile = new File(deleteFilePath);
				                log.info("delete file:"+delFile);
				                delFile.delete();
				            }
						} catch(InterruptedException e) {
							log.warn("InterruptedException on FileRunner.delete thread",e);
							return;
			            }
			        }
				}finally {
					log.info("FileRunner delete file thread stoped");
				}
			}
		};
		
		Runnable preCreateFileTask = new Runnable() {
			@Override
			public void run() {
				log.info("FileRunner pre_create file thread started");
				try {
					while (true) {
						try {
							FilePreCreateAttr createFileAttr = createQueue.take();
				            if (createFileAttr != null) {
				                try {
				                	String createFilePath = createFileAttr.path;
				                    create(createFilePath,createFileAttr.fileLimitLength);
				                } catch (IOException e) {
				                    log.error("预创建数据文件失败,pre_create file fail", e);
				                }
				            }
						}catch(InterruptedException e) {
							log.warn("InterruptedException on preCreateFileTask",e);
							return;
						}
			        }
				}finally {
					log.info("FileRunner pre_create file thread stoped");
				}
			}
		};
		
		executor.execute(deleteFileTask);
		executor.execute(preCreateFileTask);
    }
    
    public static synchronized FileRunner getInstance() {
    	if(instance == null) {
    		instance = new FileRunner();
    	}
    	return instance;
    }
    
    public void addDeleteFile(String path) {
        deleteQueue.add(path);
    }

    public void addCreateFile(String path,int fileLimitLength) {
        createQueue.add(new FilePreCreateAttr(path,fileLimitLength));
    }
    
    private boolean create(String path,int fileLimitLength) throws IOException {
        File file = new File(path);
        if (file.exists() == false) {
        	log.info("pre_create file:"+file);
            FileMappedByteBuffer fmbb = LogEntity.createLogEntityFile(file, fileLimitLength);
            fmbb.close();
            
            return true;
        } else {
            return false;
        }
    }
    
    private static class FilePreCreateAttr{
    	int fileLimitLength;
    	String path;
		public FilePreCreateAttr(String path, int fileLimitLength) {
			super();
			this.path = path;
			this.fileLimitLength = fileLimitLength;
		}
		
    }
}
