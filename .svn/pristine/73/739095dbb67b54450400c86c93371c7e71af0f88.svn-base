package com.inspur.ftpparserframework.log.obj;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.inspur.ftpparserframework.util.StringUtil;
import com.inspur.ftpparserframework.util.TimeUtil;

public class ParserMiddleLog extends BaseLog implements Comparable<ParserMiddleLog>
{
	private Logger log = Logger.getLogger(getClass());
	/**
	 * 1、文件转换开始日志格式：
	 * 
	 * #文件转换#-[环节=xx]-[开始]-[源文件=xx,大小=xxByte,时间=yyyy-MM-dd
	 * HH:mm:ss][待处理文件=yy,大小=yyByte,时间=yyyy-MM-dd HH:mm:ss]
	 * 
	 * 
	 * 2、文件转换正常结束日志格式：
	 * 
	 * #文件转换#-[环节=xx]-[结束]-[源文件=xx,大小=xxByte,时间=yyyy-MM-ddHH:mm:ss
	 * ]-[待处理文件=yy,大小=xxByte,时间=yyyy-MM-dd
	 * HH:mm:ss]-[处理后文件=zz,大小=zzByte,时间=yyyy-MM-dd HH:mm:ss]-[耗时=n毫秒]
	 * 
	 * 
	 * 3、文件转换出错日志格式：
	 * 
	 * #文件转换#-[环节=xx]-[出错]-[源文件=xx,大小=xxByte,时间=yyyy-MM-dd
	 * HH:mm:ss]-[待处理文件=yy,大小=xxByte,时间=yyyy-MM-dd HH:mm:ss]-[错误信息=xx]
	 **/

	public static final String flag = "#文件转换#";

	/**
	 * 待处理文件
	 */
	private BaseFile needHandleFile = new BaseFile();

	/**
	 * 处理后文件
	 */
	private BaseFile handledFile = new BaseFile();

	private final String FPF_PARSER_MIDDLE = "FPF_PARSER_MIDDLE";

	public BaseFile getNeedHandleFile()
	{
		return needHandleFile;
	}

	public void setNeedHandleFile(BaseFile needHandleFile)
	{
		this.needHandleFile = needHandleFile;
	}

	public BaseFile getHandledFile()
	{
		return handledFile;
	}

	public void setHandledFile(BaseFile handledFile)
	{
		this.handledFile = handledFile;
	}

	@Override
	protected String getLogRegex()
	{
		String logTimeRegex = "\\[(.{19})\\]";
		String handleNameRegex = "-\\[环节=(\\S+)\\]";
		String stateRegex = "-\\[(\\S+)\\]";
		String srcFileRegex = "-\\[源文件=(\\S+),大小=(\\d+)Byte,时间=(.{19})\\]";
		String needHandleFileRegex = "-\\[待处理文件=(\\S+),大小=(\\d+)Byte,时间=(.{19})\\]";
		String handledFileRegex = "-\\[处理后文件=(\\S+),大小=(\\d+)Byte,时间=(.{19})\\]";
		String usedTimeRegex = "-\\[耗时=(\\d+)毫秒\\]";
		String errorMessageRegex = "-\\[错误信息=(.*)\\]";

		return logTimeRegex + "\\S+" + flag + handleNameRegex + stateRegex + srcFileRegex + needHandleFileRegex + "("
				+ handledFileRegex + ")*(" + usedTimeRegex + ")*(" + errorMessageRegex + ")*";
	}

	@Override
	protected void getInstance(Matcher matcher) throws Exception
	{
		String logTime = matcher.group(1);
		String handleName = matcher.group(2);
		String handleState = matcher.group(3);
		String srcFilePath = matcher.group(4);
		String srcFileLength = matcher.group(5);
		String srcFileLastModifiedTime = matcher.group(6);
		String needHandleFilePath = matcher.group(7);
		String needHandleFileLength = matcher.group(8);
		String needHandleFileLastModifiedTime = matcher.group(9);
		String handledFilePath = matcher.group(11);
		String handledFileLength = matcher.group(12);
		String handledFileLastModifiedTime = matcher.group(13);
		String usedTimeMillis = matcher.group(15);
		String errorMessage = matcher.group(17);

		this.setLogTime(TimeUtil.str2date(logTime));
		this.setHandleName(handleName);
		this.setHandleState(handleState);
		if (this.STATE_START.equals(handleState))
		{
			this.setStartTime(TimeUtil.str2date(logTime));
		} else if (this.STATE_END.equals(handleState) || this.STATE_ERROR.equals(handleState))
		{
			this.setEndTime(TimeUtil.str2date(logTime));
		}

		this.getSrcFile().setPath(srcFilePath);
		this.getSrcFile().setLength(Long.parseLong(srcFileLength));
		this.getSrcFile().setLastModifiedTime(TimeUtil.str2date(srcFileLastModifiedTime));

		this.needHandleFile.setPath(needHandleFilePath);
		this.needHandleFile.setLength(Long.parseLong(needHandleFileLength));
		this.needHandleFile.setLastModifiedTime(TimeUtil.str2date(needHandleFileLastModifiedTime));

		this.handledFile.setPath(StringUtil.isEmpty(handledFilePath) ? "" : handledFilePath);
		this.handledFile.setLength(StringUtil.isEmpty(handledFileLength) ? 0 : Long.parseLong(handledFileLength));
		this.handledFile.setLastModifiedTime(StringUtil.isEmpty(handledFileLastModifiedTime) ? null : TimeUtil
				.str2date(handledFileLastModifiedTime));

		this.setUsedTimeMillis(StringUtil.isEmpty(usedTimeMillis) ? 0 : Long.parseLong(usedTimeMillis));
		this.setErrorMessage(errorMessage);
	}

	@Override
	protected String getThisCsvTitle()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(needHandleFile.getThisCsvTitle("待处理")).append(",").append(handledFile.getThisCsvTitle("处理后"));

		return sb.toString();
	}

	@Override
	protected String this2Csv()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(needHandleFile.this2Csv(false)).append(",").append(handledFile.this2Csv(false));

		return sb.toString();
	}

	public static void main(String[] args)
	{
		String start = "[2012-11-28 14:16:50][com.inspur.ftpparserframework.util.ZipUtil.gunzip(ZipUtil.java:68)][INFO]-#文件转换#-[环节=文件解压]-[开始]-[源文件=/home/oracle/wuyg/td-mr-xml/data/input/JN_ZTE_OMC/POTEVIO/TD-SCDMA_MRS_ZTE_POTEVIO_OMCR_990_20120904110000.xml.gz,大小=112273Byte,时间=2012-11-27 17:57:12]-[待处理文件=/home/oracle/wuyg/td-mr-xml/data/input/JN_ZTE_OMC/POTEVIO/TD-SCDMA_MRS_ZTE_POTEVIO_OMCR_990_20120904110000.xml.gz,大小=112273Byte,时间=2012-11-27 17:57:12]";
		String end = "[2012-11-28 14:16:50][com.inspur.ftpparserframework.util.ZipUtil.gunzip(ZipUtil.java:73)][INFO]-#文件转换#-[环节=文件解压]-[结束]-[源文件=/home/oracle/wuyg/td-mr-xml/data/input/JN_ZTE_OMC/POTEVIO/TD-SCDMA_MRS_ZTE_POTEVIO_OMCR_990_20120904110000.xml.gz,大小=112273Byte,时间=2012-11-27 17:57:12]-[待处理文件=/home/oracle/wuyg/td-mr-xml/data/input/JN_ZTE_OMC/POTEVIO/TD-SCDMA_MRS_ZTE_POTEVIO_OMCR_990_20120904110000.xml.gz,大小=112273Byte,时间=2012-11-27 17:57:12]-[处理后文件=/home/oracle/wuyg/td-mr-xml/data/input/JN_ZTE_OMC/POTEVIO/TD-SCDMA_MRS_ZTE_POTEVIO_OMCR_990_20120904110000.xml,大小=761095Byte,时间=2012-11-28 14:16:50]-[耗时=17毫秒]";
		String error = "[2012-11-28 14:16:50][com.inspur.ftpparserframework.util.ZipUtil.gunzip(ZipUtil.java:73)][INFO]-#文件转换#-[环节=文件解压]-[出错]-[源文件=/home/oracle/wuyg/td-mr-xml/data/input/JN_ZTE_OMC/POTEVIO/TD-SCDMA_MRS_ZTE_POTEVIO_OMCR_990_20120904110000.xml.gz,大小=112273Byte,时间=2012-11-27 17:57:12]-[待处理文件=/home/oracle/wuyg/td-mr-xml/data/input/JN_ZTE_OMC/POTEVIO/TD-SCDMA_MRS_ZTE_POTEVIO_OMCR_990_20120904110000.xml.gz,大小=112273Byte,时间=2012-11-27 17:57:12]-[处理后文件=/home/oracle/wuyg/td-mr-xml/data/input/JN_ZTE_OMC/POTEVIO/TD-SCDMA_MRS_ZTE_POTEVIO_OMCR_990_20120904110000.xml,大小=761095Byte,时间=2012-11-28 14:16:50]-[耗时=17毫秒]-[错误信息=Null pointer]";
		String regex = new ParserMiddleLog().getLogRegex();
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

//	@Override
	public int compareTo(ParserMiddleLog o)
	{
		if (this.getSrcFile().equals(o.getSrcFile()) && this.getHandleName().equals(o.getHandleName()))
		{
			return 0;
		} else
		{
			return -1;
		}
	}

	@Override
	public boolean equals(Object o)
	{
		return this.compareTo((ParserMiddleLog) o) == 0;
	}

	//====================================日志输出至数据库 START====================================
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
		String sql = "select * from " + FPF_PARSER_MIDDLE + " where SRC_FILE_PATH = '" + getSrcFile().getPath()
				+ "' and HANDLE_NAME='" + getHandleName() + "'";
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
				+ FPF_PARSER_MIDDLE
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
				"NEEDHANDLE_FILE_PATH ,"
				+ //
				"NEEDHANDLE_FILE_LENGTH ,"
				+ //
				"NEEDHANDLE_FILE_LASTMODIFYTIME ,"
				+ //
				"HANDLED_FILE_PATH ,"
				+ //
				"HANDLED_FILE_LENGTH ,"
				+ //
				"HANDLED_FILE_LASTMODIFYTIME "
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
				"'"
				+ getHandleName()
				+ "',"
				+ //
				"'"
				+ getHandleState()
				+ "',"
				+ //
				"'"
				+ getSrcFile().getPath()
				+ "',"
				+ //
				"'"
				+ getSrcFile().getLength()
				+ "',"
				+ //
				"to_date('"
				+ TimeUtil.date2str(getSrcFile().getLastModifiedTime(), "yyyyMMddHHmmss")
				+ "','yyyymmddhh24miss'),"
				+ //
				"to_date('" + TimeUtil.date2str(getStartTime(), "yyyyMMddHHmmss")
				+ "','yyyymmddhh24miss'),"
				+ //
				"to_date('" + TimeUtil.date2str(getEndTime(), "yyyyMMddHHmmss")
				+ "','yyyymmddhh24miss'),"
				+ //
				"'" + getUsedTimeMillis()
				+ "',"
				+ //
				"'" + getErrorMessage().replaceAll("'", "''")
				+ "',"
				+ //;
				"'" + getNeedHandleFile().getPath()
				+ "',"
				+ //
				"'" + getNeedHandleFile().getLength()
				+ "',"
				+ //
				"to_date('" + TimeUtil.date2str(getNeedHandleFile().getLastModifiedTime(), "yyyyMMddHHmmss")
				+ "','yyyymmddhh24miss'),"
				+ //
				"'" + getHandledFile().getPath() + "',"
				+ //
				"'" + getHandledFile().getLength() + "',"
				+ //
				"to_date('" + TimeUtil.date2str(getHandledFile().getLastModifiedTime(), "yyyyMMddHHmmss")
				+ "','yyyymmddhh24miss')" + //
				")";

		log.debug("日志入库:" + sql);
		conn.createStatement().execute(sql);
	}

	@Override
	protected void update2Db(Connection conn) throws Exception
	{
		String sql = "update "
				+ FPF_PARSER_MIDDLE
				+ " set "
				+ //
				"HANDLE_STATE='"
				+ getHandleState()
				+ "',"
				+ //
				"END_TIME=to_date('"
				+ TimeUtil.date2str(getEndTime(), "yyyyMMddHHmmss")
				+ "','yyyymmddhh24miss'),"//
				+ "USEDTIME_MILLIS='"
				+ getUsedTimeMillis()
				+ "',"
				+ //
				"ERROR_MESSAGE='"
				+ getErrorMessage()
				+ "',"
				+ //;
				"HANDLED_FILE_PATH='"
				+ getHandledFile().getPath()
				+ "',"
				+ //
				"HANDLED_FILE_LENGTH='"
				+ getHandledFile().getLength()
				+ "',"
				+ //;
				"HANDLED_FILE_LASTMODIFYTIME=to_date('"
				+ TimeUtil.date2str(getHandledFile().getLastModifiedTime(), "yyyyMMddHHmmss")
				+ "','yyyymmddhh24miss')"
				+ //
				" where SRC_FILE_PATH='" + getSrcFile().getPath() + "' and HANDLE_NAME='"
				+ getHandleName() + "'";

		log.debug("日志更新:" + sql);
		conn.createStatement().executeUpdate(sql);

	}
	//====================================日志输出至数据库 END====================================
}
