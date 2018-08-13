package com.yuantek.batchupdate;

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

import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.pro.ProFTPClient;
import com.yuantek.ftp.FTPConnector;

public class FTPConnectorPro {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//private ProFTPClient client = new ProFTPClient();
	private long update = 0;
	
	private FTPClient ftpClient;
    /**
     * This boolean is a signal for a connection weather first created.Once it is created at first time, it need to move working directory to remote path.
     * In this case ,a ftp connection has a short lifetime.
     * The connection will die when one path updated success.At the next time a new path will be uploaded, the connector will reconstruct a new ftp connection.
     */
    private boolean create = false;
	public boolean connect(String host, int port, String userName, String password){
		/*try {
			client.setRemoteHost(host);
			//client.setRemotePort(port);
			client.connect();
			client.login(userName, password);
			return true;
		} catch (FTPException | IOException e) {
			e.printStackTrace();
			return false;
		}*/
		ftpClient = new FTPClient();
		ftpClient.setControlEncoding("utf-8");
		try{
			ftpClient.connect(host, port);
			ftpClient.login(userName, password);
			int replyCode = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(replyCode)) return false;
			//connected = true;
			return true;
		}catch(Exception e){
			e.printStackTrace();
			//logger.error("Fail to connect to remote server because {}.", e.toString());
			return false;
		}
	}
	
	/**
	 * Update file with batch of one directory.
	 * @param dirPath
	 * @param destPath
	 * @return
	 */
	public boolean updateDir(String dirPath, String destPath){
		/*if (!client.connected()) return false;
		try{
			if (!client.existsDirectory(destPath)){
				client.mkdir(destPath);
			}
			createDirs(destPath, client);
			client.chdir(destPath);
			File dir = new File(dirPath);
			for (String file : dir.list()){
				client.put(dirPath + "/" + file, destPath);
			}
			client.mput(dirPath, file -> {
			return true;
			});	
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}finally{
		}*/
		
		File dir = new File (dirPath);
		for (File f : dir.listFiles()){
			updateFile(f, destPath);
		}
		return true;
	}

	/**
	 * Upload one single file to remote path.
	 * @param sourceFile
	 * @param destPath
	 */
	public void updateFile(File sourceFile, String destPath){
		BufferedInputStream bis = null;
		try{
			File sourceFileNew = new File(sourceFile.getAbsolutePath());
			if (sourceFileNew.length() == 0) {
				logger.error("Find file {} witch is empty", sourceFileNew.getAbsolutePath());
			}
			bis = new BufferedInputStream(new FileInputStream(sourceFileNew));
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			if (!create) {
				createDictionary(destPath);
				create = true;
			}
			String fileName = sourceFile.getName();
			if (!ftpClient.printWorkingDirectory().equals(destPath))
				ftpClient.changeWorkingDirectory(destPath);
			//byte[] byt = new byte[1024];
			//int length = bis.read(byt);
		//	ByteBuffer buffer = ByteBuffer.allocate(5 * 1024 * 1024);
			/*OutputStream os = ftpClient.storeFileStream(fileName);
			BufferedOutputStream bos = new BufferedOutputStream(os);
			
			while (length > 0) {
				bos.write(byt, 0, length);
				bos.flush();
//				buffer.put(byt, 0, length);
				length = bis.read(byt);
			}*/
			//boolean sent = ftpClient.storeUniqueFile(fileName, bis);
			boolean sent = ftpClient.storeFile(fileName, bis);
			if (!sent) {
				logger.error("Fail to up"
						+ "load file {} because cannot open stream.", sourceFile.getName());
			}
			 System.out.println("temp file " + fileName + " existance : " + existFile(fileName));
			//System.out.println("remote File name " + sourceFile.getName());
			 boolean nameChange = ftpClient.rename(fileName, getFileZipName(fileName));
			 System.out.println("temp file " + getFileZipName(fileName) + " existance : " + existFile(getFileZipName(fileName) ));
			//System.out.println("remote File rename " + getFileZipName(fileName) + " change " + nameChange);
			if (nameChange) {
				logger.debug("Upload  {} success.", sourceFile.getName());
			}
			update ++;
			 bis.close();
//			bos.close();
			/*for (int i = 0; i < destPath.split("/").length; i++)
				ftpClient.changeToParentDirectory();*/
		}catch(Exception e){
			if (e instanceof NullPointerException ) {
				logger.error("Fail to upload file {} because cannot open stream.", sourceFile.getName());
			}
			logger.error("Fail to upload file {} because {}.", sourceFile.getName(), e.toString());
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
	
	/*public void changeName(String dir) throws IOException, FTPException{
		String[] names = client.dir();
		for (String name : names){
			System.out.println(name);
		}
	}*/
	
	public void close() {
		try{
			ftpClient.quit();
			ftpClient.disconnect();
			create = false;
			update = 0;
		}catch(Exception e){
			create = true;
			logger.error("Cannot disconnect because {}", e.toString());
		}
	}
	
	public void changeIngtoZip(String dirPath, String[] names) throws IOException, FTPException{
		for (String name : names){
			//client.rename(dirPath + "/" + name, getFileZipName(dirPath + "/" + name));
		}
/*		for (String file : client.dir(dirPath)){
			if (file.endsWith(".zip")){
				client.rename(file, getFileZipName(file));
				//System.out.println(FTPConnector.getFileTempName(file));
			}
		};*/
	}
	
	private void createDirs(String dirName, ProFTPClient client) throws IOException, FTPException{
		String path = "";
		for (String currentPath : dirName.split("/")){
			path += currentPath + "/";
			if (!client.existsDirectory(path)){
				client.mkdir(path);
			}
		}
	}
	
	public static String getFileZipName(String fileName){
		String[] names = fileName.split("\\.");
		String result = "";
		for (int i = 0; i < names.length - 1; i ++)
			result += names[i];
		result += ".zip";
		return result;
	}
	
	public long getUpdate() {
		return update;
	}

	public static void main(String[] args) throws IOException, FTPException {
		/*ConfigContainer.initConfig();
		FTPConnectorPro connector = new FTPConnectorPro();
		connector.connect(ConfigContainer.hostName, ConfigContainer.port, ConfigContainer.userName, ConfigContainer.password);
		connector.updateDir(args[0], "/data/tensoft/icmzip");
		connector.close();*/
		/*File f = new File("144-610000-610000-1531453282-00058.zip");
		System.out.println(f.length());*/
		//首先传一个目录的文件
		FTPConnector connector = new FTPConnector();
		connector.connect("176.2.7.1", 21, "ftpuser", "111111");
		for (int i = 0; i < 2; i++) {
			new Thread(()->{
				File dir = new File(args[0]);
				for (File singleFile : dir.listFiles()) {
						connector.uploadFile("test", FTPConnector.getFileTempName(singleFile.getName()), singleFile);
				}
				
			}).start();; 
		}
	}
}
