/*
 * Copyright (c) 2003 - 2005 Langchao LG Information Systems Co.,Ltd.
 * All Rights Reserved.
 */
package com.inspur.ftpparserframework.config;


/**
 * 配置信息读取器
 */
public class ConfigReader
{
	/**
	 * 加载配置信息
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
	 * 获取SystemConfig.xml中配置的参数值。
	 * @param key 参数值的路径，描述方式为X.Y.Z，表示取文件中<Config><X><Y><Z>value</Z></Y></X></Config>的值。
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
