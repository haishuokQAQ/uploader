package com.yuantek;

import java.io.File;

import com.yuantek.batchupdate.AfterUpload;
import com.yuantek.batchupdate.FileScannerNew;
import com.yuantek.batchupdate.UpdateManagerPro;
import com.yuantek.util.TelnetServer;

import ch.qos.logback.core.joran.spi.JoranException;

public class BatchStart {
	public static void main(String[] args) {
		start();
	}
	
	public static void start() {
		try {
			Start.load(new File("logback.xml"));
		} catch (JoranException e) {
			System.out.println("Load log config fail because " + e.toString());
		}
		TelnetServer.startTelnet();
		ConfigContainer.initConfig();
		UpdateManagerPro.getInstance();
		new AfterUpload().start();
		new FileScannerNew().initScanner();
	}
	
}
