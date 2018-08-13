package com.yuantek.test;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FileCreateTestStart {
	public static void main(String[] args) {
		WriteConfig.initConfig();
		LoadFile.getInstance();
		Executors.newScheduledThreadPool(5).scheduleAtFixedRate(()->{
			try{
				FileWriter.getInstance().writePeriod();
			}catch(Exception e){
						
			}
		}, 0, 1, TimeUnit.SECONDS);
	}
}
