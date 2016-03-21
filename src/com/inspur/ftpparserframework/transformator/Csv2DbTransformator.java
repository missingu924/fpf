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
 * CSV文件或格式标准的TXT文件入库转换器
 * 
 * @author 武玉刚
 * 
 */
public class Csv2DbTransformator implements ITransformator
{
	/**
	 * 用于指定CSV2DB转换时所需的XML配置文件的参数名
	 */
	public static final String csv2dbmap = "csv2dbmap";
	/**
	 * 用于指定CSV2DB数据入库时对应的数据库表的参数名
	 */
	public static final String csv2dbtable = "csv2dbtable";
	
	/*
	 * 固定配置配置文件、数据库映射配置文件名，便于数据质量管理
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
			// 1、解析csv2dbmap配置文件，获取映射关系
			
			Object csvMapFileName = paramMap.get(csv2dbmap);
			if (csvMapFileName == null)
			{
				throw new GetParameterException("无法获取" + csv2dbmap + "参数值,该参数值用于指定" + getTransformatorName()
						+ "的配置文件,对应的待处理文件为:" + middleFile.getCanonicalPath());
			} else
			{
				csvMapFile = new File(csvMapFileName.toString());
				if (!csvMapFile.exists())
				{
					throw new GetParameterException("未找到" + getTransformatorName() + "转换所需的" + csv2dbmap + "文件:"
							+ csvMapFile.getCanonicalPath());
				}
			}

			Object tableNameObj = paramMap.get(csv2dbtable);
			if (tableNameObj == null)
			{
				throw new GetParameterException("无法获取csv2dbtable参数值,该参数值用于指定csv2db对应的数据库表,对应的csv文件为:"
						+ middleFile.getCanonicalPath());
			} else
			{
				tableName = tableNameObj.toString();
			}

			Csv2DbMapConfig config = (Csv2DbMapConfig) DigesterHelper.parseFromXmlFile(Csv2DbMapConfig.class,
					csvMapFile.getCanonicalPath());
			Csv2DbMap csv2DbMap = config.getCsv2DbMapByTable(tableName);

			// 2、构造表
			DbLoaderTable table = new DbLoaderTable(csv2DbMap.getDbTable());

			// 3、读取dataFile文件表头并拆分
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

			// 4、检查数据文件的列是否完整，如果与解析配置文件中有而数据文件中没有，则抛出异常
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
				throw new Exception("数据文件字段缺失：" + fieldsNotInDataFile);
			}

			// 5、根据dataFile文件表头的顺序排列数据库对应的列，无需入库的列进行过滤
			List<DbLoaderColumn> dbLoaderColumns = new ArrayList<DbLoaderColumn>();
			if (csvFields != null)
			{
				for (int i = 0; i < csvFields.length; i++)
				{
					String csvField = csvFields[i];

					// 配置文件中已配置该字段且对应的数据库字段名不为空，则需要从csv文件中加载该列，则加到列表中
					Csv2DbColumn csv2DbColumn = csv2DbMap.getCsv2DbColumnByCsvField(csvField);
					if (csv2DbColumn != null && !StringUtil.isEmpty(csv2DbColumn.getDbColName()))
					{

						dbLoaderColumns.add(csv2DbColumn.getDbLoderColumn());
					}
					// 配置文件中未配置该字段，则不需要从csv文件中加载该列，则过滤之
					else
					{
						DbLoaderColumn dbloaderColumn = new DbLoaderColumn();
						dbloaderColumn.setName(csvField);
						dbloaderColumn.setFiller(true);

						dbLoaderColumns.add(dbloaderColumn);
					}
				}
			}

			// 6、对于不从dataFile中取值的数据库列设定其值（如固定值等，可以从外部通过paraMap传入）
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

			// 7、加载入库
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
				throw new GetParameterException("无法获取" + key + "对应的参数值,参数值可通过命令行输入。");
			}
		}
		return constantValue;
	}
}
