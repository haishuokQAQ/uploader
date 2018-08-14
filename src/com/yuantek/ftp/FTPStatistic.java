package com.yuantek.ftp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ftpµÄÍ³¼Æ
 * @author kong.haishuo
 *
 */
public class FTPStatistic {
	private static AtomicLong updateSucc = new AtomicLong();
	
	private static long lastSucc = 0;
	
	private static long lastTime = System.currentTimeMillis();
	
	private static AtomicLong updateFail = new AtomicLong();
	
	
	public static void addUpdate() {
		updateSucc.incrementAndGet();
	}
	
	public static void addFail() {
		updateFail.incrementAndGet();
	}
	
	
	public static String getUpdateStatus() {
		StringBuilder sb = new StringBuilder();
		long timeUse = System.currentTimeMillis() - lastTime;
		lastTime = System.currentTimeMillis();
		long nowCount = updateSucc.get();
		lastSucc = updateSucc.get();
		long speed = (nowCount - lastSucc) * 1000 / (timeUse + 1);
		sb.append("Total upload count ").append(updateSucc.get()).append("    Fail count : ").append(updateFail).append("\r\n");
		sb.append("Upload speed").append(speed).append("\r\n");
		return sb.toString();
	}
}
