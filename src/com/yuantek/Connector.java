package com.yuantek;

import java.io.File;

public interface Connector {
	public boolean connect(String host, int port, String userName, String password);
	public boolean uploadFile(String pathName, String fileName, File sourceFile);
}
