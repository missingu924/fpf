package com.inspur.ftpparserframework.util;

import java.io.*;
import java.util.Date;

import org.apache.log4j.Logger;

import com.inspur.ftpparserframework.config.ConfigReader;

/**
 * �ļ���������
 * 
 * @author �����
 * 
 */
public class FileUtil
{
	private static Logger log = Logger.getLogger(FileUtil.class);

	/**
	 * �����ļ�����ȡ��ʱ�ļ� �ļ�������ʽΪ����ļ��ĸ�Ŀ¼xml/INSPUR_GSM_CM_MOTOROLA_omcsp1_20090512.xml
	 * 
	 * @param fileName
	 *            String
	 * @return File
	 */
	// public static File getXmlFile(DBInfoObj dbinfo, String fileName)
	// {
	// String fileHomeDir = Table2FileConfigObj.getFileHomeDir();
	// String city = dbinfo.getCity();
	//
	// String filePath = fileHomeDir + "/" + (StringUtil.isEmpty(city) ? "" : city + "/")
	// + getFullFileName(dbinfo.getId(), fileName);
	// File xmlFile = new File(filePath);
	// if (!xmlFile.getParentFile().exists())
	// {
	// xmlFile.getParentFile().mkdirs();
	// }
	// return xmlFile;
	// }
	/**
	 * ɾ��dirĿ¼��daysǰ���ļ�
	 * 
	 * @param dir
	 * @param days
	 * @throws IOException
	 */
	public synchronized static void deleteFilesNdaysAgo(File dir, int days) throws IOException
	{
		// if (dir.exists())
		// {
		// File[] files = dir.listFiles();
		// if (files == null)
		// {
		// return;
		// }
		// for (int j = 0; j < files.length; j++)
		// {
		// File file = files[j];
		// if (file.isFile() && ((System.currentTimeMillis() - file.lastModified()) > days * 24 * 3600 * 1000l))
		// {
		// boolean rst = file.delete();
		// log.info("�ļ������洢ʱ��,ɾ��[" + (rst ? "�ɹ�" : "ʧ��") + "]:" + file.getCanonicalPath());
		// }
		// }
		// }

		// modify by wuyg 2013-1-21,�ļ�ɾ�������ɵ������߳�ִ��,��ռ�����߳�ʱ��

		FileDeleteThread t = new FileDeleteThread(dir, days);
		t.start();
	}

	public synchronized static boolean checkDirExistsAndMkDir(File dir)
	{
		if (dir == null)
		{
			return false;
		}
		if (!dir.exists())
		{
			checkDirExistsAndMkDir(dir.getParentFile());
			dir.mkdir();
		}
		if (dir.exists() && dir.isDirectory())
		{
			return true;
		} else
		{
			return false;
		}

	}

	public static void copy(File fileFrom, File fileTo) throws Exception
	{
		FileInputStream in = null;
		FileOutputStream out = null;
		try
		{

			if (!fileTo.getParentFile().exists())
			{
				fileTo.getParentFile().mkdirs();
			}
			if (!fileTo.exists())
			{
				fileTo.createNewFile();
			}

			in = new FileInputStream(fileFrom);
			out = new FileOutputStream(fileTo);
			byte[] bt = new byte[1024];
			int count;
			while ((count = in.read(bt)) > 0)
			{
				out.write(bt, 0, count);
			}

		} finally
		{
			if (in != null)
			{
				in.close();
			}
			if (out != null)
			{
				out.close();
			}
		}
	}

	/*
	 * ����xml�ļ���һ�ж�ȡ�ļ����롣����<?xml version="1.0" encoding="UTF-8"?>������Ϊ������ʽΪUTF-8
	 * 
	 */
	public static String getXmlFileEncode(File file) throws IOException
	{
		String encode = "UTF-8";
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			if (line != null)
			{
				line = line.trim();
				String keyStr = "encoding=\"";
				if (line.startsWith("<?xml") && line.contains(keyStr))
				{
					int fromIndex = line.indexOf(keyStr) + keyStr.length();
					encode = line.substring(fromIndex, line.indexOf("\"", fromIndex));
				}
			}

		} catch (Exception e)
		{
			try
			{
				throw e;
			} catch (Exception e1)
			{
				e1.printStackTrace();
			}
		} finally
		{
			if (reader != null)
			{
				reader.close();
			}
		}
		return encode;

	}

}
