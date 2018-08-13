package com.yuantek.batchupdate;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.yuantek.ConfigContainer;

public class FileScannerNew {
	private final static int period = 5;
	
	//private BlockingQueue<String> dirQueue = new ArrayBlockingQueue<>(10000);
	
	private static long lastExecute = System.currentTimeMillis();
	
	private static long lastScanEnd = System.currentTimeMillis();
	
	private String path = ConfigContainer.scanDir;
	
	private UpdateManagerPro ump = UpdateManagerPro.getInstance();
	
	
	public void initScanner(){
		Executors.newScheduledThreadPool(5).scheduleAtFixedRate(()->scanDir(), 0, period, TimeUnit.MINUTES);
	}
	
	
	
	public void scanDir(){
		lastExecute = System.currentTimeMillis();
		File base = new File(path);
		System.out.println("Start scan " + path + ". At " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss-sss").format(new Date()));
		for (File dayFile : base.listFiles()){
			System.out.println("Scan day file " + dayFile.getAbsolutePath());
			for (File hourFile : dayFile.listFiles()){
				System.out.println("Scan hour file " + hourFile.getAbsolutePath());
				scanHour(hourFile);
			}
		}
		scanDirHistory();
		lastScanEnd = System.currentTimeMillis();
	}
	
	
	private void scanDirHistory(){
		String today = new SimpleDateFormat("yyyy-M-d").format(new Date());
		long formerPeriod = System.currentTimeMillis() / (period * 60 * 1000) * (period * 60 * 1000) - (period * 60 * 1000);
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd/H/m");
		File base = new File(path);
		System.out.println("Start scan history");
		for (File dayFile : base.listFiles()){
			if (dayFile.getAbsolutePath().contains(today)){
				for (File hourFile : dayFile.listFiles()){
					for (File periodFile : hourFile.listFiles()){
						try{
							String timePath = splitTimePath(periodFile.getAbsolutePath());
							if (formater.parse(timePath).getTime() <= formerPeriod && validateEmpty(periodFile)){
								updateFile(periodFile);
							}
						}catch(Exception e){
							continue;
						}
					}
				}
			}
		}
	}

	private static String splitTimePath(String name){
		String result = "";
		String[] paths = name.split("/");
		for (int i = paths.length - 3; i < paths.length; i++){
			result += paths[i];
			if (i != paths.length - 1) result += "/";
		}
		return result;
	}
	
	private static boolean validateEmpty(File dir){
		if (!dir.isDirectory()) return false;
		if (dir.list().length < 1) return false;
		return true;
	}
	
	private void scanHour(File f){
		if (f.listFiles() == null || !f.isDirectory()) return;
		for (File periodFile : f.listFiles()){
			if (periodFile.isDirectory() && periodFile.getAbsolutePath().contains(getLastDatePath())){
				updateFile(periodFile);
			}else{
				scanHour(periodFile);
			}
		}
	}
	
	private void updateFile(File f){
		for (File dir : f.listFiles()){
			//validateFiles(dir);
			if (dir.getName().endsWith("not_match")){
				ump.insertFileNotMatch(dir.getAbsolutePath());
			}else{
				ump.insertFile(dir.getAbsolutePath());
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void validateFiles(File dir){
		for (File f : dir.listFiles()){
		//	getFileTempName(f.getAbsolutePath());
      		f.renameTo(new File(getFileTempName(f.getAbsolutePath())));
		
		}
	}
	
	public static String getLastDatePath(){
		long timeStamp = System.currentTimeMillis() / (period * 60 * 1000) * (period * 60 * 1000) - (period * 60 * 1000);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d/H/mm");
		String str = sdf.format(new Date(timeStamp));
		return str;
	}
	
	public static String getFileTempName(String fileName){
		String[] names = fileName.split("\\.");
		String result = "";
		for (int i = 0; i < names.length - 1; i ++)
			result += names[i];
		result += ".ing";
		return result;
	}
	
	
	public static String getFileZipName(String fileName){
		String[] names = fileName.split("\\.");
		String result = "";
		for (int i = 0; i < names.length - 1; i ++)
			result += names[i];
		result += ".zip";
		return result;
	}
	
	public static String getScanTime(){
		StringBuilder sb = new StringBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sb.append("Last start time : ").append(sdf.format(new Date(lastExecute))).append("       ").append("Last scan end time : ").append(sdf.format(new Date(lastScanEnd))).append("\r\n");
		sb.append("Now time : ").append(sdf.format(new Date()));
		return sb.toString();
	}
	
	public static void main(String[] args) {
		String path = "aaa/2018-08-01/8/15";
		System.out.println(splitTimePath(path));
	}
}
