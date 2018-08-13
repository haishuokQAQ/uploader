package com.yuantek.batchupdate;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.yuantek.ConfigContainer;

public class ChangeNameThread extends Thread{
	private FTPConnectorPro connector = new FTPConnectorPro();
	private BlockingQueue<String> nameQueue = new ArrayBlockingQueue<>(5000);
	private boolean connected = false;
	private String nowWorkingDirectory = "";
	
	@Override
	public void run() {
		String fileName = null;
		while(true) {
			try {
				
			}catch(Exception e) {
				
			}
		}
	}
	
	public void connect(){
		connected = connector.connect(ConfigContainer.hostName, ConfigContainer.port, ConfigContainer.userName, ConfigContainer.password);
	}
	private void waitForConnect(){
		while (!connected){
			connect();
		}
	}
}
