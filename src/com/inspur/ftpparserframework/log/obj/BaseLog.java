package com.inspur.ftpparserframework.log.obj;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.inspur.ftpparserframework.config.ConfigReader;
import com.inspur.ftpparserframework.extend.IBasicInfoParser;
import com.inspur.ftpparserframework.util.StringUtil;
import com.inspur.ftpparserframework.util.TimeUtil;

public abstract class BaseLog
{
	public final String STATE_START = "��ʼ";
	public final String STATE_END = "����";
	public final String STATE_ERROR = "����";

	private Logger log = Logger.getLogger(getClass());
	/**
	 * ��־��¼ʱ��
	 */
	private Date logTime;

	/**
	 * ������������
	 */
	private String handleName;

	/**
	 * ��������״̬��һ���Ϊ����ʼ������������ ���ࡣ
	 */
	private String handleState;

	/**
	 * Դ�ļ�
	 */
	private BaseFile srcFile = new BaseFile();

	/**
	 * ������ʼʱ��
	 */
	private Date startTime;

	/**
	 * ��������ʱ��
	 */
	private Date endTime;

	/**
	 * �ļ�������ʱ����λ����
	 */
	private long usedTimeMillis;

	/**
	 * ������Ϣ
	 */
	private String errorMessage = "";

	/**
	 * �ļ�������Ϣ
	 */
	private BasicInfo basicInfo = null;

	/**
	 * ��ע��Ϣ
	 */
	private String mark = "";

	public String getMark()
	{
		return mark;
	}

	public void setMark(String mark)
	{
		this.mark = mark;
	}

	public BasicInfo getBasicInfo() throws Exception
	{
		if (basicInfo == null)
		{
			String basicInfoParserClz = ConfigReader.getProperties("extend.basicInfoParser");
			if (!StringUtil.isEmpty(basicInfoParserClz) && !StringUtil.isEmpty(getSrcFile().getPath()))
			{
				IBasicInfoParser basicInfoParser = (IBasicInfoParser) Thread.currentThread().getContextClassLoader()
						.loadClass(basicInfoParserClz).newInstance();
				basicInfo = basicInfoParser.parseBasicInfo(new File(getSrcFile().getPath()));
			} else
			{
				basicInfo = new BasicInfo();
			}
		}

		return basicInfo;
	}

	public void setBasicInfo(BasicInfo basicInfo)
	{
		this.basicInfo = basicInfo;
	}

	public Date getLogTime()
	{
		return logTime;
	}

	public void setLogTime(Date logTime)
	{
		this.logTime = logTime;
	}

	public String getHandleName()
	{
		return handleName;
	}

	public void setHandleName(String handleName)
	{
		this.handleName = handleName;
	}

	public String getHandleState()
	{
		return handleState;
	}

	public void setHandleState(String handleState)
	{
		this.handleState = handleState;
	}

	public BaseFile getSrcFile()
	{
		return srcFile;
	}

	public void setSrcFile(BaseFile srcFile) throws ParseException
	{
		this.srcFile = srcFile;
	}

	public Date getStartTime()
	{
		return startTime;
	}

	public void setStartTime(Date startTime)
	{
		this.startTime = startTime;
	}

	public Date getEndTime()
	{
		return endTime;
	}

	public void setEndTime(Date endTime)
	{
		this.endTime = endTime;
	}

	public String getErrorMessage()
	{
		return StringUtil.isEmpty(errorMessage) ? "" : errorMessage;
	}

	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	public long getUsedTimeMillis()
	{
		return usedTimeMillis;
	}

	public void setUsedTimeMillis(long usedTimeMillis)
	{
		this.usedTimeMillis = usedTimeMillis;
	}

	/**
	 * �ϲ��������������ֵ
	 * 
	 * @param other
	 * @throws Exception
	 */
	public void merge(Object other) throws Exception
	{
		Logger log = Logger.getLogger(BeanUtils.class.getPackage().getName());
		log.setLevel(Level.INFO);

		PropertyDescriptor[] ps = PropertyUtils.getPropertyDescriptors(other.getClass());
		for (int i = 0; i < ps.length; i++)
		{
			PropertyDescriptor p = ps[i];
			Object propertyValue = PropertyUtils.getProperty(other, p.getName());
			if (propertyValue != null)
			{
				BeanUtils.setProperty(this, p.getName(), propertyValue);
			}
		}
	}

	// ====================================��־���� START====================================
	/**
	 * ��־����������log�ַ�������ȡ����log����
	 * 
	 * @param logStr
	 */
	public boolean parseLog(String logStr) throws Exception
	{
		Pattern p = Pattern.compile(getLogRegex());
		Matcher m = p.matcher(logStr);
		if (m.matches())
		{
			getInstance(m);
			return true;
		} else
		{
			return false;
		}
	}

	/**
	 * ��־������������ʵ�֣����ؾ�����־�������������ʽ��
	 * 
	 * @return
	 */
	protected abstract String getLogRegex();

	/**
	 * ��־�����������������ʽ�Ľ������log����
	 * 
	 * @param matcher
	 * @return
	 */
	protected abstract void getInstance(Matcher matcher) throws Exception;

	// ====================================��־���� END====================================

	// ====================================��־�����CSV�ļ� START====================================
	/**
	 * ��־�����CSV����ȡcsv���ʱ�ı�ͷ��
	 * 
	 * @return
	 */
	public String getCsvTitle()
	{
		return getBasicCsvTitle() + "," + getThisCsvTitle();
	}

	/**
	 * ��־�����CSV����ȡcsv���ʱ�����ݡ�
	 * 
	 * @return
	 * @throws ParseException
	 */
	public String toCsv() throws ParseException
	{
		return basic2Csv() + "," + this2Csv();
	}

	/**
	 * ��־�����CSV��������Ϣ��ͷ�����
	 * 
	 * @return
	 */
	private String getBasicCsvTitle()
	{
		StringBuilder sb = new StringBuilder();

		if (this.basicInfo != null)
		{
			//sb.append(this.basicInfo.getThisCsvTitle()).append(",");//
		}

		sb.append("��������").append(",")//
				.append("����״̬").append(",")//
				.append(this.srcFile.getThisCsvTitle("Դ")).append(",")//
				.append("���δ�����ʼʱ��").append(",")//
				.append("���δ�������ʱ��").append(",")//
				.append("���δ�����ʱ(����)").append(",")//
				.append("���δ���������Ϣ").append(",")//
				.append("��ע");

		return sb.toString();
	}

	/**
	 * ��־�����CSV��������Ϣ���������
	 * 
	 * @return
	 * @throws ParseException
	 */
	private String basic2Csv() throws ParseException
	{
		StringBuilder sb = new StringBuilder();
		if (this.basicInfo != null)
		{
			//sb.append(this.basicInfo.this2Csv()).append(",");//
		}
		sb.append(handleName).append(",")//
				.append(handleState).append(",")//
				.append(srcFile.this2Csv(true)).append(",")//
				.append(TimeUtil.date2str(startTime)).append(",")//
				.append(TimeUtil.date2str(endTime)).append(",")//
				.append(getUsedTimeMillis()).append(",")//
				.append(StringUtil.isEmpty(errorMessage) ? "" : "\"" + errorMessage + "\"").append(",")//
				.append(mark);

		return sb.toString();
	}

	/**
	 * ��־�����CSV��������ʵ�֣���������еı�ͷ��Ϣ��
	 * 
	 * @return
	 */
	protected abstract String getThisCsvTitle();

	/**
	 * ��־�����CSV��������ʵ�֣���������е�������Ϣ��
	 * 
	 * @return
	 */
	protected abstract String this2Csv();

	// ====================================��־�����CSV�ļ� END====================================

	// ====================================��־��������ݿ� START====================================
	/**
	 * ���ݿ�������������¸���־��Ϣ�����ݿ���
	 * 
	 * @throws Exception
	 */
	public void saveOrUpdate() throws Exception
	{
		Connection conn = getConnection();
		try
		{
			// if (!isTableExists(conn))
			// {
			// createTable(conn);
			// } else if (isExistsInDb(conn))
			// {
			// update2Db(conn);
			// } else
			// {
			// save2Db(conn);
			// }

			// modify by wuyg 2013-1-21,���ټ�����ݿ����Ƿ������Ӧ��¼,��������²�������ظ��������,����ÿ�μ���ǱȽϺķ�ʱ���,���Ը�Ϊֱ�����
			save2Db(conn);
		} catch (Exception e)
		{
			log.error(e.getMessage(), e);
			e.printStackTrace();
		} finally
		{
			if (conn != null)
			{
				conn.close();
			}
		}
	}

	private static BasicDataSource ds = null;

	private Connection getConnection() throws SQLException
	{
		if (ds == null)
		{
			String driverClassName = ConfigReader.getProperties("log.dbDriverClassName");
			String url = ConfigReader.getProperties("log.dbUrl");
			String user = ConfigReader.getProperties("log.dbUser");
			String password = ConfigReader.getProperties("log.dbPassword");

			ds = new BasicDataSource();
			ds.setDriverClassName(driverClassName);
			ds.setUrl(url);
			ds.setUsername(user);
			ds.setPassword(password);
			ds.setInitialSize(10);
			ds.setMaxActive(100);
			ds.setMaxIdle(5);
			ds.setMaxWait(5);
			ds.setValidationQuery("select 1 from dual");// add by lex 20140418
			ds.setTestOnBorrow(true);// add by lex 20140418
			ds.setTestOnReturn(true);// add by lex 20140418
			ds.setTestWhileIdle(true);// add by lex 20140418
		}

		Connection conn = ds.getConnection();

		return conn;
	}

	/**
	 * ���ݿ�������ж���־��������ı������������е����ݶ����Ƿ��Ѿ������ݿ��д���
	 * 
	 * @return
	 * @throws Exception
	 */
	protected abstract boolean isTableExists(Connection conn) throws Exception;

	/**
	 * ���ݿ������������־��������ı������������е����ݿ����
	 * 
	 * @return
	 * @throws Exception
	 */
	protected abstract void createTable(Connection conn) throws Exception;

	/**
	 * ���ݿ�������ж���־�����Ƿ��Ѿ���⡣
	 * 
	 * @return
	 * @throws Exception
	 */
	protected abstract boolean isExistsInDb(Connection conn) throws Exception;

	/**
	 * ���ݿ��������־���󱣴���⡣
	 * 
	 * @throws Exception
	 */
	protected abstract void save2Db(Connection conn) throws Exception;

	/**
	 * ���ݿ�����������Ѿ�������־����
	 * 
	 * @throws Exception
	 */
	protected abstract void update2Db(Connection conn) throws Exception;
	// ====================================��־��������ݿ� END====================================
}