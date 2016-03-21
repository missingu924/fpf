package com.inspur.ftpparserframework.log.obj;

import java.sql.Connection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.inspur.ftpparserframework.util.StringUtil;
import com.inspur.ftpparserframework.util.TimeUtil;

public class FtpNestedDownloadLog extends BaseLog
{
	/**
	 * 1、递归下载开始日志格式： #文件递归下载#-[开始]-[FTP服务器=xx,目录=yy,过滤条件=ff]
	 * 
	 * 2、递归下载正常结束日志格式：
	 * #文件递归下载#-[结束]-[FTP服务器=xx,目录=yy,过滤条件=ff]-[下载文件数=mm]-[耗时=nn毫秒]
	 * 
	 * 3、递归下载出错日志格式： #文件递归下载#-[出错]-[FTP服务器=xx,目录=yy,过滤条件=ff]-[耗时=nn毫秒]-[错误信息=ee]
	 */

	private Logger log = Logger.getLogger(getClass());
	private static final String TABLE_FPF_FTP_NESTDL = "FPF_FTP_NESTDL";

	/**
	 * FTP服务器名
	 */
	private String ftpServerName;

	/**
	 * FTP服务器目录
	 */
	private String ftpDir = "";

	/**
	 * 过滤条件
	 */
	private String ftpFilter = "";

	/**
	 * 下载文件数
	 */
	private long downloadFilesCount;

	public static final String flag = "#文件递归下载#";

	public String getFtpServerName()
	{
		return ftpServerName;
	}

	public void setFtpServerName(String ftpServerName)
	{
		this.ftpServerName = ftpServerName;
	}

	public String getFtpDir()
	{
		return ftpDir;
	}

	public void setFtpDir(String ftpDir)
	{
		this.ftpDir = ftpDir;
	}

	public String getFtpFilter()
	{
		return ftpFilter;
	}

	public void setFtpFilter(String ftpFilter)
	{
		this.ftpFilter = ftpFilter;
	}

	public long getDownloadFilesCount()
	{
		return downloadFilesCount;
	}

	public void setDownloadFilesCount(long downloadFilesCount)
	{
		this.downloadFilesCount = downloadFilesCount;
	}

	@Override
	protected String getLogRegex()
	{
		String logTimeRegex = "\\[(.{19})\\]";
		String stateRegex = "-\\[(\\S+)\\]";
		String ftpServerRegex = "-\\[FTP服务器=(\\S+),目录=(\\S+),过滤条件=([^\\]]*)\\]";
		String downloadFilesCountRegex = "-\\[下载文件数=(\\d+)\\]";
		String usedTimeRegex = "-\\[耗时=(\\d+)毫秒\\]";
		String errorMessageRegex = "-\\[错误信息=(.*)\\]";

		return logTimeRegex + "\\S+" + flag + stateRegex + ftpServerRegex + "(" + downloadFilesCountRegex + ")*("
				+ usedTimeRegex + ")*(" + errorMessageRegex + ")*";
	}

	@Override
	protected void getInstance(Matcher matcher) throws Exception
	{
		Date logTime = TimeUtil.str2date(matcher.group(1));
		String handleState = matcher.group(2);
		String ftpServerName = matcher.group(3);
		String ftpDir = matcher.group(4);
		String ftpFilter = matcher.group(5);
		long downloadFilesCount = 0;
		if (!StringUtil.isEmpty(matcher.group(7)))
		{
			downloadFilesCount = Long.parseLong(matcher.group(7));
		}
		long usedTimeMillis = 0;
		if (!StringUtil.isEmpty(matcher.group(9)))
		{
			usedTimeMillis = Long.parseLong(matcher.group(9));
		}
		String errorMessage = matcher.group(11);

		this.setLogTime(logTime);
		this.setHandleName(flag);
		this.setHandleState(handleState);
		if (this.STATE_START.equals(handleState))
		{
			this.setStartTime(logTime);
		} else if (this.STATE_END.equals(handleState) || this.STATE_ERROR.equals(handleState))
		{
			this.setEndTime(logTime);
		}
		this.setFtpServerName(ftpServerName);
		this.setFtpDir(ftpDir);
		this.setFtpFilter(ftpFilter);
		this.setDownloadFilesCount(downloadFilesCount);
		this.setUsedTimeMillis(usedTimeMillis);
		this.setErrorMessage(errorMessage);
	}

	@Override
	protected String getThisCsvTitle()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("FTP服务器").append(",").append("FTP目录").append(",").append("FTP过滤条件");

		return sb.toString();
	}

	@Override
	protected String this2Csv()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(ftpServerName).append(",").append(ftpDir).append(",").append(ftpFilter);

		return sb.toString();
	}

	@Override
	protected boolean isTableExists(Connection conn) throws Exception
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected void createTable(Connection conn) throws Exception
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean isExistsInDb(Connection conn) throws Exception
	{
		// 任何情况下均返回false，即每次递归下载都要入库保存
		return false;
	}

	@Override
	protected void save2Db(Connection conn) throws Exception
	{
		String sql = "insert into " + TABLE_FPF_FTP_NESTDL + //
				"(" + //
				"DOMAIN," + //
				"TECHNOLOGY," + //
				"VENDOR," + //
				"DATA_TYPE ," + //
				"BUSINESS_TYPE ," + //
				"NE_TYPE ," + //
				"TIME_TYPE ," + //
				"NE_NAME ," + //
				"HANDLE_NAME ," + //
				"HANDLE_STATE," + //
				"FTP_SERVER_NAME ," + //
				"FTP_DIR ," + //
				"FTP_FILTER ," + //
				"FTP_DL_FILES_COUNT ," + //
				"START_TIME," + //
				"END_TIME," + //
				"USEDTIME_MILLIS," + //
				"ERROR_MESSAGE" + //
				") " + //
				"values" + //
				"(" + //
				"'" + getBasicInfo().getDomain() + "'," + //
				"'" + getBasicInfo().getTechnology() + "'," + //
				"'" + getBasicInfo().getVendor() + "'," + //
				"'" + getBasicInfo().getDataType() + "'," + //
				"'" + getBasicInfo().getBusinessType() + "'," + //
				"'" + getBasicInfo().getNeType() + "'," + //
				"'" + getBasicInfo().getTimeType() + "'," + //
				"'" + getBasicInfo().getNeName() + "'," + //
				"'" + getHandleName() + "'," + //
				"'" + getHandleState() + "'," + //
				"'" + getFtpServerName() + "'," + //
				"'" + getFtpDir() + "'," + //
				"'" + getFtpFilter() + "'," + //
				"'" + getDownloadFilesCount() + "'," + //
				"to_date('" + TimeUtil.date2str(getStartTime(), "yyyyMMddHHmmss") + "','yyyymmddhh24miss')," + //
				"to_date('" + TimeUtil.date2str(getEndTime(), "yyyyMMddHHmmss") + "','yyyymmddhh24miss')," + //
				"'" + getUsedTimeMillis() + "'," + //
				"'" + getErrorMessage().replaceAll("'", "''") + "'" + //
				")";

		log.debug("日志入库:" + sql);
		conn.createStatement().execute(sql);
	}

	@Override
	protected void update2Db(Connection conn) throws Exception
	{
		// do nothing.

	}

	@Override
	public boolean equals(Object obj)
	{
		FtpNestedDownloadLog other = (FtpNestedDownloadLog) obj;
		return this.ftpServerName.equalsIgnoreCase(other.ftpServerName) && this.getFtpDir().equals(other.getFtpDir());
	}

	public static void main(String[] args)
	{
		String start = "[2012-12-18 17:14:27][com.inspur.ftpparserframework.ftp.FtpUtil.downloadNested(FtpUtil.java:61)][INFO]-#文件递归下载#-[开始]-[FTP服务器=JN_ZTE_OMC,目录=/home/oracle/wuyg/temp/data/input,过滤条件=*MRS*.gz]";
		String end = "[2012-12-18 17:14:27][com.inspur.ftpparserframework.ftp.FtpUtil.downloadNested(FtpUtil.java:80)][INFO]-#文件递归下载#-[结束]-[FTP服务器=JN_ZTE_OMC,目录=/home/oracle/wuyg/temp/data/input,过滤条件=]-[下载文件数=4]-[耗时=277毫秒]";
		String error = "[2012-12-18 17:14:27][com.inspur.ftpparserframework.ftp.FtpUtil.downloadNested(FtpUtil.java:80)][INFO]-#文件递归下载#-[结束]-[FTP服务器=JN_ZTE_OMC,目录=/home/oracle/wuyg/temp/data/input,过滤条件=*MRS*.gz]-[耗时=277毫秒]-[错误信息=no files]";
		String regex = new FtpNestedDownloadLog().getLogRegex();
		System.out.println(regex);
		Pattern p = Pattern.compile(regex);

		System.out.println("START");
		Matcher m = p.matcher(start);
		if (m.matches())
		{
			for (int i = 0; i <= m.groupCount(); i++)
			{
				System.out.println(i + " : " + m.group(i));
			}
		}

		System.out.println("END");
		m = p.matcher(end);
		if (m.matches())
		{
			for (int i = 0; i <= m.groupCount(); i++)
			{
				System.out.println(i + " : " + m.group(i));
			}
		}

		System.out.println("ERROR");
		m = p.matcher(error);
		if (m.matches())
		{
			for (int i = 0; i <= m.groupCount(); i++)
			{
				System.out.println(i + " : " + m.group(i));
			}
		}
	}
}
