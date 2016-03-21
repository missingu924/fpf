package com.inspur.ftpparserframework.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

import com.inspur.ftpparserframework.log.obj.ParserMainLog;
import com.inspur.ftpparserframework.log.obj.ParserMiddleLog;
import com.inspur.ftpparserframework.log.obj.ParserSqlldrLog;
import com.inspur.ftpparserframework.report.Test;
import com.inspur.ftpparserframework.util.StringUtil;
import com.inspur.ftpparserframework.util.TimeUtil;

public class LogParserOld
{
	private static Logger log = Logger.getLogger(Test.class);

	public static List<ParserMainLog> parserLogs = new ArrayList<ParserMainLog>();// �ļ�����������־
	public static List<ParserMiddleLog> parserMiddleLogs = new ArrayList<ParserMiddleLog>();// �ļ�ת����־
	public static List<ParserSqlldrLog> sqlldrLogs = new ArrayList<ParserSqlldrLog>();// ���������־
	public static List<ParserMainLog> errorParserLogs = new ArrayList<ParserMainLog>();// ������ļ�
	public static List<ParserMainLog> justStartParserLogs = new ArrayList<ParserMainLog>();// ����ʼδ��������ļ�

	public static void main(String[] args)
	{
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
					// FPF������־
					System.out.println("��" + (i + 1) + "����־�ļ�������ʼ:" + logFile.getCanonicalPath());
					parseOneFile(logFile);
					System.out.println("��" + (i + 1) + "����־�ļ���������:" + logFile.getCanonicalPath());
				} else
				{
					System.out.println("��־�ļ�������,����:" + logFile.getCanonicalPath());
				}
			}

			System.out.println("���ι�������" + parserLogs.size() + "������־," + parserMiddleLogs.size() + "���ļ�ת����־,"
					+ sqlldrLogs.size() + "�����������־");
			System.out.println("�ϲ���־��Ϣ����");

			// ����parselog����Ϣ
			for (int i = 0; i < parserLogs.size(); i++)
			{
				ParserMainLog pl = parserLogs.get(i);
				for (int j = 0; j < sqlldrLogs.size(); j++)
				{
					ParserSqlldrLog sl = sqlldrLogs.get(j);
					if (sl.getSrcFile().getPath().equals(pl.getSrcFile().getPath()))
					{
						pl.setSuccRows(pl.getSuccRows() + sl.getSuccRows());
						pl.setErrorRows(pl.getErrorRows() + sl.getErrorRows());

						if ("����".equals(sl.getHandleState()))
						{
							pl.setHandleState(sl.getHandleState());
						}
					}
				}

				if (pl.getHandleState().equals(pl.STATE_ERROR))
				{
					errorParserLogs.add(pl);
				} else if (pl.getHandleState().equals(pl.STATE_START))
				{
					justStartParserLogs.add(pl);
				}
			}

			String nowTime = TimeUtil.nowTime2str("yyyyMMddHHmmss");
			// д�ļ������������־��Ϣ
			File csvFile = new File(logFile.getParent(), "parser-" + nowTime + ".csv");
			System.out.println("�ļ�����������־��Ϣд�����ļ�:" + csvFile.getCanonicalPath());
			PrintWriter pw = new PrintWriter(csvFile);
			pw.println(new ParserMainLog().getCsvTitle());
			for (int i = 0; i < parserLogs.size(); i++)
			{
				pw.println(parserLogs.get(i).toCsv());
			}
			pw.close();

			// д�ļ�ת����־��Ϣ
			csvFile = new File(logFile.getParent(), "middle-" + nowTime + ".csv");
			System.out.println("�ļ�ת����־��Ϣд�����ļ�:" + csvFile.getCanonicalPath());
			pw = new PrintWriter(csvFile);
			pw.println(new ParserMiddleLog().getCsvTitle());
			for (int i = 0; i < parserMiddleLogs.size(); i++)
			{
				pw.println(parserMiddleLogs.get(i).toCsv());
			}
			pw.close();

			// д�����־��Ϣ
			csvFile = new File(logFile.getParent(), "sqlldr-" + nowTime + ".csv");
			System.out.println("���������־��Ϣд�����ļ�:" + csvFile.getCanonicalPath());
			pw = new PrintWriter(csvFile);
			pw.println(new ParserSqlldrLog().getCsvTitle());
			for (int i = 0; i < sqlldrLogs.size(); i++)
			{
				pw.println(sqlldrLogs.get(i).toCsv());
			}
			pw.close();

			if (errorParserLogs.size() > 0)
			{
				System.out.println("\nע��:����" + errorParserLogs.size() + "���ļ����ν������ִ���,���ע:");
				for (int i = 0; i < errorParserLogs.size(); i++)
				{
					ParserMainLog l = errorParserLogs.get(i);
					System.out.println("�ļ�=" + l.getSrcFile().getPath() + ",������Ϣ=" + l.getErrorMessage());
				}
			}

			if (justStartParserLogs.size() > 0)
			{
				System.out.println("\nע��:����" + justStartParserLogs.size() + "���ļ�����δ������,���ע:");
				for (int i = 0; i < justStartParserLogs.size(); i++)
				{
					ParserMainLog l = justStartParserLogs.get(i);
					System.out.println("�ļ�=" + l.getSrcFile().getPath() + ",��ʼʱ��="
							+ TimeUtil.date2str(l.getStartTime()));
				}
			}
		} catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}
	}

	private static void parseOneFile(File logFile) throws FileNotFoundException, IOException, Exception
	{
//		BufferedReader bf = new BufferedReader(new FileReader(logFile));
		
		BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(logFile), "UTF-8"));
		String line = null;
		int lineNum = 0;
		while ((line = bf.readLine()) != null)
		{
			// ���������־
			if (line.indexOf(ParserSqlldrLog.flag) > 0)
			{
				ParserSqlldrLog sl = new ParserSqlldrLog();
				if (sl.parseLog(line))
				{
					ParserSqlldrLog preSl = getSqlldrLog(sl.getSrcFile().getPath(), sl.getDataFile().getPath());
					if (preSl != null)
					{
						preSl.merge(sl);
					} else
					{
						sqlldrLogs.add(sl);
					}
				}
			}
			// �ļ�ת����־
			else if (line.indexOf(ParserMiddleLog.flag) > 0)
			{
				ParserMiddleLog pml = new ParserMiddleLog();
				if (pml.parseLog(line))
				{
					ParserMiddleLog prePml = getParserMiddleLogByFilePath(pml.getSrcFile().getPath(),
							pml.getHandleName());
					if (prePml != null)
					{
						prePml.merge(pml);
					} else
					{
						parserMiddleLogs.add(pml);
					}
				}
			}
			// �ļ�����������־
			else if (line.indexOf(ParserMainLog.flag) > 0)
			{
				ParserMainLog pl = new ParserMainLog();
				if (pl.parseLog(line))
				{
					ParserMainLog prePl = getParserLogByFilePath(pl.getSrcFile().getPath());
					if (prePl != null)
					{
						prePl.merge(pl);
					} else
					{
						parserLogs.add(pl);
					}
				}
			}

			if (++lineNum % 10000 == 0)
			{
				System.out.println("�ѽ���" + lineNum + "��");
			}
		}
		System.out.println("���ļ�������" + lineNum + "��");

		bf.close();
	}

	private static ParserMiddleLog getParserMiddleLogByFilePath(String srcFilePath, String handleName)
	{
		ParserMiddleLog l = null;
		for (int i = parserMiddleLogs.size() - 1; i >= 0; i--)// �������,����ʱ�������
		{
			ParserMiddleLog tmp = parserMiddleLogs.get(i);
			if (srcFilePath.equals(tmp.getSrcFile().getPath()) && handleName.equals(tmp.getHandleName()))
			{
				l = tmp;
				break;
			}
		}

		return l;
	}

	public static ParserMainLog getParserLogByFilePath(String srcFilePath)
	{
		ParserMainLog l = null;
		for (int i = parserLogs.size() - 1; i >= 0; i--)// �������,����ʱ�������
		{
			ParserMainLog tmp = parserLogs.get(i);
			if (srcFilePath.equals(tmp.getSrcFile().getPath()))
			{
				l = tmp;
				break;
			}
		}

		return l;
	}

	public static ParserSqlldrLog getSqlldrLog(String srcFilePath, String dataFilePath)
	{
		ParserSqlldrLog l = null;
		for (int i = sqlldrLogs.size() - 1; i >= 0; i--)// �������,����ʱ�������
		{
			ParserSqlldrLog tmp = sqlldrLogs.get(i);
			if (srcFilePath.equals(tmp.getSrcFile().getPath()) && dataFilePath.equals(tmp.getDataFile().getPath()))
			{
				l = tmp;
				break;
			}
		}

		return l;
	}
}
