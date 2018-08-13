package com.yuantek.deprecated;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class ReadFile {
	public static void main(String[] args) throws Exception {
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File("ziptest\\workspace.rar")));
		File f = new File("outTest.rar");
		FileOutputStream sops = new FileOutputStream(f);
		int out = 0;
		int out2 = 0;
		while(bis.read() > 0){
			out += bis.read();
			byte[] data = new byte[bis.read()];
			out2 += data.length;
			bis.read(data);
			sops.write(data);
			sops.flush();
		}
		System.out.println(out + " " + out2);
		bis.close();
		sops.close();
		
	}
}
