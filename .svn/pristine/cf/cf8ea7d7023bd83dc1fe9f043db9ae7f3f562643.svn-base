package com.inspur.ftpparserframework.log.obj;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.inspur.ftpparserframework.util.StringUtil;
import com.inspur.ftpparserframework.util.TimeUtil;

public class ParserSqlldrLog extends BaseLog implements Comparable<ParserSqlldrLog>
{
	private Logger log = Logger.getLogger(getClass());
	/**
	 * 1、数据入库开始日志格式：
	 * 
	 * #数据入库#-[开始]-[源文件=xx,大小=xxByte,时间=yyyy-MM-dd
	 * HH:mm:ss]-[待入库文件=yy,大小=yyByte,时间=yyyy-MM-dd HH:mm:ss]
	 * 
	 * 
	 * 2、数据入库正常结束日志格式：
	 * 
	 * #数据入库#-[结束]-[源文件=xx,大小=xxByte,时间=yyyy-MM-dd
	 * HH:mm:ss]-[待入库文件=yy,大小=xxByte,时间=yyyy-MM-dd HH:mm:ss]-[成功条数=x]-[耗时=n毫秒]
	 * 
	 * 
	 * 3、数据入库出错日志格式：
	 * 
	 * #数据入库#-[出错]-[源文件=xx,大小=xxByte,时间=yyyy-MM-dd
	 * HH:mm:ss]-[待入库文件=yy,大小=xxByte,时间=yyyy-MM-dd
	 * HH:mm:ss]-[成功条数=x,失败条数=y]-[耗时=n毫秒]-[错误信息=xx]
	 **/
	public static final String flag = "#数据入库#";

	/**
	 * 待入库文件
	 */
	private BaseFile dataFile = new BaseFile();

	/**
	 * 入库表名
	 */
	private String tableName;

	/**
	 * 入库成功条数
	 */
	private long succRows;

	/**
	 * 入库失败条数
	 */
	private long errorRows;

	private final String FPF_PARSER_SQLLDR = "FPF_PARSER_SQLLDR";

	public String getTableName()
	{
		return tableName;
	}

	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}

	public BaseFile getDataFile()
	{
		return dataFile;
	}

	public void setDataFile(BaseFile dataFile)
	{
		this.dataFile = dataFile;
	}

	public long getSuccRows()
	{
		return succRows;
	}

	public void setSuccRows(long succRows)
	{
		this.succRows = succRows;
	}

	public long getErrorRows()
	{
		return errorRows;
	}

	public void setErrorRows(long errorRows)
	{
		this.errorRows = errorRows;
	}

	@Override
	protected String getLogRegex()
	{
		String logTimeRegex = "\\[(.{19})\\]";
		String stateRegex = "-\\[(\\S+)\\]";
		String srcFileRegex = "-\\[源文件=(\\S+),大小=(\\d+)Byte,时间=(.{19})\\]";
		String dataFileRegex = "-\\[待入库文件=(\\S+),大小=(\\d+)Byte,时间=(.{19})\\]";
		String tableNameRegex = "-\\[表名=([\\w_\\d]+)\\]";
		String rowsRegex = "-\\[成功条数=(\\d+)(,失败条数=(\\d+))*]";
		String usedTimeRegex = "-\\[耗时=(\\d+)毫秒\\]";
		String errorMessageRegex = "-\\[错误信息=(.*)\\]";

		return logTimeRegex + "\\S+" + flag + stateRegex + srcFileRegex + dataFileRegex + tableNameRegex + "("
				+ rowsRegex + ")*(" + usedTimeRegex + ")*(" + errorMessageRegex + ")*";
	}

	@Override
	protected void getInstance(Matcher matcher) throws Exception
	{
		String logTime = matcher.group(1);
		String handleState = matcher.group(2);
		String srcFilePath = matcher.group(3);
		String srcFileLength = matcher.group(4);
		String srcFileLastModifiedTime = matcher.group(5);
		String dataFilePath = matcher.group(6);
		String dataFileLength = matcher.group(7);
		String dataFileLastModifiedTime = matcher.group(8);
		String tableName = matcher.group(9);
		String succRows = matcher.group(11);
		String errorRows = matcher.group(13);
		String usedTimeMillis = matcher.group(15);
		String errorMessage = matcher.group(17);

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

		this.getSrcFile().setPath(srcFilePath);
		this.getSrcFile().setLength(Long.parseLong(srcFileLength));
		this.getSrcFile().setLastModifiedTime(TimeUtil.str2date(srcFileLastModifiedTime));

		this.dataFile.setPath(dataFilePath);
		this.dataFile.setLength(Long.parseLong(dataFileLength));
		this.dataFile.setLastModifiedTime(TimeUtil.str2date(dataFileLastModifiedTime));

		this.setTableName(tableName);
		this.succRows = StringUtil.isEmpty(succRows) ? 0 : Long.parseLong(succRows);
		this.errorRows = StringUtil.isEmpty(errorRows) ? 0 : Long.parseLong(errorRows);

		this.setUsedTimeMillis(StringUtil.isEmpty(usedTimeMillis) ? 0 : Long.parseLong(usedTimeMillis));
		this.setErrorMessage(errorMessage);
	}

	@Override
	protected String getThisCsvTitle()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(dataFile.getThisCsvTitle("待入库")).append(",").append("表名").append(",").append("成功条数").append(",")
				.append("失败条数");

		return sb.toString();
	}

	@Override
	protected String this2Csv()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(dataFile.this2Csv(false)).append(",").append(tableName).append(",").append(succRows).append(",")
				.append(errorRows);

		return sb.toString();
	}

	public static void main(String[] args)
	{
		String start = "[2012-11-28 14:17:00][com.inspur.ftpparserframework.util.OracleUtil.load(OracleUtil.java:85)][INFO]-#数据入库#-[开始]-[源文件=/home/oracle/wuyg/td-mr-xml/data/input/JN_ZTE_OMC/POTEVIO/TD-SCDMA_MRS_ZTE_POTEVIO_OMCR_990_2012090411000_1.xml.gz,大小=112274Byte,时间=2012-11-27 17:57:12]-[待入库文件=/home/oracle/wuyg/td-mr-xml/data/sqlldr/txt/TD-SCDMA_MRS_ZTE_POTEVIO_OMCR_990_2012090411000_1_TadvPccpchRscp.txt,大小=3958Byte,时间=2012-11-28 14:17:00]-[表名=TadvPccpchRscp]";
		String end = "[2012-11-28 14:17:09][com.inspur.ftpparserframework.util.OracleUtil.load(OracleUtil.java:205)][INFO]-#数据入库#-[结束]-[源文件=/home/oracle/wuyg/td-mr-xml/data/input/JN_ZTE_OMC/DATANG/TD-SCDMA_MRS_DATANG_OMCR_905_20121120150000.xml.gz,大小=81280Byte,时间=2012-11-27 16:29:25]-[待入库文件=/home/oracle/wuyg/td-mr-xml/data/sqlldr/txt/TD-SCDMA_MRS_DATANG_OMCR_905_20121120150000_UtranUppts.txt,大小=140626Byte,时间=2012-11-28 14:17:08]-[表名=TadvPccpchRscp]-[成功条数=678,失败条数=0]-[耗时=176毫秒]";
		String error = "[2012-11-28 14:17:00][com.inspur.ftpparserframework.util.OracleUtil.load(OracleUtil.java:210)][ERROR]-#数据入库#-[出错]-[源文件=/home/oracle/wuyg/td-mr-xml/data/input/JN_ZTE_OMC/POTEVIO/TD-SCDMA_MRS_ZTE_POTEVIO_OMCR_990_2012090411000_1.xml.gz,大小=112274Byte,时间=2012-11-27 17:57:12]-[待入库文件=/home/oracle/wuyg/td-mr-xml/data/sqlldr/txt/TD-SCDMA_MRS_ZTE_POTEVIO_OMCR_990_2012090411000_1_TadvPccpchRscp.txt,大小=3958Byte,时间=2012-11-28 14:17:00]-[表名=Td_Mr_TadvPccpchRscp01]-[成功条数=2,失败条数=1]-[耗时=91毫秒]-[错误信息=SQL*Loader-466: Column FILESN does not exist in table TD_MR_TADVPCCPCHRSCP.]";
		String regex = new ParserSqlldrLog().getLogRegex();
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
	public int compareTo(ParserSqlldrLog o)
	{
		if (this.getSrcFile().equals(o.getSrcFile()) && this.dataFile.equals(o.dataFile))
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
		return this.compareTo((ParserSqlldrLog) o) == 0;
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
		String sql = "select * from " + FPF_PARSER_SQLLDR + " where SRC_FILE_PATH = '" + getSrcFile().getPath()
				+ "' and DATA_FILE_PATH='" + getDataFile().getPath() + "'";
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
				+ FPF_PARSER_SQLLDR
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
				"SUCC_ROWS,"
				+ //
				"ERROR_ROWS,"
				+ //
				"TABLE_NAME,"
				+ //
				"DATA_FILE_PATH ,"
				+ //
				"DATA_FILE_LENGTH ,"
				+ //
				"DATA_FILE_LASTMODIFYTIME "
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
				"'" + getErrorMessage().replaceAll("'", "''") + "',"
				+ //;
				"'" + getSuccRows() + "',"
				+ //
				"'" + getErrorRows() + "',"
				+ //
				"'" + getTableName() + "',"
				+ //
				"'" + getDataFile().getPath() + "',"
				+ //
				"'" + getDataFile().getLength() + "',"
				+ //
				"to_date('" + TimeUtil.date2str(getDataFile().getLastModifiedTime(), "yyyyMMddHHmmss")
				+ "','yyyymmddhh24miss')" + //
				")";

		log.debug("日志入库:" + sql);
		conn.createStatement().execute(sql);
	}

	@Override
	protected void update2Db(Connection conn) throws Exception
	{
		String sql = "update " + FPF_PARSER_SQLLDR + " set "
				+ //
				"HANDLE_STATE='" + getHandleState() + "',"
				+ //
				"END_TIME=to_date('" + TimeUtil.date2str(getEndTime(), "yyyyMMddHHmmss")
				+ "','yyyymmddhh24miss'),"//
				+ "USEDTIME_MILLIS='" + getUsedTimeMillis() + "',"
				+ //
				"ERROR_MESSAGE='" + getErrorMessage().replaceAll("'", "''") + "',"
				+ //;
				"SUCC_ROWS='" + getSuccRows() + "',"
				+ //
				"ERROR_ROWS='" + getErrorRows() + "'"
				+ //;
				" where SRC_FILE_PATH='" + getSrcFile().getPath() + "' and DATA_FILE_PATH='" + getDataFile().getPath()
				+ "'";

		log.debug("日志更新:" + sql);
		conn.createStatement().executeUpdate(sql);

	}
	//====================================日志输出至数据库 END====================================
}
