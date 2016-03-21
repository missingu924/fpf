package com.inspur.ftpparserframework.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.inspur.ftpparserframework.log.obj.FtpConnectLog;
import com.inspur.ftpparserframework.log.obj.FtpNestedDownloadLog;
import com.inspur.ftpparserframework.log.obj.FtpOneFileDownloadLog;
import com.inspur.ftpparserframework.report.Test;
import com.inspur.ftpparserframework.util.TimeUtil;

/**
 * FTP��־������
 * @author �����
 *
 */
public class FtpLogAnalyzer
{
	private static Logger log = Logger.getLogger(Test.class);

	public static List<FtpConnectLog> ftpConnectLogs = new ArrayList<FtpConnectLog>();// FTP������־
	public static List<FtpNestedDownloadLog> ftpNestLogs = new ArrayList<FtpNestedDownloadLog>();// FTP�ݹ�������־
	public static List<FtpOneFileDownloadLog> ftpOneFileLogs = new ArrayList<FtpOneFileDownloadLog>();// FTP���ļ�������־

	public static List<FtpOneFileDownloadLog> errorFtpOneFileLogs = new ArrayList<FtpOneFileDownloadLog>();// ������ļ�

	public static String nowTime = TimeUtil.nowTime2str("yyyyMMddHHmmss");

	public static File ftpConnectCsvFile = null;
	public static File ftpNestCsvFile = null;
	public static File ftpOneFileCsvFile = null;
	public static PrintWriter ftpConnectCsvPw = null;
	public static PrintWriter ftpNestecCsvPw = null;
	public static PrintWriter ftpOneFileCsvPw = null;

	public static long startTime = System.currentTimeMillis();
	public static long endTime = 0;

	public static long totalStartTime = startTime;

	public static void main(String[] args)
	{
		args=new String[]{"c:/ftpparser.log"};
		try
		{
			if (args.length == 0)
			{
				System.out.println("��ָ���������־��ȫ·��,����ļ�֮���ö��Ÿ���.");
				System.exit(-1);
			}
			String[] filePaths = args[0].split("\\,");

			File logFile = null;
			for (int i = 0; i < filePaths.length; i++)
			{
				logFile = new File(filePaths[i]);
				if (logFile.exists())
				{
					if (ftpConnectCsvFile == null)
					{
						ftpConnectCsvFile = new File(logFile.getParent(), "ftpconnect-" + nowTime + ".csv");
						ftpConnectCsvPw = new PrintWriter(ftpConnectCsvFile);
						ftpConnectCsvPw.println(new FtpConnectLog().getCsvTitle());
						ftpNestCsvFile = new File(logFile.getParent(), "ftpnest-" + nowTime + ".csv");
						ftpNestecCsvPw = new PrintWriter(ftpNestCsvFile);
						ftpNestecCsvPw.println(new FtpNestedDownloadLog().getCsvTitle());
						ftpOneFileCsvFile = new File(logFile.getParent(), "ftponefile-" + nowTime + ".csv");
						ftpOneFileCsvPw = new PrintWriter(ftpOneFileCsvFile);
						ftpOneFileCsvPw.println(new FtpOneFileDownloadLog().getCsvTitle());
					}

					// FPF������־
					System.out.println("��" + (i + 1) + "����־�ļ�������ʼ:" + logFile.getCanonicalPath());
					parseOneFile(logFile);
					System.out.println("��" + (i + 1) + "����־�ļ���������:" + logFile.getCanonicalPath());
				} else
				{
					System.out.println("��־�ļ�������,����:" + logFile.getCanonicalPath());
				}
			}

			// дFTP������־			
			System.out.println("FTP������־��Ϣд�����ļ�:" + ftpConnectCsvFile.getCanonicalPath());
			for (int i = 0; i < ftpConnectLogs.size(); i++)
			{
				ftpConnectCsvPw.println(ftpConnectLogs.get(i).toCsv());
			}
			ftpConnectCsvPw.close();

			// дFTP�ݹ�����		
			System.out.println("FTP�ݹ�������־��Ϣд�����ļ�:" + ftpNestCsvFile.getCanonicalPath());
			for (int i = 0; i < ftpNestLogs.size(); i++)
			{
				ftpNestecCsvPw.println(ftpNestLogs.get(i).toCsv());
			}
			ftpNestecCsvPw.close();

			// дFTP���ļ�������־			
			System.out.println("FTP���ļ�������־��Ϣд�����ļ�:" + ftpOneFileCsvFile.getCanonicalPath());
			for (int i = 0; i < ftpOneFileLogs.size(); i++)
			{
				ftpOneFileCsvPw.println(ftpOneFileLogs.get(i).toCsv());
			}
			ftpOneFileCsvPw.close();

			System.out.println("������־�����ۼƺ�ʱ" + (System.currentTimeMillis() - totalStartTime) / 1000.0 + "��");

			if (errorFtpOneFileLogs.size() > 0)
			{
				System.out.println("\nע��:����" + errorFtpOneFileLogs.size() + "���ļ����ν������ִ���,���ע:");
				for (int i = 0; i < errorFtpOneFileLogs.size(); i++)
				{
					FtpOneFileDownloadLog l = errorFtpOneFileLogs.get(i);
					System.out.println("�ļ�=" + l.getSrcFile().getPath() + ",������Ϣ=" + l.getErrorMessage() + ",��ע��Ϣ="
							+ l.getMark());
				}
			}
		} catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}
	}

	private static void parseOneFile(File logFile) throws FileNotFoundException, IOException, Exception
	{
		BufferedReader bf = new BufferedReader(new FileReader(logFile));
		String line = null;
		int lineNum = 0;
		while ((line = bf.readLine()) != null)
		{
			// ����һ����־
			parseOneLine(line);

			if (++lineNum % 10000 == 0)
			{
				endTime = System.currentTimeMillis();
				System.out.println("�ѽ���" + lineNum + "��,���κ�ʱ" + (endTime - startTime) / 1000.0 + "��");
				ftpOneFileCsvPw.flush();
				ftpNestecCsvPw.flush();
				ftpConnectCsvPw.flush();
			} else
			{
				startTime = endTime;
			}
		}
		System.out.println("���ļ�������" + lineNum + "��");

		bf.close();
	}

	/**
	 * ����һ����־��Ϣ
	 * 
	 * @param line
	 * @throws Exception
	 * @throws ParseException
	 */
	public static void parseOneLine(String line) throws Exception
	{
		// ���������־
		if (line.indexOf(FtpOneFileDownloadLog.flag) > 0)
		{
			FtpOneFileDownloadLog ofdl = new FtpOneFileDownloadLog();
			if (ofdl.parseLog(line))
			{
				FtpOneFileDownloadLog preOfdl = getFtpOneFileDownloadLog(ofdl);
				if (preOfdl != null)
				{
					preOfdl.merge(ofdl);
					if (ftpOneFileCsvPw!=null)
					{						
						ftpOneFileCsvPw.println(preOfdl.toCsv());
					}
					else
					{
						preOfdl.saveOrUpdate();
					}					
					ftpOneFileLogs.remove(preOfdl);
				} else
				{
					ftpOneFileLogs.add(ofdl);
				}
			}
		}
		// �ļ�ת����־
		else if (line.indexOf(FtpNestedDownloadLog.flag) > 0)
		{
			FtpNestedDownloadLog ndl = new FtpNestedDownloadLog();
			if (ndl.parseLog(line))
			{
				FtpNestedDownloadLog preNdl = getFtpNestedDownloadLog(ndl);
				if (preNdl != null)
				{
					preNdl.merge(ndl);
					if (ftpNestecCsvPw!=null)
					{
						ftpNestecCsvPw.println(preNdl.toCsv());
					}
					else
					{
						preNdl.saveOrUpdate();
					}					
					ftpNestLogs.remove(preNdl);
				} else
				{
					ftpNestLogs.add(ndl);
				}
			}
		}
		// �ļ�����������־
		else if (line.indexOf(FtpConnectLog.flag) > 0)
		{
			FtpConnectLog cl = new FtpConnectLog();
			if (cl.parseLog(line))
			{
				FtpConnectLog preCl = getFtpConnectLog(cl);
				if (preCl != null)
				{
					preCl.merge(cl);
					if (ftpConnectCsvPw!=null)
					{
						ftpConnectCsvPw.println(preCl.toCsv());
					}
					else
					{
						preCl.saveOrUpdate();
					}
					ftpConnectLogs.remove(preCl);
				} else
				{
					ftpConnectLogs.add(cl);
				}
			}
		}
	}

	public static FtpConnectLog getFtpConnectLog(FtpConnectLog other)
	{
		FtpConnectLog l = null;
		for (int i = ftpConnectLogs.size() - 1; i >= 0; i--)// �������,����ʱ�������
		{
			FtpConnectLog tmp = ftpConnectLogs.get(i);
			if (tmp.equals(other))
			{
				l = tmp;
				break;
			}
		}

		return l;
	}

	public static FtpOneFileDownloadLog getFtpOneFileDownloadLog(FtpOneFileDownloadLog other)
	{
		FtpOneFileDownloadLog l = null;
		for (int i = ftpOneFileLogs.size() - 1; i >= 0; i--)// �������,����ʱ�������
		{
			FtpOneFileDownloadLog tmp = ftpOneFileLogs.get(i);
			if (tmp.equals(other))
			{
				l = tmp;
				break;
			}
		}

		return l;
	}

	public static FtpNestedDownloadLog getFtpNestedDownloadLog(FtpNestedDownloadLog other)
	{
		FtpNestedDownloadLog l = null;
		for (int i = ftpNestLogs.size() - 1; i >= 0; i--)// �������,����ʱ�������
		{
			FtpNestedDownloadLog tmp = ftpNestLogs.get(i);
			if (tmp.equals(other))
			{
				l = tmp;
				break;
			}
		}

		return l;
	}
}
