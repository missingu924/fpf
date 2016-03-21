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

import com.inspur.ftpparserframework.log.obj.ParserMainLog;
import com.inspur.ftpparserframework.log.obj.ParserMiddleLog;
import com.inspur.ftpparserframework.log.obj.ParserSqlldrLog;
import com.inspur.ftpparserframework.report.Test;
import com.inspur.ftpparserframework.util.TimeUtil;

/**
 * 文件处理日志解析器
 * @author 武玉刚
 *
 */
public class ParserLogAnalyzer
{
	private static Logger log = Logger.getLogger(Test.class);

	public static List<ParserMainLog> rootLogs = new ArrayList<ParserMainLog>();// 文件解析入库跟日志
	public static List<ParserMiddleLog> middleLogs = new ArrayList<ParserMiddleLog>();// 文件转换日志
	public static List<ParserSqlldrLog> sqlldrLogs = new ArrayList<ParserSqlldrLog>();// 数据入库日志

	public static List<ParserMainLog> errorParserLogs = new ArrayList<ParserMainLog>();// 出错的文件

	public static String nowTime = TimeUtil.nowTime2str("yyyyMMddHHmmss");

	public static File rootCsvFile = null;
	public static File middleCsvFile = null;
	public static File sqlldrCsvFile = null;
	public static PrintWriter rootCsvPw = null;
	public static PrintWriter middleCsvPw = null;
	public static PrintWriter sqlldrCsvPw = null;

	public static long startTime = System.currentTimeMillis();
	public static long endTime = 0;

	public static long totalStartTime = startTime;

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
					if (rootCsvFile == null)
					{
						rootCsvFile = new File(logFile.getParent(), "parser-" + nowTime + ".csv");
						rootCsvPw = new PrintWriter(rootCsvFile);
						rootCsvPw.println(new ParserMainLog().getCsvTitle());
						middleCsvFile = new File(logFile.getParent(), "middle-" + nowTime + ".csv");
						middleCsvPw = new PrintWriter(middleCsvFile);
						middleCsvPw.println(new ParserMiddleLog().getCsvTitle());
						sqlldrCsvFile = new File(logFile.getParent(), "sqlldr-" + nowTime + ".csv");
						sqlldrCsvPw = new PrintWriter(sqlldrCsvFile);
						sqlldrCsvPw.println(new ParserSqlldrLog().getCsvTitle());
					}

					// FPF解析日志
					System.out.println("第" + (i + 1) + "个日志文件解析开始:" + logFile.getCanonicalPath());
					parseOneFile(logFile);
					System.out.println("第" + (i + 1) + "个日志文件解析结束:" + logFile.getCanonicalPath());
				} else
				{
					System.out.println("日志文件不存在,请检查:" + logFile.getCanonicalPath());
				}
			}

			// 写文件解析入库总日志信息			
			System.out.println("文件解析入库根日志信息写出到文件:" + rootCsvFile.getCanonicalPath());
			for (int i = 0; i < rootLogs.size(); i++)
			{
				rootCsvPw.println(rootLogs.get(i).toCsv());
			}
			rootCsvPw.close();

			// 写文件转换日志信息			
			System.out.println("文件转换日志信息写出到文件:" + middleCsvFile.getCanonicalPath());
			for (int i = 0; i < middleLogs.size(); i++)
			{
				middleCsvPw.println(middleLogs.get(i).toCsv());
			}
			middleCsvPw.close();

			// 写入库日志信息			
			System.out.println("数据入库日志信息写出到文件:" + sqlldrCsvFile.getCanonicalPath());
			for (int i = 0; i < sqlldrLogs.size(); i++)
			{
				sqlldrCsvPw.println(sqlldrLogs.get(i).toCsv());
			}
			sqlldrCsvPw.close();

			System.out.println("本次日志解析累计耗时" + (System.currentTimeMillis() - totalStartTime) / 1000.0 + "秒");

			if (errorParserLogs.size() > 0)
			{
				System.out.println("\n注意:以下" + errorParserLogs.size() + "个文件本次解析出现错误,请关注:");
				for (int i = 0; i < errorParserLogs.size(); i++)
				{
					ParserMainLog l = errorParserLogs.get(i);
					System.out.println("文件=" + l.getSrcFile().getPath() + ",错误信息=" + l.getErrorMessage() + ",备注信息="
							+ l.getMark());
				}
			}

			if (rootLogs.size() > 0)
			{
				System.out.println("\n注意:以下" + rootLogs.size() + "个文件本次未解析完,请关注:");
				for (int i = 0; i < rootLogs.size(); i++)
				{
					ParserMainLog l = rootLogs.get(i);
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
		BufferedReader bf = new BufferedReader(new FileReader(logFile));
		String line = null;
		int lineNum = 0;
		while ((line = bf.readLine()) != null)
		{
			// 解析一行日志
			parseOneLine(line);

			if (++lineNum % 10000 == 0)
			{
				endTime = System.currentTimeMillis();
				System.out.println("已解析" + lineNum + "行,本次耗时" + (endTime - startTime) / 1000.0 + "秒");
				sqlldrCsvPw.flush();
				middleCsvPw.flush();
				rootCsvPw.flush();
			} else
			{
				startTime = endTime;
			}
		}
		System.out.println("该文件共解析" + lineNum + "行");

		bf.close();
	}

	/**
	 * 解析一行日志信息
	 * 
	 * @param line
	 * @throws Exception
	 * @throws ParseException
	 */
	public static void parseOneLine(String line) throws Exception
	{
		// 数据入库日志
		if (line.indexOf(ParserSqlldrLog.flag) > 0)
		{
			ParserSqlldrLog sl = new ParserSqlldrLog();
			if (sl.parseLog(line))
			{
				ParserSqlldrLog preSl = getSqlldrLog(sl);
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
				ParserMiddleLog prePml = getParserMiddleLog(pml);
				if (prePml != null)
				{
					prePml.merge(pml);
				} else
				{
					middleLogs.add(pml);
				}
			}
		}
		// 文件解析入库根日志
		else if (line.indexOf(ParserMainLog.flag) > 0)
		{
			ParserMainLog pl = new ParserMainLog();
			if (pl.parseLog(line))
			{
				ParserMainLog prePl = getParserLog(pl);
				if (prePl != null)
				{
					// 合并日志信息
					prePl.merge(pl);

					// 完善根日志信息
					for (int j = 0; j < sqlldrLogs.size(); j++)
					{
						ParserSqlldrLog sl = sqlldrLogs.get(j);
						if (sl.getSrcFile().equals(prePl.getSrcFile()))
						{
							prePl.setSuccRows(prePl.getSuccRows() + sl.getSuccRows());
							prePl.setErrorRows(prePl.getErrorRows() + sl.getErrorRows());

							if (sl.STATE_ERROR.equals(sl.getHandleState()))
							{
								prePl.setHandleState(sl.getHandleState());
								prePl.setErrorMessage(sl.getErrorMessage());
							}
						}
					}

					// 日志显示文件处理已正常结束或出错,即一次解析完毕。
					if (prePl.STATE_END.equals(prePl.getHandleState())
							|| prePl.STATE_ERROR.equals(prePl.getHandleState()))
					{
						// 输出到文件(三类日志都输出),输出后从列表中移除(三类日志列表都移除)
						if (rootCsvPw != null)
						{
							rootCsvPw.println(prePl.toCsv());
						} else
						{
							prePl.saveOrUpdate();
						}

						rootLogs.remove(prePl);

						for (int i = 0; i < middleLogs.size(); i++)
						{
							ParserMiddleLog ml = middleLogs.get(i);
							if (prePl.getSrcFile().equals(ml.getSrcFile()))
							{
								if (middleCsvPw != null)
								{
									middleCsvPw.println(ml.toCsv());
								} else
								{
									ml.saveOrUpdate();
								}

								middleLogs.remove(i);
								i--;
							}
						}

						for (int i = 0; i < sqlldrLogs.size(); i++)
						{
							ParserSqlldrLog sl = sqlldrLogs.get(i);
							if (prePl.getSrcFile().equals(sl.getSrcFile()))
							{
								if (sqlldrCsvPw != null)
								{
									sqlldrCsvPw.println(sl.toCsv());
								} else
								{
									sl.saveOrUpdate();
								}

								sqlldrLogs.remove(i);
								i--;
							}
						}

						// 出错的文件
						if (prePl.STATE_ERROR.equals(prePl.getHandleState()))
						{
							errorParserLogs.add(prePl);
						}
					}
				} else
				{
					rootLogs.add(pl);
				}

				// error log
				ParserMainLog errorParserLog = getErrorParserLog(pl);
				if (errorParserLog != null && pl.STATE_END.equals(pl.getHandleState()))//之前解析出过错误但最终解析成功
				{
					errorParserLog.setMark(TimeUtil.date2str(pl.getEndTime()) + "再次处理成功");
				}
			}
		}
	}

	private static ParserMiddleLog getParserMiddleLog(ParserMiddleLog other)
	{
		ParserMiddleLog l = null;
		for (int i = middleLogs.size() - 1; i >= 0; i--)// 倒序查找,即找时间最晚的
		{
			ParserMiddleLog tmp = middleLogs.get(i);
			if (tmp.equals(other))
			{
				l = tmp;
				break;
			}
		}

		return l;
	}

	public static ParserMainLog getParserLog(ParserMainLog other)
	{
		ParserMainLog l = null;
		for (int i = rootLogs.size() - 1; i >= 0; i--)// 倒序查找,即找时间最晚的
		{
			ParserMainLog tmp = rootLogs.get(i);
			if (tmp.equals(other))
			{
				l = tmp;
				break;
			}
		}

		return l;
	}

	public static ParserMainLog getErrorParserLog(ParserMainLog other)
	{
		ParserMainLog l = null;
		for (int i = errorParserLogs.size() - 1; i >= 0; i--)// 倒序查找,即找时间最晚的
		{
			ParserMainLog tmp = errorParserLogs.get(i);
			if (tmp.equals(other))
			{
				l = tmp;
				break;
			}
		}

		return l;
	}

	public static ParserSqlldrLog getSqlldrLog(ParserSqlldrLog other)
	{
		ParserSqlldrLog l = null;
		for (int i = sqlldrLogs.size() - 1; i >= 0; i--)// 倒序查找,即找时间最晚的
		{
			ParserSqlldrLog tmp = sqlldrLogs.get(i);
			if (tmp.equals(other))
			{
				l = tmp;
				break;
			}
		}

		return l;
	}
}
