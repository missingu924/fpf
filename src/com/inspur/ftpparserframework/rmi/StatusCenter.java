package com.inspur.ftpparserframework.rmi;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.inspur.ftpparserframework.Main;
import com.inspur.ftpparserframework.util.TimeUtil;

/**
 * FPFʵ��״̬ע������
 * 
 * @author �����
 * 
 */
public class StatusCenter
{
	/**
	 * ���سɹ��ļ���
	 */
	private static long dlSuccFiles;
	/**
	 * ����ʧ���ļ���
	 */
	private static long dlErrorFiles;
	/**
	 * �����ɹ��ļ���
	 */
	private static long parseSuccFiles;
	/**
	 * ����ʧ���ļ���
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
