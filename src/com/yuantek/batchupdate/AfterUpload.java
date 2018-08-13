package com.yuantek.batchupdate;

import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AfterUpload extends Thread{
	private static BlockingQueue<ResultBean> pathQueue = new ArrayBlockingQueue<>(100000);
	private UpdateManagerPro ump = UpdateManagerPro.getInstance();
	private static Logger logger = LoggerFactory.getLogger(AfterUpload.class);
	public static AtomicLong deleteFail = new AtomicLong();
	@Override
	public void run() {
		ResultBean rb = null;
		while(true){
			try{
				rb = pathQueue.take();
				if (!rb.isSuccess()){
					if (rb.getPath().endsWith("not_match")){
						ump.insertFileNotMatch(rb.getPath());
					}else{
						ump.insertFile(rb.getPath());
					}
				}else{
					File list = new File(rb.getPath());
					if (!list.exists()) continue;
					for (File currentFile : list.listFiles()){
						try{
						Files.delete(currentFile.toPath());
						}catch(NullPointerException e){
							if (currentFile != null && currentFile.exists()){
								currentFile.delete();
								logger.info("Throw NullPointerException but file exists");
							}
						}
					}
					Files.delete(list.toPath());
				}
			}catch(Exception e){
				logger.error("Fail to delete file .Cause {}", e.toString());
				pathQueue.offer(rb);
			}
		}
	}
	
	public static int getQueueStatus(){
		return pathQueue.size();
	}
	
	public static void insert(ResultBean rb){
		try{
			pathQueue.add(rb);
		}catch(Exception e){
			deleteFail.incrementAndGet();
		}
	}
}
