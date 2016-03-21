package com.inspur.ftpparserframework.transformator.obj.excel2DbMap;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "sheet")
public class SheetInfo
{
	private int idx;
	private String name;
	private int titleRowNum;
	private int dataRowStartNum;
	private String identifiedBy;
	private String description;
	private String tableName;
	private List<ColumnInfo> columnInfos = new ArrayList<ColumnInfo>();

	public int getIdx()
	{
		return idx;
	}

	@XmlAttribute(name = "idx")
	public void setIdx(int idx)
	{
		this.idx = idx;
	}

	public String getName()
	{
		return name;
	}

	@XmlAttribute(name = "name")
	public void setName(String name)
	{
		this.name = name;
	}

	public int getTitleRowNum()
	{
		return titleRowNum;
	}

	@XmlAttribute(name = "titleRowNum")
	public void setTitleRowNum(int titleRowNum)
	{
		this.titleRowNum = titleRowNum;
	}

	public int getDataRowStartNum()
	{
		return dataRowStartNum;
	}

	@XmlAttribute(name = "dataRowStartNum")
	public void setDataRowStartNum(int dataRowStartNum)
	{
		this.dataRowStartNum = dataRowStartNum;
	}

	public String getIdentifiedBy()
	{
		return identifiedBy;
	}

	@XmlAttribute(name = "identifiedBy")
	public void setIdentifiedBy(String identifiedBy)
	{
		this.identifiedBy = identifiedBy;
	}

	public String getDescription()
	{
		return description;
	}

	@XmlAttribute(name = "description")
	public void setDescription(String description)
	{
		this.description = description;
	}

	public List<ColumnInfo> getColumnInfos()
	{
		return columnInfos;
	}

	@XmlElement(name = "column")
	public void setColumnInfos(List<ColumnInfo> columnInfos)
	{
		this.columnInfos = columnInfos;
	}

	public String getTableName()
	{
		return tableName;
	}

	@XmlAttribute(name = "tableName")
	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}

}
