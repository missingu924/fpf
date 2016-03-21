package com.inspur.ftpparserframework.extend;

import java.io.File;
import java.util.LinkedHashMap;
import org.apache.log4j.Logger;
public class DefaultFileParser
{
	private Logger log = Logger.getLogger(super.getClass());

	  public void parse(File file, LinkedHashMap<String, String> defaultColumnMap)
	    throws Exception
	  {
	    this.log.info(file.getCanonicalPath() + " 使用默认文件解析程序，线程sleep 5秒，其他不做任何处理。");

	    Thread.sleep(5000L);
	  }
}
