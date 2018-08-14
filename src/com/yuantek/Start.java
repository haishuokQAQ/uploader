package com.yuantek;

import java.io.File;

import org.slf4j.LoggerFactory;

import com.yuantek.ftp.FTPStatistic;
import com.yuantek.util.RuntimeStatus;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

public class Start {
	public static void main(String[] args) {
		start();
	}
	
	public static void start() {
		try {
			load(new File("logback.xml"));
		} catch (JoranException e) {
			System.out.println("Load log config fail because " + e.toString());
		}
		ConfigContainer.initConfig();
		UpdateManager.getInstance();
		FileScanner.getInstance().setBaseDir(ConfigContainer.scanDir);
		FileScanner.getInstance().initScanner();
	}
	
	 static void load(File externalConfigFile) throws JoranException {
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();	
		if (!externalConfigFile.exists()) {
			System.out.println("Log config cannot find.");
			return;
		}
		if (!externalConfigFile.canRead()){
			System.out.println("Cannot read log config file.");
			return;
		}
		JoranConfigurator configurator = new JoranConfigurator();
		configurator.setContext(lc);
		lc.reset();
		configurator.doConfigure(externalConfigFile);
		StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
	}
}
