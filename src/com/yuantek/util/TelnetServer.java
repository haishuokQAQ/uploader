package com.yuantek.util;

import com.ampthon.telnetd.TelnetD;
import com.ampthon.telnetd.cmds.CommandTree;
import com.yuantek.ConfigContainer;
import com.yuantek.batchupdate.FileScannerNew;
import com.yuantek.batchupdate.UploadThread;

public class TelnetServer {
	public static void startTelnet(){
		CommandTree.registerCmd(null, "send", "", paras -> RuntimeStatus.getUpdateCount());
		if (ConfigContainer.mode == 0) {
			CommandTree.registerCmd(null, "thread", "", paras -> RuntimeStatus.getThreadStatus());
			CommandTree.registerCmd(null, "queue", "", paras -> RuntimeStatus.getQueueStatus());
			CommandTree.registerCmd(null, "scan", "", paras -> FileScannerNew.getScanTime());
			CommandTree.registerCmd(null, "now", "", paras -> UploadThread.getNowStatus());
		}
		TelnetD td = TelnetD.createTelnetD(7777);
		td.setPrompt("ftp->");
		td.start();
	}
}
