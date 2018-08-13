package com.yuantek.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileWriter {
	private static FileWriter instance = new FileWriter();
	
	public FileWriter() {
	}

	public static FileWriter getInstance() {
		return instance;
	}
	
	public void writePeriod() throws IOException{
		long num = 17895424;
		System.out.println(num);
		int totalNum = LoadFile.getFileNum(num);
		System.out.println("Total file num : " + totalNum);
		int eachNum = totalNum / WriteConfig.protocols.size() / 2;
		if (eachNum == 0){
			eachNum = 2;
		}
		int max = totalNum;
		System.out.println("Each dir file num : " + eachNum);
		String baseDir = WriteConfig.basePath + "/" + getDatePath();
		for (String protocol : WriteConfig.protocols){
			if (max <= 0) break;
			String matchDir = baseDir + "/" + protocol;
			String notMatchDir = baseDir + "/" + protocol + "_not_match";
			writeFile(eachNum, matchDir);
			max -= eachNum;
			writeFile(eachNum, notMatchDir);
			max -= eachNum;
		}
	}
	
	private void writeFile(int num, String dir) throws IOException{
		File parent = new File(dir);
		if (!parent.exists()){
			parent.mkdirs();
		}
		for (int i = 0; i < num; i++){
			File f = new File(dir + "/" + System.nanoTime() + "_" + i + ".zip");
			FileOutputStream fow = new FileOutputStream(f);
			fow.write(LoadFile.getData());
			fow.flush();
			fow.close();
		}
	}
	public static String getDatePath(){
		long timeStamp = System.currentTimeMillis() / (5 * 60 * 1000) * (5 * 60 * 1000);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d/H/mm");
		String str = sdf.format(new Date(timeStamp));
		return str;
	}
}
