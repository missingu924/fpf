package com.inspur.ftpparserframework.util;

import java.io.*;
import java.net.*;

import org.apache.commons.digester.*;
import org.apache.commons.digester.xmlrules.*;
import org.apache.log4j.*;
import org.xml.sax.*;

/**
 * <p>
 * Title: DigesterHelper
 * </p>
 * 
 * <p>
 * Description: Digester的Helper程序，封装了Digester的操作
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * <p>
 * Company: Langchao LG Information Systems Co.,Ltd
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class DigesterHelper
{
	/**
	 * 根据规则将xml inputstream映射为对象
	 * 
	 * @param xmlRuleFile
	 *            String
	 * @param is
	 *            InputStream
	 * @return Object
	 * @throws SAXException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static Object parseFromInputStream(URL xmlRuleFile, InputStream is) throws SAXException,
			MalformedURLException, IOException
	{
//		Level level = Logger.getRootLogger().getLevel();
		try
		{
			//关闭rootLogger，防止digester的调试信息输出，xml解析结束后rootLogger的级别应该恢复到此前的状态
//			Logger.getRootLogger().setLevel(Level.OFF);
			//根据xml设置的映射规则创建Digester
			Digester dis = DigesterLoader.createDigester(xmlRuleFile);
			//保证web环境里面正常使用
			dis.setUseContextClassLoader(true);
			//解析xml
			return dis.parse(is);
		} finally
		{
//			Logger.getRootLogger().setLevel(level);
			if (is != null)
			{
				is.close();
			}
		}
	}

	/**
	 * 根据规则将xml file映射为对象
	 * 
	 * @param destObjClz
	 *            Class 解析得到的对象的类名，由此类名得到其解析规则对应的配置文件。
	 *            配置文件与该类在同一级目录下，名字为类名+Rule.xml。
	 * @param is
	 *            InputStream
	 * @return Object
	 * @throws Exception
	 */
	public static Object parseFromXmlFile(Class destObjClz, InputStream is) throws Exception
	{
		String className = destObjClz.getName();
		String xmlRulePath = className.replace('.', '/') + "Rule.xml";
		URL xmlRuleUrl = Thread.currentThread().getContextClassLoader().getResource(xmlRulePath);
		if (xmlRuleUrl == null)
		{
			throw new Exception("用于解析" + className + "的配置文件" + xmlRulePath + "在ClassPath里面不存在,请检查.");
		}
		return parseFromInputStream(xmlRuleUrl, is);
	}

	/**
	 * 根据规则将xml String映射为对象
	 * 
	 * @param xmlRuleFile
	 *            URL
	 * @param xmlString
	 *            String
	 * @return Object
	 * @throws Exception
	 */
	public static Object parseFromXmlString(URL xmlRuleFile, String xmlString) throws Exception
	{
		return parseFromInputStream(xmlRuleFile, new ByteArrayInputStream(xmlString.getBytes()));
	}

	/**
	 * 根据规则将xml String映射为对象
	 * 
	 * @param destObjClz
	 *            Class 解析得到的对象的类名，由此类名得到其解析规则对应的配置文件。
	 *            配置文件与该类在同一级目录下，名字为类名+Rule.xml。
	 * @param xmlString
	 *            String
	 * @return Object
	 * @throws Exception
	 */
	public static Object parseFromXmlString(Class destObjClz, String xmlString) throws Exception
	{
		String className = destObjClz.getName();
		String xmlRulePath = className.replace('.', '/') + "Rule.xml";
		URL xmlRuleUrl = Thread.currentThread().getContextClassLoader().getResource(xmlRulePath);
		if (xmlRuleUrl == null)
		{
			throw new Exception("用于解析" + className + "的配置文件" + xmlRulePath + "在ClassPath里面不存在,请检查.");
		}
		return parseFromInputStream(xmlRuleUrl, new ByteArrayInputStream(xmlString.getBytes()));
	}

	/**
	 * 根据规则将xml file映射为对象
	 * 
	 * @param xmlRuleFile
	 *            URL
	 * @param xmlFile
	 *            String
	 * @return Object
	 * @throws Exception
	 */
	public static Object parseFromXmlFile(URL xmlRuleFile, String xmlFile) throws Exception
	{
		return parseFromInputStream(xmlRuleFile, new FileInputStream(xmlFile));
	}

	/**
	 * 根据规则将xml file映射为对象
	 * 
	 * @param destObjClz
	 *            Class 解析得到的对象的类名，由此类名得到其解析规则对应的配置文件。
	 *            配置文件与该类在同一级目录下，名字为类名+Rule.xml。
	 * @param xmlFile
	 *            String
	 * @return Object
	 * @throws Exception
	 */
	public static Object parseFromXmlFile(Class destObjClz, String xmlFile) throws Exception
	{
		String className = destObjClz.getName();
		String xmlRulePath = className.replace('.', '/') + "Rule.xml";
		URL xmlRuleUrl = Thread.currentThread().getContextClassLoader().getResource(xmlRulePath);
		if (xmlRuleUrl == null)
		{
			throw new Exception("用于解析" + className + "的配置文件" + xmlRulePath + "在ClassPath里面不存在,请检查.");
		}
		return parseFromInputStream(xmlRuleUrl, new FileInputStream(xmlFile));
	}

	/**
	 * 根据规则将xml file映射为对象
	 * 
	 * @param destObjClz
	 *            Class 解析得到的对象的类名，由此类名得到其解析规则对应的配置文件和参数配置文件。
	 *            解析规则配置文件与该类在同一级目录下，名字为类名+Rule.xml， 参数配置文件与该类在同一级目录下，名字为类名+.xml
	 * @return Object
	 * @throws Exception
	 */
	public static Object parseByClzz(Class destObjClz) throws Exception
	{
		String className = destObjClz.getName();
		String xmlRulePath = className.replace('.', '/') + "Rule.xml";
		URL xmlRuleUrl = Thread.currentThread().getContextClassLoader().getResource(xmlRulePath);
		if (xmlRuleUrl == null)
		{
			throw new Exception("用于解析" + className + "的配置文件" + xmlRulePath + "在ClassPath里面不存在,请检查.");
		}
		String xmlFilePath = className.replace('.', '/') + ".xml";
		URL xmlFileUrl = Thread.currentThread().getContextClassLoader().getResource(xmlFilePath);
		if (xmlRuleUrl == null)
		{
			throw new Exception(className + "的参数配置文件在ClassPath里面不存在,请检查.");
		}
		return parseFromInputStream(xmlRuleUrl, xmlFileUrl.openStream());
	}

}
