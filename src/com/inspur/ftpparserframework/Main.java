package com.inspur.ftpparserframework;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.inspur.ftpparserframework.config.ConfigReader;
import com.inspur.ftpparserframework.extend.DefaultTempFileCleaner;
import com.inspur.ftpparserframework.extend.IFilePretreater;
import com.inspur.ftpparserframework.extend.ItempFileCleaner;
import com.inspur.ftpparserframework.ftp.FtpThread;
import com.inspur.ftpparserframework.ftp.FtpThreadTimeoutChecker;
import com.inspur.ftpparserframework.ftp.obj.FtpServer;
import com.inspur.ftpparserframework.ftp.obj.FtpServerConfig;
import com.inspur.ftpparserframework.parser.ParseThread;
import com.inspur.ftpparserframework.rmi.IStatusReporter;
import com.inspur.ftpparserframework.rmi.StatusReporterImpl;
import com.inspur.ftpparserframework.util.Constant;
import com.inspur.ftpparserframework.util.DigesterHelper;
import com.inspur.ftpparserframework.util.StringUtil;
import com.inspur.ftpparserframework.util.XMLFactory;
import com.inspur.meta.fpf.rmi.FpfRmiImpl;
import com.inspur.meta.fpf.rmi.IFpfRmi;
import com.inspur.meta.fpf.rmi.pojo.DBConf;
import com.inspur.meta.fpf.rmi.pojo.DBInfo;
import com.inspur.meta.fpf.rmi.pojo.TaskPublishConf;
import com.inspur.meta.fpf.rmi.pojo.TaskPublishConfInfo;

/**
 * FPF���������������ɼ����������̡�<br/> <br/> ����˵��: ./startup.sh [-ftp y|n] [-parse y|n] [-input inputDir] [-server svr1,svr2,svr3]
 * [-debug y|n]<br/> <br/>
 * 
 * �ÿ��������ʵ��ftp�ļ����ؼ��������ĵ��ȿ�ܡ�<br/> <br/>
 * 
 * ����ѡ�����:<br/> <br/>
 * 
 * -h ����<br/> <br/>
 * 
 * -ftp ��ѡ��Ƿ�����FTP���� (y ��|n ��Ĭ��Ϊn��)��<br/> <br/>
 * 
 * -parse ��ѡ��Ƿ������ļ�����(y ��|n ��Ĭ��Ϊn��)��<br/> <br/>
 * 
 * -server ��ѡ�������Ҫ�ɼ��ͽ����ķ�������Ϣ,����������ð�Ƕ��Ÿ����������øò�����Ĭ�ϴ������з����������ݡ�<br/> <br/>
 * 
 * -input ��ѡ��������ļ�·��(Ĭ��Ϊ�����Ŀ¼�µ�../data/inputĿ¼)��<br/> <br/>
 * 
 * -debug ��ѡ��Ƿ����״̬(y ��|n ��Ĭ��Ϊn��),��������״̬����������в������м��ļ�������ɾ����<br/> <br/>
 * 
 * @author �����
 * 
 */
public class Main
{
	private static Logger log = Logger.getLogger(Main.class);

	/**
	 * �Ƿ�����FTP�ļ��ɼ�(y ��|n ��Ĭ��Ϊn��)��
	 */
	public static String ftp = "n";
	/**
	 * �Ƿ������ļ�����(y ��|n ��Ĭ��Ϊn��)
	 */
	public static String parse = "n";
	/**
	 * RMI�˿ں�
	 */
	public static int rmiport = -1;
	/**
	 * �������ļ�����Ŀ¼
	 */
	public static String input = "";
	/**
	 * ʵ������ʱ��
	 */
	public static Date startTime = new Date();
	/**
	 * ʵ��ͨ��������д��ݽ����Ĳ����б�
	 */
	public static Map<String, Object> argParamMap = new HashMap<String, Object>();
	/**
	 * ftp�ɼ���ѯ���ʱ��,��λ��
	 */
	public static long ftpScanInterval;
	/**
	 * ftp�ɼ�����೤ʱ�������ɵ��ļ�,��λ��
	 */
	public static long ftpFileLastModifyTime;
	/**
	 * ftp�ɼ��̳߳�ʱʱ��,��λ��
	 */
	public static long ftpTimeOut;
	/**
	 * �����̳߳�����߳���
	 */
	public static int parserThreadpoolSize;
	/**
	 * �ļ�������ѯɨ����ʱ��,��λ��
	 */
	public static long parserScanInterval;
	/**
	 * �������������FTP�������������FTP��������֮���ö��ŷָ�
	 */
	public static String serverStr = null;

	private static FtpThreadTimeoutChecker checker;
	/**
	 * FTP�������б�
	 */
	public static List<String> servers = new ArrayList<String>();

	public static Map<String, TaskPublishConfInfo> taskPublishConfInfoMap = new HashMap<String, TaskPublishConfInfo>();

	private static void usage()
	{
		System.out
				.println("����˵��: ./startup.sh [-ftp y|n] [-parse y|n] [-rmiport portNo][-input inputDir] [-server svr1,svr2,svr3] [-debug y|n]");
	}

	private static void help()
	{
		usage();
		System.out.println("\n�ÿ��������ʵ��ftp�ļ����ؼ��������ĵ��ȿ�ܡ�");
		System.out.println("\n����ѡ�����:");
		System.out.println("\n\t-h            ����");
		System.out.println("\n\t-ftp          ��ѡ��Ƿ�����FTP����  (y ��|n ��Ĭ��Ϊn��)��");
		System.out.println("\n\t-parse        ��ѡ��Ƿ������ļ�����(y ��|n ��Ĭ��Ϊn��)��");
		System.out.println("\n\t-rmiport      ��ѡ�����RMI��������Ķ˿ںţ������ָ��������RMI����");
		System.out.println("\n\t-server       ��ѡ�������Ҫ�ɼ��ͽ����ķ�������Ϣ,����������ð�Ƕ��Ÿ����������øò�����Ĭ�ϴ������з����������ݡ�");
		System.out.println("\n\t-input        ��ѡ��������ļ�·��(Ĭ��Ϊ�����Ŀ¼�µ�../data/inputĿ¼)��");
		System.out.println("\n\t-debug        ��ѡ��Ƿ����״̬(y ��|n ��Ĭ��Ϊn��),��������״̬����������в������м��ļ�������ɾ����");

		System.exit(-1);
	}

	private static void setGlobalProperties()
	{
		String key = "javax.xml.transform.TransformerFactory";
		String value = "org.apache.xalan.xsltc.trax.TransformerFactoryImpl";
		Properties props = System.getProperties();
		props.put(key, value);
		System.setProperties(props);
	}

	public static void main(String[] args)
	{

		// if (!SignProvider.isLicense())
		// {
		// return;
		// }
		try
		{
			if (args.length > 0)
			{
				// 1����ȡ�����в���
				getParameters(args);
				setGlobalProperties();
				if (serverStr == null)
				{
					log.info("server����δָ������������server������");
				} else
				{
					log.info("server������ָ��������" + serverStr + "������");
				}

				// 2������Զ��RMI�ӿ�
				if (rmiport > 0)
				{
					startStatusReporterServer();
				}

				// 3������FTP�ļ�����
				startFtpDownload();

				// 4�������ļ�����
				startParser();
			} else
			{
				help();
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private static void startStatusReporterServer() throws Exception
	{
		// ����һ��Զ�̶���
		IStatusReporter RStatusReporter = new StatusReporterImpl();
		// ���������ϵ�Զ�̶���ע���Registry��ʵ������ָ���˿�Ϊport����˿ڣ���һ���ز����٣�JavaĬ�϶˿���1099�����ز���ȱ��һ����ȱ��ע������������޷��󶨶���Զ��ע�����
		LocateRegistry.createRegistry(rmiport);
		// ��Զ�̶���ע�ᵽRMIע��������ϣ�������ΪRStatusReporter
		Naming.bind("rmi://localhost:" + rmiport + "/" + IStatusReporter.RMI_NAME, RStatusReporter);
		IFpfRmi rQualityManager = new FpfRmiImpl();
		Naming.bind("rmi://localhost:" + rmiport + "/" + IFpfRmi.RMI_NAME, rQualityManager);
	}

	private static void startParser() throws Exception, InterruptedException
	{
		if ("Y".equalsIgnoreCase(parse))
		{
			log.info("====================�ļ������߳�����====================");
			File dir = Constant.DATA_INPUT_DIR;
			if (StringUtil.isEmpty(input))
			{
				if (!dir.exists())
				{
					dir.mkdirs();
				}
			} else
			{
				dir = new File(input);
			}

			// 1����������̳߳�
			parserThreadpoolSize = 0;
			try
			{
				parserThreadpoolSize = Integer.parseInt(ConfigReader.getProperties("parser.threadpool"));
				log.info("SystemConfig.xml���õĽ����̳߳�����߳���(parser.threadpool):" + parserThreadpoolSize);
			} catch (Exception e)
			{
				log.info("SystemConfig.xmlδ���ý����̳߳�����߳���(parser.threadpool),ϵͳ����Ĭ������:"
						+ Constant.DEFAULT_THREAD_POLL_SIZE);
				parserThreadpoolSize = Constant.DEFAULT_THREAD_POLL_SIZE;
			}

			while (true)
			{
				ExecutorService pool =null;
				//modify by lex 20140619 try��������������½����߳��˳�����
				try
				{
					// 2���������������в�������ʱ�ļ��������������ʱһЩ�����ļ�����������������
					String tempFileCleanerClass = ConfigReader.getProperties("extend.tempFileCleaner");
					if (!StringUtil.isEmpty(tempFileCleanerClass))
					{
						log.info("SystemConfig.xml���õ���ʱ�ļ������� (extend.tempFileCleaner):" + tempFileCleanerClass);
					} else
					{
						tempFileCleanerClass = DefaultTempFileCleaner.class.getName();
						log.warn("SystemConfig.xmlδ������ʱ�ļ�������(extend.tempFileCleaner),�����Ƿ�����,ϵͳʹ��Ĭ�ϵ���ʱ�ļ�������:"
								+ tempFileCleanerClass);
					}
					ItempFileCleaner cleaner = (ItempFileCleaner) Thread.currentThread().getContextClassLoader().loadClass(
							tempFileCleanerClass).newInstance();
					cleaner.clean(dir);

					// 3����������
					pool = Executors.newFixedThreadPool(parserThreadpoolSize);
					parseAndLoad(pool, dir);
					// modify by lex 2014-4-2 start
					// �ļ���������߳�ǰȫ�ִ���
					String filePretreaterClz = ConfigReader.getProperties("extend.filePretreater");
					if (filePretreaterClz != null)
					{
						IFilePretreater filePretreater = (IFilePretreater) Thread.currentThread().getContextClassLoader()
								.loadClass(filePretreaterClz.trim()).newInstance();
						filePretreater.setEnd();
					}
					// modify by lex 2014-4-2 end
				} catch (Exception e)
				{
					log.error(e.getMessage());
					e.printStackTrace();
					
				}finally{
					if (pool!=null)
					{
						pool.shutdown();
						while (!pool.awaitTermination(1, TimeUnit.MILLISECONDS))
							;
					}
				}
				// 4������
				parserScanInterval = 0;
				try
				{
					parserScanInterval = Integer.parseInt(ConfigReader.getProperties("parser.scanInterval"));
					log.info("SystemConfig.xml���õ��ļ�������ѯɨ����ʱ��(parser.scanInterval):" + parserScanInterval + "��");
				} catch (Exception e)
				{
					log.info("SystemConfig.xmlδ�����ļ�������ѯɨ����ʱ��(parser.scanInterval),ϵͳ����Ĭ������:"
							+ Constant.DEFAULT_PARSE_INTERVAL + "��");
					parserScanInterval = Constant.DEFAULT_PARSE_INTERVAL;
				}
				String loopScan = ConfigReader.getProperties("parser.loopScan");
				if (loopScan != null && loopScan.equalsIgnoreCase("false"))
				{
					log.info("������ɣ��˳�����");
					break;
				}
				Thread.sleep(parserScanInterval * 1000l);
			}
		}
	}

	private static void startFtpDownload() throws Exception
	{
		if ("Y".equalsIgnoreCase(ftp))
		{
			log.info("====================FTP�����߳�������ʼ====================");
			FtpServerConfig config = (FtpServerConfig) DigesterHelper.parseFromXmlFile(FtpServerConfig.class, Thread
					.currentThread().getContextClassLoader().getResourceAsStream(Constant.FTP_SERVER_CONFIG_FILE));

			checker = new FtpThreadTimeoutChecker(ftpTimeOut);// ftp ��ʱ����߳�
			List<FtpServer> ftpServerList = config.getFtpServerList();
			for (int i = 0; i < ftpServerList.size(); i++)
			{
				FtpServer ftpServer = ftpServerList.get(i);

				if (StringUtil.isEmpty(serverStr) || servers.contains(ftpServer.getName()))
				{
					FtpThread ftp = new FtpThread(ftpServer, ftpScanInterval, ftpFileLastModifyTime,
							Constant.FTP_DOWNLOAD); // ����ftp�ɼ��߳�
					ftp.start();
					checker.check(ftp); // ���볬ʱ���
				}
			}
			// �����������·��������ļ���Ϣ 20130826 ����� start
			File[] ftpConfDir = Constant.QUALITY_FTP_CONF_DIR.listFiles();
			for (int i = 0; i < ftpConfDir.length; i++)
			{
				String publisth_task_id = ftpConfDir[i].getName();
				String confdir = Constant.QUALITY_FTP_CONF_DIR.getCanonicalPath() + File.separator + publisth_task_id
						+ File.separator + Constant.QUALITY_CONF;
				File testFile = new File(confdir);
				if (testFile.exists())
				{
					addorUpdateTask(publisth_task_id);
				}
			}
			// �����������·��������ļ���Ϣ 20130826 ����� end
			checker.start();

			log.info("====================FTP�����߳��������,������" + ftpServerList.size() + "�������߳�====================");
		}
	}

	private static void getParameters(String args[])
	{
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].startsWith("-"))
			{
				if (i + 1 == args.length || args[i + 1].charAt(0) == '-')
				{
					help();
				} else
				{
					String argName = args[i].replaceFirst("-", "");
					if ("h".equals(argName) || "help".equals(argName))
					{
						help();
					}

					String argValue = args[++i];

					if ("ftp".equals(argName))
					{
						ftp = argValue;
					} else if ("parse".equals(argName))
					{
						parse = argValue;
					} else if ("input".equals(argName))
					{
						input = argValue;
					} else if ("server".equals(argName))
					{
						serverStr = argValue;
					} else if ("rmiport".equals(argName))
					{
						rmiport = Integer.parseInt(argValue);
					} else
					{
						argParamMap.put(argName, argValue);
					}
				}
			}
		}
	}

	/**
	 * ���������ļ�
	 * 
	 * @param file
	 * @throws Exception
	 */
	private static void parseAndLoad(ExecutorService pool, File file) throws Exception
	{
		if (file.isDirectory())
		{
			// �����������ļ����µ��ļ�
			if (file.getName().equalsIgnoreCase("bak") || file.getName().equalsIgnoreCase("bad"))
			{
				return;
			}

			File[] files = file.listFiles();
			if (files == null)
			{
				return;
			}

			for (int i = 0; i < files.length; i++)
			{
				parseAndLoad(pool, files[i]);
			}
		} else
		{
			boolean needParse = false;

			if (!StringUtil.isEmpty(serverStr))// ���������server��Ϣ����ֻ������Щserver���ļ�
			{
				for (int i = 0; i < servers.size(); i++)
				{
					if (file.getCanonicalPath().indexOf(servers.get(i)) >= 0)
					{
						needParse = true;
						break;
					}
				}
			} else
			{
				needParse = true;
			}

			if (needParse)
			{
				// ÿ���ļ�����һ�������̣߳�����Ϊÿһ���̹߳���һ��������paramMap
				Map<String, Object> threadSafeParamMap = new HashMap<String, Object>();
				Iterator<String> iter = argParamMap.keySet().iterator();
				while (iter.hasNext())
				{
					String key = iter.next();
					threadSafeParamMap.put(key, argParamMap.get(key));
				}
				// modify by lex 2014-4-2 start
				// �ļ���������߳�ǰȫ�ִ���
				String filePretreaterClz = ConfigReader.getProperties("extend.filePretreater");
				if (filePretreaterClz != null&&!filePretreaterClz.equals(""))
				{
					if (file.getName().endsWith(IFilePretreater.SUFFIX))
					{// �ļ��Ѿ��Ǵ�������ļ�����������߳�
						Thread parserThread = new ParseThread(file, threadSafeParamMap);
						pool.execute(parserThread);
					} else
					{// ȫ�ִ���
						IFilePretreater filePretreater = (IFilePretreater) Thread.currentThread()
								.getContextClassLoader().loadClass(filePretreaterClz.trim()).newInstance();
						File pretreatedFile = null;
						try
						{//add by lex 20140526 ��ֹ�쳣���³����˳�
							pretreatedFile = filePretreater.filePretreater(file, threadSafeParamMap);
						} catch (Exception e)
						{
							log.error(e.getMessage());
							e.printStackTrace();
						}
						if (pretreatedFile != null)
						{// ��������ļ�����������߳�
							Thread parserThread = new ParseThread(pretreatedFile, threadSafeParamMap);
							pool.execute(parserThread);
						}
					}
				} else
				{// ����ȫ�ִ�����ֱ�ӽ�������߳�
					Thread parserThread = new ParseThread(file, threadSafeParamMap);
					pool.execute(parserThread);
				}
				// modify by lex 2014-4-2 end

				// Thread parserThread = new ParseThread(file, threadSafeParamMap);
				// pool.execute(parserThread);
			} else
			{
				log.debug(file.getCanonicalPath() + "�����ڱ�����Ҫ�����ķ��������ļ������ԡ�");
			}
		}
	}

	/**
	 * ����Task
	 * 
	 * @param publisth_task_id
	 * @return
	 * @throws Exception
	 */
	public static String addorUpdateTask(String publisth_task_id) throws Exception
	{
		// String returnJson = null;
		Boolean success = false;
		if ("Y".equalsIgnoreCase(ftp))
		{
			if (checker == null)
			{
				checker = new FtpThreadTimeoutChecker(ftpTimeOut);
				checker.start();
			}
			InputStream fis = null;
			try
			{
				TaskPublishConfInfo taskPublishConfInfo = new TaskPublishConfInfo();
				taskPublishConfInfo.setPublisth_task_id(publisth_task_id);
				// 1����ȡ�����Ӧ��ftp��db���
				String confDir = Constant.QUALITY_FTP_CONF_DIR.getCanonicalPath() + File.separator + publisth_task_id
						+ File.separator + Constant.QUALITY_CONF;
				XMLFactory taskPublishConfFactory = new XMLFactory(TaskPublishConf.class);
				fis = new FileInputStream(confDir);
				TaskPublishConf taskPublishConf = taskPublishConfFactory.unmarshal(fis);
				fis.close();
				String ftp_id = taskPublishConf.getFtp_id();
				String db_id = taskPublishConf.getDb_id();
				// 2������ftp��Ų���ftp��Ϣ�����������߳�
				String ftpServerConfigDir = Constant.QUALITY_FTP_CONF_FILE.getCanonicalPath();
				FtpServerConfig config = (FtpServerConfig) DigesterHelper.parseFromXmlFile(FtpServerConfig.class,
						ftpServerConfigDir);
				List<FtpServer> ftpServerList = config.getFtpServerList();
				for (int i = 0; i < ftpServerList.size(); i++)
				{
					FtpServer ftpServer = ftpServerList.get(i);
					if (ftp_id.equals(ftpServer.getId()))
					{
						ftpServer.setTaskPublishId(publisth_task_id);
						taskPublishConfInfo.setFtpServer(ftpServer);
						checker.check(ftpServer); // ���볬ʱ���
						break;
					}
				}
				// 3������db��Ų���db��Ϣ�������Ա����ʱ��ʹ��
				String dBConfDir = Constant.QUALITY_DB_CONF_FILE.getCanonicalPath();
				XMLFactory dBConfFactory = new XMLFactory(DBConf.class);
				fis = new FileInputStream(dBConfDir);
				DBConf dBConf = dBConfFactory.unmarshal(fis);
				fis.close();
				List<DBInfo> dbinfos = dBConf.getdBInfos();
				for (int i = 0; i < dbinfos.size(); i++)
				{
					DBInfo dBInfo = dbinfos.get(i);
					if (db_id.equals(dBInfo.getId()))
					{
						taskPublishConfInfo.setDBInfo(dBInfo);
						break;
					}
				}
				taskPublishConfInfoMap.put(taskPublishConfInfo.getPublisth_task_id(), taskPublishConfInfo);
				success = true;
			} catch (Exception e)
			{
				success = false;
				e.printStackTrace();
				throw e;
			} finally
			{
				if (fis != null)
				{
					try
					{
						fis.close();
					} catch (IOException e)
					{
					}
				}
			}
		} else
		{
			log.warn("δ����FTP���ع��ܣ���������FTP���ط���!!!");
			success = false;
			throw new Exception("δ����FTP���ع��ܣ���������FTP���ط���!!!");
		}
		return success.toString();
	}

	/**
	 * ɾ��Task
	 * 
	 * @param publisth_task_name
	 * @return
	 * @throws Exception
	 */
	public static String delTask(String publisth_task_id)
	{
		TaskPublishConfInfo taskPublishConfInfo = taskPublishConfInfoMap.get(publisth_task_id);
		checker.delFtp(taskPublishConfInfo.getFtpServer());
		return "true";
	}

	// ��ǰ��ʼ�������������ط�ʹ�á�
	static
	{
		ftpScanInterval = 0;
		try
		{
			ftpScanInterval = Integer.parseInt(ConfigReader.getProperties("ftp.scanInterval"));
			log.info("SystemConfig.xml���õ�ftp��ѯ���ʱ��(ftp.scanInterval):" + ftpScanInterval + "��");
		} catch (Exception e)
		{
			log.info("SystemConfig.xmlδ����ftp��ѯ���ʱ��(ftp.scanInterval),ϵͳ����Ĭ������:" + Constant.DEFAULT_FTP_INTERVAL + "��");
			ftpScanInterval = Constant.DEFAULT_FTP_INTERVAL;
		}

		ftpFileLastModifyTime = 0;
		try
		{
			ftpFileLastModifyTime = Long.parseLong(ConfigReader.getProperties("ftp.fileLastModifyTime"));
			log.debug("SystemConfig.xml���õ�FTP�ɼ�����೤ʱ�������ɵ��ļ�(ftp.fileLastModifyTime):" + ftpFileLastModifyTime + "��");
		} catch (Exception e)
		{
			log.info("SystemConfig.xmlδ����FTP�ɼ�����೤ʱ�������ɵ��ļ�(ftp.fileLastModifyTime),ϵͳ����Ĭ������:"
					+ Constant.FTP_FILELASTMODIFYTIME_DEFAULT_VALUE + "��");
			ftpFileLastModifyTime = Constant.FTP_FILELASTMODIFYTIME_DEFAULT_VALUE;
		}

		ftpTimeOut = 0;
		try
		{
			ftpTimeOut = Integer.parseInt(ConfigReader.getProperties("ftp.timeOut"));
			log.info("SystemConfig.xml���õ�ftp��ʱʱ��(ftp.timeOut):" + ftpTimeOut + "��");
		} catch (Exception e)
		{
			log.info("SystemConfig.xmlδ����ftp��ʱʱ��(ftp.timeOut),ϵͳ����Ĭ������:" + Constant.DEVAULT_FTP_TIME_OUT + "��");
			ftpTimeOut = Constant.DEVAULT_FTP_TIME_OUT;
		}

	}
}