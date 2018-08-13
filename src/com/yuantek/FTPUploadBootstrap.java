package com.yuantek;

import com.yuantek.util.TelnetServer;

public class FTPUploadBootstrap {
	public static void main(String[] args) {
		ConfigContainer.initConfig();
		TelnetServer.startTelnet();
		if (ConfigContainer.mode == 0) {
			BatchStart.start();
		}
		else {
			Start.start();
		}
	}
}
