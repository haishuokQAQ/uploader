package com.yuantek.deprecated;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TarDir {
	private static Logger logger = LoggerFactory.getLogger(TarDir.class);
	
	/**
	 * 使用command进行打包
	 * @param dir
	 * @throws IOException
	 */
	public static File tarDirCommand(File dir) throws IOException{
		String tarFile = dir.getAbsolutePath() + "_test" + ".tar";
		File f = new File(tarFile);
		Process pro = Runtime.getRuntime().exec("tar -cf " + f.getAbsolutePath() + " -C " + dir.getAbsolutePath() + " .");
		boolean result = true;
		try {
			result = pro.waitFor(10, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
		}
		if (result)
			return new File(tarFile);
		else {
			//对打包失败进行记录
			logger.error("An error has been reported when taring dictionary {}.Error message is {}"
					, dir.getAbsoluteFile(), getErrMessage(pro.getErrorStream()));
			return null;
		}
	}
	
	/**
	 * 读取errorInputStream中的错误信息
	 * @param errInputStream
	 * @return
	 */
	private static String getErrMessage(InputStream errInputStream){
		int len=-1;
		byte bytes[]=new byte[1024];
		StringBuffer sb=new StringBuffer();
		try {
			while((len=errInputStream.read(bytes))!=-1){
				sb.append(new String(bytes,0,len));
			}
		} catch (IOException e){
			e.printStackTrace();
		}
		try {
			errInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
}
