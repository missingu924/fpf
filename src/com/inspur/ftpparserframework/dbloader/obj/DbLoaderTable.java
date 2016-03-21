package com.inspur.ftpparserframework.dbloader.obj;

public class DbLoaderTable
{
	public DbLoaderTable()
	{
	}
	public DbLoaderTable(String name)
	{
		this.name=name;
	}
	
	private String name;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
