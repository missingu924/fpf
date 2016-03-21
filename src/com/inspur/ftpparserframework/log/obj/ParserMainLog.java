package com.inspur.ftpparserframework.log.obj;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.inspur.ftpparserframework.util.StringUtil;
import com.inspur.ftpparserframework.util.TimeUtil;

public class ParserMainLog extends BaseLog implements Comparable<ParserMainLog>
{
	private Logger log = Logger.getLogger(getClass());

	/**
	 * 1���ļ�ת����ʼ��־��ʽ��
	 * 
	 * #�ļ��������#-[��ʼ]-[Դ�ļ�=xx,��С=xxByte,ʱ��=yyyy-MM-dd HH:mm:ss]
	 * 
	 * 2���ļ�ת������������־��ʽ��
	 * 
	 * #�ļ��������#-[����]-[Դ�ļ�=xx,��С=xxByte,ʱ��=yyyy-MM-dd HH:mm:ss]-[��ʱ=n����]
	 * 
	 * 3���ļ�ת��������־��ʽ��
	 * 
	 * #�ļ��������#-[����]-[Դ�ļ�=xx,��С=xxByte,ʱ��=yyyy-MM-dd
	 * HH:mm:ss]-[��ʱ=n����]-[������Ϣ=xx]
	 **/

	public static final String flag = "#�ļ��������#";

	/**
	 * ���ɹ�����
	 */
	private long succRows;

	/**
	 * ���ʧ������
	 */
	private long errorRows;

	private final String FPF_PARSER_MAIN = "FPF_PARSER_MAIN";

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

	//====================================��־���� START====================================
	@Override
	protected String getLogRegex()
	{
		String logTimeRegex = "\\[(.{19})\\]";
		String stateRegex = "-\\[(\\S+)\\]";
		String srcFileRegex = "-\\[Դ�ļ�=(\\S+),��С=(\\d+)Byte,ʱ��=(.{19})\\]";
		String usedTimeRegex = "-\\[��ʱ=(\\d+)����\\]";
		String errorMessageRegex = "-\\[������Ϣ=(.*)\\]";

		return logTimeRegex + "\\S+" + flag + stateRegex + srcFileRegex + "(" + usedTimeRegex + ")*("
				+ errorMessageRegex + ")*";
	}

	@Override
	protected void getInstance(Matcher matcher) throws Exception
	{
		Date logTime = TimeUtil.str2date(matcher.group(1));
		String handleState = matcher.group(2);
		String srcFilePath = matcher.group(3);
		long srcFileLength = Long.parseLong(matcher.group(4));
		Date srcFileLastModifiedTime = TimeUtil.str2date(matcher.group(5));
		long usedTimeMillis = 0;
		if (!StringUtil.isEmpty(matcher.group(7)))
		{
			usedTimeMillis = Long.parseLong(matcher.group(7));
		}
		String errorMessage = matcher.group(9);

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
		this.getSrcFile().setPath(srcFilePath);
		this.getSrcFile().setLength(srcFileLength);
		this.getSrcFile().setLastModifiedTime(srcFileLastModifiedTime);
		this.setUsedTimeMillis(usedTimeMillis);
		this.setErrorMessage(errorMessage);
	}

	//====================================��־���� END====================================

	//====================================��־�����CSV�ļ� START====================================
	@Override
	protected String getThisCsvTitle()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("�ɹ�����").append(",").append("ʧ������");

		return sb.toString();
	}

	@Override
	protected String this2Csv()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(succRows).append(",").append(errorRows);

		return sb.toString();
	}

	//====================================��־�����CSV�ļ� END====================================
//	@Override
	public int compareTo(ParserMainLog o)
	{
		if (this.getSrcFile().equals(o.getSrcFile()))
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
		return this.compareTo((ParserMainLog) o) == 0;
	}

	//====================================��־��������ݿ� START====================================
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
		String sql = "select * from " + FPF_PARSER_MAIN + " where src_file_path = '" + super.getSrcFile().getPath()
				+ "'";
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
				+ FPF_PARSER_MAIN
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
				"ERROR_ROWS"
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
				"'" + getHandleName() + "',"
				+ //
				"'" + getHandleState() + "',"
				+ //
				"'" + getSrcFile().getPath() + "',"
				+ //
				"'" + getSrcFile().getLength() + "',"
				+ //
				"to_date('" + TimeUtil.date2str(getSrcFile().getLastModifiedTime(), "yyyyMMddHHmmss")
				+ "','yyyymmddhh24miss')," + //
				"to_date('" + TimeUtil.date2str(getStartTime(), "yyyyMMddHHmmss") + "','yyyymmddhh24miss')," + //
				"to_date('" + TimeUtil.date2str(getEndTime(), "yyyyMMddHHmmss") + "','yyyymmddhh24miss')," + //
				"'" + getUsedTimeMillis() + "'," + //
				"'" + getErrorMessage().replaceAll("'", "''") + "'," + //;
				"'" + getSuccRows() + "'," + //
				"'" + getErrorRows() + "'" + //
				")";

		log.debug("��־���:" + sql);
		conn.createStatement().execute(sql);
	}

	@Override
	protected void update2Db(Connection conn) throws Exception
	{
		String sql = "update " + FPF_PARSER_MAIN + " set "
				+ //
				"HANDLE_STATE='" + getHandleState() + "',"
				+ //
				"END_TIME=to_date('" + TimeUtil.date2str(getEndTime(), "yyyyMMddHHmmss") + "','yyyymmddhh24miss'),"
				+ "USEDTIME_MILLIS='" + getUsedTimeMillis() + "'," + //
				"ERROR_MESSAGE='" + getErrorMessage() + "'," + //;
				"SUCC_ROWS='" + getSuccRows() + "'," + //
				"ERROR_ROWS='" + getErrorRows() + "'" + //;
				" where SRC_FILE_PATH='" + getSrcFile().getPath() + "'";

		log.debug("��־����:" + sql);
		conn.createStatement().executeUpdate(sql);
	}

	//====================================��־��������ݿ� END====================================

	public static void main(String[] args)
	{
		String start = "[2012-12-18 12:44:33][com.inspur.ftpparserframework.parser.ParseThread.run(ParseThread.java:45)][pool-49302-thread-5][INFO]-#�ļ��������#-[��ʼ]-[Դ�ļ�=D:\\workspace1\\data\\input\\TDPM\\test\\PM201211280825+0800168B20121128.0800+0800-0815+0800_job298-3.xml,��С=11616Byte,ʱ��=2012-11-28 09:00:06]\n"
				.replaceAll("\n", "");
		String end = "[2012-11-28 14:17:09][com.inspur.ftpparserframework.util.OracleUtil.load(OracleUtil.java:205)][pool-49302-thread-5][INFO]-#�������#-[����]-[Դ�ļ�=/home/oracle/wuyg/td-mr-xml/data/input/JN_ZTE_OMC/DATANG/TD-SCDMA_MRS_DATANG_OMCR_905_20121120150000.xml.gz,��С=81280Byte,ʱ��=2012-11-27 16:29:25]-[������ļ�=/home/oracle/wuyg/td-mr-xml/data/sqlldr/txt/TD-SCDMA_MRS_DATANG_OMCR_905_20121120150000_UtranUppts.txt,��С=140626Byte,ʱ��=2012-11-28 14:17:08]-[����=TadvPccpchRscp]-[�ɹ�����=678,ʧ������=0]-[��ʱ=176����]";
		String error = "[2012-11-28 14:17:00][com.inspur.ftpparserframework.util.OracleUtil.load(OracleUtil.java:210)][pool-49302-thread-5][ERROR]-#�������#-[����]-[Դ�ļ�=/home/oracle/wuyg/td-mr-xml/data/input/JN_ZTE_OMC/POTEVIO/TD-SCDMA_MRS_ZTE_POTEVIO_OMCR_990_2012090411000_1.xml.gz,��С=112274Byte,ʱ��=2012-11-27 17:57:12]-[������ļ�=/home/oracle/wuyg/td-mr-xml/data/sqlldr/txt/TD-SCDMA_MRS_ZTE_POTEVIO_OMCR_990_2012090411000_1_TadvPccpchRscp.txt,��С=3958Byte,ʱ��=2012-11-28 14:17:00]-[����=Td_Mr_TadvPccpchRscp01]-[�ɹ�����=2,ʧ������=1]-[��ʱ=91����]-[������Ϣ=SQL*Loader-466: Column FILESN does not exist in table TD_MR_TADVPCCPCHRSCP.]";
		String regex = new ParserMainLog().getLogRegex();
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