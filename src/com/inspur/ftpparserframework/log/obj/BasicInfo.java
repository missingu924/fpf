package com.inspur.ftpparserframework.log.obj;

import java.util.Date;

import com.inspur.ftpparserframework.util.TimeUtil;

public class BasicInfo
{
	/**
	 * 域，专业
	 */
	private String domain;

	/**
	 * 制式
	 */
	private String technology;

	/**
	 * 厂商
	 */
	private String vendor;

	/**
	 * 数据类型
	 */
	private String dataType;

	/**
	 * 业务类型
	 */
	private String businessType;

	/**
	 * 上报网元粒度
	 */
	private String neType;

	/**
	 * 上报周期
	 */
	private String timeType;

	/**
	 * 网元名称
	 */
	private String neName;

	/**
	 * 文件报告时间
	 */
	private Date reportTime;

	/**
	 * 文件中数据开始时间
	 */
	private Date dataStartTime;

	/**
	 * 文件中数据结束时间
	 */
	private Date dataEndTime;

	public String getTechnology()
	{
		return technology;
	}

	public void setTechnology(String technology)
	{
		this.technology = technology;
	}

	public String getVendor()
	{
		return vendor;
	}

	public void setVendor(String vendor)
	{
		this.vendor = vendor;
	}

	public String getDataType()
	{
		return dataType;
	}

	public void setDataType(String dataType)
	{
		this.dataType = dataType;
	}

	public String getBusinessType()
	{
		return businessType;
	}

	public void setBusinessType(String businessType)
	{
		this.businessType = businessType;
	}

	public String getNeType()
	{
		return neType;
	}

	public void setNeType(String neType)
	{
		this.neType = neType;
	}

	public String getTimeType()
	{
		return timeType;
	}

	public void setTimeType(String timeType)
	{
		this.timeType = timeType;
	}

	public String getNeName()
	{
		return neName;
	}

	public void setNeName(String neName)
	{
		this.neName = neName;
	}

	public Date getReportTime()
	{
		return reportTime;
	}

	public void setReportTime(Date reportTime)
	{
		this.reportTime = reportTime;
	}

	public Date getDataStartTime()
	{
		return dataStartTime;
	}

	public void setDataStartTime(Date dataStartTime)
	{
		this.dataStartTime = dataStartTime;
	}

	public Date getDataEndTime()
	{
		return dataEndTime;
	}

	public void setDataEndTime(Date dataEndTime)
	{
		this.dataEndTime = dataEndTime;
	}

	public String getDomain()
	{
		return domain;
	}

	public void setDomain(String domain)
	{
		this.domain = domain;
	}

	public String getThisCsvTitle()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("专业").append(",")//
				.append("制式").append(",")//
				.append("厂商").append(",")//
				.append("数据类型").append(",")//
				.append("业务类型").append(",")//
				.append("上报网元粒度").append(",")//
				.append("上报时间周期").append(",")//
				.append("网元名称").append(",")//
				.append("文件报告时间");

		return sb.toString();
	}

	public String this2Csv()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(this.getDomain()).append(",")//
				.append(this.getTechnology()).append(",")//
				.append(this.getVendor()).append(",")//
				.append(this.getDataType()).append(",")//
				.append(this.getBusinessType()).append(",")//
				.append(this.getNeType()).append(",")//
				.append(this.getTimeType()).append(",")//
				.append(this.getNeName()).append(",")//
				.append(TimeUtil.date2str(reportTime));

		return sb.toString();
	}
}
