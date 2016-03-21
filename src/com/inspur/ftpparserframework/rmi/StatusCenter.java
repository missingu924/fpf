package com.inspur.ftpparserframework.rmi;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.inspur.ftpparserframework.Main;
import com.inspur.ftpparserframework.util.TimeUtil;

/**
 * FPF实例状态注册中心
 * 
 * @author 武玉刚
 * 
 */
public class StatusCenter
{
	/**
	 * 下载成功文件数
	 */
	private static long dlSuccFiles;
	/**
	 * 下载失败文件数
	 */
	private static long dlErrorFiles;
	/**
	 * 解析成功文件数
	 */
	private static long parseSuccFiles;
	/**
	 * 解析失败文件数
	 */
	private static long parseErrorFiles;

	public static long getDlSuccFiles()
	{
		return dlSuccFiles;
	}

	public synchronized static void oneFileDlSucc()
	{
		dlSuccFiles = dlSuccFiles + 1;
	}

	public static long getDlErrorFiles()
	{
		return dlErrorFiles;
	}

	public synchronized static void oneFileDlError()
	{
		dlErrorFiles = dlErrorFiles + 1;
	}

	public static long getParseSuccFiles()
	{
		return parseSuccFiles;
	}

	public synchronized static void oneFileParseSucc()
	{
		parseSuccFiles = parseSuccFiles + 1;
	}

	public static long getParseErrorFiles()
	{
		return parseErrorFiles;
	}

	public synchronized static void oneFileParseError()
	{
		parseErrorFiles = parseErrorFiles + 1;
	}

}
