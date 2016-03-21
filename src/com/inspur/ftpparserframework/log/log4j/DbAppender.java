package com.inspur.ftpparserframework.log.log4j;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import com.inspur.ftpparserframework.log.FtpLogAnalyzer;
import com.inspur.ftpparserframework.log.ParserLogAnalyzer;

/**
 * ����log4j����־���ʵ��
 * @author �����
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
			ParserLogAnalyzer.parseOneLine(line);//�ļ�������־
			FtpLogAnalyzer.parseOneLine(line);//FTP������־
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
