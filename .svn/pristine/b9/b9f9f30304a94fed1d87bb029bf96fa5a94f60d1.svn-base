package com.inspur.ftpparserframework.ftp;

import java.util.Date;
import java.util.Set;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;

import com.inspur.ftpparserframework.ftp.obj.FtpServer;
import com.inspur.ftpparserframework.util.Constant;

public class UpDownLoadThread extends Thread
{
	private static Logger log = Logger.getLogger(FtpUtil.class);
	// private ThreadLocal<FTPClient> ftpLocal = new ThreadLocal<FTPClient>();
	private FtpThread ftpthread;
	private FTPFile srcFile;
	private String ftpWorkingDirectory;
	private String storedir;
	private String loadMode;
	private Set<String> alreayDlFiles;
	private FileCount downloadFileCount;
	private MyFTPThreadPool myFTPThreadPool = null;

	public UpDownLoadThread(FtpThread ftpthread, FTPFile srcFile, String ftpWorkingDirectory, String storeDir,
			String loadMode, Set<String> alreayDlFiles, FileCount downloadFileCount, MyFTPThreadPool myFTPThreadPool)
	{
		this.ftpthread = ftpthread;
		this.srcFile = srcFile;
		this.ftpWorkingDirectory = ftpWorkingDirectory;
		this.storedir = storeDir;
		this.loadMode = loadMode;
		this.alreayDlFiles = alreayDlFiles;
		this.downloadFileCount = downloadFileCount;
		this.myFTPThreadPool = myFTPThreadPool;

	}

	@Override
	public void run()
	{
		if (loadMode != null && (loadMode.equals(Constant.FTP_DOWNLOAD) || loadMode.equals(Constant.FTP_RE_DOWNLOAD)))
		{
			FtpServer ftpServer = ftpthread.getFtpServer();
			String ftpKey = "(" + ftpServer.getName() + "," + Thread.currentThread().getName() + ","
					+ Thread.currentThread().getId() + ")";
			FTPClient ftp = null;
			try
			{
				MyFTPClient myFtp = myFTPThreadPool.getFTPClient(ftpServer, ftpKey);
				ftp = myFtp.getFtp();
				ftp.changeWorkingDirectory(ftpWorkingDirectory);
				boolean downloadSucc = FtpUtil.downloadOnce(ftpthread, ftp, srcFile, storedir, loadMode
						.equals(Constant.FTP_RE_DOWNLOAD), alreayDlFiles);
				if (downloadSucc)
				{
					downloadFileCount.addOne();
				}
				// 设置FTP线程的最新活动时间
				myFtp.setLastUseTime(new Date());
			} catch (Exception e)
			{
				log.error(e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
