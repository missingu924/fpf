package com.inspur.ftpparserframework.dbloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;

import com.inspur.ftpparserframework.Main;
import com.inspur.ftpparserframework.config.ConfigReader;
import com.inspur.ftpparserframework.dbloader.obj.DbLoaderColumn;
import com.inspur.ftpparserframework.dbloader.obj.DbLoaderDatabase;
import com.inspur.ftpparserframework.dbloader.obj.DbLoaderTable;
import com.inspur.ftpparserframework.quality.dbLoaderConf.DBLoaderConf;
import com.inspur.ftpparserframework.util.Constant;
import com.inspur.ftpparserframework.util.StringUtil;
import com.inspur.ftpparserframework.util.XMLFactory;
import com.inspur.meta.fpf.rmi.pojo.DBInfo;
import com.inspur.meta.fpf.rmi.pojo.TaskPublishConfInfo;

/**
 * ������⹤����
 * 
 * @author �����
 * 
 */
public class DbLoaderUtil
{
	private static Logger log = Logger.getLogger(DbLoaderUtil.class);

	public static final String ORACLE = "ORACLE";
	public static final String SYBASE = "SYBASE";
	public static final String DB2 = "DB2";
	public static final String INFORMIX = "INFORMIX";
	public static final String SQLLDR = "SQLLDR";
	public static final String MYSQL = "MYSQL";
	public static final String GBASE = "GBASE";
	public static final String GBASECLUSTER = "GBASECLUSTER";

	/**
	 * ���ݼ������
	 * 
	 * @param srcFile
	 *            ��ԭʼ���ļ�
	 * @param dataFile
	 *            �����ص������ļ�
	 * @param table
	 *            ���ݱ���Ϣ
	 * @param columns
	 *            �����ֶ���Ϣ
	 * @throws Exception
	 */
	public static void load2db(File srcFile, File dataFile, DbLoaderTable table, List<DbLoaderColumn> columns)
			throws Exception
	{
		load2db(srcFile, dataFile, table, columns, IDbLoader.DEFAULT_FIELDS_TERMINATED_BY,
				IDbLoader.DEFAULT_OPTIONALLY_ENCLOSED_BY);
	}

	/**
	 * ���ݼ������
	 * 
	 * @param srcFile
	 *            ��ԭʼ���ļ�
	 * @param dataFile
	 *            �����ص������ļ�
	 * @param table
	 *            ���ݱ���Ϣ
	 * @param columns
	 *            �����ֶ���Ϣ
	 * @throws Exception
	 */
	public static void load2db(File srcFile, File dataFile, DbLoaderTable table, List<DbLoaderColumn> columns,
			String fieldsTeminatedBy, String optionallyEnclosedBy) throws Exception
	{
		String dbType = ConfigReader.getProperties("dbType");
		if (StringUtil.isEmpty(dbType))
		{
			dbType = DbLoaderUtil.ORACLE; // Ĭ��ʹ��Oracle
		}

		// ʹ��sqlldr��������
		if (dbType.equals(DbLoaderUtil.ORACLE))
		{
			String dbuserid = null;

			String srcFilePath = srcFile.getCanonicalPath().replaceAll("\\\\", "/");

			if (srcFilePath.contains("input/~") && srcFilePath.contains("~/"))
			{// �����������������ݿ���������
				String publisth_task_id=srcFilePath.substring(srcFilePath.indexOf("input/~")+7,srcFilePath.indexOf("~/"));
				TaskPublishConfInfo taskPublishConfInfo=Main.taskPublishConfInfoMap.get(publisth_task_id);
				DBInfo dBInfo =taskPublishConfInfo.getDBInfo();
				String ip=dBInfo.getIp();
				String port=dBInfo.getPort();
				String sid=dBInfo.getSid();
				sid=ip+":"+port+"/"+sid;
				dbuserid = dBInfo.getUser() + "/" + dBInfo.getPassword() + "@" + sid;
			} else
			{// ����ʱ��ֱ�Ӷ�ȡ�����ļ�
				dbuserid = ConfigReader.getProperties("sqlldr.dbuserid");
				if (StringUtil.isEmpty(dbuserid))
				{
					throw new Exception("SystemConfig.xml��δ����sqlldr.dbuserid����,����.");
				}
			}
			DbLoaderDatabase db = new DbLoaderDatabase(dbuserid);
			IDbLoader loader = getDbLoader(dbType);

			loader.load(srcFile, dataFile, db, table, columns, IDbLoader.DEFAULT_LOAD_MODE, fieldsTeminatedBy,
					optionallyEnclosedBy);
		}

		// �ʺ���gbload��������
		if (dbType.equals(DbLoaderUtil.GBASE))
		{
			IDbLoader loader = getDbLoader(dbType);

			loader.load(srcFile, dataFile, null, table, null);
		}

		// �ʺ���gbase��Ⱥ��������
		if (dbType.equals(DbLoaderUtil.GBASECLUSTER))
		{
			IDbLoader loader = getDbLoader(dbType);

			loader.load(srcFile, dataFile, null, table, null);
		}

	}

	public static IDbLoader getDbLoader(String dbType) throws Exception
	{
		if (ORACLE.equalsIgnoreCase(dbType))
		{
			return new OracleLoader();
		} else if (GBASE.equalsIgnoreCase(dbType))
		{
			return new GBaseLoader();
		} else if (GBASECLUSTER.equalsIgnoreCase(dbType))
		{
			return new GBaseClusterLoader();
		}

		else
		{
			log.error("�в�֧�ָ����ݿ����͵����ݼ���:" + dbType);
			return null;
		}
	}

	public static DBLoaderConf getDBLoaderConf() throws Exception
	{
		DBLoaderConf dbLoaderConf = null;
		if (Constant.QUALITY_DB_CONF_FILE.exists())
		{// �����������������ݿ���������
			InputStream fis = null;
			try
			{
				fis = new FileInputStream(Constant.QUALITY_DB_CONF_FILE);
				XMLFactory dBLoaderConfFactory = new XMLFactory(DBLoaderConf.class);
				dbLoaderConf = dBLoaderConfFactory.unmarshal(fis);
			} catch (Exception e)
			{
				throw e;
			} finally
			{
				if (fis != null)
				{
					fis.close();
				}
			}
		}
		return dbLoaderConf;
	}
}
