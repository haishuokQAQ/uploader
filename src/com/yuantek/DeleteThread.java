package com.yuantek;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteThread extends Thread{
	private BlockingQueue<File> deleteQueue = new ArrayBlockingQueue<>(10000);
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void insert(File f){
		deleteQueue.add(f);
	}
	
	@Override
	public void run() {
		File f = null;
		while(true){
			try{
				f = deleteQueue.take();
				f.delete();
				if (f.exists()){
					deleteFileClear(f);
				}
			}catch(Exception e){
				if (e instanceof InterruptedException) break;
				if (e instanceof NoSuchFileException) {
					if (f.exists())  {
						deleteFileClear(f);
					}
				}
			}
		}
	}
	
	private void deleteFileClear(File f){
		if (f.exists()){
			if (!f.delete()){
				try {
					Runtime.getRuntime().exec("rm -f " + f.getAbsolutePath());
				} catch (IOException e) {
					logger.error("fail to delete file {}.", f.getAbsoluteFile());
				}
			}
		}
	}
	
	public static void main(String[] args) {
		while(true){
			File f = null;
 			try{
				f = new File("test/ftptest");
				if (f.exists()) Files.delete(f.toPath());
				else f.createNewFile();
 			}catch (Exception e){
				try{
					if (f.exists()) {
						System.out.println(1);
						f.delete();
					}
				}catch(Exception e1){
					System.out.println(2);
				}
			}
		}
		
	}
}
