package com.inspur.ftpparserframework.log.obj;

import java.io.File;
import java.util.Date;

import com.inspur.ftpparserframework.util.StringUtil;
import com.inspur.ftpparserframework.util.TimeUtil;

public class BaseFile implements Comparable<BaseFile>
{
	/**
	 * �ļ�ȫ·��
	 */
	private String path;

	/**
	 * �ļ����ȣ���λByte
	 */
	private long length;

	/**
	 * �ļ�����޸�ʱ��
	 */
	private Date lastModifiedTime;

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	public long getLength()
	{
		return length;
	}

	public void setLength(long length)
	{
		this.length = length;
	}

	public Date getLastModifiedTime()
	{
		return lastModifiedTime;
	}

	public void setLastModifiedTime(Date lastModifiedTime)
	{
		this.lastModifiedTime = lastModifiedTime;
	}

	public String getThisCsvTitle(String prefix)
	{
		StringBuilder sb = new StringBuilder();

		sb.append(prefix).append("�ļ�").append(",")//
				.append(prefix).append("�ļ���С(Byte)").append(",")//
				.append(prefix).append("�ļ�����޸�ʱ��");//

		return sb.toString();
	}

	public String this2Csv(boolean printFileFullPath)
	{
		StringBuilder sb = new StringBuilder();

		if (printFileFullPath)
		{
			sb.append(StringUtil.isEmpty(path) ? "" : path).append(",");//

		} else
		{
			sb.append(StringUtil.isEmpty(path) ? "" : new File(path).getName()).append(",");
		}
		sb.append(StringUtil.isEmpty(path) ? "" : length).append(",")//
				.append(TimeUtil.date2str(lastModifiedTime));//

		return sb.toString();
	}

//	@Override
	public int compareTo(BaseFile o)
	{
		if (this.path.equals(o.path))
		{
			return 0;
		}
		else
		{
			return -1;
		}
	}
	
	@Override
	public boolean equals(Object o)
	{
		return this.compareTo((BaseFile)o)==0;
	}
}
