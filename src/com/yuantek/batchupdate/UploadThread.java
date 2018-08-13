package com.yuantek.batchupdate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yuantek.ConfigContainer;
import com.yuantek.PathManager;
import com.yuantek.util.RuntimeStatus;

/**
 * 针对目录的上传线程，从共享的目录队列取数据后使用FTPConnectorPro进行mput操作
 * @author kong.haishuo
 *
 */
public class UploadThread extends Thread{
	private static List<UploadThread> uts = new ArrayList<>();
	
	private BlockingQueue<String> dirQueue = null;
	
	private FTPConnectorPro connector = new FTPConnectorPro();
	
	private String baseDir = null;
	
	private boolean connected = false;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static Map<String, AtomicLong> updateFiles = new ConcurrentHashMap<>();
	
	private static AtomicLong timeUse = new AtomicLong();
	
	private static AtomicLong totalCount = new AtomicLong();
	
	private static AtomicLong failCount = new AtomicLong();
	
	private String operating = "";
	
	public UploadThread(BlockingQueue<String> dirQueue, String baseDir) {
		this.dirQueue = dirQueue;
		this.baseDir = baseDir;
		connect();
		uts.add(this);
	}

	public void connect(){
		connected = connector.connect(ConfigContainer.hostName, ConfigContainer.port, ConfigContainer.userName, ConfigContainer.password);
	}

	@Override
	public void run() {
		String path = null;
		ResultBean rb = null;
		while(true){
			try{
				waitForConnect();
				path = dirQueue.take();
				operating = path;
				rb = new ResultBean();
				rb.setPath(path);
				long start = System.currentTimeMillis();
				if (path == null) {
					rb.setSuccess(true);
					insertRb(rb);
					operating = "";
					rb = null;
					path = null;
					continue;
				}
				File pathFile = new File(path);
				if (!pathFile.exists()) {
					rb.setSuccess(true);
					insertRb(rb);
					operating = "";
					rb = null;
					path = null;
					continue;
				}
				validateFiles(path);
				String destPath = baseDir +"/" + PathManager.getTimeBaseString();
				logger.info("Upload : {} remoteDir : {}/{}", path, baseDir, PathManager.getTimeBaseString());
				connector.updateDir(path, destPath);
				long timeUse = System.currentTimeMillis() - start;
				UploadThread.timeUse.addAndGet(timeUse);
				logger.info("Upload {} compelete.Use {} second(s)", path, (System.currentTimeMillis() - start) / 1000);
				File dir = new File(path);
				connector.changeIngtoZip(destPath, dir.list());
				updateUpdateFiles(path);
				rb.setSuccess(true);
				connector.close();
				connected = false;
				operating = "";
			}catch(Exception e){
				/*if (path != null) {
					recoverFiles(path);
				}*/
				if (rb != null){
					rb.setSuccess(false);
				}
				
				failCount.incrementAndGet();
				logger.error("Update dir {} error because {}", path == null?"":path, e.toString());
				e.printStackTrace();
				connector.close();
				connected = false;
			}finally{
				insertRb(rb);
				rb = null;
			}
		}
	}
	
	
	private void insertRb(ResultBean rb) {
		if (rb != null) {
			AfterUpload.insert(rb);
		}
	}
	
	private void validateFiles(String dirPath){
		File dir = new File(dirPath);
		for (File f : dir.listFiles()){
		//	getFileTempName(f.getAbsolutePath());
      		f.renameTo(new File(getFileTempName(f.getAbsolutePath())));
		
		}
	}
	
	private void recoverFiles(String dirPath){
		File dir = new File(dirPath);
		for (File f : dir.listFiles()){
			f.renameTo(new File(getFileZipName(f.getAbsolutePath())));
			
			
		}
		
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
	
	private void updateFail(String path){
		
	}
	
	private void updateUpdateFiles(String path) {
		File dir = new File(path);
		File[] list = dir.listFiles();
		String[] paths = path.split("/");
		String tableName = paths[paths.length - 1];
		int nums = list.length;
		totalCount.addAndGet(nums);
		RuntimeStatus.updateTableUpload(tableName, nums);
		/*for (File currentFile : list){
			Files.delete(currentFile.toPath());
		}
		Files.delete(dir.toPath());*/
		if (!updateFiles.containsKey(baseDir)){
			updateFiles.put(baseDir, new AtomicLong());
		}
		updateFiles.get(baseDir).addAndGet(nums);
	}
	
	
	public static String getUploadNum(){
		StringBuilder sb = new StringBuilder();
		for (Entry<String, AtomicLong> entry : updateFiles.entrySet()){
			sb.append(entry.getKey()).append(" ").append(entry.getValue().get()).append("\r\n");
		}
		long totalUse = timeUse.get();
		long count = totalCount.get();
		sb.append("Avg speed : ").append(count * 1000 / totalUse).append("\r\n");
		
		
		return sb.toString();
	}
	
	
	public static String getNowStatus() {
		StringBuilder sb = new StringBuilder();
		sb.append("Now Status \r\n");
		int pos = 1;
		for (UploadThread ut : uts) {
			sb.append("Thread ").append(pos).append(" now updating ").append(ut.operating).append("  upload num ").append(ut.connector.getUpdate()).append("\r\n");
			pos ++;
		}
		return sb.toString();
	}
	
	private void waitForConnect(){
		while (!connected){
			connect();
		}
	}
	
	public static void main(String[] args) {
		File dir = new File("lib");
		for (String file : dir.list())
			System.out.println(file);
	}
}
