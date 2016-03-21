package com.inspur.ftpparserframework.parser;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.inspur.ftpparserframework.config.ConfigReader;
import com.inspur.ftpparserframework.exception.GetParameterException;
import com.inspur.ftpparserframework.rmi.StatusCenter;
import com.inspur.ftpparserframework.transformator.TransformatEngine;
import com.inspur.ftpparserframework.util.Constant;
import com.inspur.ftpparserframework.util.FileUtil;
import com.inspur.ftpparserframework.util.FilterUtil;
import com.inspur.ftpparserframework.util.TimeUtil;

/**
 * 文件解析线程，解析一个文件即启动一个解析线程实例。
 * @author 武玉刚
 *
 */
public class ParseThread extends Thread
{
	private Logger log = Logger.getLogger(getClass());
	private File file = null;
	private Map<String, Object> paramMap = new HashMap<String, Object>();

	//	private static String deleteOldFilesDay = "";

	private static Map<String, String> deleteOldFileMap = new HashMap<String, String>();

	public ParseThread(File file, Map<String, Object> paramMap)
	{
		this.file = file;
		this.paramMap = paramMap;
	}

	@Override
	public void run()
	{
		String fileInfo = "";
		long startTime = System.currentTimeMillis();
		try
		{
			fileInfo = "[源文件=" + file.getCanonicalPath() + ",大小=" + file.length() + "Byte,时间="
					+ TimeUtil.date2str(file.lastModified()) + "]";

			if (FilterUtil.shouldParse(file))
			{
				// 日志格式：#文件解析入库#-[开始]-[源文件=xx,大小=xxByte,时间=yyyy-MM-dd HH:mm:ss]
				log.info("#文件解析入库#-[开始]-" + fileInfo);

				// step1:解析文件
				new TransformatEngine().transformat(file, paramMap);

				// step2:文件列表记录，记录今天已处理过的文件，避免重复处理
				// modify by wuyg 2013-01-17,去掉对已处理文件的记录。
				// 因为文件处理完成后会转到bak目录下,而解析时是不处理bak目录下的文件的,因此不必要在这里记录。
				// 当需要解析的文件非常多的时候,此处频繁的打开和关闭文件是比较耗费时间的。

				//				FilterUtil.recordParsedSuccFile(file);

				// step3:备份原始文件
				File bakDir = new File(file.getParent() + "/bak/");
				File bakFile = new File(bakDir, file.getName());
				if (!bakFile.getParentFile().exists())
				{
					bakFile.getParentFile().mkdirs();
				}
				boolean bakOk = file.renameTo(bakFile);
				log.info("文件备份[" + (bakOk ? "成功" : "失败") + "]：源文件=" + file.getCanonicalPath() + ", 备份文件="
						+ bakFile.getCanonicalPath());

				// step4:删除过期的原始和备份文件,默认保存3天
				int srcfileDays = 3;
				try
				{
					srcfileDays = Integer.parseInt(ConfigReader.getProperties("parser.fileSavedDays"));
					log.debug("SystemConfig.xml配置原始文件保存时长(parser.fileSavedDays):" + srcfileDays + "天");
				} catch (Exception e)
				{
					log.debug("SystemConfig.xml未原始文件保存时长(parser.fileSavedDays)，使用系统默认时间:"
							+ Constant.PARSER_FILE_SAVED_DAYS + "天");
					srcfileDays = Constant.PARSER_FILE_SAVED_DAYS;
					log.error(e.getMessage(), e);
				}

				// step5:删除n天前的数据，为避免每次运行都进行文件检索耗费较多时间，限定只在每天的0,6,12,18点进行文件删除工作
				String day = TimeUtil.nowTime2str("dd");
				String hour = TimeUtil.nowTime2str("HH");
				if (!day.equals(deleteOldFileMap.get(bakDir.getCanonicalPath()))
						&& ("00".equals(hour) || "06".equals(hour) || "12".equals(hour) || "16".equals(hour) || "18"
								.equals(hour)))
				{
					FileUtil.deleteFilesNdaysAgo(file.getParentFile(), srcfileDays);
					FileUtil.deleteFilesNdaysAgo(bakDir, srcfileDays);

					// add by wuyg 2013-1-17 删除3天前的日志文件，放在这里不太合适，但是总比没有好。
					FileUtil.deleteFilesNdaysAgo(Constant.LOGS_DIR, 3);

					//					deleteOldFilesDay = day;
					deleteOldFileMap.put(bakDir.getCanonicalPath(), day);
				}

				long endTime = System.currentTimeMillis();
				// 日志格式：#文件解析入库#-[结束]-[源文件=xx,大小=xxByte,时间=yyyy-MM-dd HH:mm:ss]-[耗时=n毫秒] 
				log.info("#文件解析入库#-[结束]-" + fileInfo + "-[耗时=" + (endTime - startTime) + "毫秒]");
				
				// 记录解析成功文件数
				StatusCenter.oneFileParseSucc();
			}
		} catch (Exception e)
		{
			try
			{
				// 日志格式：#文件解析入库#-[出错]-[源文件=xx,大小=xxByte,时间=yyyy-MM-dd HH:mm:ss]-[耗时=n毫秒]-[错误信息=xx]
				log.error("#文件解析入库#-[出错]-" + fileInfo + "-[耗时=" + (System.currentTimeMillis() - startTime)
						+ "毫秒]-[错误信息=" + e.getMessage() + "]");
				
				// 记录解析失败文件数
				StatusCenter.oneFileParseError();
				
				log.error(e.getMessage(), e);

				if (!(e instanceof GetParameterException))
				{
					// step6:解析失败的文件放到bad子目录下，但如果是不需要移动文件的错误则不需要移动到bad目录下
					File badDir = new File(file.getParent() + "/bad/");
					File badFile = new File(badDir, file.getName());
					if (!badFile.getParentFile().exists())
					{
						badFile.getParentFile().mkdirs();
					}
					file.renameTo(badFile);
				}

				// 文件列表记录，记录今天处理失败的文件
				FilterUtil.recordParsedFailFile(file);
			} catch (Exception e1)
			{
			}
		}
	}
}
