package com.yuantek.ftp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yuantek.Connector;

public class FTPConnector implements Connector {
	private FTPClient ftpClient;
	
	private boolean connected = false;
	
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public boolean connect(String host, int port, String userName, String password){
		ftpClient = new FTPClient();
		ftpClient.setControlEncoding("utf-8");
		try{
			ftpClient.connect(host, port);
			ftpClient.login(userName, password);
			//ftpClient.setDataTimeout(300000);
			int replyCode = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(replyCode)) return false;
			connected = true;
			return true;
		}catch(Exception e){
			logger.error("Fail to connect to remote server because {}.", e.toString());
			return false;
		}
	}

	public boolean uploadFile(String pathName, String fileName, File sourceFile){
		if (!connected){
			System.out.println("Not connect yet");
			return false;
		}
		BufferedInputStream bis = null;
		try{
			File sourceFileNew = new File(sourceFile.getAbsolutePath());
			if (sourceFileNew.length() == 0) {
				logger.error("Find file {} witch is empty", sourceFileNew.getAbsolutePath());
				return false;
			}
			bis = new BufferedInputStream(new FileInputStream(sourceFileNew));
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			createDictionary(pathName);
			ftpClient.changeWorkingDirectory(pathName);
			String fileTempName = getFileTempName(fileName);
			boolean sent = ftpClient.storeFile(fileTempName, bis);
			if (!sent) {
				logger.error("Fail send file {}.", fileName);
				ftpClient.deleteFile(fileTempName);
				return false;
			}else {
				boolean rename = ftpClient.rename(fileTempName, getFileZipName(fileTempName));
				if (rename) {
					if (existFile(getWrongFileName(getFileZipName(fileTempName)))) {
						logger.info("Find wrong fileName {}.", getWrongFileName(getFileZipName(fileTempName)));
						boolean renameCorrect = ftpClient.rename(getWrongFileName(getFileZipName(fileTempName)), getCorretFileName(getFileZipName(fileTempName)));
						logger.info("Try to correct filename {}, {}", fileTempName, renameCorrect);
					}
					logger.debug("Upload  {} success.", sourceFile.getName());
				}else {
					logger.error("Change name error. File is {}", sourceFile.getName());
				}
			}
			
			for (int i = 0; i < pathName.split("/").length; i++)
				ftpClient.changeToParentDirectory();
			FTPStatistic.addUpdate();
			return true;
		}catch(Exception e){
			logger.error("Fail to update file {}.Because {}.", sourceFile, e.toString());
			connected = false;
			return false;
		}
	}
	
	
	private void createDictionary(String path) throws IOException{
		String[] dirs = path.split("/");
		String tempPath = "";
		for (int i = 0; i < dirs.length ;i ++){
			tempPath += dirs[i] + "/";
			if (!existFile(tempPath)) makeDic(tempPath);
		}
		
	}
	
	public static String getCorretFileName(String fileName) {
		String result = "";
		String[] parts = fileName.split("\\.");
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].endsWith("-)")){
				parts[i] = parts[i].substring(0, parts[i].length() - 2);
			}
			result += parts[i];
			if (i < parts.length - 1) {
				result += ".";
			}
		}
		return result;
	}
	
	public static String getWrongFileName(String fileName) {
		String result = "";
		String[] parts = fileName.split("\\.");
		for (int i = 0; i < parts.length; i++) {
			if (i != parts.length - 1) {
				if (i > 0) {
					result += ".";
				}
				result += parts[i];
			}else {
				result += "-).";
				result += parts[i];
			}
		}
		return result;
	}
	
	private void makeDic(String path) throws IOException{
		ftpClient.makeDirectory(path);
	}
	
	private boolean existFile(String path) throws IOException{
		FTPFile[] ftpFileArr = ftpClient.listFiles(path);
		if (ftpFileArr.length > 0){
			return true;
		}
		return false;
	}
	
	public FTPClient getFtpClient() {
		return ftpClient;
	}
	
	public static String getFileTempName(String fileName){
		String[] names = fileName.split("\\.");
		String result = "";
		for (int i = 0; i < names.length - 1; i ++)
			result += names[i];
		result += ".ing";
		return result;
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public static String getFileZipName(String fileName){
		String[] names = fileName.split("\\.");
		String result = "";
		for (int i = 0; i < names.length - 1; i ++)
			result += names[i];
		result += ".zip";
		return result;
	}

	public static void main(String[] args) {
		String wrongName = getWrongFileName("123-21313-23123-22222.zip");
		System.out.println(getWrongFileName("123-21313-23123-22222.zip"));
		System.out.println(getCorretFileName(wrongName));
	}
}
