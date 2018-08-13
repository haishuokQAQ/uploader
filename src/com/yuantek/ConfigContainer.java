package com.yuantek;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ampthon.configcommon.XMLConfHolder;

public class ConfigContainer {
	//FTP鍦板潃
	public static String hostName = "192.168.2.228";
	
	public static String postName = "/data/tensoft/testzip";
	
	//FTP绔彛
	public static int port = 21;
	//FTP璐﹀彿
	public static String userName = "sa";
	//FTP瀵嗙爜
	public static String password = "111111";
	//杈撳嚭鐩綍
	public static String outBaseDir = "test";
	//鏈尮閰嶈緭鍑虹洰褰�
	public static String notMatchDir = "notmatchtest";
	//鎵弿鐩綍
	public static String scanDir = "lib";
	//鏂囦欢鏄惁鍒犻櫎
	public static boolean delete = false;
	//绾跨▼鏁�
	public static int thread = 1;
	
	public static int mode = 0;
	
	public static int overTime = 5000;
	
	public static boolean extraMode = false;
	
	private static Logger logger = LoggerFactory.getLogger(ConfigContainer.class);
	
	public static void initConfig(){
		try {
			XMLConfHolder holder = XMLConfHolder.createHolder("config.xml");
			hostName = holder.getString("ftp_host");
			port = holder.getInt("ftp_port");
			userName = holder.getString("ftp_username");
			password = holder.getString("ftp_password");
			outBaseDir = holder.getString("base_dir");
			scanDir = holder.getString("scan_dir");
			delete = holder.getBoolean("delete");
			thread = holder.getInt("thread");
			notMatchDir = holder.getString("not_match_dir");
			if (holder.getString("mode") != null) {
				mode = holder.getInt("mode");
			}
			if (holder.getString("scan_interval") != null) {
				overTime = holder.getInt("scan_interval") * 1000;
			}
			if (holder.getString("extra_mode") != null) {
				extraMode = holder.getBoolean("extra_mode");
			}
			if (holder.getString("postName") != null) {
				postName = holder.getString("postName");
			}
			logger.info("Connect string : {}\r\nuser : {}\r\npwd:{}\r\nremote path : {}",hostName + ":" + port, userName, password);
			logger.info("remote path : {}\r\nlocal path : {}\r\n", outBaseDir, scanDir);
		} catch (Exception e) {
			logger.error("Fail to init config because {}", e.toString());
		}
	}
}
