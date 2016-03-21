package com.inspur.ftpparserframework.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.inspur.ftpparserframework.config.ConfigReader;

/**
 * @deprecated 建议使用com.inspur.ftpparserframework.dbloader.OracleLoader或者com.inspur.ftpparserframework.dbloader.DbLoaderUtil
 * 
 */
@Deprecated
public class OracleUtil
{
	public static String LOAD_MODE_INSERT = "INSERT";
	public static String LOAD_MODE_APPEND = "APPEND";
	public static String LOAD_MODE_REPLACE = "REPLACE";
	public static String LOAD_MODE_TRUNCATE = "TRUNCATE";

	private static Logger log = Logger.getLogger(OracleUtil.class);

	private static final int ORA_IN_LIMIT = 1000;

	public static StringBuffer getOraInSql(String columnName, List<String> values)
	{
		StringBuffer sql = new StringBuffer();

		List<String> inList = new ArrayList<String>();
		String inSql = "";
		for (int i = 0; i < values.size(); i++)
		{
			inSql += "'" + values.get(i) + "',";

			if ((i + 1) % ORA_IN_LIMIT == 0 || (i == values.size() - 1))
			{
				inList.add(inSql.substring(0, inSql.length() - 1));
				inSql = "";
			}
		}

		for (int i = 0; i < inList.size(); i++)
		{
			if (i > 0)
			{
				sql.append(" or ");
			}
			sql.append(columnName).append(" in (").append(inList.get(i)).append(") ");
		}

		return sql;
	}

	/**
	 * 通过oracle的sqlldr进行数据入库
	 * 
	 * @param srcFile
	 *            最原始的文件，记录日志是需要使用最原始文件的全路径作为标识。
	 * @param dataFile
	 *            入库的数据文件
	 * @param tableName
	 *            入库的表名
	 * @param columns
	 *            入库的字段名列表
	 * @param loadMode
	 *            入库模式
	 * @throws Exception
	 */
	public static void load(File srcFile, File dataFile, String tableName, List<String> columns, String loadMode)
			throws Exception
	{
		String errorMessage = "";

		StringBuffer succRows = new StringBuffer();
		StringBuffer errorRows = new StringBuffer();
		StringBuffer errorLog = new StringBuffer();

		String srcFileInfo = "[源文件=" + srcFile.getCanonicalPath() + ",大小=" + srcFile.length() + "Byte,时间="
				+ TimeUtil.date2str(srcFile.lastModified()) + "]";
		String dataFileInfo = "[待入库文件=" + dataFile.getCanonicalPath() + ",大小=" + dataFile.length() + "Byte,时间="
				+ TimeUtil.date2str(dataFile.lastModified()) + "]";
		String tableInfo = "[表名=" + tableName + "]";

		long startTime = System.currentTimeMillis();

		try
		{
			// #数据入库#-[开始]-[源文件=xx,大小=xxByte,时间=yyyy-MM-dd HH:mm:ss]-[待入库文件=yy,大小=yyByte,时间=yyyy-MM-dd HH:mm:ss]-[表名=xx]
			log.info("#数据入库#-[开始]-" + srcFileInfo + "-" + dataFileInfo + "-" + tableInfo);

			// 1 all files
			File baseDir = Constant.BASE_DIR;
			if (!baseDir.exists())
			{
				baseDir.mkdirs();
			}
			String baseFileName = dataFile.getName();

			File controlFile = new File(Constant.ORACLE_CTL, baseFileName + ".ctl");
			if (!controlFile.getParentFile().exists())
			{
				controlFile.getParentFile().mkdirs();
			}

			File logFile = new File(Constant.ORACLE_LOG, baseFileName + ".log");
			if (!logFile.getParentFile().exists())
			{
				logFile.getParentFile().mkdirs();
			}

			File badFile = new File(Constant.ORACLE_BAD, baseFileName + ".bad");
			if (!badFile.getParentFile().exists())
			{
				badFile.getParentFile().mkdirs();
			}

			File archiveFile = new File(Constant.ORACLE_ARC, baseFileName + ".arc");
			if (!archiveFile.getParentFile().exists())
			{
				archiveFile.getParentFile().mkdirs();
			}

			// 2racle control file
			PrintWriter pw = new PrintWriter(controlFile);
			pw.println(" LOAD DATA");
			pw.println(" CHARACTERSET ZHS16GBK");
			pw.println(" INFILE '" + dataFile.getAbsolutePath() + "'");
			if (StringUtil.isEmpty(loadMode))
			{
				loadMode = LOAD_MODE_INSERT;
			}
			pw.println(loadMode);
			pw.println(" INTO TABLE " + tableName + " FIELDS TERMINATED BY ',' TRAILING NULLCOLS ");
			pw.println(" (");

			for (int i = 0; i < columns.size(); i++)
			{
				String column = columns.get(i);
				pw.print("\t" + column);
				// modify by wuyg 2010-10-29 for td mr
				if ("starttime".equalsIgnoreCase(column) || "start_time".equalsIgnoreCase(column)
						|| "endtime".equalsIgnoreCase(column) || "end_time".equalsIgnoreCase(column)
						|| "timestamp".equalsIgnoreCase(column) || "time_stamp".equalsIgnoreCase(column))
				{
					pw.print(" date 'yyyymmddhh24miss'");
				}
				if (i != columns.size() - 1)
				{
					pw.println(",");
				}
			}

			pw.println("\n )");
			pw.close();

			// 3shell
			String userid = ConfigReader.getProperties("sqlldr.dbuserid");
			String shell = "sqlldr userid="
					+ userid
					+ " control="
					+ controlFile.getCanonicalPath()
					+ " log="
					+ logFile.getCanonicalPath()
					+ " bad= "
					+ badFile.getCanonicalPath()
					+ " skip=1 parallel=true bindsize=20000000 readsize=20000000 errors=999999999 rows=5000 columnarrayrows=10000";

			log.info("#数据入库#-" + srcFileInfo + "-" + dataFileInfo + "-[入库脚本=" + shell + "]");

			// 4run
			String exeResult;
			Process process = Runtime.getRuntime().exec(shell);
			BufferedReader out = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			while ((exeResult = out.readLine()) != null)
			{
				if (exeResult.length() > 0)
				{
					log.info("#数据入库#-" + srcFileInfo + "-" + dataFileInfo + "-[入库标准输出=" + exeResult + "]");
				}
			}
			while ((exeResult = error.readLine()) != null)
			{
				if (exeResult.length() > 0)
				{
					errorMessage += exeResult;
					log.error("#数据入库#-" + srcFileInfo + "-" + dataFileInfo + "-[入库错误输出=" + exeResult + "]");
				}
			}
			out.close();
			error.close();
			process.waitFor();

			// 5archive
			// dataFile.renameTo(archiveFile);

			// 6parse log file
			if (logFile.exists())
			{
				parseLog(logFile, succRows, errorRows, errorLog);
			}
			errorMessage += errorLog;

			// 日志格式：#数据入库#-[结束]-[源文件=xx,大小=xxByte,时间=yyyy-MM-dd HH:mm:ss]-[待入库文件=yy,大小=xxByte,时间=yyyy-MM-dd
			// HH:mm:ss]-[成功条数=x,失败条数=y]-[耗时=n毫秒]
			if (StringUtil.isEmpty(errorMessage))
			{
				log.info("#数据入库#-[结束]-" + srcFileInfo + "-" + dataFileInfo + "-" + tableInfo + "-[成功条数="
						+ (succRows.length() == 0 ? "0" : succRows) + ",失败条数="
						+ (errorRows.length() == 0 ? "0" : errorRows) + "]-[耗时="
						+ (System.currentTimeMillis() - startTime) + "毫秒]");
			} else
			{
				log.error("#数据入库#-[出错]-" + srcFileInfo + "-" + dataFileInfo + "-" + tableInfo + "-[成功条数="
						+ (succRows.length() == 0 ? "0" : succRows) + ",失败条数="
						+ (errorRows.length() == 0 ? "0" : errorRows) + "]-[耗时="
						+ (System.currentTimeMillis() - startTime) + "毫秒]-[错误信息=" + errorMessage + "]");
			}

			// 7delete files
			// 删除n天前的数据，为避免每次运行sqlldr之前都进行文件检索耗费较多时间，限定只在每天的0,6,12,18点进行文件删除工作
			String hour = TimeUtil.nowTime2str("HH");
			if ("00".equals(hour) || "06".equals(hour) || "12".equals(hour) || "18".equals(hour))
			{
				log.info("删除过期文件开始");

				int sqlldrFileDays = 0;
				try
				{
					sqlldrFileDays = Integer.parseInt(ConfigReader.getProperties("sqlldr.fileSavedDays"));
					log.debug("SystemConfig.xml配置的sqlldr过程文件存储时长(sqlldr.fileSavedDays):" + sqlldrFileDays + "天");
				} catch (Exception e)
				{
					log.info("SystemConfig.xml未配置sqlldr过程文件存储时长(sqlldr.fileSavedDays),系统采用默认设置:"
							+ Constant.SQLLDR_FILE_SAVED_DAYS + "天");
					sqlldrFileDays = Constant.SQLLDR_FILE_SAVED_DAYS;
				}

				FileUtil.deleteFilesNdaysAgo(Constant.ORACLE_ARC, sqlldrFileDays);
				FileUtil.deleteFilesNdaysAgo(Constant.ORACLE_BAD, sqlldrFileDays);
				FileUtil.deleteFilesNdaysAgo(Constant.ORACLE_CTL, sqlldrFileDays);
				FileUtil.deleteFilesNdaysAgo(Constant.ORACLE_LOG, sqlldrFileDays);
				FileUtil.deleteFilesNdaysAgo(Constant.ORACLE_TXT, sqlldrFileDays);

				log.info("删除过期文件结束");
			}
		} catch (Exception e)
		{
			log.error("#数据入库#-[出错]-" + srcFileInfo + "-" + dataFileInfo + "-" + tableInfo + "-[成功条数="
					+ (succRows.length() == 0 ? "0" : succRows) + ",失败条数="
					+ (errorRows.length() == 0 ? "0" : errorRows) + "]-[耗时=" + (System.currentTimeMillis() - startTime)
					+ "毫秒]-[错误信息=" + errorMessage + "]");
			throw e;
		}
	}

	// 该方法仍然保留，保证未更新到新版本的老程序仍能够正常入库。
	public static void load(String tableName, List<String> columns, String loadMode, String dataFilePath)
			throws Exception
	{
		File dataFile = new File(dataFilePath);
		load(dataFile, dataFile, tableName, columns, loadMode);
	}

	/**
	 * 解析sqlldr的log文件并输出数据入库成功和失败的条数
	 * 
	 * @param logFile
	 * @param succRows
	 * @param errorRows
	 * @param errorLog
	 */
	private static void parseLog(File logFile, StringBuffer succRows, StringBuffer errorRows, StringBuffer errorLog)
	{
		FileReader fr = null;
		BufferedReader bf = null;
		try
		{
			fr = new FileReader(logFile);
			bf = new BufferedReader(fr);
			String line = null;
			while ((line = bf.readLine()) != null)
			{
				// Oracle sqlldr 英文日志记录成功失败条数时区分单复数。只有一条成功的时候，log形如： "1 Row successfully loaded." 多条时形如： "10 Rows
				// successfully loaded. "
				// Oracle sqlldr 英文日志log形如：
				// 137 Rows successfully loaded.
				// 0 Rows not loaded due to data errors.

				// Oracle sqlldr 中文日志log形如：
				// 5 行 加载成功。
				// 由于数据错误, 2行 没有加载。

				line = line.replaceAll("Rows", "Row");

				int succRowsIndex = line.indexOf("Row successfully loaded");// 英文日志
				if (succRowsIndex < 0)
				{
					succRowsIndex = line.indexOf("行 加载成功");// 中文日志
				}
				if (succRowsIndex >= 0)
				{
					succRows.append(line.substring(0, succRowsIndex).trim());
				}

				int errorRowsIndex = line.indexOf("Row not loaded due to data errors");// 英文日志
				if (errorRowsIndex < 0 && line.indexOf("由于数据错误,") >= 0)
				{
					line = line.replaceAll("由于数据错误,", "");// 形如：" 2行 没有加载。"
					errorRowsIndex = line.indexOf("行 没有加载");// 中文日志
				}
				if (errorRowsIndex >= 0)
				{
					errorRows.append(line.substring(0, errorRowsIndex).trim());
				}

				if (line.indexOf("ORA-") >= 0 && errorLog.length() == 0)// 只取一行错误信息，避免信息过多日志复杂
				{
					errorLog.append(line);
				}
			}
		} catch (Exception e)
		{
			log.error(e.getMessage(), e);
		} finally
		{
			try
			{
				if (bf != null)
				{
					bf.close();
				}
				if (fr != null)
				{
					fr.close();
				}
			} catch (Exception e)
			{
			}
		}
	}

	public static void main(String[] args)
	{
		try
		{
			// File srcFile = new File("c:/test.xml");
			// File dataFile = new File("c:/test_data.txt");
			// String tableName = "pm.f_g_c_cell_hour";
			// List<String> columns = new ArrayList<String>();
			// columns.add("start_time");
			// columns.add("id");
			//
			// load(srcFile, dataFile, tableName, columns, null);

			File logFile = new File("c:/TD-SCDMA_MRS_DATANG_OMCR_905_20121120150000_UtranTxPower.txt.log");
			StringBuffer succRows = new StringBuffer();
			StringBuffer errorRows = new StringBuffer();
			StringBuffer errorLog = new StringBuffer();

			parseLog(logFile, succRows, errorRows, errorLog);

			System.out.println(succRows);
			System.out.println(errorRows);
			System.out.println(errorLog);

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
