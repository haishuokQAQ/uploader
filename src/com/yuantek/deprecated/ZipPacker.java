package com.yuantek.deprecated;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 每个zipPacker可以生成一个zip文件，用于进行对文件夹下的所有文件压缩
 * @author kong.haishuo
 *
 */
public class ZipPacker {
	private ByteArrayOutputStream baos = new ByteArrayOutputStream();
	private ZipOutputStream zos = new ZipOutputStream(baos);
	
	public byte[] getOutZip(List<File> files) throws IOException{
		for (File f : files){
			RandomAccessFile aFile = new RandomAccessFile(f, "r");
			FileChannel channel = aFile.getChannel();
//			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f), 10 * 1024 * 1024);
			ByteBuffer buf = ByteBuffer.allocate(1024 * 8192);
			int byteRead = channel.read(buf);
			channel.read(buf);
			buf.flip();
			byte[] data = buf.array();
			System.out.println(data.length);
//			bis.read(data);
			zos.putNextEntry(new ZipEntry(f.getName()));
			zos.write(data);
			zos.closeEntry();
	//		bis.close();
			data = null;
		}
		return baos.toByteArray();
	}
	
	public static void main(String[] args) throws Exception{
		File f = new File("ziptest");
		List<File> fileList = new ArrayList<>();
		for (File temp: f.listFiles()){
			fileList.add(temp);
		}
		File out = new File("out.zip");
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(out));
		ZipPacker zp = new ZipPacker();
		bos.write(zp.getOutZip(fileList));
		bos.flush();
		bos.close();
	}
}
