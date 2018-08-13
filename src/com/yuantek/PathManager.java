package com.yuantek;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PathManager {
	public static String getTimeBaseString(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
		return sdf.format(new Date());
	}
}
