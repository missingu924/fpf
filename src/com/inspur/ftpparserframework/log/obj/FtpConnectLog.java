package com.inspur.ftpparserframework.log.obj;

import java.sql.Connection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.inspur.ftpparserframework.util.TimeUtil;

public class FtpConnectLog extends BaseLog
{
	/**
	 * 1、连接开始日志格式： #FTP服务器连接#-[开始]-[FTP服务器=xx]
	 * 
	 * 2、连接正常结束日志格式： #FTP服务器连接#-[结束]-[FTP服务器=xx]
	 * 
	 * 3、连接出错日志格式： #FTP服务器连接#-[出错]-[FTP服务器=xx]-[错误信息=ee]
	 */

	private Logger log = Logger.getLogger(getClass());
	private static final String TABLE_FPF_FTP_CONNECT = "FPF_FTP_CONNECT";

	/**
	 * FTP服务器名
	 */
	private String ftpServerName;

	public static final String flag = "#FTP服务器连接#";

	public String getFtpServerName()
	{
		return ftpServerName;
	}

	public void setFtpServerName(String ftpServerName)
	{
		this.ftpServerName = ftpServerName;
	}

	@Override
	protected String getLogRegex()
	{
		String logTimeRegex = "\\[(.{19})\\]";
		String stateRegex = "-\\[(\\S+)\\]";
		String srcFileRegex = "-\\[FTP服务器=([^\\]]*)\\]";
		String errorMessageRegex = "-\\[错误信息=(.*)\\]";

		return logTimeRegex + "\\S+" + flag + stateRegex + srcFileRegex + "(" + errorMessageRegex + ")*";
	}

	@Override
	protected void getInstance(Matcher matcher) throws Exception
	{
		Date logTime = TimeUtil.str2date(matcher.group(1));
		String handleState = matcher.group(2);
		String ftpServerName = matcher.group(3);
		String errorMessage = matcher.group(5);

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
		this.setErrorMessage(errorMessage);
	}

	@Override
	protected String getThisCsvTitle()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("FTP服务器");

		return sb.toString();
	}

	@Override
	protected String this2Csv()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(ftpServerName);

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
		// 任何情况下均返回false，即每次ftp连接都要入库保存
		return false;
	}

	@Override
	protected void save2Db(Connection conn) throws Exception
	{
		String sql = "insert into "
			+ TABLE_FPF_FTP_CONNECT
			+ //
			"("
			+ //
			"DOMAIN,"
			+ //
			"TECHNOLOGY,"
			+ //
			"VENDOR,"
			+ //
			"DATA_TYPE ,"
			+ //
			"BUSINESS_TYPE ,"
			+ //
			"NE_TYPE ,"
			+ //
			"TIME_TYPE ,"
			+ //
			"NE_NAME ,"
			+ //
			"REPORT_TIME ,"
			+ //
			"HANDLE_NAME ,"
			+ //
			"HANDLE_STATE,"
			+ //
			"FTP_SERVER_NAME ,"
			+ //
			"START_TIME,"
			+ //
			"END_TIME,"
			+ //
			"ERROR_MESSAGE"
			+ //
			") "
			+ //
			"values"
			+ //
			"("
			+ //
			"'" + getBasicInfo().getDomain()
			+ "',"
			+ //
			"'"
			+ getBasicInfo().getTechnology()
			+ "',"
			+ //
			"'"
			+ getBasicInfo().getVendor()
			+ "',"
			+ //
			"'"
			+ getBasicInfo().getDataType()
			+ "',"
			+ //
			"'"
			+ getBasicInfo().getBusinessType()
			+ "',"
			+ //
			"'"
			+ getBasicInfo().getNeType()
			+ "',"
			+ //
			"'"
			+ getBasicInfo().getTimeType()
			+ "',"
			+ //
			"'"
			+ getBasicInfo().getNeName()
			+ "',"
			+ //
			"to_date('"
			+ TimeUtil.date2str(getBasicInfo().getReportTime(), "yyyyMMddHHmmss")
			+ "','yyyymmddhh24miss'),"
			+ //
			"'" + getHandleName()
			+ "',"
			+ //
			"'" + getHandleState()
			+ "',"
			+ //
			"'" + getFtpServerName()
			+ "',"
			+ //
			"to_date('" + TimeUtil.date2str(getStartTime(), "yyyyMMddHHmmss") + "','yyyymmddhh24miss'),"
			+ //
			"to_date('" + TimeUtil.date2str(getEndTime(), "yyyyMMddHHmmss") + "','yyyymmddhh24miss'),"
			+ //
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
		FtpConnectLog other = (FtpConnectLog) obj;
		return this.ftpServerName.equalsIgnoreCase(other.ftpServerName);
	}

	public static void main(String[] args)
	{
		String start = "[2012-12-24 11:23:16][com.inspur.ftpparserframework.ftp.FtpUtil.connect(FtpUtil.java:510)][INFO]-#FTP服务器连接#-[开始]-[FTP服务器=JN_ZTE_OMC]";
		String end = "[2012-12-24 11:23:16][com.inspur.ftpparserframework.ftp.FtpUtil.connect(FtpUtil.java:534)][INFO]-#FTP服务器连接#-[结束]-[FTP服务器=JN_ZTE_OMC]";
		String error = "[2012-12-18 17:14:27][com.inspur.ftpparserframework.ftp.FtpUtil.connect(FtpUtil.java:534)][INFO]-#FTP服务器连接#-[出错]-[FTP服务器=JN_ZTE_OMC]-[错误信息=time out]";
		String regex = new FtpConnectLog().getLogRegex();
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
