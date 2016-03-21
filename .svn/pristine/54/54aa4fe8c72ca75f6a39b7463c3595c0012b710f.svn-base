package com.inspur.ftpparserframework.log.log4j;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import com.inspur.ftpparserframework.log.FtpLogAnalyzer;
import com.inspur.ftpparserframework.log.ParserLogAnalyzer;

/**
 * 基于log4j的日志入库实现
 * @author 武玉刚
 *
 */
public class DbAppender extends AppenderSkeleton
{
	@Override
	protected void append(LoggingEvent le)
	{
		try
		{
			String line = this.getLayout().format(le).replaceAll("\n|\r", "");
			ParserLogAnalyzer.parseOneLine(line);//文件解析日志
			FtpLogAnalyzer.parseOneLine(line);//FTP下载日志
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void close()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean requiresLayout()
	{
		// TODO Auto-generated method stub
		return false;
	}

}
