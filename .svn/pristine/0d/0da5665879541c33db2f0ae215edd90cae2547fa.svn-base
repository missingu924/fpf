package com.inspur.ftpparserframework.transformator.obj;

import com.inspur.ftpparserframework.dbloader.obj.DbLoaderColumn;

public class Csv2DbColumn
{
	private String dbColName;
	private String dbColType;
	private String dbColFormat;
	private String csvField;
	private String constantValue;
	private boolean isConstant = false;// lex 20130802 当constantValue取值为传入值，而传入值又为空时候标识是constant,内部使用
	

	public String getDbColName()
	{
		return dbColName;
	}

	public void setDbColName(String dbColName)
	{
		this.dbColName = dbColName;
	}

	public String getDbColType()
	{
		return dbColType;
	}

	public void setDbColType(String dbColType)
	{
		this.dbColType = dbColType;
	}

	public String getDbColFormat()
	{
		return dbColFormat;
	}

	public void setDbColFormat(String dbColFormat)
	{
		this.dbColFormat = dbColFormat;
	}

	public String getCsvField()
	{
		return csvField;
	}

	public void setCsvField(String csvFiled)
	{
		this.csvField = csvFiled;
	}

	public String getConstantValue()
	{
		return constantValue;
	}

	public void setConstantValue(String constantValue)
	{
		this.constantValue = constantValue;
		this.isConstant = true;
	}
	

	public boolean isConstant()
	{
		return isConstant;
	}
	

	public void setConstant(boolean isConstant)
	{
		this.isConstant = isConstant;
	}

	public DbLoaderColumn getDbLoderColumn()
	{
		DbLoaderColumn c = new DbLoaderColumn();
		c.setName(dbColName);
		//c.setType(dbColType);
		//add by 徐恩龙 数据质量侧传入的都有dbColType，只保留date类型
		if (dbColType!=null&&dbColType.equalsIgnoreCase("date"))
		{
			c.setType(dbColType);
		}
		c.setFormat(dbColFormat);
		c.setConstantValue(constantValue);
		c.setConstant(isConstant);
		return c;
	}
}
