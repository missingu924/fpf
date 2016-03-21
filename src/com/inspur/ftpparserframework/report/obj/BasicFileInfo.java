package com.inspur.ftpparserframework.report.obj;

import java.util.Date;

import com.inspur.ftpparserframework.util.StringUtil;
import com.inspur.ftpparserframework.util.TimeUtil;

/**
 * �����࣬�ļ����������Ϣ
 * 
 * @author wuyg
 * 
 */
public abstract class BasicFileInfo
{
	/**
	 * �ļ�ȫ·��
	 */
	private String filepath;
	/**
	 * �ļ����ȣ���λByte
	 */
	private long length;
	/**
	 * �ļ�����޸�ʱ��
	 */
	private Date lastModifiedTime;

	/**
	 * ����ʼʱ��
	 */
	private String startTime;
	/**
	 * �������ʱ��
	 */
	private String endTime;
	/**
	 * �Ƿ���ɹ���Ĭ��Ϊtrue
	 */
	private boolean processOk = true;

	/**
	 * ������Ϣ
	 */
	private String errorMessage;
	/**
	 * �ļ������ʱ
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
	 * ��ȡ�ļ���������ʱ������λ�롣
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
	 * ��ȡcsv���ʱ�ı�ͷ
	 * 
	 * @return
	 */
	public String getCsvTitle()
	{
		return getBasicCsvTitle() + "," + getThisCsvTitle();
	}

	/**
	 * ��ȡcsv���ʱ������
	 * 
	 * @return
	 */
	public String toCsv()
	{
		return basic2Csv() + "," + this2Csv();
	}

	/**
	 * ������Ϣ��ͷ���
	 * 
	 * @return
	 */
	private String getBasicCsvTitle()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("�ļ�·��").append(",")//
				.append("�ļ���С(Byte)").append(",")//
				.append("�ļ�����޸�ʱ��").append(",")//
				.append("�ļ�����ʼʱ��").append(",")//
				.append("�ļ��������ʱ��").append(",")//
				.append("�ļ������ʱ(��)").append(",")//
				.append("�ļ�����ɹ�").append(",")//
				.append("�ļ����������Ϣ");
		return sb.toString();
	}

	/**
	 * ������Ϣ�������
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
	 * ������ʵ�֣���������еı�ͷ��Ϣ
	 * 
	 * @return
	 */
	protected abstract String getThisCsvTitle();

	/**
	 * ������ʵ�֣���������е�������Ϣ
	 * 
	 * @return
	 */
	protected abstract String this2Csv();
}
