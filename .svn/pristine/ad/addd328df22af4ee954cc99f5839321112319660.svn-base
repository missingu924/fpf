package com.inspur.ftpparserframework.report.obj;

import java.util.ArrayList;
import java.util.List;

/**
 * 主文件解析入库记录信息
 * 
 * @author wuyg
 * 
 */
public class ParsedMainFileInfo extends BasicFileInfo
{
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
	private String reportTime;
	/**
	 * 文件中数据开始时间
	 */
	private String dataStartTime;
	/**
	 * 文件中数据结束时间
	 */
	private String dataEndTime;
	/**
	 * 子文件信息列表
	 */
	private List<ParsedSubFileInfo> subFileInfos = new ArrayList<ParsedSubFileInfo>();
	/**
	 * 入库成功总条数，默认0
	 */
	private long succRowsTotal;
	/**
	 * 入库失败总条数，默认0
	 */
	private long errorRowsTotal;

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

	public String getReportTime()
	{
		return reportTime;
	}

	public void setReportTime(String reportTime)
	{
		this.reportTime = reportTime;
	}

	public String getDataStartTime()
	{
		return dataStartTime;
	}

	public void setDataStartTime(String dataStartTime)
	{
		this.dataStartTime = dataStartTime;
	}

	public String getDataEndTime()
	{
		return dataEndTime;
	}

	public void setDataEndTime(String dataEndTime)
	{
		this.dataEndTime = dataEndTime;
	}

	public List<ParsedSubFileInfo> getSubFileInfos()
	{
		return subFileInfos;
	}

	public long getSubFileCount()
	{
		return subFileInfos.size();
	}

	public long getSuccRowsTotal()
	{
		for (int i = 0; i < subFileInfos.size(); i++)
		{
			this.succRowsTotal += subFileInfos.get(i).getSuccRows();
		}
		return succRowsTotal;
	}

	public long getErrorRowsTotal()
	{
		for (int i = 0; i < subFileInfos.size(); i++)
		{
			this.errorRowsTotal += subFileInfos.get(i).getErrorRows();
		}
		return errorRowsTotal;
	}

	public void addSubFileInfo(ParsedSubFileInfo subFileInfo)
	{
		this.subFileInfos.add(subFileInfo);
	}

	public ParsedSubFileInfo getParsedSubFileInfoBySubFilePath(String subFilePath)
	{
		ParsedSubFileInfo sf = null;

		for (int i = 0; i < subFileInfos.size(); i++)
		{
			ParsedSubFileInfo tmp = subFileInfos.get(i);
			if (subFilePath.equals(tmp.getFilepath()))
			{
				sf = tmp;

				if (sf.getEndTime() == null)
				{
					return sf;
				}
			}
		}

		return sf;
	}

	@Override
	protected String getThisCsvTitle()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("制式").append(",")//
				.append("厂商").append(",")//
				.append("数据类型").append(",")//
				.append("业务类型").append(",")//
				.append("上报网元粒度").append(",")//
				.append("上报时间周期").append(",")//
				.append("网元名称").append(",")//
				.append("文件报告时间").append(",")//
				.append("解析生成子文件数").append(",")//
				.append("入库成功总条数").append(",")//
				.append("入库失败总条数");
		return sb.toString();
	}

	@Override
	protected String this2Csv()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(this.technology).append(",")//
				.append(this.vendor).append(",")//
				.append(this.dataType).append(",")//
				.append(this.businessType).append(",")//
				.append(this.neType).append(",")//
				.append(this.timeType).append(",")//
				.append(this.neName).append(",")//
				.append(this.reportTime).append(",")//
				.append(getSubFileCount()).append(",")//
				.append(getSuccRowsTotal()).append(",")//
				.append(getErrorRowsTotal());//
		return sb.toString();
	}
}
