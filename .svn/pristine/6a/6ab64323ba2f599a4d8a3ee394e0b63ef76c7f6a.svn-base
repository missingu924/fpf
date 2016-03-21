package com.inspur.ftpparserframework.log.obj;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.inspur.ftpparserframework.util.StringUtil;
import com.inspur.ftpparserframework.util.TimeUtil;

public class FtpOneFileDownloadLog extends BaseLog
{
	/**
	 * 1、下载开始日志格式： #单文件下载#-[开始]-[FTP服务器=FTPSVR]-[源文件=xx,大小=xxByte,时间=yyyy-MM-dd
	 * HH:mm:ss]
	 * 
	 * 2、下载正常结束日志格式：
	 * #单文件下载#-[结束]-[FTP服务器=FTPSVR]-[源文件=xx,大小=xxByte,时间=yyyy-MM-dd
	 * HH:mm:ss]-[目的文件=yy,大小=xxByte,时间=yyyy-MM-dd HH:mm:ss]-[耗时=n毫秒]
	 * 
	 * 3、下载出错日志格式： #单文件下载#-[出错]-[FTP服务器=FTPSVR]-[源文件=xx,大小=xxByte,时间=yyyy-MM-dd
	 * HH:mm:ss]-[耗时=n毫秒]-[错误信息=xx]
	 */

	private Logger log = Logger.getLogger(getClass());
	private static final String TABLE_FPF_FTP_ONEFILEDL = "FPF_FTP_ONEFILEDL";

	/**
	 * FTP服务器名
	 */
	private String ftpServerName;

	/**
	 * 目的文件
	 */
	private BaseFile destFile = new BaseFile();

	public final static String flag = "#单文件下载#";

	public String getFtpServerName()
	{
		return ftpServerName;
	}

	public void setFtpServerName(String ftpServerName)
	{
		this.ftpServerName = ftpServerName;
	}

	public BaseFile getDestFile()
	{
		return destFile;
	}

	public void setDestFile(BaseFile destFile)
	{
		this.destFile = destFile;
	}

	@Override
	protected String getLogRegex()
	{
		String logTimeRegex = "\\[(.{19})\\]";
		String stateRegex = "-\\[(\\S+)\\]";
		String ftpServerRegex = "-\\[FTP服务器=(\\S+)]";
		String srcFileRegex = "-\\[源文件=(\\S+),大小=(\\d+)Byte,时间=(.{19})\\]";
		String destFileRegex = "-\\[目的文件=(\\S+),大小=(\\d+)Byte,时间=(.{19})\\]";
		String usedTimeRegex = "-\\[耗时=(\\d+)毫秒\\]";
		String errorMessageRegex = "-\\[错误信息=(.*)\\]";

		return logTimeRegex + "\\S+" + flag + stateRegex + ftpServerRegex + srcFileRegex + "(" + destFileRegex + ")*("
				+ usedTimeRegex + ")*(" + errorMessageRegex + ")*";
	}

	@Override
	protected void getInstance(Matcher matcher) throws Exception
	{
		String logTime = matcher.group(1);
		String handleState = matcher.group(2);
		String ftpServerName = matcher.group(3);
		String srcFilePath = matcher.group(4);
		String srcFileLength = matcher.group(5);
		String srcFileLastModifiedTime = matcher.group(6);
		String destFilePath = matcher.group(8);
		String destFileLength = matcher.group(9);
		String destFileLastModifiedTime = matcher.group(10);
		String usedTimeMillis = matcher.group(12);
		String errorMessage = matcher.group(14);

		this.setLogTime(TimeUtil.str2date(logTime));
		this.setHandleName(flag);
		this.setHandleState(handleState);
		if (this.STATE_START.equals(handleState))
		{
			this.setStartTime(TimeUtil.str2date(logTime));
		} else if (this.STATE_END.equals(handleState) || this.STATE_ERROR.equals(handleState))
		{
			this.setEndTime(TimeUtil.str2date(logTime));
		}

		this.setFtpServerName(ftpServerName);

		this.getSrcFile().setPath(srcFilePath);
		this.getSrcFile().setLength(Long.parseLong(srcFileLength));
		this.getSrcFile().setLastModifiedTime(TimeUtil.str2date(srcFileLastModifiedTime));

		this.destFile.setPath(StringUtil.isEmpty(destFilePath) ? "" : destFilePath);
		this.destFile.setLength(StringUtil.isEmpty(destFileLength) ? 0 : Long.parseLong(destFileLength));
		this.destFile.setLastModifiedTime(StringUtil.isEmpty(destFileLastModifiedTime) ? null : TimeUtil
				.str2date(destFileLastModifiedTime));

		this.setUsedTimeMillis(StringUtil.isEmpty(usedTimeMillis) ? 0 : Long.parseLong(usedTimeMillis));
		this.setErrorMessage(errorMessage);
	}

	@Override
	protected String getThisCsvTitle()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("FTP服务器").append(",")//
				.append(destFile.getThisCsvTitle("目的文件"));

		return sb.toString();
	}

	@Override
	protected String this2Csv()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(ftpServerName).append(",")//
				.append(destFile.this2Csv(true));

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
		String sql = "select * from " + TABLE_FPF_FTP_ONEFILEDL + " where src_file_path = '"
				+ super.getSrcFile().getPath() + "'";
		ResultSet rst = conn.createStatement().executeQuery(sql);
		if (rst.next())
		{
			rst.close();

			return true;
		}
		return false;
	}

	@Override
	protected void save2Db(Connection conn) throws Exception
	{
		String sql = "insert into "
				+ TABLE_FPF_FTP_ONEFILEDL
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
				"DATA_START_TIME ,"
				+ //
				"DATA_END_TIME ,"
				+ //
				"HANDLE_NAME ,"
				+ //
				"HANDLE_STATE,"
				+ //
				"FTP_SERVER_NAME ,"
				+ //
				"SRC_FILE_PATH ,"
				+ //
				"SRC_FILE_LENGTH ,"
				+ //
				"SRC_FILE_LASTMODIFYTIME ,"
				+ //
				"START_TIME,"
				+ //
				"END_TIME,"
				+ //
				"USEDTIME_MILLIS,"
				+ //
				"ERROR_MESSAGE,"
				+ //
				"DEST_FILE_PATH ,"
				+ //
				"DEST_FILE_LENGTH ,"
				+ //
				"DEST_FILE_LASTMODIFYTIME "
				+ //
				") "
				+ //
				"values"
				+ //
				"("
				+ //
				"'"
				+ getBasicInfo().getDomain()
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
				(getBasicInfo().getReportTime() == null ? "''," : "to_date('"
						+ TimeUtil.date2str(getBasicInfo().getReportTime(), "yyyyMMddHHmmss")
						+ "','yyyymmddhh24miss'),")
				+ //
				(getBasicInfo().getDataStartTime() == null ? "''," : "to_date('"
						+ TimeUtil.date2str(getBasicInfo().getDataStartTime(), "yyyyMMddHHmmss")
						+ "','yyyymmddhh24miss'),")
				+ //
				(getBasicInfo().getDataEndTime() == null ? "''," : "to_date('"
						+ TimeUtil.date2str(getBasicInfo().getDataEndTime(), "yyyyMMddHHmmss")
						+ "','yyyymmddhh24miss'),")
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
				"'" + getSrcFile().getPath()
				+ "',"
				+ //
				"'" + getSrcFile().getLength()
				+ "',"
				+ //
				"to_date('" + TimeUtil.date2str(getSrcFile().getLastModifiedTime(), "yyyyMMddHHmmss")
				+ "','yyyymmddhh24miss'),"
				+ //
				"to_date('" + TimeUtil.date2str(getStartTime(), "yyyyMMddHHmmss") + "','yyyymmddhh24miss'),"
				+ //
				"to_date('" + TimeUtil.date2str(getEndTime(), "yyyyMMddHHmmss") + "','yyyymmddhh24miss'),"
				+ //
				"'" + getUsedTimeMillis() + "',"
				+ //
				"'" + getErrorMessage() + "',"
				+ //
				"'" + getDestFile().getPath() + "',"
				+ //
				"'" + getDestFile().getLength() + "',"
				+ //
				"to_date('" + TimeUtil.date2str(getDestFile().getLastModifiedTime(), "yyyyMMddHHmmss")
				+ "','yyyymmddhh24miss')" + //
				")";

		log.debug("日志入库:" + sql);
		conn.createStatement().execute(sql);
	}

	@Override
	protected void update2Db(Connection conn) throws Exception
	{
		String sql = "update " + TABLE_FPF_FTP_ONEFILEDL
				+ " set "
				+ //
				"HANDLE_STATE='" + getHandleState()
				+ "',"
				+ //
				"START_TIME=to_date('" + TimeUtil.date2str(getStartTime(), "yyyyMMddHHmmss")
				+ "','yyyymmddhh24miss'),"
				+ //
				"END_TIME=to_date('" + TimeUtil.date2str(getEndTime(), "yyyyMMddHHmmss") + "','yyyymmddhh24miss'),"
				+ //
				"DEST_FILE_PATH='" + destFile.getPath() + "',"
				+ //
				"DEST_FILE_LENGTH='" + destFile.getLength() + "',"
				+ //
				"DEST_FILE_LASTMODIFYTIME=to_date('"
				+ TimeUtil.date2str(destFile.getLastModifiedTime(), "yyyyMMddHHmmss") + "','yyyymmddhh24miss')," + //
				"USEDTIME_MILLIS='" + getUsedTimeMillis() + "'," + //
				"ERROR_MESSAGE='" + getErrorMessage() + "'" + //;
				" where SRC_FILE_PATH='" + getSrcFile().getPath() + "'";

		log.debug("日志更新:" + sql);
		conn.createStatement().executeUpdate(sql);
	}

	@Override
	public boolean equals(Object obj)
	{
		FtpOneFileDownloadLog other = (FtpOneFileDownloadLog) obj;
		return this.ftpServerName.equalsIgnoreCase(other.ftpServerName)
				&& this.getSrcFile().getPath().equals(other.getSrcFile().getPath());
	}

	public static void main(String[] args)
	{
		String start = "[2012-12-18 17:14:27][com.inspur.ftpparserframework.ftp.FtpUtil.downloadOnce(FtpUtil.java:291)][INFO]-#单文件下载#-[开始]-[FTP服务器=JN_ZTE_OMC]-[源文件=/home/oracle/wuyg/temp/data/input/POTEVIO/TD-SCDMA_MRS_ZTE_POTEVIO_OMCR_990_20120904110000.xml.gz,大小=112273Byte,时间=2012-11-26 06:15:00]";
		String end = "[2012-12-18 17:14:27][com.inspur.ftpparserframework.ftp.FtpUtil.downloadOnce(FtpUtil.java:304)][INFO]-#单文件下载#-[结束]-[FTP服务器=JN_ZTE_OMC]-[源文件=/home/oracle/wuyg/temp/data/input/POTEVIO/TD-SCDMA_MRS_ZTE_POTEVIO_OMCR_990_20120904110000.xml.gz,大小=112273Byte,时间=2012-11-26 06:15:00]-[目的文件=/home/oracle/wuyg/td-mr-xml/data/input/JN_ZTE_OMC/POTEVIO/TD-SCDMA_MRS_ZTE_POTEVIO_OMCR_990_20120904110000.xml.gz,大小=112273Byte,时间=2012-12-18 17:14:27]-[耗时=6毫秒]";
		String error = "[2012-12-18 17:14:27][com.inspur.ftpparserframework.ftp.FtpUtil.downloadOnce(FtpUtil.java:291)][INFO]-#单文件下载#-[开始]-[FTP服务器=JN_ZTE_OMC]-[源文件=/home/oracle/wuyg/temp/data/input/POTEVIO/TD-SCDMA_MRS_ZTE_POTEVIO_OMCR_990_20120904110000.xml.gz,大小=112273Byte,时间=2012-11-26 06:15:00]-[错误信息=nothing]";
		String regex = new FtpOneFileDownloadLog().getLogRegex();
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
