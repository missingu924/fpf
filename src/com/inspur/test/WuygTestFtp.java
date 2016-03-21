package com.inspur.test;

import com.inspur.ftpparserframework.ftp.FtpThread;
import com.inspur.ftpparserframework.ftp.FtpUtil;
import com.inspur.ftpparserframework.ftp.obj.FtpServer;

public class WuygTestFtp
{
	public static void main(String[] args)
	{
		try
		{
			FtpServer ftpServer = new FtpServer();
			ftpServer.setName("ftp");
			
			FtpThread ftpThread = new FtpThread(ftpServer, 360000l, 10000000l, "download");
			
			FtpUtil.downloadNested(ftpThread, "10.18.1.26", 21, "tmn", "tmn123",
					"/u3/data/tianjin/LTE-MR/test/2014-02-24-test/", "c:/test", null, 36000000l, false);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
