package com.inspur.ftpparserframework.ftp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;

import com.inspur.ftpparserframework.Main;
import com.inspur.ftpparserframework.ftp.obj.FtpServer;
import com.inspur.ftpparserframework.util.Constant;

/**
 * FTP�̼߳��������ĳFTP�̵߳��𳬹�5���ӣ���������FTP�̡߳�
 * 
 * @author �����
 * 
 */
public class FtpThreadTimeoutChecker extends Thread
{
	private Logger log = Logger.getLogger(this.getClass());
	private List<FtpThread> ftpThreads = new ArrayList<FtpThread>();
	private long ftpTimeOut = Constant.DEVAULT_FTP_TIME_OUT; // ftp�̵߳�������Ĭ�ϳ�ʱʱ��
	private int scanInterval = 60;// ftp�߳�Ĭ�ϼ��ʱ������60 ��

	private final Object lock = new Object();// ͬ����

	public FtpThreadTimeoutChecker(long ftpTimeOut)
	{
		this.ftpTimeOut = ftpTimeOut;
	}

	@Override
	public void run()
	{
		try
		{
			while (true)
			{
				synchronized (lock)
				{
					for (int i = 0; i < ftpThreads.size(); i++)
					{
						FtpThread f = ftpThreads.get(i);

						long interval = System.currentTimeMillis() - f.getStartTime().getTime();
						if (interval > ftpTimeOut * 1000l)
						{
							log.info("FTP�̳߳�ʱ����(��ʱʱ��Ϊ" + ftpTimeOut + "��)����ӦFTP������Ϊ��" + f.getFtpServer().getName());

							// ������һ���߳�
							FtpThread ftp = new FtpThread(f.getFtpServer(), f.getInterval(), f.getFileLastModifyTime(),
									f.getMode());
							ftp.start();

							// ������
							this.ftpThreads.set(i, ftp);

							// ���̳߳�ʱֹͣ
							f.stop();
							f = null;
						} else
						{
							log
									.info("FTP�߳������У�����������" + (interval / 1000) + "�룬��ӦFTP������Ϊ��"
											+ f.getFtpServer().getName());
							FTPClient ftp=f.getFtp();
							//add by lex 20140519
							log.debug("ftp:"+(ftp!=null?ftp:"null")+";ftp.isConnected()?"+(ftp!=null&&ftp.isConnected()));
//							log.debug("ftp.isConnected()?"+(ftp!=null&&ftp.isConnected()));
							if (ftp!=null&&ftp.isConnected())
							{
								try
								{
									boolean sendOk=ftp.sendNoOp();
									if (sendOk)
									{
										f.setStartTime(new Date());
									}
									log.info("ftp��ѯ�ͻ�����" + f.getFtpServer().getName() + "("
											+ f.getFtpServer().getIp() + ")����NOOP����:" + sendOk);
								} catch (IOException e)
								{
									log.error(e.getMessage(), e);
									e.printStackTrace();
								}
								
							}
							
						}
					}
				}

				sleep(scanInterval * 1000l);
			}
		} catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}
	}

	public void check(FtpThread t)
	{
		synchronized (lock)
		{
			ftpThreads.add(t);
		}
	}

	public void check(FtpServer ftpServer)
	{
		synchronized (lock)
		{
			int i = 0;
			for (i = 0; i < ftpThreads.size(); i++)
			{
				FtpThread f = ftpThreads.get(i);
				if (f.getName().equals(ftpServer.getName()))
				{
					log.info("ftp������" + ftpServer.getName() + "�߳��Ѿ����ڣ��滻���µ�����...");
					// ������һ���߳�
					FtpThread ftp = new FtpThread(ftpServer, f.getInterval(), f.getFileLastModifyTime(), f.getMode());
					ftp.start();
					// ������
					this.ftpThreads.set(i, ftp);
					// ���߳�ֹͣ
					f.stop();
					f = null;
					break;
				}
			}
			if (i == ftpThreads.size())
			{
				log.info("����ftp������" + ftpServer.getName() + "�߳�");
				// ������һ���߳�
				FtpThread ftp = new FtpThread(ftpServer, Main.ftpScanInterval, Main.ftpFileLastModifyTime,
						Constant.FTP_DOWNLOAD); // ����ftp�ɼ��߳�
				ftp.start();
				// ������
				this.ftpThreads.add(ftp);
			}
		}
	}

	public void delFtp(FtpServer ftpServer)
	{
		for (int i = 0; i < ftpThreads.size(); i++)
		{
			FtpThread f = ftpThreads.get(i);
			if (f.getName().equals(ftpServer.getName()))
			{
				log.info("ֹͣftp������" + ftpServer.getName() + "");
				this.ftpThreads.remove(i);
				f.stop();
				f = null;
				break;
			}

		}
	}

	public static void main(String[] args)
	{
		FtpThreadTimeoutChecker checker = new FtpThreadTimeoutChecker(1800);
		for (int i = 0; i < 1; i++)
		{
			FtpServer server = new FtpServer();
			server.setName("test");
			server.setIp("10.18.1.26");
			server.setPort(21);
			server.setUser("nwom");
			server.setPassword("nwom123");
			server.setDir("/home/nwom/user/wuyg");

			FtpThread ftp = new FtpThread(server, 10, 3600, Constant.FTP_DOWNLOAD);
			ftp.start();

			checker.check(ftp);
		}
		checker.start();
	}
}