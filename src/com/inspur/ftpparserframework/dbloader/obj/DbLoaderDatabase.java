package com.inspur.ftpparserframework.dbloader.obj;

public class DbLoaderDatabase
{
	public DbLoaderDatabase()
	{
	}
	
	public DbLoaderDatabase(String dbuserid)
	{
		this.dbuserid = dbuserid;
	}
	
	
	private String dbuserid;

	public String getDbuserid()
	{
		return dbuserid;
	}

	public void setDbuserid(String dbuserid)
	{
		this.dbuserid = dbuserid;
	}


}
