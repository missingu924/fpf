package com.inspur.ftpparserframework.transformator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;

import com.inspur.ftpparserframework.dbloader.DbLoaderUtil;
import com.inspur.ftpparserframework.dbloader.obj.DbLoaderColumn;
import com.inspur.ftpparserframework.dbloader.obj.DbLoaderTable;
import com.inspur.ftpparserframework.exception.GetParameterException;
import com.inspur.ftpparserframework.transformator.obj.Csv2DbColumn;
import com.inspur.ftpparserframework.transformator.obj.Csv2DbMap;
import com.inspur.ftpparserframework.transformator.obj.Csv2DbMapConfig;
import com.inspur.ftpparserframework.util.Constant;
import com.inspur.ftpparserframework.util.DigesterHelper;
import com.inspur.ftpparserframework.util.StringUtil;

/**
 * CSV�ļ����ʽ��׼��TXT�ļ����ת����
 * 
 * @author �����
 * 
 */
public class Csv2DbTransformator implements ITransformator
{
	/**
	 * ����ָ��CSV2DBת��ʱ�����XML�����ļ��Ĳ�����
	 */
	public static final String csv2dbmap = "csv2dbmap";
	/**
	 * ����ָ��CSV2DB�������ʱ��Ӧ�����ݿ��Ĳ�����
	 */
	public static final String csv2dbtable = "csv2dbtable";
	
	/*
	 * �̶����������ļ������ݿ�ӳ�������ļ���������������������
	 */
	public static final String DEFAULT_CSV2DBMAPFILE="../conf/Csv2DbMap.xml";
	public static final String DEFAULT_SRCNAME2DBTABLEFILE = "../conf/srcName2dbtable.csv";

	// @Override
	public String getTransformatorName()
	{
		return "CSV2DB";
	}

	// @Override
	public String getDestFileSuffix()
	{
		return "";
	}

	// @Override
	public void transformat(File srcFile, File middleFile, File destFile, Map<String, Object> paramMap)
			throws Exception
	{
		FileInputStream in = null;
		CSVReader csvReader = null;
		File csvMapFile = null;
		String tableName = null;
		try
		{
			// 1������csv2dbmap�����ļ�����ȡӳ���ϵ
			
			Object csvMapFileName = paramMap.get(csv2dbmap);
			if (csvMapFileName == null)
			{
				throw new GetParameterException("�޷���ȡ" + csv2dbmap + "����ֵ,�ò���ֵ����ָ��" + getTransformatorName()
						+ "�������ļ�,��Ӧ�Ĵ������ļ�Ϊ:" + middleFile.getCanonicalPath());
			} else
			{
				csvMapFile = new File(csvMapFileName.toString());
				if (!csvMapFile.exists())
				{
					throw new GetParameterException("δ�ҵ�" + getTransformatorName() + "ת�������" + csv2dbmap + "�ļ�:"
							+ csvMapFile.getCanonicalPath());
				}
			}

			Object tableNameObj = paramMap.get(csv2dbtable);
			if (tableNameObj == null)
			{
				throw new GetParameterException("�޷���ȡcsv2dbtable����ֵ,�ò���ֵ����ָ��csv2db��Ӧ�����ݿ��,��Ӧ��csv�ļ�Ϊ:"
						+ middleFile.getCanonicalPath());
			} else
			{
				tableName = tableNameObj.toString();
			}

			Csv2DbMapConfig config = (Csv2DbMapConfig) DigesterHelper.parseFromXmlFile(Csv2DbMapConfig.class,
					csvMapFile.getCanonicalPath());
			Csv2DbMap csv2DbMap = config.getCsv2DbMapByTable(tableName);

			// 2�������
			DbLoaderTable table = new DbLoaderTable(csv2DbMap.getDbTable());

			// 3����ȡdataFile�ļ���ͷ�����
			// csvReader = new CSVReader(new FileReader(middleFile), csv2DbMap.getSeparator().charAt(0), csv2DbMap
			// .getQuotechar().charAt(0));
			Object characterSet = paramMap.get(Constant.CHARACTERSET);
			if (characterSet != null)
			{
				in = new FileInputStream(middleFile);
				csvReader = new CSVReader(new InputStreamReader(in, (String) characterSet), csv2DbMap.getSeparator()
						.charAt(0), csv2DbMap.getQuotechar().charAt(0));
			} else
			{
				csvReader = new CSVReader(new FileReader(middleFile), csv2DbMap.getSeparator().charAt(0), csv2DbMap
						.getQuotechar().charAt(0));
			}

			String[] csvFields = csvReader.readNext();

			// 4����������ļ������Ƿ��������������������ļ����ж������ļ���û�У����׳��쳣
			boolean dataFileFieldsOk = true;
			String fieldsNotInDataFile = "";
			List<Csv2DbColumn> csv2dbColumns = csv2DbMap.getColumns();
			for (int i = 0; i < csv2dbColumns.size(); i++)
			{
				Csv2DbColumn csv2DbColumn = csv2dbColumns.get(i);
				if (!StringUtil.isEmpty(csv2DbColumn.getCsvField()))
				{
					boolean dataFileContainsThisField = false;
					for (int j = 0; j < csvFields.length; j++)
					{
						if (csvFields[j].equals(csv2DbColumn.getCsvField()))
						{
							dataFileContainsThisField = true;
							break;
						}
					}

					dataFileFieldsOk = dataFileFieldsOk && dataFileContainsThisField;

					if (!dataFileContainsThisField)
					{
						fieldsNotInDataFile += csv2DbColumn.getCsvField() + ", ";
					}
				}
			}
			if (!dataFileFieldsOk)
			{
				throw new Exception("�����ļ��ֶ�ȱʧ��" + fieldsNotInDataFile);
			}

			// 5������dataFile�ļ���ͷ��˳���������ݿ��Ӧ���У����������н��й���
			List<DbLoaderColumn> dbLoaderColumns = new ArrayList<DbLoaderColumn>();
			if (csvFields != null)
			{
				for (int i = 0; i < csvFields.length; i++)
				{
					String csvField = csvFields[i];

					// �����ļ��������ø��ֶ��Ҷ�Ӧ�����ݿ��ֶ�����Ϊ�գ�����Ҫ��csv�ļ��м��ظ��У���ӵ��б���
					Csv2DbColumn csv2DbColumn = csv2DbMap.getCsv2DbColumnByCsvField(csvField);
					if (csv2DbColumn != null && !StringUtil.isEmpty(csv2DbColumn.getDbColName()))
					{

						dbLoaderColumns.add(csv2DbColumn.getDbLoderColumn());
					}
					// �����ļ���δ���ø��ֶΣ�����Ҫ��csv�ļ��м��ظ��У������֮
					else
					{
						DbLoaderColumn dbloaderColumn = new DbLoaderColumn();
						dbloaderColumn.setName(csvField);
						dbloaderColumn.setFiller(true);

						dbLoaderColumns.add(dbloaderColumn);
					}
				}
			}

			// 6�����ڲ���dataFile��ȡֵ�����ݿ����趨��ֵ����̶�ֵ�ȣ����Դ��ⲿͨ��paraMap���룩
			List<Csv2DbColumn> csv2DbColumns = csv2DbMap.getColumns();
			for (int i = 0; i < csv2DbColumns.size(); i++)
			{
				Csv2DbColumn csv2DbColumn = csv2DbColumns.get(i);
				if (!StringUtil.isEmpty(csv2DbColumn.getConstantValue()))
				{
					csv2DbColumn.setConstantValue(getConstantValue(paramMap, csv2DbColumn.getConstantValue()));

					dbLoaderColumns.add(csv2DbColumn.getDbLoderColumn());
				}
			}

			// 7���������
			DbLoaderUtil.load2db(srcFile, middleFile, table, dbLoaderColumns, csv2DbMap.getSeparator() + "", csv2DbMap
					.getQuotechar()
					+ "");
		} finally
		{
			if (in != null)
			{
				in.close();
			}
			if (csvReader != null)
			{
				csvReader.close();
			}
		}
	}

	private String getConstantValue(Map<String, Object> paramMap, String constantValue) throws Exception
	{
		if (constantValue.startsWith("$") && constantValue.endsWith("$"))
		{
			String key = constantValue.replaceAll("\\$", "");
			Object valueObj = paramMap.get(key);
			if (valueObj != null)
			{
				constantValue = valueObj.toString();
			} else
			{
				throw new GetParameterException("�޷���ȡ" + key + "��Ӧ�Ĳ���ֵ,����ֵ��ͨ�����������롣");
			}
		}
		return constantValue;
	}
}
