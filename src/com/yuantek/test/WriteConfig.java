package com.yuantek.test;

import java.io.File;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class WriteConfig {
	public static Set<String> protocols = new HashSet<>();
	
	public static String basePath = "test";
	
	public static void initConfig(){
		File f = new File("TABLE_NAME");
		try{
			Scanner sc = new Scanner(f);
			while (sc.hasNextLine()){
				String line = sc.nextLine();
				String tableName = line.split(" ")[0];
				protocols.add(tableName);
			}
			sc.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
