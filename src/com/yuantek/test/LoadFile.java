package com.yuantek.test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class LoadFile {
	
	private static byte[] data ;
	
	private static LoadFile instance = new LoadFile();
	
	private LoadFile() {
		try{
			File f = new File("144-610000-610000-1531453282-00058.zip");
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
			byte[] bbb = new byte[1024];
			ByteBuffer byteBuf = ByteBuffer.allocate(5 * 1024 * 1024);
			int remain = bis.read(bbb);
			while (remain != -1){
				byteBuf.put(bbb, 0, remain);
				remain = bis.read(bbb);
			}
			byteBuf.flip();
			System.out.println(byteBuf.limit());
			data = new byte[byteBuf.limit()];
			System.arraycopy(byteBuf.array(), 0, data, 0, byteBuf.limit());
			byteBuf.clear();
			bis.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static byte[] getData() {
		return data;
	}


	public static LoadFile getInstance() {
		return instance;
	}
	
	
	public static int getFileNum(long size){
		int dataSize = data.length;
		int num = (int) (size / dataSize);
		System.out.println(size + "/" + dataSize + "=" + num);
		return num;
	}

	public static void main(String[] args) throws IOException {
		getInstance();
		File out = new File("abc.zip");
		FileOutputStream fos = new FileOutputStream(out);
		fos.write(data);
		fos.close();
	}
}
