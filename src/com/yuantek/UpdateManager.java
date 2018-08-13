package com.yuantek;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.yuantek.ftp.UpdateThread;

public class UpdateManager {
	private static UpdateManager instance = new UpdateManager();
	
	private AtomicInteger pos = new AtomicInteger();
	private AtomicInteger posNotMatch = new AtomicInteger();
	
	private List<UpdateThread> threads = new ArrayList<>();
	
	private List<UpdateThread> extraThreads = new ArrayList<>();
	
	private List<UpdateThread> notMatchThreads = new ArrayList<>();
	
	private UpdateManager() {
		for (int i = 0; i < ConfigContainer.thread; i++){
			UpdateThread ut = new UpdateThread(ConfigContainer.hostName, ConfigContainer.port, ConfigContainer.userName, ConfigContainer.password, i, true);
			extraThreads.add(ut);
			ut.start();
		}
		if (ConfigContainer.extraMode) {
			for (int i = 0; i < ConfigContainer.thread; i++){
				UpdateThread ut = new UpdateThread(ConfigContainer.hostName, ConfigContainer.port, ConfigContainer.userName, ConfigContainer.password, i, ConfigContainer.postName);
				threads.add(ut);
				ut.start();
			}
		}
		for (int i = 0; i < ConfigContainer.thread; i++){
			UpdateThread ut = new UpdateThread(ConfigContainer.hostName, ConfigContainer.port, ConfigContainer.userName, ConfigContainer.password, i + 2, false);
			notMatchThreads.add(ut);
			ut.start();
		}
	}
	
	
	public void insertFile(File f){
		if (pos.get() == threads.size()) pos.set(0);
		threads.get(pos.get()).insert(f);
		pos.incrementAndGet();
		if (ConfigContainer.extraMode) {
			insertFileExtra(f);
		}
	}
	
	private static AtomicInteger extraPath = new AtomicInteger();
	public void insertFileExtra(File f) {
		if (extraPath.get() == extraThreads.size()) {
			extraPath.set(0);
		}
		extraThreads.get(extraPath.get()).insert(f);
	}
	
	public void insertFileNotMatch(File f){
		if (posNotMatch.get() == notMatchThreads.size()) posNotMatch.set(0);
		notMatchThreads.get(posNotMatch.get()).insert(f);
		posNotMatch.incrementAndGet();
		if (ConfigContainer.extraMode) {
			insertFileExtra(f);
				
		}
	}
	
	public static UpdateManager getInstance() {
		return instance;
	}
	
	
}
