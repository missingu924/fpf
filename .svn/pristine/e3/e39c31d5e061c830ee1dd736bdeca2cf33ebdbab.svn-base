package com.inspur.ftpparserframework.report.obj;

/**
 * 主文件解析入库是产生的子文件的信息
 * 
 * @author wuyg
 * 
 */
public class ParsedSubFileInfo extends BasicFileInfo
{
	/**
	 * 主文件全路径
	 */
	private String mainFilePath;
	/**
	 * 入库成功条数，默认0
	 */
	private long succRows;
	/**
	 * 入库失败条数，默认0
	 */
	private long errorRows;

	public String getMainFilePath()
	{
		return mainFilePath;
	}

	public void setMainFilePath(String mainFilePath)
	{
		this.mainFilePath = mainFilePath;
	}

	public long getSuccRows()
	{
		return succRows;
	}

	public void setSuccRows(long succRows)
	{
		this.succRows = succRows;
	}

	public long getErrorRows()
	{
		return errorRows;
	}

	public void setErrorRows(long errorRows)
	{
		this.errorRows = errorRows;
	}

	@Override
	protected String getThisCsvTitle()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("主文件路径").append(",")//
				.append("入库成功条数").append(",")//
				.append("入库失败条数");
		return sb.toString();
	}

	@Override
	protected String this2Csv()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(mainFilePath).append(",")//
				.append(succRows).append(",")//
				.append(errorRows);
		return sb.toString();
	}
}
