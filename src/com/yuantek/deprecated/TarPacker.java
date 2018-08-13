package com.yuantek.deprecated;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

/**
 * 每个tarPacker可以生成一个tar文件，用于进行对文件夹下的所有文件压缩
 * @author kong.haishuo
 *
 */
public class TarPacker {
	private ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
	private TarArchiveOutputStream tarArchive = new TarArchiveOutputStream(fileOut);

	public byte[] getOutZip(List<File> files) throws IOException{
		for (File f : files){
			TarArchiveEntry entry = new TarArchiveEntry(f.getName());
			tarArchive.putArchiveEntry(entry);
			
			tarArchive.closeArchiveEntry();
			tarArchive.flush();
		}
		return fileOut.toByteArray();
	}
	
	public static void main(String[] args) throws Exception{
		File f = new File("ziptest");
		List<File> fileList = new ArrayList<>();
		for (File temp: f.listFiles()){
			fileList.add(temp);
		}
		File out = new File("out.zip");
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(out));
		TarPacker zp = new TarPacker();
		bos.write(zp.getOutZip(fileList));
		bos.flush();
		bos.close();
	}
}
