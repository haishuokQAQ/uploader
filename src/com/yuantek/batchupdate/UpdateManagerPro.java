package com.yuantek.batchupdate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yuantek.ConfigContainer;
import com.yuantek.util.RuntimeStatus;

public class UpdateManagerPro {
	private static UpdateManagerPro instance = new UpdateManagerPro();
	
	private List<UploadThread> threads = new ArrayList<>();
	
	private List<UploadThread> notMatchThreads = new ArrayList<>();
	
	private BlockingQueue<String> matchQueue = new ArrayBlockingQueue<>(10000);
	private BlockingQueue<String> notMatchQueue = new ArrayBlockingQueue<>(10000);
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private UpdateManagerPro() {
		for (int i = 0; i < ConfigContainer.thread; i++){
			UploadThread ut = new UploadThread(matchQueue, ConfigContainer.outBaseDir);
			threads.add(ut);
			RuntimeStatus.updateThread(ut, true);
			ut.start();
		}
		for (int i = 0; i < ConfigContainer.thread; i++){
			UploadThread ut = new UploadThread(notMatchQueue, ConfigContainer.notMatchDir);
			notMatchThreads.add(ut);
			RuntimeStatus.updateThread(ut, false);
			ut.start();
		}
	}
	
	
	public void insertFile(String match){
		try{
			matchQueue.add(match);
		}catch(Exception e){
			RuntimeStatus.dropped.incrementAndGet();
		}
	}
	
	public void insertFileNotMatch(String notMatch){
		try{
			notMatchQueue.add(notMatch);
		}catch(Exception e){
			RuntimeStatus.droppedNotMatch.incrementAndGet();
		}
	}
	
	public static UpdateManagerPro getInstance() {
		return instance;
	}
	
	public int getMatchQueueStatus(){
		return matchQueue.size();
	}
	
	public int getNotMatchQueueStatus(){
		return notMatchQueue.size();
	}
}
