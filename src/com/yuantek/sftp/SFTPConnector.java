package com.yuantek.sftp;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.yuantek.Connector;
import com.yuantek.ftp.FTPConnector;

public class SFTPConnector implements Connector{
	private JSch jsch = new JSch();
	private String ip;
	private String user;
	private String passwd;
	private int port;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private boolean inited = false;
	
	private void init(String ip, String user, String passwd, int port){
		this.ip = ip;
		this.user = user;
		this.passwd = passwd;
		this.port = port;
		inited = true;
	}
	
	
	public void connect(String ip, String user, String passwd, int port) throws Exception{
		if (!inited){
			init(ip, user, passwd, port);
		}
		Session session = null;
		if (port <=0 || port > 65535){
			session = jsch.getSession(user, ip);
		}else{
			session = jsch.getSession(user, ip, port);
		}
		if (session == null) {
			throw new Exception("session is null.");
		}
		setConfig(session);
		session.connect(1000);
	}
	
	private void setConfig(Session session){
		session.setPassword(passwd);
		session.setConfig("StrictHostKeyChecking", "no");
	}


	@Override
	public boolean connect(String host, int port, String userName, String password) {
		if (!inited){
			init(host, userName, password, port);
		}
		try{
			Session session = null;
			if (port <=0 || port > 65535){
				session = jsch.getSession(user, ip);
			}else{
				session = jsch.getSession(user, ip, port);
			}
			if (session == null) {
				return false;
			}
			setConfig(session);
			session.connect(1000);
		}catch(Exception e){
			StackTraceElement[] elements = e.getStackTrace();
			StringBuilder sb = new StringBuilder();
			for (StackTraceElement element : elements){
				sb.append(element.toString()).append("      ");
			}
			logger.error("Fail to connect to sftp server.Beacuse {}.Stack is {}",e.toString(),sb.toString());
			return false;
		}
		return true;
	}


	@Override
	public boolean uploadFile(String pathName, String fileName, File sourceFile) {
		Session session = null;
		try{
			session = getConnSession(1000);
			if (session == null){
				logger.error("Cannot get session while update {}", FTPConnector.getFileZipName(fileName));
				return false;
			}
		}catch(Exception e){
			
		}
		return true;
	}
	
	private Session getConnSession(int overtime) throws Exception{
		Session session = jsch.getSession(user, ip, port);
		session.setPassword(passwd);
		setConfig(session);
		return session;
	}
	
	
}
