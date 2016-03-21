package com.inspur.ftpparserframework.report.obj;

import java.util.Date;

import com.inspur.ftpparserframework.util.StringUtil;
import com.inspur.ftpparserframework.util.TimeUtil;

/**
 * 抽象类，文件处理基本信息
 * 
 * @author wuyg
 * 
 */
public abstract class BasicFileInfo
{
	/**
	 * 文件全路径
	 */
	private String filepath;
	/**
	 * 文件长度，单位Byte
	 */
	private long length;
	/**
	 * 文件最后修改时间
	 */
	private Date lastModifiedTime;

	/**
	 * 处理开始时间
	 */
	private String startTime;
	/**
	 * 处理结束时间
	 */
	private String endTime;
	/**
	 * 是否处理成功，默认为true
	 */
	private boolean processOk = true;

	/**
	 * 错误信息
	 */
	private String errorMessage;
	/**
	 * 文件处理耗时
	 */
	private double usedTime;

	public String getErrorMessage()
	{
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	public String getFilepath()
	{
		return filepath;
	}

	public void setFilepath(String filepath)
	{
		this.filepath = filepath;
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

	public String getStartTime()
	{
		return startTime;
	}

	public void setStartTime(String startTime)
	{
		this.startTime = startTime;
	}

	public String getEndTime()
	{
		return endTime;
	}

	public void setEndTime(String endTime)
	{
		this.endTime = endTime;
	}

	public boolean isProcessOk()
	{
		return processOk;
	}

	public void setProcessOk(boolean processOk)
	{
		this.processOk = processOk;
	}

	public void setUsedTime(double usedTime)
	{
		this.usedTime = usedTime;
	}

	/**
	 * 获取文件处理所用时长，单位秒。
	 * 
	 * @return
	 */
	public double getUsedTime()
	{
		try
		{
			if (this.usedTime == 0)
			{
				if (this.startTime != null & this.endTime != null)
				{
					return (TimeUtil.str2date(this.endTime).getTime() - TimeUtil.str2date(this.startTime).getTime()) / 1000;
				} else
				{
					return -1;
				}
			} else
			{
				return this.usedTime;
			}

		} catch (Exception e)
		{
			return -1;
		}
	}

	/**
	 * 获取csv输出时的表头
	 * 
	 * @return
	 */
	public String getCsvTitle()
	{
		return getBasicCsvTitle() + "," + getThisCsvTitle();
	}

	/**
	 * 获取csv输出时的数据
	 * 
	 * @return
	 */
	public String toCsv()
	{
		return basic2Csv() + "," + this2Csv();
	}

	/**
	 * 基本信息表头输出
	 * 
	 * @return
	 */
	private String getBasicCsvTitle()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("文件路径").append(",")//
				.append("文件大小(Byte)").append(",")//
				.append("文件最后修改时间").append(",")//
				.append("文件处理开始时间").append(",")//
				.append("文件处理结束时间").append(",")//
				.append("文件处理耗时(秒)").append(",")//
				.append("文件处理成功").append(",")//
				.append("文件处理错误信息");
		return sb.toString();
	}

	/**
	 * 基本信息数据输出
	 * 
	 * @return
	 */
	private String basic2Csv()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(filepath).append(",")//
				.append(length).append(",")//
				.append(TimeUtil.date2str(lastModifiedTime)).append(",")//
				.append(startTime).append(",")//
				.append(endTime).append(",")//
				.append(getUsedTime()).append(",")//
				.append(processOk).append(",")//
				.append(StringUtil.isEmpty(errorMessage) ? "" : "\"" + errorMessage + "\"");
		return sb.toString();
	}

	/**
	 * 具体类实现，输出其特有的表头信息
	 * 
	 * @return
	 */
	protected abstract String getThisCsvTitle();

	/**
	 * 具体类实现，输出其特有的数据信息
	 * 
	 * @return
	 */
	protected abstract String this2Csv();
}
