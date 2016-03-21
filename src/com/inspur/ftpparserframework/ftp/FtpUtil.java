package com.inspur.ftpparserframework.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;

import com.inspur.ftpparserframework.config.ConfigReader;
import com.inspur.ftpparserframework.ftp.obj.FtpServer;
import com.inspur.ftpparserframework.rmi.StatusCenter;
import com.inspur.ftpparserframework.util.Constant;
import com.inspur.ftpparserframework.util.FilterUtil;
import com.inspur.ftpparserframework.util.StringUtil;
import com.inspur.ftpparserframework.util.TimeUtil;

/**
 * FTP�ļ������ϴ�������
 * 
 * @author �����
 * 
 */
public class FtpUtil
{
	private static Logger log = Logger.getLogger(FtpUtil.class);

	/**
	 * ����ĳ��Ŀ¼�µ������ļ�������Ŀ¼Ƕ�׵���������
	 * 
	 * @param ftpthread
	 * @param ip
	 * @param port
	 * @param user
	 * @param password
	 * @param ftpdir
	 * @param storedir
	 * @param filter
	 * @param fileLastModifyTime
	 * @param isReDownload
	 * @return
	 * @throws Exception
	 */
	public static boolean downloadNested(FtpThread ftpthread, String ip, int port, String user, String password,
			String ftpdir, String storedir, String filter, long fileLastModifyTime, boolean isReDownload)
			throws Exception
	{
		String ftpServerName = ftpthread != null ? ftpthread.getFtpServer().getName() : ip;
		FTPClient ftp = null;
		boolean rst = true;
		// int downloadFileCount = 0;
		Date startTime = new Date();
		MyFTPThreadPool myFTPThreadPool = null;// add by lex 20140619
		try
		{
			// add by lex 20140619 start
			int threadpool = ftpthread.getFtpServer().getThreadpool();
			if (threadpool > 1)
			{
				myFTPThreadPool = new MyFTPThreadPool(threadpool);
			}
			// add by lex 20140619 end

			// ��־��ʽ��#�ļ��ݹ�����#-[��ʼ]-[FTP������=xx,Ŀ¼=yy,��������=ff]
			log.info("#�ļ��ݹ�����#-[��ʼ]-[FTP������=" + ftpServerName + ",Ŀ¼=" + ftpdir + ",��������=" + filter + "]");

			// ��ʽ���ļ�·��
			storedir = storedir.replace('\\', '/');
			if (!storedir.endsWith("/"))
			{
				storedir += "/";
			}

			// ����FTP
			ftp = connect(ftpServerName, ip, port, user, password);
			ftpthread.setFtp(ftp);// ��ظ�ֵ

			// ��ȡFTP��������ǰʱ��
			Calendar ftpTimeNow = getFtpTimeNow(ftpServerName, ftp, ftpdir);

			// ��ȡ�����������ص��ļ��б�
			// List<String> alreayDlFiles = FilterUtil.getRecordFileList(FilterUtil.PREFIX_DOWNLOAD);
			Set<String> alreayDlFiles = FilterUtil.getRecordFileList(FilterUtil.PREFIX_DOWNLOAD);// modify by lex
			// 20140619,��List��ΪSet����ѯЧ��
			// �����ļ�
			FileCount downloadFileCount = new FileCount();

			downloadNested(ftpServerName, ftpthread, ftp, ftpdir, filter, storedir, ftpTimeNow, fileLastModifyTime,
					isReDownload, alreayDlFiles, myFTPThreadPool, downloadFileCount);
			if (myFTPThreadPool != null)
			{
				myFTPThreadPool.shutdown();
				myFTPThreadPool=null;
			}
			// ��־��ʽ��#�ļ��ݹ�����#-[����]-[FTP������=xx,Ŀ¼=yy,��������=ff]-[�����ļ���=mm]-[��ʱ=nn����]
			log.info("#�ļ��ݹ�����#-[����]-[FTP������=" + ftpServerName + ",Ŀ¼=" + ftpdir + ",��������=" + filter + "]-[�����ļ���="
					+ downloadFileCount.getNum() + "]-[��ʱ=" + (new Date().getTime() - startTime.getTime()) + "����]");
		} catch (Exception e)
		{
			// ��־��ʽ��#�ļ��ݹ�����#-[����]-[FTP������=xx,Ŀ¼=yy,��������=ff]-[��ʱ=nn����]-[������Ϣ=ee]
			log.info("#�ļ��ݹ�����#-[����]-[FTP������=" + ftpServerName + ",Ŀ¼=" + ftpdir + ",��������=" + filter + "]-[��ʱ="
					+ (new Date().getTime() - startTime.getTime()) + "����]-[������Ϣ=" + e.getMessage() + "]");

			throw e;// ������������׳�����ftp��ʱ����̲߳����Ա������ɼ��̡߳�����
		} finally
		{
			if (myFTPThreadPool != null)
			{
				myFTPThreadPool.shutdown();
				myFTPThreadPool=null;
			}
			try
			{
				if (ftp != null)
				{
					ftp.disconnect();
					ftpthread.setFtp(null);// ��ظ�ֵΪ��
					ftp = null;
				}
			} catch (IOException ex)
			{
			}
		}
		return rst;
	}

	public static void downloadNested(String ftpServerName, FtpThread ftpthread, FTPClient ftp, String pathname,
			String filter, String storeDir, Calendar ftpTimeNow, long fileLastModifyTime, boolean isReDownload,
			Set<String> alreayDlFiles, MyFTPThreadPool myFTPThreadPool, FileCount downloadFileCount) throws Exception
	{
		long starTime = System.currentTimeMillis();
		// int downloadFileCount = 0;

		// �л�Ŀ¼����ȡ�ļ��б�
		ftp.changeWorkingDirectory(pathname);
		// modify by wuyg 2014-3-21,ע�͵�������һ������������ģʽ��connect��������ͳһ����
		// ftp.enterLocalPassiveMode();
		FTPFile[] files = ftp.listFiles(filter);
		if (files == null || files.length == 0)
		{
			files = ftp.listFiles();// �����ļ���Ƕ��ʱ�����˵�
		}
		log.info("�ļ��б����:Ŀ¼="+pathname+",�ļ�����=" + files.length + ",��ʱ=" + (System.currentTimeMillis() - starTime) + "����");
		if (files == null || files.length == 0)
		{
			log
					.info("FTP������"
							+ (ftpthread != null ? ftpthread.getFtpServer().getName() : ftp.getRemoteAddress()
									.getHostAddress()) + "��Ŀ¼" + pathname + "��û���κη��Ϲ�����(" + filter + ")���ļ���");
			return;
		}

		boolean needFiltered = !StringUtil.isEmpty(filter);// �Ƿ���Ҫ����
		String[] names = null;
		long listNameStarTime = System.currentTimeMillis();
		if (needFiltered)
		{
			names = ftp.listNames(filter);
		} else
		{
			names = ftp.listNames();
		}
		Set<String> nameSet=new HashSet<String>();
		if (names!=null&&names.length>0)
		{
			log.debug("�ļ����б����:Ŀ¼="+pathname+",�ļ�������=" + names.length + ",��ʱ=" + (System.currentTimeMillis() - listNameStarTime) + "����");
			listNameStarTime = System.currentTimeMillis();
			for (int i = 0; i < names.length; i++)
			{
				nameSet.add(names[i]);
			}
			log.debug("�ļ����б����ӵ�Set���:Ŀ¼="+pathname+",�ļ�������=" + names.length + ",��ʱ=" + (System.currentTimeMillis() - listNameStarTime) + "����");
		}
		// 2���������ֳ���ǰĿ¼�µ��ļ�����Ŀ¼
		List<FTPFile> ftpFiles = new ArrayList<FTPFile>();// �ļ�
		List<FTPFile> ftpDirs = new ArrayList<FTPFile>();// Ŀ¼

		for (int i = 0; i < files.length; i++)
		{
			FTPFile file = files[i];

			if (file.getName().equals(".") || file.getName().equals(".."))
			{
				continue;
			}

			if (file.isDirectory())
			{
				ftpDirs.add(file);
			} else
			{
				ftpFiles.add(file);
			}
		}
		// 3���ȴ�����ǰĿ¼�µ��ļ�
		for (int i = 0; i < ftpFiles.size(); i++)
		{
			// ����һ���ļ�ʱ����Ϊ����
			ftpthread.setStartTime(new Date());
			if (i % 1000 == 0 || i == ftpFiles.size() - 1)
			{
				log.info("FTP������=" + ftpServerName + ",�����ļ���=" + i);
			}

			// ��ȡ�ļ�
			FTPFile file = ftpFiles.get(i);

			// �ж��ļ�������ʱ���Ƿ��ڲɼ�Ҫ��ķ�Χ�ڡ� �������ftp�ļ��ϴ���Ȩ�ޣ�����ϴ�һ���ļ�����ȡ��ʱ����Ϊftp�������ĵ�ǰʱ��;������ʹ�òɼ���������ʱ�䣨���Ҫ��ɼ���������ftp������ʱ��Ҫͬ������
			long timeNow = System.currentTimeMillis();
			if (ftpTimeNow != null)
			{
				timeNow = ftpTimeNow.getTimeInMillis();
			}

			// 3.1���ж��ļ��Ƿ������n�������ɵ��ļ���Ĭ��ֻ�������1��Сʱ���ɵ��ļ�
			if (!isReDownload)// ����ʱ�������ļ�����ʱ��
			{
				if (timeNow - file.getTimestamp().getTimeInMillis() > fileLastModifyTime * 1000l)
				{
					log.debug(file.getName() + "��ftp�������������ѳ���:" + fileLastModifyTime + "�룬���ٲɼ���");
					continue;
				}
			}

			// 3.2���ж��ļ���ftp������������ʱ���Ƿ񳬹�20��
			if (timeNow - file.getTimestamp().getTimeInMillis() < 20 * 1000l)
			{
				// (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date(System.currentTimeMillis()))
				log.debug(file.getName() + "��ftp�����������ɲ���:" + 20 + "�룬�ݲ��ɼ���");
				continue;
			}

			// 3.3���ж��ļ���С�Ƿ�Ϊ0
			if (file.getSize() == 0)
			{
				log.debug(file.getName() + "��СΪ0���ݲ��ɼ���");
				continue;
			}

			// 3.4�����й���
			boolean downloadSucc = false;
			if (!needFiltered)
			{
				// downloadSucc = downloadOnce(ftpthread, ftp, file, storeDir, isReDownload, alreayDlFiles);
				// add by lex 20140619 start
				if (myFTPThreadPool != null)
				{
					Thread upDownLoadThread = new UpDownLoadThread(ftpthread, file, pathname,
							storeDir, isReDownload ? Constant.FTP_RE_DOWNLOAD : Constant.FTP_DOWNLOAD, alreayDlFiles,
							downloadFileCount,myFTPThreadPool);
					myFTPThreadPool.execute(upDownLoadThread);
				} else
				{
					downloadSucc = downloadOnce(ftpthread, ftp, file, storeDir, isReDownload, alreayDlFiles);
				}
				// add by lex 20140619 end
			} else
			{
				boolean needDownload = false;

//				if (names != null)
//				{
//					for (int j = 0; j < names.length; j++)
//					{
//						if (file.getName().equals(names[j]))
//						{
//							needDownload = true;
//							break;
//						}
//					}
//				}
				if (nameSet.contains(file.getName()));
				{
					needDownload = true;
				}

				if (needDownload)
				{
					// downloadSucc = downloadOnce(ftpthread, ftp, file, storeDir, isReDownload, alreayDlFiles);
					// add by lex 20140619 start
					if (myFTPThreadPool != null)
					{
						Thread upDownLoadThread = new UpDownLoadThread(ftpthread, file, pathname,
								storeDir, isReDownload ? Constant.FTP_RE_DOWNLOAD : Constant.FTP_DOWNLOAD,
								alreayDlFiles, downloadFileCount,myFTPThreadPool);
						myFTPThreadPool.execute(upDownLoadThread);
					} else
					{
						downloadSucc = downloadOnce(ftpthread, ftp, file, storeDir, isReDownload, alreayDlFiles);
					}
					// add by lex 20140619 end
				} else
				{
					log.debug("FTP������" + ftp.getRemoteAddress().getHostAddress() + "Ŀ¼" + pathname + "�µ��ļ�" + file
							+ "�����Ϲ�������(" + filter + ")Ҫ��,�����ء�");
				}
			}
			// ���سɹ��ļ�����1
			if (downloadSucc)
			{
				// downloadFileCount++;
				downloadFileCount.addOne();
			}
		}

		// 4���ٴ�����ǰĿ¼�µ���Ŀ¼
		for (int i = 0; i < ftpDirs.size(); i++)
		{
			FTPFile file = ftpDirs.get(i);
			downloadNested(ftpServerName, ftpthread, ftp, pathname + "/" + file.getName(), filter, storeDir + "/"
					+ file.getName(), ftpTimeNow, fileLastModifyTime, isReDownload, alreayDlFiles, myFTPThreadPool,
					downloadFileCount);
		}
		// return downloadFileCount;
	}

	/**
	 * ֻ����һ�Σ����۳ɹ����
	 * 
	 * @param ftp
	 * @param srcFileName
	 * @param storedir
	 * @param delete
	 * @return
	 */
	public static boolean downloadOnce(FtpThread ftpthread, FTPClient ftp, FTPFile srcFile, String storedir,
			boolean isReDownload, Set<String> alreayDlFiles) throws Exception
	{
		String srcFileInfo = "[Դ�ļ�=" + ftp.printWorkingDirectory() + "/" + srcFile.getName() + ",��С="
				+ srcFile.getSize() + "Byte,ʱ��=" + TimeUtil.date2str(srcFile.getTimestamp().getTime()) + "]";
		Date dlStartTime = new Date();
		String ftpServerName = ftpthread != null ? ftpthread.getFtpServer().getName() : "";

		FileOutputStream fout = null;// Ŀ���ļ���
		try
		{
			boolean rst = true;

			// 1�������ļ������ڹ�������.tmp��ʶ
			String srcFileName = srcFile.getName();
			storedir = storedir.replace('\\', '/');
			if (!storedir.endsWith("/"))
			{
				storedir += "/";
			}
			File destFileTmp = new File(storedir + srcFileName + ".tmp");
			File destFile = new File(storedir + srcFileName);
			if (!destFileTmp.getParentFile().exists())
			{
				destFileTmp.getParentFile().mkdirs();
			}

			// 2���ж��ļ��Ƿ��Ѿ����ع�
			String recordInfo = destFile.getCanonicalPath() + "-"
					+ TimeUtil.date2str(srcFile.getTimestamp().getTime(), "yyyyMMddHHmmss");
			if (isReDownload)// ����
			{
				if (!FilterUtil.shouldReDownload(recordInfo))
					return false;
			} else
			{
				if (alreayDlFiles.contains(recordInfo))
				{
					return false;
				}
			}

			// 3������Ϊ��ʱ�ļ�
			// ��־��ʽ��#���ļ�����#-[��ʼ]-[FTP��������=FTPSVR]-[Դ�ļ�=xx,��С=xxByte,ʱ��=yyyy-MM-dd HH:mm:ss]
			log.info("#���ļ�����#-[��ʼ]-[FTP������=" + ftpServerName + "]-" + srcFileInfo);
			fout = new FileOutputStream(destFileTmp);
			// modify by wuyg 2014-3-21,ע�͵�������һ������������ģʽ��connect��������ͳһ����
			// ftp.enterLocalPassiveMode();
			rst = ftp.retrieveFile(srcFileName, fout);
			fout.close();// ����رգ�������ʱ�ļ��޷���������

			// 4�����غ����ʱ�ļ�������Ϊ��ʽ�ļ�
			if (destFile.exists())
			{
				rst = destFile.delete();
			}
			rst &= destFileTmp.renameTo(destFile);

			// 5������FTP�̵߳����»ʱ��
			if (ftpthread != null)
			{
				ftpthread.setStartTime(new Date());
			}

			// 6�������ص��ļ�����м�¼,��������������ļ���СΪ0�򲻼�¼
			if (destFile.length() == srcFile.getSize())
			{
				FilterUtil.recordDownloadedFile(recordInfo);
				// ��־��ʽ��#���ļ�����#-[����]-[FTP��������=FTPSVR]-[Դ�ļ�=xx,��С=xxByte,ʱ��=yyyy-MM-dd
				// HH:mm:ss]-[Ŀ���ļ�=yy,��С=xxByte,ʱ��=yyyy-MM-dd HH:mm:ss]-[��ʱ=n����]
				log.info("#���ļ�����#-[����]-[FTP������=" + ftpServerName + "]" + "-" + srcFileInfo + "-[Ŀ���ļ�="
						+ destFile.getCanonicalPath() + ",��С=" + destFile.length() + "Byte,ʱ��="
						+ TimeUtil.date2str(destFile.lastModified()) + "]" + "-[��ʱ="
						+ (new Date().getTime() - dlStartTime.getTime()) + "����]");

				// ��¼���سɹ��ļ���
				StatusCenter.oneFileDlSucc();
				// ɾ�����������ļ�
				if (ftpthread.getFtpServer().isDelete())
				{
					log.info("#����˵��ļ�ɾ��#-[��ʼ]-[FTP������=" + ftpServerName + "]-" + srcFileInfo);
					Date delStartTime = new Date();
					boolean isDelete = false;
					try
					{
						isDelete = ftp.deleteFile(srcFile.getName());
						if (isDelete)
						{
							log.info("#����˵��ļ�ɾ��#-[����]-[FTP������=" + ftpServerName + "]" + "-" + srcFileInfo + "-[��ʱ="
									+ (new Date().getTime() - delStartTime.getTime()) + "����]");
						} else
						{
							log.info("#����˵��ļ�ɾ��#-[����]-[FTP������=" + ftpServerName + "]" + "-" + srcFileInfo + "-[��ʱ="
									+ (new Date().getTime() - delStartTime.getTime()) + "����]");
						}
					} catch (Exception e)
					{
						log.info("#����˵��ļ�ɾ��#-[����]-[FTP������=" + ftpServerName + "]" + "-" + srcFileInfo + "-[��ʱ="
								+ (new Date().getTime() - delStartTime.getTime()) + "����]" + "-[������Ϣ=" + e.getMessage()
								+ "]");
					}

				}

			} else
			{
				// ��־��ʽ��#���ļ�����#-[����]-[FTP��������=FTPSVR]-[Դ�ļ�=xx,��С=xxByte,ʱ��=yyyy-MM-dd HH:mm:ss]-[��ʱ=n����]-[������Ϣ=xx]
				log.error("#���ļ�����#-[����]-[FTP������=" + ftpServerName + "]-" + srcFileInfo + "-[��ʱ="
						+ (new Date().getTime() - dlStartTime.getTime()) + "����]");
				if (rst)
				{// add by lex 20140621���ļ��ɹ���¼��������"#���ļ�����#-[����]",���ɴ˴����⣬����֤
					log.warn("���سɹ������غ���ļ���С��ԭ�ļ�������Ȼ��Ϊ����ʧ��");
					rst = false;
				}

				// ��¼����ʧ���ļ���
				StatusCenter.oneFileDlError();
			}
			return rst;
		} catch (Exception e)
		{
			// ��־��ʽ��#���ļ�����#-[����]-[FTP��������=FTPSVR]-[Դ�ļ�=xx,��С=xxByte,ʱ��=yyyy-MM-dd HH:mm:ss]-[��ʱ=n����]-[������Ϣ=xx]
			log.error("#���ļ�����#-[����]-[FTP������=" + ftpServerName + "]-" + srcFileInfo + "-[��ʱ="
					+ (new Date().getTime() - dlStartTime.getTime()) + "����]");

			// ��¼����ʧ���ļ���
			StatusCenter.oneFileDlError();

			throw e;
		} finally
		{
			if (fout != null)
			{
				fout.close();
			}
		}
	}

	/**
	 * �ϴ�ĳ��Ŀ¼�µ������ļ�������Ŀ¼Ƕ�׵��ϴ�����
	 * 
	 * @param ip
	 * @param port
	 * @param ftpUser
	 * @param ftpPwd
	 * @param ftpDir
	 * @param localDirOrFile
	 */
	public static void uploadNested(FtpThread ftpthread, String ip, int port, String ftpUser, String ftpPwd,
			String ftpDir, String localDirOrFile) throws Exception
	{
		FTPClient ftp = null;
		try
		{
			File local = new File(localDirOrFile);
			ftp = connect((ftpthread != null ? ftpthread.getFtpServer().getName() : ip), ip, port, ftpUser, ftpPwd);
			uploadNested(ftpthread, ftp, ftpDir, local.getCanonicalPath().replaceAll("\\\\", "/"), local);
		}
		// ���������д��󲶻�ֱ���׳�����ftp��ʱ����̴߳�������
		finally
		{
			try
			{
				if (ftp != null)
					ftp.disconnect();
			} catch (Exception e)
			{
			}
		}
	}

	private static void uploadNested(FtpThread ftpthread, FTPClient ftp, String ftpDir, String localDir, File file)
			throws Exception
	{
		if (file.isDirectory())
		{
			File[] files = file.listFiles();
			if (files != null)
			{
				for (int i = 0; i < files.length; i++)
				{
					File f = files[i];
					if (f.isDirectory())
					{
						uploadNested(ftpthread, ftp, ftpDir, localDir, f);
					} else
					{
						uploadOnce(ftpthread, ftp, ftpDir, localDir, f);
					}
				}
			}
		} else
		{
			uploadOnce(ftpthread, ftp, ftpDir, localDir, file);
		}
	}

	private static void uploadOnce(FtpThread ftpthread, FTPClient ftp, String ftpDir, String localDir, File file)
			throws Exception
	{
		FileInputStream is = null;
		try
		{
			// �ж��ļ��Ƿ������n�������ɵ��ļ���Ĭ��ֻ�������1��Сʱ���ɵ��ļ�
			long fileLastMofidyTime = 0;// ��λ ��
			try
			{
				fileLastMofidyTime = Long.parseLong(ConfigReader.getProperties("ftp.fileLastModifyTime"));
				log.debug("SystemConfig.xml���õ�FTP�ϴ�����೤ʱ�������ɵ��ļ�(ftp.fileLastModifyTime):" + fileLastMofidyTime);
			} catch (Exception e)
			{
				log.info("SystemConfig.xmlδ����FTP�ϴ�����೤ʱ�������ɵ��ļ�(ftp.fileLastModifyTime),ϵͳ����Ĭ������:"
						+ Constant.FTP_FILELASTMODIFYTIME_DEFAULT_VALUE);
				fileLastMofidyTime = Constant.FTP_FILELASTMODIFYTIME_DEFAULT_VALUE;
			}

			// �ж��ļ�������ʱ���Ƿ��ڲɼ�Ҫ��ķ�Χ�ڡ�
			long timeNow = System.currentTimeMillis();

			if (timeNow - file.lastModified() > fileLastMofidyTime * 1000l)
			{
				log.debug(file.getCanonicalPath() + "�����ѳ���:" + fileLastMofidyTime + "�룬�����ϴ���");
				return;
			}
			if (timeNow - file.lastModified() < 20 * 1000l)
			{
				log.debug(file.getCanonicalPath() + "���ɲ���:" + 20 + "�룬�ݲ��ϴ���");
				return;
			}

			// ���ϴ��������ظ��ϴ�
			if (!FilterUtil.shouldUpload(file))
			{
				log.debug("�ļ����ϴ����������ظ��ϴ���" + file.getCanonicalPath());
				return;
			}

			File sourceFile = file;
			String srcParentpathRelative = file.getParentFile().getCanonicalPath().replaceAll("\\\\", "/").replaceAll(
					localDir, "");

			String destFileName = sourceFile.getName();
			String destParentPath = ftpDir + srcParentpathRelative;

			String tempDestFileName = destFileName + ".tmp";

			// ��Ŀ¼
			ftp.changeWorkingDirectory(ftpDir);
			String[] dirs = srcParentpathRelative.split("/");
			for (int i = 0; i < dirs.length; i++)
			{
				String dir = dirs[i];
				ftp.makeDirectory(dir);
				ftp.changeWorkingDirectory(dir);
			}

			ftp.changeWorkingDirectory(destParentPath);

			// �洢�ļ���ftp��/ftpDirĿ¼��,�ϴ�������ʹ���ļ���+.tmp��׺����ǡ�
			log.info("�ļ��ϴ���ʼ��" + file.getCanonicalPath());
			is = new FileInputStream(sourceFile);
			ftp.storeFile(tempDestFileName, is);

			// �ϴ���ɺ�Ļ�ԭ��
			ftp.rename(tempDestFileName, destFileName);

			// ��¼���ϴ����ļ�
			FilterUtil.recordUploadedFile(file);

			// ����FTP�̵߳����»ʱ��
			if (ftpthread != null)
			{
				ftpthread.setStartTime(new Date());
			}

			log.info("�ļ��ϴ���ɣ�" + file.getCanonicalPath());
		} finally
		{
			if (is != null)
			{
				is.close();
			}
		}
	}

	/**
	 * ���ӵ�ftp������
	 * 
	 * @param ip
	 * @param port
	 * @param ftpUser
	 * @param ftpPwd
	 * @return
	 */
	public static FTPClient connect(String ftpServerName, String ip, int port, String ftpUser, String ftpPwd)
			throws Exception
	{
		try
		{
			// ��־��ʽ��#FTP����������#-[��ʼ]-[FTP������=xx]
			log.info("#FTP����������#-[��ʼ]-[FTP������=" + ftpServerName + "]");

			FTPClient ftp = new FTPClient();

			ftp.connect(ip, port);
			ftp.login(ftpUser, ftpPwd);

			// ftp.setFileType(FTP.ASCII_FILE_TYPE); // ASCII_FILE_TYPE��ʽ�����ļ�

			ftp.setFileType(FTP.BINARY_FILE_TYPE);// BINARY_FILE_TYPE��ʽ�����ļ�

			// modify by wuyg 2014-3-21
			setFtpMode(ftp);

			// ��־��ʽ��#FTP����������#-[����]-[FTP������=xx]
			log.info("#FTP����������#-[����]-[FTP������=" + ftpServerName + "]");

			return ftp;
		} catch (Exception e)
		{
			// ��־��ʽ��#FTP����������#-[����]-[FTP������=xx]-[������Ϣ=ee]
			log.error("#FTP����������#-[����]-[FTP������=" + ftpServerName + "]-[������Ϣ=" + e.getMessage() + "]");
			throw e;
		}
	}

	private static void setFtpMode(FTPClient ftp) throws Exception
	{
		// modify by wuyg 2014-3-21������FTPģʽ������ģʽ�򱻶�ģʽ��һ��Ĭ�ϲ��ñ���ģʽ
		String ftpMode = ConfigReader.getProperties("ftp.mode");
		if (StringUtil.isEmpty(ftpMode))
		{
			ftpMode = Constant.FTP_MODE_PASV;// Ĭ��Ϊ����ģʽ

			log.info("SystemConfig.xml��û������ftp��ģʽ(ftp.mode),Ĭ�ϲ���:" + ftpMode);
		}

		if (Constant.FTP_MODE_PASV.equalsIgnoreCase(ftpMode))
		{
			/**
			 * FTPClient.listFiles()����FTPClient.retrieveFile()����ʱ����ֹͣ�����ʲô��Ӧ��û�� �� ���ּ���״̬���ڵ�������������֮ǰ�� ����FTPClient.
			 * enterLocalPassiveMode();�����������˼����ÿ����������֮ǰ��ftp client����ftp server��ͨһ���˿����������ݡ�ΪʲôҪ�������أ���Ϊftp
			 * server����ÿ�ο�����ͬ�Ķ˿����������� ��������linux�ϣ����ڰ�ȫ���ƣ�����ĳЩ�˿�û�п��������Ծͳ���������OK����������
			 */
			ftp.enterLocalPassiveMode();
			log.info("FTP����PASV����ģʽ");
		} else if (Constant.FTP_MODE_PORT.equalsIgnoreCase(ftpMode))
		{
			ftp.enterLocalActiveMode();
			log.info("FTP����PORT����ģʽ,���ص�ַ��" + ftp.getLocalAddress().getHostAddress() + "");
			// ftp.setActivePortRange(10000, 60000);
		}
	}

	private static Calendar getFtpTimeNow(String ftpServerName, FTPClient ftp, String ftpdir) throws Exception
	{
		Calendar ftpTimeNow = null;
		FileInputStream checkTimeFileInput = null;
		try
		{
			// �ϴ�һ�����ļ�
			File f = new File(Constant.DATA_DIR, "inspur_check_time.txt");
			if (!f.getParentFile().exists())
			{
				f.mkdirs();
			}
			if (!f.exists())
			{
				f.createNewFile();
			}
			if (f.length() == 0)
			{
				PrintWriter pw = new PrintWriter(f);
				pw.println("get the ftp server time");
				pw.close();
			}

			checkTimeFileInput = new FileInputStream(f);
			boolean isStoreFile = ftp.storeFile(f.getName(), checkTimeFileInput);

			// ��ȡ������ļ���ʱ����Ϊftp�������ĵ�ǰʱ��
			FTPFile[] fs = ftp.listFiles(f.getName());
			if (isStoreFile && fs != null && fs.length > 0)
			{
				FTPFile fu = fs[0];
				ftpTimeNow = fu.getTimestamp();
				if (ftpTimeNow != null)
				{
					log.info("[FTP������=" + ftpServerName + ",FTP��������ǰʱ��=" + TimeUtil.date2str(ftpTimeNow.getTime())
							+ ",�ɼ���������ǰʱ��=" + TimeUtil.nowTime2str() + ",FTP������ʱ��-�ɼ�������ʱ��="
							+ (ftpTimeNow.getTimeInMillis() - new Date().getTime()) + "����]");
				}
			} else
			{
				log.error("[FTP������=" + ftpServerName + ",��ȡFTP��������ǰʱ��ʧ�ܣ�����÷�����FTP�����Ƿ����ϴ���ɾ��Ȩ�ޣ���]");
				throw new Exception("[FTP������=" + ftpServerName + ",��ȡFTP��������ǰʱ��ʧ�ܣ�����÷�����FTP�����Ƿ����ϴ���ɾ��Ȩ�ޣ���]");
			}

			return ftpTimeNow;
		} catch (Exception e)
		{
			// modify by wuyg 2013-5-9 ,��ȡftpTimeNow����ʱ�����׳��쳣���ڲ�������
			log.error(e.getMessage(), e);
			return null;
		} finally
		{
			if (checkTimeFileInput != null)
			{
				checkTimeFileInput.close();
			}
		}
	}

	public static void main(String[] args)
	{
		System.out.println(TimeUtil.nowTime2str());
		try
		{
			// downloadNested(null, "10.18.1.26", 21, "nwom", "nwom123", "/home/nwom/user/wuyg", "c:/upload");

			FtpServer ftpServer = new FtpServer();
			ftpServer.setDelete(true);
			downloadNested(new FtpThread(ftpServer, 0, 3600, null), "127.0.0.1", 21, "lexftp", "lexftp", "/",
					"d:/test1", null, 3600000, false);

		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		System.out.println(TimeUtil.nowTime2str());
	}

}