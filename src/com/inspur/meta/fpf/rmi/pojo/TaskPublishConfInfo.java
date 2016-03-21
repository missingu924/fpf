package com.inspur.meta.fpf.rmi.pojo;

import com.inspur.ftpparserframework.ftp.obj.FtpServer;

public class TaskPublishConfInfo
{
	String publisth_task_id;
	FtpServer ftpServer;
	DBInfo dBInfo;
	public String getPublisth_task_id()
	{
		return publisth_task_id;
	}
	public void setPublisth_task_id(String publisth_task_id)
	{
		this.publisth_task_id = publisth_task_id;
	}
	public FtpServer getFtpServer()
	{
		return ftpServer;
	}
	public void setFtpServer(FtpServer ftpServer)
	{
		this.ftpServer = ftpServer;
	}
	public DBInfo getDBInfo()
	{
		return dBInfo;
	}
	public void setDBInfo(DBInfo info)
	{
		dBInfo = info;
	}
}
