package com.inspur.ftpparserframework.rmi;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.inspur.ftpparserframework.Main;
import com.inspur.ftpparserframework.util.TimeUtil;

/**
 * FPF状态信息对象
 * @author 武玉刚
 *
 */
public class StatusObj implements Serializable
{
	public StatusObj()
	{
		this.setArgParamMap(Main.argParamMap);
		this.setFtpFileLastModifyTime(Main.ftpFileLastModifyTime);
		this.setFtpScanInterval(Main.ftpScanInterval);
		this.setFtpTimeOut(Main.ftpTimeOut);
		this.setParserScanInterval(Main.parserScanInterval);
		this.setParserThreadpoolSize(Main.parserThreadpoolSize);
		this.setStartTime(Main.startTime);
		this.setFtp(Main.ftp);
		this.setParse(Main.parse);
		this.setDlSuccFiles(StatusCenter.getDlSuccFiles());
		this.setDlErrorFiles(StatusCenter.getDlErrorFiles());
		this.setParseSuccFiles(StatusCenter.getParseSuccFiles());
		this.setParseErrorFiles(StatusCenter.getParseErrorFiles());
	}

	/**
	 * 实例名
	 */
	private String name;
	/**
	 * 实例启动时间
	 */
	private Date startTime;
	/**
	 * 是否启动FTP文件采集(y 是|n 否，默认为n否)。
	 */
	private String ftp;
	/**
	 * 是否启动文件解析(y 是|n 否，默认为n否)
	 */
	private String parse;
	/**
	 * 实例通过命令和行传递进来的参数列表
	 */
	private Map<String, Object> argParamMap;
	/**
	 * ftp采集轮询间隔时间,单位秒
	 */
	private long ftpScanInterval;
	/**
	 * ftp采集最近多长时间内生成的文件,单位秒
	 */
	private long ftpFileLastModifyTime;
	/**
	 * ftp采集线程超时时间,单位秒
	 */
	private long ftpTimeOut;
	/**
	 * 解析线程池最大线程数
	 */
	private int parserThreadpoolSize;
	/**
	 * 文件解析轮询扫描间隔时间,单位秒
	 */
	private long parserScanInterval;
	/**
	 * 原始文件保存天数
	 */
	private long parserFileSavedDays;
	/**
	 * 下载成功文件数
	 */
	private long dlSuccFiles;
	/**
	 * 下载失败文件数
	 */
	private long dlErrorFiles;
	/**
	 * 解析成功文件数
	 */
	private long parseSuccFiles;
	/**
	 * 解析失败文件数
	 */
	private long parseErrorFiles;
	
	public long getDlSuccFiles()
	{
		return dlSuccFiles;
	}

	public void setDlSuccFiles(long dlSuccFiles)
	{
		this.dlSuccFiles = dlSuccFiles;
	}

	public long getDlErrorFiles()
	{
		return dlErrorFiles;
	}

	public void setDlErrorFiles(long dlErrorFiles)
	{
		this.dlErrorFiles = dlErrorFiles;
	}

	public long getParseSuccFiles()
	{
		return parseSuccFiles;
	}

	public void setParseSuccFiles(long parseSuccFiles)
	{
		this.parseSuccFiles = parseSuccFiles;
	}

	public long getParseErrorFiles()
	{
		return parseErrorFiles;
	}

	public void setParseErrorFiles(long parseErrorFiles)
	{
		this.parseErrorFiles = parseErrorFiles;
	}

	public Map<String, Object> getArgParamMap()
	{
		return argParamMap;
	}

	public void setArgParamMap(Map<String, Object> argParamMap)
	{
		this.argParamMap = argParamMap;
	}

	public Date getStartTime()
	{
		return startTime;
	}

	public void setStartTime(Date startTime)
	{
		this.startTime = startTime;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public long getFtpScanInterval()
	{
		return ftpScanInterval;
	}

	public void setFtpScanInterval(long ftpScanInterval)
	{
		this.ftpScanInterval = ftpScanInterval;
	}

	public long getFtpFileLastModifyTime()
	{
		return ftpFileLastModifyTime;
	}

	public void setFtpFileLastModifyTime(long ftpFileLastModifyTime)
	{
		this.ftpFileLastModifyTime = ftpFileLastModifyTime;
	}

	public long getFtpTimeOut()
	{
		return ftpTimeOut;
	}

	public void setFtpTimeOut(long ftpTimeOut)
	{
		this.ftpTimeOut = ftpTimeOut;
	}

	public int getParserThreadpoolSize()
	{
		return parserThreadpoolSize;
	}

	public void setParserThreadpoolSize(int parserThreadpoolSize)
	{
		this.parserThreadpoolSize = parserThreadpoolSize;
	}

	public long getParserScanInterval()
	{
		return parserScanInterval;
	}

	public void setParserScanInterval(long parserScanInterval)
	{
		this.parserScanInterval = parserScanInterval;
	}

	public long getParserFileSavedDays()
	{
		return parserFileSavedDays;
	}

	public void setParserFileSavedDays(long parserFileSavedDays)
	{
		this.parserFileSavedDays = parserFileSavedDays;
	}

	public String getFtp()
	{
		return ftp;
	}

	public void setFtp(String ftp)
	{
		this.ftp = ftp;
	}

	public String getParse()
	{
		return parse;
	}

	public void setParse(String parse)
	{
		this.parse = parse;
	}

	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("\n=========================================================================\n");
		sb.append("#实例名#:" + this.name + "\n");
		sb.append("启动时间:" + TimeUtil.date2str(this.startTime) + "         已运行时长:"
				+ TimeUtil.timeInerval2str(new Date().getTime() - startTime.getTime()) + "\n");
		sb.append("\n");
		sb.append("\n");
		sb.append("#FTP下载情况#\n");
		sb.append("是否启动下载:" + this.ftp+"\n");
		sb.append("下载成功文件数:" + this.dlSuccFiles + "个               下载失败文件数:" + this.dlErrorFiles + "个\n");
		sb.append("采集轮询间隔时间:             " + this.ftpScanInterval + "秒\n");
		sb.append("采集最近多长时间内生成的文件: " + this.ftpFileLastModifyTime + "秒\n");
		sb.append("采集线程超时时间:             " + this.ftpTimeOut + "秒\n");
		sb.append("\n");
		sb.append("\n");
		sb.append("#文件解析情况#\n");
		sb.append("是否启动解析:" + this.parse + "\n");
		sb.append("解析成功文件数:" + this.parseSuccFiles + "个               解析失败文件数:" + this.parseErrorFiles + "个\n");
		sb.append("解析线程池最大线程数: " + this.parserThreadpoolSize + "个\n");
		sb.append("文件解析轮询扫描间隔时间:" + this.parserScanInterval + "秒\n");
		sb.append("=========================================================================\n");

		return sb.toString();
	}
}
