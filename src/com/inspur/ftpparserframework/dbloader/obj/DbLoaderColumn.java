package com.inspur.ftpparserframework.dbloader.obj;

import java.util.List;

import com.inspur.ftpparserframework.util.StringUtil;

public class DbLoaderColumn
{
	public DbLoaderColumn()
	{
	}

	public DbLoaderColumn(String columnStr)
	{
		if (!StringUtil.isEmpty(columnStr))
		{
			// String[] sl = columnStr.split("\\s+");
			List<String> sl = StringUtil.getStringListByString(columnStr, ' ', '\"');
			this.name = sl.get(0);
			if (sl.size() >= 2)
			{
				this.type = sl.get(1);
			}
			if (sl.size() >= 3)
			{
				for (int i = 2; i < sl.size(); i++)
				{
					this.format = this.format + " " + sl.get(i);
				}
			}
		}
	}

	private String name = "";
	private String type = "";
	private String format = "";// 如日期格式等
	private String constantValue = "";
	private boolean filler = false;
	private boolean isConstant = false;// lex 20130802 当constantValue取值为传入值，而传入值又为空时候标识是constant

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public boolean isFiller()
	{
		return filler;
	}

	public void setFiller(boolean filter)
	{
		this.filler = filter;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getFormat()
	{
		return format;
	}

	public void setFormat(String format)
	{
		this.format = format;
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

	public String toSqlCtl()
	{
		if (filler)
		{
			return name + " " + "FILLER";// Oracle的是这样，其他厂家的呢？
		} else
		{
			return name
					+ " " //
					+ (StringUtil.isEmpty(type) ? "" : type)
					+ " " //
					// + (StringUtil.isEmpty(format) ? "" : format) + " " //
					+ (StringUtil.isEmpty(format) ? "" : (format.trim().startsWith("'") ? format : "'" + format.trim()
							+ "'")) + " " //
					// + (StringUtil.isEmpty(constantValue) ? "" : (" constant '" + constantValue + "'"));
					// + (isConstant ? (" constant '" + constantValue + "'") : "");
					+ (isConstant ? (" constant " + (constantValue == null ? "null" : "'" + constantValue + "'")) : "");
		}
	}
}
