package com.inspur.ftpparserframework.ftp;

import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import com.inspur.ftpparserframework.ftp.obj.FtpServer;
import com.inspur.ftpparserframework.util.Constant;

public class MyFTPThreadPool
{
	private static Logger log = Logger.getLogger(MyFTPThreadPool.class);
	private ExecutorService pool = null;
	private Map<String, MyFTPClient> ftpMap = new Hashtable<String, MyFTPClient>();
	private DaemonThread daemonThread = null;// 守护线程
	private final Object lock = new Object();// 同步锁
	private boolean isShutdown = false;// 是否关闭

	public MyFTPThreadPool(int threadpool)
	{
		pool = Executors.newFixedThreadPool(threadpool);
		daemonThread = new DaemonThread();
		daemonThread.start();
	}

	public void execute(Runnable command)
	{
		pool.execute(command);
	}

	public void shutdown() throws InterruptedException
	{
		if (daemonThread != null)
		{
			daemonThread.stop();
		}
		if (pool != null)
		{
			pool.shutdown();
			while (!pool.awaitTermination(1, TimeUnit.MILLISECONDS))
				;
			pool = null;
		}
		isShutdown = true;
		clearAllFtp();

	}

	private void clearAllFtp()
	{
		synchronized (lock)
		{
			for (String key : ftpMap.keySet())
			{
				MyFTPClient myFTP = ftpMap.get(key);
				try
				{
					FTPClient ftp = myFTP.getFtp();
					if (ftp != null && ftp.isConnected())
					{
						ftp.disconnect();
					}
				} catch (IOException e)
				{
					log.error(e.getMessage());
				}
			}
			ftpMap.clear();
		}
	}

	public MyFTPClient getFTPClient(FtpServer ftpServer, String ftpId) throws Exception
	{
		if (isShutdown)
		{
			throw new Exception("FTP连接池已经关闭");
		}
		synchronized (lock)
		{
			String ftpKey = ftpId;
			if (ftpMap.containsKey(ftpKey))
			{
				log.debug("复用ftp连接：" + ftpKey);
				MyFTPClient myFTP = ftpMap.get(ftpKey);
				FTPClient ftpClient = myFTP.getFtp();
				ftpClient = getOkFTP(ftpClient, ftpServer);
				myFTP = new MyFTPClient(ftpClient, ftpServer);
				ftpMap.put(ftpKey, myFTP);
				return myFTP;
			} else
			{
				log.debug("新建ftp连接：" + ftpKey);
				FTPClient ftpClient = getOkFTP(null, ftpServer);
				MyFTPClient myFTP = new MyFTPClient(ftpClient, ftpServer);
				ftpMap.put(ftpKey, myFTP);
				return myFTP;
			}
		}
	}

	private final FTPClient getOkFTP(FTPClient ftpClient, FtpServer ftpServer) throws Exception
	{
		FTPClient ftp = ftpClient;
		if (ftp == null)
		{
			ftp = FtpUtil.connect(ftpServer.getName(), ftpServer.getIp(), ftpServer.getPort(), ftpServer.getUser(),
					ftpServer.getPassword());
		}
		// /////////////////////
		int i = 0;
		for (i = 0; i < 3; i++)
		{// 3次重连机会，超过三次抛出错误
			// /判断可用方式1///////////
			 boolean canUse = true;
			try
			{
				canUse = ftp.sendNoOp();

			} catch (IOException e)
			{
				log.error(e.getMessage());
				e.printStackTrace();
				canUse = false;
			}
			if (canUse)
			// 判断可用方式1///////////
			// 判断可用方式2，官方的，但是在湖南测试慢，费解///////////
//			int reply = ftp.getReplyCode();
//			if (FTPReply.isPositiveCompletion(reply))
			// 判断可用方式2///////////
			{// 可用
				if (i != 0)
				{
					log.info(ftpServer.getName() + "，第" + (i) + "次尝试重连成功");
				}
				break;
			} else
			{
				log.info(ftpServer.getName() + "连接不可用，第" + (i + 1) + "次尝试重连...");
				try
				{
					ftp.disconnect();
				} catch (Exception e)
				{
				}
				ftp = FtpUtil.connect(ftpServer.getName(), ftpServer.getIp(), ftpServer.getPort(), ftpServer.getUser(),
						ftpServer.getPassword());
			}
		}
		if (i == 3)
		{// 3次重连均为成功，抛出错误
			throw new Exception("FTP连接失败，请检查FTP服务器允许的并发数是否满足要求");
		}
		return ftp;

		// /////////////////////
	}

	private class DaemonThread extends Thread
	{
		@Override
		public void run()
		{
			while (true)
			{
				try
				{
					synchronized (lock)
					{
						for (String key : ftpMap.keySet())
						{
							{
								MyFTPClient myFTP = ftpMap.get(key);
								long interval = System.currentTimeMillis() - myFTP.getLastUseTime().getTime();
								long totalMillis = System.currentTimeMillis() - myFTP.getStartTime().getTime();
								if (interval > Constant.DEVAULT_FTP_TIME_OUT * 1000l)
								{
									log.info("FTP" + key + "连接超时重新连接(超时时间为" + Constant.DEVAULT_FTP_TIME_OUT + "秒)");

									MyFTPClient newMyFTP = getFTPClient(myFTP.getFtpServer(), key);
									ftpMap.put(key, newMyFTP);

									myFTP.getFtp().disconnect();
									myFTP = null;
								} else
								{

									log.info("FTP" + key + "连接正常，已连接" + (totalMillis / 1000) + "秒，距上次传输文件或成功发送NOOP"
											+ (interval / 1000) + "秒");
									FTPClient ftp = myFTP.getFtp();
									if (ftp != null && ftp.isConnected())
									{
										try
										{
											boolean sendOk = ftp.sendNoOp();
											if (sendOk)
											{
												myFTP.setLastUseTime(new Date());
											}
											log.info("ftp业务客户端向" + myFTP.getFtpServer().getName() + "("
													+ myFTP.getFtpServer().getIp() + ")发送NOOP命令:" + sendOk);
										} catch (IOException e)
										{
											log.error(e.getMessage(), e);
											e.printStackTrace();
										}

									}

								}
							}
						}
					}
					sleep(60 * 1000l);
				} catch (Exception e)
				{
					log.error(e.getMessage(), e);
				}
			}

		}

	}

}