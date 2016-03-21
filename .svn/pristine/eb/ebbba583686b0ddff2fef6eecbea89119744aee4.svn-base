package com.inspur.ftpparserframework.dbloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

import org.apache.log4j.Logger;

import com.inspur.ftpparserframework.config.ConfigReader;
import com.inspur.ftpparserframework.dbloader.obj.DbLoaderColumn;
import com.inspur.ftpparserframework.dbloader.obj.DbLoaderDatabase;
import com.inspur.ftpparserframework.dbloader.obj.DbLoaderTable;
import com.inspur.ftpparserframework.util.Constant;
import com.inspur.ftpparserframework.util.FileUtil;
import com.inspur.ftpparserframework.util.StringUtil;
import com.inspur.ftpparserframework.util.TimeUtil;

/**
 * GBase集群数据入库
 * @author 郭启刚
 *
 */
public class GBaseClusterLoader implements IDbLoader
{
	private Logger log = Logger.getLogger(getClass());

	public static final String LOAD_MODE_INSERT = "INSERT";
	public static final String LOAD_MODE_APPEND = "APPEND";
	public static final String LOAD_MODE_REPLACE = "REPLACE";
	public static final String LOAD_MODE_TRUNCATE = "TRUNCATE";

	public static final String DEFAULT_LOAD_MODE = LOAD_MODE_APPEND;
	public static final String DEFAULT_FIELDS_TERMINATED_BY = ",";
	public static final String DEFAULT_OPTIONALLY_ENCLOSED_BY = "\"";

	public static String deleteOldFilesDay = "";

//	@Override
	public void load(File srcFile, File dataFile, DbLoaderDatabase db, DbLoaderTable table, List<DbLoaderColumn> columns)
			throws Exception
	{
		load(srcFile, dataFile, db, table, columns, DEFAULT_LOAD_MODE, DEFAULT_FIELDS_TERMINATED_BY,
				DEFAULT_OPTIONALLY_ENCLOSED_BY);
	}

//	@Override
	public void load(File srcFile, File dataFile, DbLoaderDatabase db, DbLoaderTable table,
			List<DbLoaderColumn> columns, String loadMode) throws Exception
	{
		load(srcFile, dataFile, db, table, columns, loadMode, DEFAULT_FIELDS_TERMINATED_BY,
				DEFAULT_OPTIONALLY_ENCLOSED_BY);
	}

//	@Override
	public void load(File srcFile, File dataFile, DbLoaderDatabase db, DbLoaderTable table,
			List<DbLoaderColumn> columns, String loadMode, String fieldsTeminatedBy, String optionallyEnclosedBy)
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
		String tableInfo = "[表名=" + table.getName() + "]";

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

			File controlFile = new File(Constant.GBASE_CTL, baseFileName + ".cfg");
			if (!controlFile.getParentFile().exists())
			{
				controlFile.getParentFile().mkdirs();
			}

			File logFile = new File(Constant.GBASE_LOG, baseFileName + ".log");
			if (!logFile.getParentFile().exists())
			{
				logFile.getParentFile().mkdirs();
			}

			File badFile = new File(Constant.GBASE_BAD, baseFileName + ".bad");
			if (!badFile.getParentFile().exists())
			{
				badFile.getParentFile().mkdirs();
			}

			File archiveFile = new File(Constant.GBASE_ARC, baseFileName + ".arc");
			if (!archiveFile.getParentFile().exists())
			{
				archiveFile.getParentFile().mkdirs();
			}

			// 2racle control file
			String gbaseControlPath = ConfigReader.getProperties("gbldr.gbcontrolfile");
			String gbaseControlFile = gbaseControlPath+ table.getName()+".cfg";
			StringBuffer sbControlFileContent = new StringBuffer();
			File file = new File(gbaseControlFile);
	        InputStream in = null;
	        try {
	            in = new FileInputStream(file);
	            int tempbyte = 0;
	            while ((tempbyte = in.read()) != -1) {
	            	sbControlFileContent.append((char)tempbyte);
	            }
	            in.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	            log.info("#获取gbload控制文件出错#-" + gbaseControlFile + "-[文件内容=" + sbControlFileContent.toString() + "]");
	        } finally
			{
				if (in != null)
				{
					in.close();
				}

			}
	        
			PrintWriter pw = new PrintWriter(controlFile);
			pw.println(sbControlFileContent.toString().replace("##loadfilelist##", dataFile.getAbsolutePath()));
			pw.close();

			// 3shell
			//gbloader -ugbase -pgbase20110531 -T10 -l${LOGFILE_DIR}/${filename}.log -C${BADFILE_DIR}/${filename}.errdata ${CTLFILE_DIR}/${filename}.ctl
			String userId = ConfigReader.getProperties("gbldr.dbuserid");
			String userPass = ConfigReader.getProperties("gbldr.dbuserpass");
			String gbPort = ConfigReader.getProperties("gbldr.gbport");
			String gbIp = ConfigReader.getProperties("gbldr.gbip");
			String shell = "/opt/dispatch_server/dispcli -u" + userId + " -p" + userPass + " -P" + gbPort + " -h " + gbIp
	            	+ " -t 0"
					+ " -l"
					+ logFile.getCanonicalPath()
					+ " "
					+ controlFile.getCanonicalPath();

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
			if (srcFile.getCanonicalPath() != dataFile.getCanonicalPath())
			{
				// 如果最原始的文件本身就是数据文件，则不由该程序备份，交由调用改程序的上一级程序处理

				// modify by wuyg 2013-1-17,不在存arc文件,有些情况下该类文件数据量过于庞大,而且一般用不到。
				//				dataFile.renameTo(archiveFile);
				dataFile.delete();
			}

			// 6parse log file
			if (logFile.exists())
			{
				parseLog(logFile, succRows, errorRows, errorLog);
			}
			errorMessage += errorLog;

			// 7delete files
			deleteOldFiles();

			// 日志格式：#数据入库#-[结束]-[源文件=xx,大小=xxByte,时间=yyyy-MM-dd HH:mm:ss]-[待入库文件=yy,大小=xxByte,时间=yyyy-MM-dd HH:mm:ss]-[成功条数=x,失败条数=y]-[耗时=n毫秒] 
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
		} catch (Exception e)
		{
			log.error("#数据入库#-[出错]-" + srcFileInfo + "-" + dataFileInfo + "-" + tableInfo + "-[成功条数="
					+ (succRows.length() == 0 ? "0" : succRows) + ",失败条数="
					+ (errorRows.length() == 0 ? "0" : errorRows) + "]-[耗时=" + (System.currentTimeMillis() - startTime)
					+ "毫秒]-[错误信息=" + errorMessage + "]");
			throw e;
		}
	}

	private void deleteOldFiles() throws IOException
	{
		// 删除n天前的数据，为避免每次运行sqlldr之前都进行文件检索耗费较多时间，限定只在每天的0,6,12,18点进行文件删除工作，而且每天只运行一次。

		String day = TimeUtil.nowTime2str("dd");
		String hour = TimeUtil.nowTime2str("HH");

		if (!deleteOldFilesDay.equals(day)
				&& ("00".equals(hour) || "06".equals(hour) || "12".equals(hour) || "18".equals(hour)))
		{
			log.info("删除过期文件开始");

			int sqlldrFileDays = 0;
			try
			{
				sqlldrFileDays = Integer.parseInt(ConfigReader.getProperties("gbldr.fileSavedDays"));
				log.debug("SystemConfig.xml配置的sqlldr过程文件存储时长(gbldr.fileSavedDays):" + sqlldrFileDays + "天");
			} catch (Exception e)
			{
				log.info("SystemConfig.xml未配置sqlldr过程文件存储时长(gbldr.fileSavedDays),系统采用默认设置:"
						+ Constant.SQLLDR_FILE_SAVED_DAYS + "天");
				sqlldrFileDays = Constant.SQLLDR_FILE_SAVED_DAYS;
			}

			FileUtil.deleteFilesNdaysAgo(Constant.GBASE_ARC, sqlldrFileDays);
			FileUtil.deleteFilesNdaysAgo(Constant.GBASE_BAD, sqlldrFileDays);
			FileUtil.deleteFilesNdaysAgo(Constant.GBASE_CTL, sqlldrFileDays);
			FileUtil.deleteFilesNdaysAgo(Constant.GBASE_LOG, sqlldrFileDays);
			FileUtil.deleteFilesNdaysAgo(Constant.GBASE_TXT, sqlldrFileDays);

			deleteOldFilesDay = day;

			log.info("删除过期文件结束");
		}
	}

	/**
	 * 解析sqlldr的log文件并输出数据入库成功和失败的条数
	 * 
	 * @param logFile
	 * @param succRows
	 * @param errorRows
	 * @param errorLog
	 */
	private void parseLog(File logFile, StringBuffer succRows, StringBuffer errorRows, StringBuffer errorLog)
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
				int succRowsIndex = line.indexOf("finished: loaded");// 英文日志
				if (succRowsIndex >= 0)
				{
					succRows.append((line.substring(succRowsIndex + "finished: loaded".length(),
							line.indexOf("records, skipped")).trim()));
				}

				int errorRowsIndex = line.indexOf("records, skipped");
				if (errorRowsIndex >= 0)
				{
					errorRows.append((line.substring(errorRowsIndex + "records, skipped".length(),
							line.lastIndexOf("records")).trim()));
				}

				if (line.indexOf("ERROR:") >= 0 && errorLog.length() == 0)// 只取一行错误信息，避免信息过多日志复杂
				{
					errorLog.append(line.substring(line.indexOf("ERROR:")).trim());
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

}
