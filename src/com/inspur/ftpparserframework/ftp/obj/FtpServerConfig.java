package com.inspur.ftpparserframework.ftp.obj;

import java.util.ArrayList;
import java.util.List;

import com.inspur.ftpparserframework.util.DigesterHelper;

public class FtpServerConfig
{
	private String version;
	private List<FtpServer> ftpServerList = new ArrayList<FtpServer>();

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public List<FtpServer> getFtpServerList()
	{
		return ftpServerList;
	}

	public void setFtpServerList(List<FtpServer> ftpServerList)
	{
		this.ftpServerList = ftpServerList;
	}

	public void addFtpServer(FtpServer ftpserver)
	{
		this.ftpServerList.add(ftpserver);
	}

	public static void main(String[] args)
	{
		try
		{
			FtpServerConfig config = (FtpServerConfig) DigesterHelper.parseFromXmlFile(FtpServerConfig.class, Thread
					.currentThread().getContextClassLoader().getResourceAsStream("FtpServerConfig.xml"));
			
			System.out.println(config.getFtpServerList());
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
