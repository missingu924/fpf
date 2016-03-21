package com.inspur.ftpparserframework.report;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.inspur.ftpparserframework.report.obj.FtpDlFileInfo;
import com.inspur.ftpparserframework.report.obj.ParsedMainFileInfo;
import com.inspur.ftpparserframework.report.obj.ParsedSubFileInfo;
import com.inspur.ftpparserframework.util.StringUtil;
import com.inspur.ftpparserframework.util.TimeUtil;

public class Test
{
	private static Logger log = Logger.getLogger(Test.class);

	public static List<ParsedMainFileInfo> parsedMainFileInfoList = new ArrayList<ParsedMainFileInfo>();
	public static List<FtpDlFileInfo> ftpDlFileInfoList = new ArrayList<FtpDlFileInfo>();

	public static void main(String[] args)
	{
		String s = "[2012-11-22 23:16:55][com.inspur.ftpparserframework.util.OracleUtil.load(OracleUtil.java:198)][ERROR]-SQLLDR������⣺Դ�ļ�=/home/nwom/nbrmr/td-mr-xml/data/input/MINOS/20120910/992/TD-SCDMA_MRS_ZTE_POTEVIO_OMCR_992_20120910140000.xml.gz, �м��ļ�=/home/nwom/nbrmr/td-mr-xml/data/sqlldr/txt/TD-SCDMA_MRS_ZTE_POTEVIO_OMCR_992_20120910140000_UtranTxPower.txt:����������, log=/home/nwom/nbrmr/td-mr-xml/data/sqlldr/log/TD-SCDMA_MRS_ZTE_POTEVIO_OMCR_992_20120910140000_UtranTxPower.txt.log, bad=/home/nwom/nbrmr/td-mr-xml/data/sqlldr/bad/TD-SCDMA_MRS_ZTE_POTEVIO_OMCR_992_20120910140000_UtranTxPower.txt.bad";
		Pattern p = Pattern
				.compile("\\[(.{19})\\]\\S+SQLLDR������⣺Դ�ļ�=(\\S+), �м��ļ�=(\\S+):����������, log=(\\S+), bad=(\\S+)");
		Matcher m = p.matcher(s);
		if (m.matches())
		{
			for (int i = 0; i <= m.groupCount(); i++)
			{
				log.info(m.group(i));
			}
		}

		try
		{

			File logFile = new File("d:/tdmrlog/test/ftpparser.log");
			//			File logFile1 = new File("d:/tdmrlog/hn/ftpparser.log");

			parseOneFile(logFile);
			//			parseOneFile(logFile1);

			// д�ļ�������־��Ϣ
			PrintWriter dlfpw = new PrintWriter(new File(logFile.getParent(), "log-downloadfile-"
					+ TimeUtil.nowTime2str("yyyyMMddHHmmss") + ".csv"));
			dlfpw.println(new FtpDlFileInfo().getCsvTitle());
			for (int i = 0; i < ftpDlFileInfoList.size(); i++)
			{
				dlfpw.println(ftpDlFileInfoList.get(i).toCsv());
			}
			dlfpw.close();

			// д�ļ����������Ϣ
			PrintWriter mfpw = new PrintWriter(new File(logFile.getParent(), "log-mainfile-"
					+ TimeUtil.nowTime2str("yyyyMMddHHmmss") + ".csv"));
			PrintWriter sfpw = new PrintWriter(new File(logFile.getParent(), "log-subfile-"
					+ TimeUtil.nowTime2str("yyyyMMddHHmmss") + ".csv"));

			mfpw.println(new ParsedMainFileInfo().getCsvTitle());
			sfpw.println(new ParsedSubFileInfo().getCsvTitle());
			for (int i = 0; i < parsedMainFileInfoList.size(); i++)
			{
				ParsedMainFileInfo mf = parsedMainFileInfoList.get(i);
				mfpw.println(mf.toCsv());

				for (int n = 0; n < mf.getSubFileCount(); n++)
				{
					sfpw.println(mf.getSubFileInfos().get(n).toCsv());
				}
			}
			mfpw.close();
			sfpw.close();

		} catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}
	}

	private static void parseOneFile(File logFile) throws FileNotFoundException, IOException, Exception
	{
		BufferedReader bf = new BufferedReader(new FileReader(logFile));
		String line = null;
		while ((line = bf.readLine()) != null)
		{
			// 1�� �ļ�������־����
			// [2012-11-26 10:58:15][com.inspur.ftpparserframework.ftp.FtpUtil.downloadOnce(FtpUtil.java:286)][INFO]-#���ؿ�ʼ��[Դ�ļ�=/home/oracle/wuyg/td-mr-xml/data/input/DATANG/bak/TD-SCDMA_MRS_DATANG_OMCR_905_20121120150000.xml.gz, ��С=81280Byte, ʱ��=2012-11-26 02:44:00]
			if (line.indexOf("#���ؿ�ʼ") > 0)
			{
				ftpDlBegin(line);
			} else if (line.indexOf("#���ؽ���") > 0)
			{
				ftpDlEnd(line);
			} else if (line.indexOf("#����ʧ��") > 0)
			{
				ftpDlFail(line);
			}
			// 2���ļ����������־����
			//[2012-11-20 16:37:48][com.inspur.ftpparserframework.parser.ParseThread.run(ParseThread.java:35)][INFO]-#�ļ�������⿪ʼ��Դ�ļ�=/home/oracle/wuyg/td-mr-xml/data/input/DATANG/TD-SCDMA_MRS_DATANG_OMCR_905_20121120150000.xml
			else if (line.indexOf("#�ļ�������⿪ʼ") > 0)
			{
				mainFileBegin(line);
			}
			//[2012-11-20 16:37:58][com.inspur.ftpparserframework.parser.ParseThread.run(ParseThread.java:88)][INFO]-#�ļ�������������Դ�ļ�=/home/oracle/wuyg/td-mr-xml/data/input/DATANG/TD-SCDMA_MRS_DATANG_OMCR_905_20121120150000.xml ,�ۼƺ�ʱ=9866����
			else if (line.indexOf("#�ļ�����������") > 0)
			{
				mainFileEnd(line);
			}
			//
			else if (line.indexOf("#�ļ�����������") > 0)
			{
				mainFileFail(line);
			}
			//[2012-11-20 16:37:54][com.inspur.ftpparserframework.util.OracleUtil.load(OracleUtil.java:75)][INFO]-SQLLDR������⣺Դ�ļ�=/home/oracle/wuyg/td-mr-xml/data/input/DATANG/TD-SCDMA_MRS_DATANG_OMCR_905_20121120150000.xml, �м��ļ�=/home/oracle/wuyg/td-mr-xml/data/sqlldr/txt/INSPUR_TD_MR_MRS_DATANG_OMCR_905_20121120163748_112_46_TadvPccpchRscp.txt, ��⿪ʼ��
			else if (line.indexOf("SQLLDR�������") > 0 && line.indexOf("��⿪ʼ") > 0)
			{
				subFileBegin(line);
			}
			//[2012-11-20 16:37:55][com.inspur.ftpparserframework.util.OracleUtil.load(OracleUtil.java:186)][INFO]-SQLLDR������⣺Դ�ļ�=/home/oracle/wuyg/td-mr-xml/data/input/DATANG/TD-SCDMA_MRS_DATANG_OMCR_905_20121120150000.xml, �м��ļ�=/home/oracle/wuyg/td-mr-xml/data/sqlldr/txt/INSPUR_TD_MR_MRS_DATANG_OMCR_905_20121120163748_112_46_TadvPccpchRscp.txt, ������, ��ʱ200���롣
			else if (line.indexOf("SQLLDR�������") > 0 && line.indexOf("������") > 0)
			{
				subFileEnd(line);
			}
			//
			else if (line.indexOf("SQLLDR�������") > 0 && line.indexOf("������") > 0)
			{
				subFileFail(line);
			}

		}
	}

	private static void ftpDlBegin(String line) throws Exception
	{
		// [2012-11-26 10:58:15][com.inspur.ftpparserframework.ftp.FtpUtil.downloadOnce(FtpUtil.java:286)][INFO]-#���ؿ�ʼ��[Դ�ļ�=/home/oracle/wuyg/td-mr-xml/data/input/DATANG/bak/TD-SCDMA_MRS_DATANG_OMCR_905_20121120150000.xml.gz, ��С=81280Byte, ʱ��=2012-11-26 02:44:00]
		Pattern p = Pattern.compile("\\[(.{19})\\]\\S+#���ؿ�ʼ��\\[Դ�ļ�=(\\S+),\\s*��С=(\\d+)Byte,\\s*ʱ��=(.{19})\\]");
		Matcher m = p.matcher(line);
		if (m.matches())
		{
			String logTime = m.group(1);
			String filePath = m.group(2);
			String length = m.group(3);
			String lastModifiedTime = m.group(4);

			FtpDlFileInfo dlf = new FtpDlFileInfo();
			dlf.setFilepath(filePath);
			dlf.setStartTime(logTime);
			dlf.setLength(Long.parseLong(length));
			dlf.setLastModifiedTime(TimeUtil.str2date(lastModifiedTime));

			ftpDlFileInfoList.add(dlf);
		}
	}

	private static void ftpDlEnd(String line) throws Exception
	{
		//[2012-11-26 10:58:15][com.inspur.ftpparserframework.ftp.FtpUtil.downloadOnce(FtpUtil.java:299)][INFO]-#���ؽ�����[Դ�ļ�=/home/oracle/wuyg/td-mr-xml/data/input/DATANG/bak/TD-SCDMA_MRS_DATANG_OMCR_905_20121120150000.xml.gz, ��С=81280Byte, ʱ��=2012-11-26 02:44:00][Ŀ���ļ�=D:\workspace1\ftp-parser-framework\data\input\JN_HUAWEI_OMC\data\input\DATANG\bak\TD-SCDMA_MRS_DATANG_OMCR_905_20121120150000.xml.gz, ��С=81280Byte, ʱ��=2012-11-26 10:58:15], ���غ�ʱ=21���롣
		Pattern p = Pattern
				.compile("\\[(.{19})\\]\\S+#���ؽ�����\\[Դ�ļ�=(\\S+),\\s*��С=(\\d+)Byte,\\s*ʱ��=(.{19})\\]\\[Ŀ���ļ�=(\\S+),\\s*��С=(\\d+)Byte,\\s*ʱ��=(.{19})\\], ���غ�ʱ=(\\d+)���롣");
		Matcher m = p.matcher(line);
		if (m.matches())
		{
			String logTime = m.group(1);
			String filePath = m.group(2);
			String length = m.group(3);
			String lastModifiedTime = m.group(4);
			String destFilePath = m.group(5);
			String destFileLength = m.group(6);
			String destFileLastModifiedTime = m.group(7);
			String usedTime = m.group(8);

			FtpDlFileInfo dlf = getFtpDlFileInfoByFilePath(filePath);
			if (dlf != null)
			{
				dlf.setEndTime(logTime);
				dlf.setDestFilePath(destFilePath);
				dlf.setDestFilelength(Long.parseLong(destFileLength));
				dlf.setDestFileLastModifiedTime(TimeUtil.str2date(destFileLastModifiedTime));
				dlf.setUsedTime(Double.parseDouble(usedTime) / 1000);
				dlf.setProcessOk(true);
			}
		}
	}

	private static void ftpDlFail(String line) throws Exception
	{
		// [2012-11-26 10:58:15][com.inspur.ftpparserframework.ftp.FtpUtil.downloadOnce(FtpUtil.java:324)][ERROR]-#���س���[Դ�ļ�=/home/oracle/wuyg/td-mr-xml/data/input/DATANG/bak/TD-SCDMA_MRS_DATANG_OMCR_905_20121120150000.xml.gz, ��С=81280Byte, ʱ��=2012-11-26 02:44:00], ������Ϣ=null
		Pattern p = Pattern
				.compile("\\[(.{19})\\]\\S+#���س���\\[Դ�ļ�=(\\S+),\\s*��С=(\\d+)Byte,\\s*ʱ��=(.{19})\\], ������Ϣ=(.*)");
		Matcher m = p.matcher(line);
		if (m.matches())
		{
			String logTime = m.group(1);
			String filePath = m.group(2);
			String errorMessage = m.group(5);
			FtpDlFileInfo dlf = getFtpDlFileInfoByFilePath(filePath);
			if (dlf != null)
			{
				dlf.setEndTime(logTime);
				dlf.setProcessOk(false);
				dlf.setErrorMessage(errorMessage);
			}
		}
	}

	private static void mainFileFail(String line) throws Exception
	{
		// [2012-11-20 16:37:58][com.inspur.ftpparserframework.parser.ParseThread.run(ParseThread.java:95)][INFO]-#�ļ�����������Դ�ļ�=/home/oracle/wuyg/td-mr-xml/data/input/DATANG/TD-SCDMA_MRS_DATANG_OMCR_905_20121120150000.xml ,������Ϣ=null
		Pattern p = Pattern.compile("\\[(.{19})\\]\\S+�ļ�����������Դ�ļ�=(\\S+)\\s*, ������Ϣ=(.*)");
		Matcher m = p.matcher(line);
		if (m.matches())
		{
			String logTime = m.group(1);
			String filePath = m.group(2);
			String errorMessage = m.group(3);
			ParsedMainFileInfo mf = getParsedMainFileInfoByMainFilePath(filePath);
			if (mf != null)
			{
				mf.setEndTime(logTime);
				mf.setProcessOk(false);
				mf.setErrorMessage(errorMessage);
			}
		}
	}

	private static void subFileFail(String line) throws Exception
	{
		//[2012-11-22 23:11:44][com.inspur.ftpparserframework.util.OracleUtil.load(OracleUtil.java:187)][ERROR]-SQLLDR������⣺Դ�ļ�=/home/nwom/nbrmr/td-mr-xml/data/input/MINOS/20120906/997/TD-SCDMA_MRS_ZTE_POTEVIO_OMCR_997_20120906170000.xml.gz, �м��ļ�=/home/nwom/nbrmr/td-mr-xml/data/sqlldr/txt/TD-SCDMA_MRS_ZTE_POTEVIO_OMCR_997_20120906170000_Cs64DlBlerLog.txt, ������:SQL*Loader-500: Unable to open file (/home/nwom/nbrmr/td-mr-xml/bin/../data/sqlldr/txt/TD-SCDMA_MRS_ZTE_POTEVIO_OMCR_997_20120906170000_Cs64DlBlerLog.txt)
		Pattern p = Pattern.compile("\\[(.{19})\\]\\S+SQLLDR������⣺Դ�ļ�=(\\S+), �м��ļ�=(\\S+), ������:(.*)");
		Matcher m = p.matcher(line);
		if (m.matches())
		{
			String logTime = m.group(1);
			String mainFilePath = m.group(2);
			String subFilePath = m.group(3);
			String errorMessage = m.group(4);

			ParsedMainFileInfo mf = getParsedMainFileInfoByMainFilePath(mainFilePath);
			if (mf != null)
			{
				ParsedSubFileInfo sf = mf.getParsedSubFileInfoBySubFilePath(subFilePath);
				if (sf != null)
				{
					sf.setEndTime(logTime);
					sf.setProcessOk(false);
					sf.setErrorMessage(errorMessage);
					mf.setProcessOk(false);
				}
			}
		}

		// [2012-11-22 23:16:55][com.inspur.ftpparserframework.util.OracleUtil.load(OracleUtil.java:198)][ERROR]-SQLLDR������⣺Դ�ļ�=/home/nwom/nbrmr/td-mr-xml/data/input/MINOS/20120910/992/TD-SCDMA_MRS_ZTE_POTEVIO_OMCR_992_20120910140000.xml.gz, �м��ļ�=/home/nwom/nbrmr/td-mr-xml/data/sqlldr/txt/TD-SCDMA_MRS_ZTE_POTEVIO_OMCR_992_20120910140000_UtranTxPower.txt:����������, log=/home/nwom/nbrmr/td-mr-xml/data/sqlldr/log/TD-SCDMA_MRS_ZTE_POTEVIO_OMCR_992_20120910140000_UtranTxPower.txt.log, bad=/home/nwom/nbrmr/td-mr-xml/data/sqlldr/bad/TD-SCDMA_MRS_ZTE_POTEVIO_OMCR_992_20120910140000_UtranTxPower.txt.bad
		p = Pattern.compile("\\[(.{19})\\]\\S+SQLLDR������⣺Դ�ļ�=(\\S+), �м��ļ�=(\\S+):����������, log=(\\S+), bad=(\\S+)");
		m = p.matcher(line);
		if (m.matches())
		{
			String logTime = m.group(1);
			String mainFilePath = m.group(2);
			String subFilePath = m.group(3);
			String errorMessage = m.group(5);

			ParsedMainFileInfo mf = getParsedMainFileInfoByMainFilePath(mainFilePath);
			if (mf != null)
			{
				ParsedSubFileInfo sf = mf.getParsedSubFileInfoBySubFilePath(subFilePath);
				if (sf != null)
				{
					sf.setEndTime(logTime);
					sf.setProcessOk(false);
					sf.setErrorMessage(errorMessage);
					mf.setProcessOk(false);
				}
			}
		}
	}

	private static void mainFileBegin(String line) throws Exception
	{
		//[2012-11-20 16:37:48][com.inspur.ftpparserframework.parser.ParseThread.run(ParseThread.java:35)][INFO]-#�ļ�������⿪ʼ��Դ�ļ�=/home/oracle/wuyg/td-mr-xml/data/input/DATANG/TD-SCDMA_MRS_DATANG_OMCR_905_20121120150000.xml
		Pattern p = Pattern.compile("\\[(.{19})\\]\\S+�ļ�������⿪ʼ��Դ�ļ�=(\\S+)(,\\s*��С=(\\d+)Byte,\\s*ʱ��=(.{19}))?");
		Matcher m = p.matcher(line);
		if (m.matches())
		{
			String logTime = m.group(1);
			String filePath = m.group(2);
			String length = m.group(4);
			String lastModifiedTime = m.group(5);

			if (filePath.endsWith("xml") || filePath.endsWith("xml.gz"))
			{
				ParsedMainFileInfo mf = new ParsedMainFileInfo();
				mf.setFilepath(filePath);
				mf.setStartTime(logTime);
				if (!StringUtil.isEmpty(length))
				{
					mf.setLength(Long.parseLong(length));
				}
				if (!StringUtil.isEmpty(lastModifiedTime))
				{
					mf.setLastModifiedTime(TimeUtil.str2date(lastModifiedTime));
				}

				//TD-SCDMA_MRS_DATANG_OMCR_905_20121120150000.xml
				Pattern mp = Pattern.compile("\\S+(TD-SCDMA)_(MRS)_(\\S+)_(\\S+)_(\\S+)_(\\d{12,14})(_\\d+)?\\.\\S+");
				Matcher mm = mp.matcher(filePath);
				if (mm.matches())
				{
					String technology = mm.group(1);
					String businessType = mm.group(2);
					String vendor = mm.group(3);
					String rncId = mm.group(5);
					String reportTime = mm.group(6);

					mf.setTechnology(technology);
					mf.setDataType("MR");
					mf.setBusinessType(businessType);
					mf.setVendor(vendor);
					mf.setNeType("RNC");
					mf.setNeName(rncId);
					if (reportTime != null && reportTime.length() == 12)
					{
						reportTime += "00";
					}
					mf.setReportTime(TimeUtil.date2str(TimeUtil.str2date(reportTime, "yyyyMMddHHmmss")));
				}

				parsedMainFileInfoList.add(mf);
			}
		}
	}

	private static void mainFileEnd(String line) throws Exception
	{
		//[2012-11-20 16:37:58][com.inspur.ftpparserframework.parser.ParseThread.run(ParseThread.java:88)][INFO]-#�ļ�������������Դ�ļ�=/home/oracle/wuyg/td-mr-xml/data/input/DATANG/TD-SCDMA_MRS_DATANG_OMCR_905_20121120150000.xml ,�ۼƺ�ʱ=9866����
		Pattern p = Pattern.compile("\\[(.{19})\\]\\S+�ļ�������������Դ�ļ�=(\\S+)\\s*,�ۼƺ�ʱ=(\\d+)����");
		Matcher m = p.matcher(line);
		if (m.matches())
		{
			String logTime = m.group(1);
			String filePath = m.group(2);
			String usedTime = m.group(3);

			ParsedMainFileInfo mf = getParsedMainFileInfoByMainFilePath(filePath);
			if (mf != null)
			{
				mf.setEndTime(logTime);
				mf.setUsedTime(Double.parseDouble(usedTime) / 1000);
				if (mf.isProcessOk() == false)
				{
					mf.setProcessOk(false);
				} else
				{
					mf.setProcessOk(true);
				}
			}
		}
	}

	private static void subFileBegin(String line) throws Exception
	{
		// [2012-11-20 16:37:54][com.inspur.ftpparserframework.util.OracleUtil.load(OracleUtil.java:75)][INFO]-SQLLDR������⣺Դ�ļ�=/home/oracle/wuyg/td-mr-xml/data/input/DATANG/TD-SCDMA_MRS_DATANG_OMCR_905_20121120150000.xml, �м��ļ�=/home/oracle/wuyg/td-mr-xml/data/sqlldr/txt/INSPUR_TD_MR_MRS_DATANG_OMCR_905_20121120163748_112_46_TadvPccpchRscp.txt, ��⿪ʼ��
		Pattern p = Pattern
				.compile("\\[(.{19})\\]\\S+SQLLDR������⣺Դ�ļ�=(\\S+), �м��ļ�=(\\S+)(,\\s*�м��ļ���С=(\\d+)Byte,\\s*�м��ļ�ʱ��=(.{19}))?, ��⿪ʼ��");
		Matcher m = p.matcher(line);
		if (m.matches())
		{
			String logTime = m.group(1);
			String mainFilePath = m.group(2);
			String subFilePath = m.group(3);
			String length = m.group(5);
			String lastModifiedTime = m.group(6);

			ParsedSubFileInfo sf = new ParsedSubFileInfo();
			sf.setMainFilePath(mainFilePath);
			sf.setFilepath(subFilePath);
			sf.setStartTime(logTime);
			if (!StringUtil.isEmpty(length))
			{
				sf.setLength(Long.parseLong(length));
			}
			if (!StringUtil.isEmpty(lastModifiedTime))
			{
				sf.setLastModifiedTime(TimeUtil.str2date(lastModifiedTime));
			}

			ParsedMainFileInfo mf = getParsedMainFileInfoByMainFilePath(mainFilePath);
			if (mf != null)
			{
				mf.addSubFileInfo(sf);
			}
		}
	}

	private static void subFileEnd(String line) throws Exception
	{
		//[2012-11-20 16:37:55][com.inspur.ftpparserframework.util.OracleUtil.load(OracleUtil.java:186)][INFO]-SQLLDR������⣺Դ�ļ�=/home/oracle/wuyg/td-mr-xml/data/input/DATANG/TD-SCDMA_MRS_DATANG_OMCR_905_20121120150000.xml, �м��ļ�=/home/oracle/wuyg/td-mr-xml/data/sqlldr/txt/INSPUR_TD_MR_MRS_DATANG_OMCR_905_20121120163748_112_46_TadvPccpchRscp.txt, ������, ��ʱ200���롣
		Pattern p = Pattern
				.compile("\\[(.{19})\\]\\S+SQLLDR������⣺Դ�ļ�=(\\S+), �м��ļ�=(\\S+), ������, �ɹ�(\\d+)��, ʧ��(\\d+)��, ��ʱ(\\d+)���롣");
		Matcher m = p.matcher(line);
		if (m.matches())
		{
			String logTime = m.group(1);
			String mainFilePath = m.group(2);
			String subFilePath = m.group(3);
			String succRows = m.group(4);
			String errorRows = m.group(5);
			String usedTime = m.group(6);

			ParsedMainFileInfo mf = getParsedMainFileInfoByMainFilePath(mainFilePath);
			if (mf != null)
			{
				ParsedSubFileInfo sf = mf.getParsedSubFileInfoBySubFilePath(subFilePath);
				if (sf != null)
				{
					sf.setEndTime(logTime);
					sf.setUsedTime(Double.parseDouble(usedTime) / 1000);
					sf.setSuccRows(Long.parseLong(succRows));
					sf.setErrorRows(Long.parseLong(errorRows));
					if (Long.parseLong(errorRows) > 0)
					{
						sf.setProcessOk(false);
					} else if (sf.isProcessOk() == true)
					{
						sf.setProcessOk(true);
					}
				}
			}
		}
	}

	public static ParsedMainFileInfo getParsedMainFileInfoByMainFilePath(String mainFilePath)
	{
		ParsedMainFileInfo mf = null;
		for (int i = 0; i < parsedMainFileInfoList.size(); i++)
		{
			ParsedMainFileInfo tmp = parsedMainFileInfoList.get(i);
			if (mainFilePath.equals(tmp.getFilepath()))
			{
				mf = tmp;

				if (mf.getEndTime() == null)
				{
					return mf;
				}
			}
		}

		return mf;
	}

	public static FtpDlFileInfo getFtpDlFileInfoByFilePath(String mainFilePath)
	{
		FtpDlFileInfo dlf = null;
		for (int i = 0; i < ftpDlFileInfoList.size(); i++)
		{
			FtpDlFileInfo tmp = ftpDlFileInfoList.get(i);
			if (mainFilePath.equals(tmp.getFilepath()))
			{
				dlf = tmp;

				if (dlf.getEndTime() == null)
				{
					return dlf;
				}
			}
		}

		return dlf;
	}

}
