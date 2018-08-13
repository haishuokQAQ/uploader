package com.yuantek;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileScanner {
	private String baseDir;
	
	private static FileScanner instance = new FileScanner();
	
	private ScheduledExecutorService es = Executors.newScheduledThreadPool(1);
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private int count = 0;
	private int match = 0;
	private int notMatch = 0;
	
	private FileScanner() {
	}
	
	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}
	
	public void initScanner(){
		es.scheduleAtFixedRate(()->{
			try{
				scanFile();
			}catch(Exception e){
				
			}
		}, 0, 5, TimeUnit.SECONDS);
	}

	public static FileScanner getInstance() {
		return instance;
	}

	private void scanFile(){
		logger.info("Start scan.");
		File base = new File(baseDir);
		scanChild(base);
		logger.info("Update {} file(s).{} matched.{} not-matched.", count, match, notMatch);
		count = 0;
	}
	
	private void scanChild(File file){
		for (File f : file.listFiles()){
			if (f.isDirectory()){
				scanChild(f);
			}
			else if (f.getName().endsWith(".zip") && isUpdateable(f)){
				//f.renameTo(getTempFile(f));
				count ++;
				if (f.getAbsolutePath().contains("not_match")){
					UpdateManager.getInstance().insertFileNotMatch(f);
					match ++;
				}
				else{
					UpdateManager.getInstance().insertFile(f);
					notMatch ++;
				}
			}
		}
	}
	
	public static File getTempFile(File f){
		String name = f.getAbsolutePath();
		String[] namse = name.split("\\.");
		String destName = "";
		for (int i = 0; i < namse.length - 1; i++){
			destName += namse[i];
		}
		destName += ".temp";
		File destFile = new File(destName);
		return destFile;
	}
	
	public static File getOriginalFile(File f){
		String name = f.getAbsolutePath();
		String[] namse = name.split("\\.");
		String destName = "";
		for (int i = 0; i < namse.length - 1; i++){
			destName += namse[i];
		}
		destName += ".zip";
		File destFile = new File(destName);
		return destFile;
	}
	
	private boolean isUpdateable(File f){
		return (System.currentTimeMillis() - f.lastModified()) > 5000;
	}
	
	public static void main(String[] args) {
	}
}
