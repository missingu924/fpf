package com.inspur.ftpparserframework.ftp;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.inspur.ftpparserframework.config.ConfigReader;
import com.inspur.ftpparserframework.ftp.obj.FtpServer;
import com.inspur.ftpparserframework.ftp.obj.FtpServerConfig;
import com.inspur.ftpparserframework.util.Constant;
import com.inspur.ftpparserframework.util.DigesterHelper;
import com.inspur.ftpparserframework.util.StringUtil;

/**
 * FTP运行主程序<br/>
 * <br/>
 * 命令说明: ./ftp.sh -mode download|upload -ftptimeout intValue [-server
 * svr1,svr2,svr3] <br/>
 * <br/>
 * 
 * 该框架是用于实现ftp文件下载或上传的调度框架.<br/>
 * <br/>
 * 
 * 命令选项解释:<br/>
 * <br/>
 * 
 * -h 帮助<br/>
 * <br/>
 * 
 * -mode download|upload|redownload，必选项，上传 |下载|补采<br/>
 * <br/>
 * 
 * -ftptimeout 数字，必选项，ftp线程超时时间，单位秒，默认为半小时<br/>
 * <br/>
 * 
 * -server 可选项，本次需要采集和解析的服务器信息，多个服务器用半角逗号隔开，不设置该参数则默认处理所有服务器的数据。<br/>
 * <br/>
 * 
 * @author 武玉刚
 * 
 */
public class Main
{
	private static Logger log = Logger.getLogger(Main.class);

	private static String mode = null;// 上传或下载。
	private static String ftpTimeOutStr = null; // Ftp 线程超时检测时间（单位秒）。
	private static int ftpTimeOut = Constant.DEVAULT_FTP_TIME_OUT;// Ftp 线程超时检测时间，默认半小时。
	private static String serverStr = null; // 需处理的Ftp Server，不输入则处理所有的。
	private static List<String> servers = new ArrayList<String>();

	private static void usage()
	{
		System.out.println("命令说明: ./ftp.sh -mode download|upload -ftptimeout intValue [-server svr1,svr2,svr3] ");
	}

	private static void help()
	{
		usage();
		System.out.println("\n该框架是用于实现ftp文件下载或上传的调度框架.");
		System.out.println("\n命令选项解释:");
		System.out.println("\n\t-h            帮助");
		System.out.println("\n\t-mode         download|upload|redownload，必选项，上传 |下载|补采");
		System.out.println("\n\t-ftptimeout   数字，必选项，ftp线程超时时间，单位秒，默认为半小时");
		System.out.println("\n\t-server       可选项，本次需要采集和解析的服务器信息，多个服务器用半角逗号隔开，不设置该参数则默认处理所有服务器的数据。");

		System.exit(-1);
	}

	public static void main(String[] args)
	{
		try
		{
			if (args.length >= 0)
			{
				// 获取命令行参数
				getParameters(args);

				// 未指定上传或下载的参数，或指定的参数不符合要求
				if (!Constant.FTP_DOWNLOAD.equalsIgnoreCase(mode) && !Constant.FTP_UPLOAD.equalsIgnoreCase(mode)
						&& !Constant.FTP_RE_DOWNLOAD.equalsIgnoreCase(mode))
				{
					System.out.println("未指定上传或下载的参数，或指定的参数不符合要求");
					help();
				}

				// 未指定FTP线程超时时间，采用默认值
				if (StringUtil.isEmpty(ftpTimeOutStr))
				{
					log.info("未指定FTP线程超时时间，采用默认值" + Constant.DEVAULT_FTP_TIME_OUT + "秒");
				} else
				{
					log.info("指定的FTP线程超时时间为" + ftpTimeOut + "秒");
				}

				// 检查是否指定了需要处理FTP Server信息
				if (serverStr == null)
				{
					log.info("server参数未指定，处理所有server的数据");
				} else
				{
					log.info("server参数已指定，处理" + serverStr + "的数据");
				}
			}

			log.info("====================FTP线程启动开始====================");
			FtpServerConfig config = (FtpServerConfig) DigesterHelper.parseFromXmlFile(FtpServerConfig.class, Thread
					.currentThread().getContextClassLoader().getResourceAsStream(Constant.FTP_SERVER_CONFIG_FILE));

			long ftpInterval = 0;
			try
			{
				ftpInterval = Integer.parseInt(ConfigReader.getProperties("ftp.scanInterval"));
				log.info("SystemConfig.xml配置的ftp轮询间隔时间(ftp.scanInterval):" + ftpInterval + "秒");
			} catch (Exception e)
			{
				log.info("SystemConfig.xml未配置ftp轮询间隔时间(ftp.scanInterval),系统采用默认设置:" + Constant.DEFAULT_FTP_INTERVAL
						+ "秒");
				ftpInterval = Constant.DEFAULT_FTP_INTERVAL;
			}

			long ftpFileLastModifyTime = 0;
			try
			{
				ftpFileLastModifyTime = Long.parseLong(ConfigReader.getProperties("ftp.fileLastModifyTime"));
				log.debug("SystemConfig.xml配置的FTP采集最近多长时间内生成的文件(ftp.fileLastModifyTime):" + ftpFileLastModifyTime + "秒");
			} catch (Exception e)
			{
				log.info("SystemConfig.xml未配置FTP采集最近多长时间内生成的文件(ftp.fileLastModifyTime),系统采用默认设置:"
						+ Constant.FTP_FILELASTMODIFYTIME_DEFAULT_VALUE + "秒");
				ftpFileLastModifyTime = Constant.FTP_FILELASTMODIFYTIME_DEFAULT_VALUE;
			}

			FtpThreadTimeoutChecker checker = new FtpThreadTimeoutChecker(ftpTimeOut);// ftp 超时监控线程
			List<FtpServer> ftpServerList = config.getFtpServerList();
			for (int i = 0; i < ftpServerList.size(); i++)
			{
				FtpServer ftpServer = ftpServerList.get(i);

				if (StringUtil.isEmpty(serverStr) || servers.contains(ftpServer.getName()))
				{
					FtpThread ftp = new FtpThread(ftpServer, ftpInterval, ftpFileLastModifyTime, mode); // 启动ftp上传线程
					ftp.start();

					checker.check(ftp); // 纳入超时监控
				}
			}
			checker.start();

			log.info("====================FTP线程启动完成,共启动" + ftpServerList.size() + "个线程====================");
		} catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}
	}

	public static void getParameters(String args[])
	{
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-h") || args[i].equals("-help"))
			{
				help();
			}
			if (args[i].equals("-mode"))
			{
				if (i + 1 == args.length || args[i + 1].charAt(0) == '-')
				{
					help();
				} else
				{
					mode = args[++i];
				}
			} else if (args[i].equals("-server"))
			{
				if (i + 1 == args.length || args[i + 1].charAt(0) == '-')
				{
					help();
				} else
				{
					serverStr = args[++i];
					if (!StringUtil.isEmpty(serverStr))
					{
						servers = StringUtil.getStringListByString(serverStr);
					}
				}
			} else if (args[i].equals("-ftptimeout"))
			{
				if (i + 1 == args.length || args[i + 1].charAt(0) == '-')
				{
					help();
				} else
				{
					ftpTimeOutStr = args[++i];
					if (!StringUtil.isEmpty(ftpTimeOutStr))
					{
						ftpTimeOut = Integer.parseInt(ftpTimeOutStr);
					}
				}
			} else
			{
				System.out.println("Unrecognized option: " + args[i]);
				usage();
				System.exit(-1);
			}
		}
	}
}
