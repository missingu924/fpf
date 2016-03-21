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

	public static List<ParserMainLog> parserLogs = new ArrayList<ParserMainLog>();// 文件解析入库跟日志
	public static List<ParserMiddleLog> parserMiddleLogs = new ArrayList<ParserMiddleLog>();// 文件转换日志
	public static List<ParserSqlldrLog> sqlldrLogs = new ArrayList<ParserSqlldrLog>();// 数据入库日志
	public static List<ParserMainLog> errorParserLogs = new ArrayList<ParserMainLog>();// 出错的文件
	public static List<ParserMainLog> justStartParserLogs = new ArrayList<ParserMainLog>();// 仅开始未处理完的文件

	public static void main(String[] args)
	{
		try
		{
			if (args.length == 0)
			{
				System.out.println("请指定需解析日志的全路径,多个文件之间用逗号隔开.");
				System.exit(-1);
			}
			String[] filePaths = args[0].split("\\,");

			File logFile = null;
			for (int i = 0; i < filePaths.length; i++)
			{
				logFile = new File(filePaths[i]);
				if (logFile.exists())
				{
					// FPF解析日志
					System.out.println("第" + (i + 1) + "个日志文件解析开始:" + logFile.getCanonicalPath());
					parseOneFile(logFile);
					System.out.println("第" + (i + 1) + "个日志文件解析结束:" + logFile.getCanonicalPath());
				} else
				{
					System.out.println("日志文件不存在,请检查:" + logFile.getCanonicalPath());
				}
			}

			System.out.println("本次共解析出" + parserLogs.size() + "条根日志," + parserMiddleLogs.size() + "条文件转换日志,"
					+ sqlldrLogs.size() + "条数据入库日志");
			System.out.println("合并日志信息……");

			// 完善parselog的信息
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

						if ("出错".equals(sl.getHandleState()))
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
			// 写文件解析入库总日志信息
			File csvFile = new File(logFile.getParent(), "parser-" + nowTime + ".csv");
			System.out.println("文件解析入库根日志信息写出到文件:" + csvFile.getCanonicalPath());
			PrintWriter pw = new PrintWriter(csvFile);
			pw.println(new ParserMainLog().getCsvTitle());
			for (int i = 0; i < parserLogs.size(); i++)
			{
				pw.println(parserLogs.get(i).toCsv());
			}
			pw.close();

			// 写文件转换日志信息
			csvFile = new File(logFile.getParent(), "middle-" + nowTime + ".csv");
			System.out.println("文件转换日志信息写出到文件:" + csvFile.getCanonicalPath());
			pw = new PrintWriter(csvFile);
			pw.println(new ParserMiddleLog().getCsvTitle());
			for (int i = 0; i < parserMiddleLogs.size(); i++)
			{
				pw.println(parserMiddleLogs.get(i).toCsv());
			}
			pw.close();

			// 写入库日志信息
			csvFile = new File(logFile.getParent(), "sqlldr-" + nowTime + ".csv");
			System.out.println("数据入库日志信息写出到文件:" + csvFile.getCanonicalPath());
			pw = new PrintWriter(csvFile);
			pw.println(new ParserSqlldrLog().getCsvTitle());
			for (int i = 0; i < sqlldrLogs.size(); i++)
			{
				pw.println(sqlldrLogs.get(i).toCsv());
			}
			pw.close();

			if (errorParserLogs.size() > 0)
			{
				System.out.println("\n注意:以下" + errorParserLogs.size() + "个文件本次解析出现错误,请关注:");
				for (int i = 0; i < errorParserLogs.size(); i++)
				{
					ParserMainLog l = errorParserLogs.get(i);
					System.out.println("文件=" + l.getSrcFile().getPath() + ",错误信息=" + l.getErrorMessage());
				}
			}

			if (justStartParserLogs.size() > 0)
			{
				System.out.println("\n注意:以下" + justStartParserLogs.size() + "个文件本次未解析完,请关注:");
				for (int i = 0; i < justStartParserLogs.size(); i++)
				{
					ParserMainLog l = justStartParserLogs.get(i);
					System.out.println("文件=" + l.getSrcFile().getPath() + ",开始时间="
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
			// 数据入库日志
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
			// 文件转换日志
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
			// 文件解析入库根日志
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
				System.out.println("已解析" + lineNum + "行");
			}
		}
		System.out.println("该文件共解析" + lineNum + "行");

		bf.close();
	}

	private static ParserMiddleLog getParserMiddleLogByFilePath(String srcFilePath, String handleName)
	{
		ParserMiddleLog l = null;
		for (int i = parserMiddleLogs.size() - 1; i >= 0; i--)// 倒序查找,即找时间最晚的
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
		for (int i = parserLogs.size() - 1; i >= 0; i--)// 倒序查找,即找时间最晚的
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
		for (int i = sqlldrLogs.size() - 1; i >= 0; i--)// 倒序查找,即找时间最晚的
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
