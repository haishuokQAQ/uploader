package com.yuantek.ftp;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yuantek.ConfigContainer;
import com.yuantek.DeleteThread;
import com.yuantek.FileScanner;
import com.yuantek.PathManager;
import com.yuantek.util.RuntimeStatus;

public class UpdateThread extends Thread{
	private BlockingQueue<File> uploadQueue = new ArrayBlockingQueue<>(10000);
	
	private FTPConnector ftpConn;
	private String host;
	private int port;
	private String userName;
	private String password;
	private int id;
	private String baseDir;
	
	private DeleteThread dt;
	
	private boolean connected = false;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public UpdateThread(String host, int port, String userName, String password, int id, boolean matched) {
		this.host = host;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.id = id;
		baseDir = ConfigContainer.notMatchDir;
		if (matched) {
			baseDir = ConfigContainer.outBaseDir;
		}
		connect();
		if (ConfigContainer.delete){
			dt = new DeleteThread();
			dt.start();
		}
	}
	
	
	boolean be = false;
	public UpdateThread(String host, int port, String userName, String password, int id, String path) {
		this.host = host;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.id = id;
		baseDir = path;
		be = true;
	}

	public void insert(File f){

		File dest = null;
		try{ 
			dest = f/* FileScanner.getTempFile(f)*/;
			f.renameTo(dest);
			uploadQueue.add(dest);
		}catch(Exception e){
			if (dest != null)
				dest.renameTo(FileScanner.getOriginalFile(f));
		}
	}
	
	
	private void connect(){
		ftpConn = new FTPConnector();
		connected = ftpConn.connect(host, port, userName, password);
		if (!connected) logger.error("Update thread " + id + " connect fail.");
		else logger.info("Upload thread {} connet to remote server success!", id);
	}
	
	
	@Override
	public void run() {
		File tempFile = null;
		while (true){
			if (!ftpConn.isConnected()){
				connect();
				continue;
			}
			try {
				tempFile = uploadQueue.take();
				if (!tempFile.exists()) continue;
				String path = baseDir + "/" + PathManager.getTimeBaseString();
				boolean updated = ftpConn.uploadFile(path, tempFile.getName(), tempFile);
				String tableName = splitTableName(tempFile.getAbsolutePath());
				RuntimeStatus.updateTableUpload(tableName, 1);
				if (dt != null && updated ){
					//if ((ConfigContainer.extraMode && be) || !ConfigContainer.extraMode) {
						dt.insert(tempFile);
					//}
 				}
			}catch(Exception e){
				logger.error("Fail to update file because {}", e.toString());
			//	e.printStackTrace();
//				System.out.println(e.toString());
			}
		}
	}
	
	private String splitTableName(String path){
		String[] paths = path.split("/");
		if (paths.length < 3) return "unknown";
		return paths[paths.length - 2];
	}
	
	
	public static void main(String[] args) {
		File f = new File("a.test");
		f.renameTo(new File("b.test"));
	}
}
