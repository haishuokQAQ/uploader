package com.yuantek.util;

import com.ampthon.telnetd.TelnetD;
import com.ampthon.telnetd.cmds.CommandTree;
import com.yuantek.ConfigContainer;
import com.yuantek.batchupdate.FileScannerNew;
import com.yuantek.batchupdate.UploadThread;
import com.yuantek.ftp.FTPStatistic;

public class TelnetServer {
	public static void startTelnet(){
		CommandTree.registerCmd(null, "send", "", paras -> RuntimeStatus.getUpdateCount());
		CommandTree.registerCmd(null, "thread", "", paras -> RuntimeStatus.getThreadStatus());
		if (ConfigContainer.mode == 0) {
			CommandTree.registerCmd(null, "queue", "", paras -> RuntimeStatus.getQueueStatus());
			CommandTree.registerCmd(null, "scan", "", paras -> FileScannerNew.getScanTime());
			CommandTree.registerCmd(null, "now", "", paras -> UploadThread.getNowStatus());
		}else {
			CommandTree.registerCmd(null, "detail", "", paras -> FTPStatistic.getUpdateStatus());
		}
		TelnetD td = TelnetD.createTelnetD(7777);
		td.setPrompt("ftp->");
		td.start();
	}
}
