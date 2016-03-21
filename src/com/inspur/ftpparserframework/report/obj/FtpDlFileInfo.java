package com.inspur.ftpparserframework.report.obj;

import java.util.Date;

import org.apache.log4j.Logger;

import com.inspur.ftpparserframework.util.StringUtil;
import com.inspur.ftpparserframework.util.TimeUtil;

public class FtpDlFileInfo extends BasicFileInfo
{
	private Logger log = Logger.getLogger(this.getClass());

	/**
	 * Ŀ���ļ�ȫ·��
	 */
	private String destFilePath;
	/**
	 * Ŀ���ļ����ȣ���λByte
	 */
	private long destFilelength;
	/**
	 * Ŀ���ļ�����޸�ʱ��
	 */
	private Date destFileLastModifiedTime;

	public String getDestFilePath()
	{
		return destFilePath;
	}

	public void setDestFilePath(String destFilePath)
	{
		this.destFilePath = destFilePath;
	}

	public long getDestFilelength()
	{
		return destFilelength;
	}

	public void setDestFilelength(long destFilelength)
	{
		this.destFilelength = destFilelength;
	}

	public Date getDestFileLastModifiedTime()
	{
		return destFileLastModifiedTime;
	}

	public void setDestFileLastModifiedTime(Date destFileLastModifiedTime)
	{
		this.destFileLastModifiedTime = destFileLastModifiedTime;
	}

	@Override
	protected String getThisCsvTitle()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Ŀ���ļ�·��").append(",")//
				.append("Ŀ���ļ���С").append(",")//
				.append("Ŀ���ļ�����޸�ʱ��").append(",")//
				.append("�ļ���С���(Byte)");
		return sb.toString();
	}

	@Override
	protected String this2Csv()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(this.destFilePath).append(",")//
				.append(this.destFilelength).append(",")//
				.append(TimeUtil.date2str(this.destFileLastModifiedTime)).append(",")//
				.append((destFilelength - super.getLength()));
		return sb.toString();
	}

}
