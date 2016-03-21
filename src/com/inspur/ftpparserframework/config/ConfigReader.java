/*
 * Copyright (c) 2003 - 2005 Langchao LG Information Systems Co.,Ltd.
 * All Rights Reserved.
 */
package com.inspur.ftpparserframework.config;


/**
 * ������Ϣ��ȡ��
 */
public class ConfigReader
{
	/**
	 * ����������Ϣ
	 */
	private synchronized static void loadProperties()
	{
		if (properties == null)
		{
			// Create a manager with the full path to the xml config file.
			try
			{
				// get the config
				String filepath = Thread.currentThread().getContextClassLoader().getResource(CONFIG_PATH).getPath();
				properties = new XMLProperties("file:///" + filepath);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * ��ȡSystemConfig.xml�����õĲ���ֵ��
	 * @param key ����ֵ��·����������ʽΪX.Y.Z����ʾȡ�ļ���<Config><X><Y><Z>value</Z></Y></X></Config>��ֵ��
	 * @return
	 */
	public static String getProperties(String key)
	{
		if (properties == null)
		{
			loadProperties();
		}
		return properties.getPorperty(key);
	}

	private static XMLProperties properties;
	private static final String CONFIG_PATH = "SystemConfig.xml";
}
