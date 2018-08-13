package com.yuantek.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.yuantek.batchupdate.AfterUpload;
import com.yuantek.batchupdate.UpdateManagerPro;

public class RuntimeStatus {
	private static List<Thread> matchUpdateThread = new ArrayList<>();
	private static List<Thread> notMatchUpdateThread = new ArrayList<>(); 
	
	private static Map<String, AtomicLong> updateStatus = new ConcurrentHashMap<>();
	
	public static AtomicLong dropped = new AtomicLong();
	public static AtomicLong droppedNotMatch = new AtomicLong();
	
	public static void updateThread(Thread th, boolean matched){
		if (matched){
			matchUpdateThread.add(th);
		}else{
			notMatchUpdateThread.add(th);
		}
	}
	
	public static void updateTableUpload(String tableName, int num){
		if (!updateStatus.containsKey(tableName)){
			updateStatus.put(tableName, new AtomicLong());
		}
		updateStatus.get(tableName).addAndGet(num);
	}
	
	public static String getThreadStatus(){
		StringBuilder sb = new StringBuilder();
		sb.append("********\r\n");
		sb.append("Thread match : \r\n");
		int pos = 1;
		for (Thread thread : matchUpdateThread){
			sb.append("Update-thread-").append(pos).append(" : ").append(thread.getState().name()).append("\r\n"); 
		}
		sb.append("********\r\n");
		sb.append("Thread not match : \r\n");
		pos = 1;
		for (Thread thread : notMatchUpdateThread){
			sb.append("Update-thread-notmatch-").append(pos).append(" : ").append(thread.getState().name()).append("\r\n"); 
		}
		
		return sb.toString();
		
	}
	
	public static String getUpdateCount(){
		StringBuilder sb = new StringBuilder();
		sb.append("Update count").append("\r\n");
		sb.append("----------------------------------------\r\n");
		for (Entry<String, AtomicLong> entry : updateStatus.entrySet()){
			sb.append(entry.getKey()).append("     ").append(entry.getValue().get()).append("\r\n");
		}
		return sb.toString();
	}
	
	
	public static String getQueueStatus(){
		StringBuilder sb = new StringBuilder();
		int match = UpdateManagerPro.getInstance().getMatchQueueStatus();
		int notMatch = UpdateManagerPro.getInstance().getNotMatchQueueStatus();
		sb.append("Match queue used : ").append(match)
		.append("     remain : ").append(10000 - match).append("    over count : ").append(dropped.get()).append("\r\n");
		sb.append("Not match queue used : ").append(notMatch)
		.append("     remain : ").append(10000 - notMatch).append("    over count : ").append(droppedNotMatch.get());
		sb.append("Delete queue used : ").append(AfterUpload.getQueueStatus()).append(" over count ").append(AfterUpload.deleteFail.get());
		return sb.toString();
	}
}
