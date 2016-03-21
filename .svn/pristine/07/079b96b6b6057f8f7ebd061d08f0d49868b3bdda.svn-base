package com.inspur.ftpparserframework.transformator.obj;

import java.util.ArrayList;
import java.util.List;

public class Csv2DbMap
{
	private String dbTable;
	private String description;
	private String quotechar = "\"";
	private String separator = ",";
	private String fileRegext;
	private String hasTitle;
	private String encoding;
	private List<Csv2DbColumn> columns = new ArrayList<Csv2DbColumn>();

	public String getQuotechar()
	{
		return quotechar;
	}

	public void setQuotechar(String quotechar)
	{
		this.quotechar = quotechar;
	}

	public String getSeparator()
	{
		return separator;
	}

	public void setSeparator(String separator)
	{
		this.separator = separator;
	}

	public String getDbTable()
	{
		return dbTable;
	}

	public void setDbTable(String dbTable)
	{
		this.dbTable = dbTable;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public List<Csv2DbColumn> getColumns()
	{
		return columns;
	}

	public void setColumns(List<Csv2DbColumn> columns)
	{
		this.columns = columns;
	}

	public void addColumn(Csv2DbColumn column)
	{
		this.columns.add(column);
	}

	public Csv2DbColumn getCsv2DbColumnByCsvField(String fieldName)
	{
		for (int i = 0; i < this.columns.size(); i++)
		{
			if (fieldName.equalsIgnoreCase(this.columns.get(i).getCsvField()))
				return this.columns.get(i);
		}
		return null;
	}

	public String getFileRegext()
	{
		return fileRegext;
	}

	public void setFileRegext(String fileRegext)
	{
		this.fileRegext = fileRegext;
	}

	public String getHasTitle()
	{
		return hasTitle;
	}

	public void setHasTitle(String hasTitle)
	{
		this.hasTitle = hasTitle;
	}

	public String getEncoding()
	{
		return encoding;
	}

	public void setEncoding(String encoding)
	{
		this.encoding = encoding;
	}
	
}
