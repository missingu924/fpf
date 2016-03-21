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
import com.inspur.ftpparserframework.util.DigesterHelper;
import com.inspur.ftpparserframework.util.StringUtil;

/**
 * CSV文件或格式标准的TXT文件入库转换器
 * 
 * @author 武玉刚
 * 
 */
public class Csv2DbTransformator4meta implements ITransformator
{
	public static final String CSV2DBMAPFILE = "Csv2DbMap.xml";
	public static final String DEFAULT_CSV2DBMAPFILE = "../conf/Csv2DbMap.xml";

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

		// 0、查找映射文件
		String srcFilePath = srcFile.getCanonicalPath().replaceAll("\\\\", "/");
		if (srcFilePath.contains("input/~") && srcFilePath.contains("~/"))
		{
			String publisth_task_id = srcFilePath.substring(srcFilePath.indexOf("input/~") + 7, srcFilePath
					.indexOf("~/"));
			csvMapFile = new File("../conf/quality/ftpServers/" + publisth_task_id + "/" + CSV2DBMAPFILE);
			if (!csvMapFile.exists())
			{
				throw new GetParameterException("没找到任务[" + publisth_task_id + "]对应的字段映射文件["
						+ csvMapFile.getCanonicalPath() + "]，请核查！");
			}
		} else
		{
			csvMapFile = new File(DEFAULT_CSV2DBMAPFILE);
			if (!csvMapFile.exists())
			{
				throw new GetParameterException("没找到默认字段映射文件[" + csvMapFile.getCanonicalPath() + "]，请核查！");
			}
		}
		try
		{
			// 1、映射文件实例化
			Csv2DbMapConfig config = (Csv2DbMapConfig) DigesterHelper.parseFromXmlFile(Csv2DbMapConfig.class,
					csvMapFile.getCanonicalPath());
			// 2、根据文件名匹配映射文件，查找入库映射
			Csv2DbMap csv2DbMap = config.getCsv2DbMapBySrcFileName(srcFile.getName());
			if (csv2DbMap==null)
			{
				throw new Exception("未找到文件"+srcFile.getName()+"对应的入库配置信息");
			}
			// 3、构造表
			DbLoaderTable table = new DbLoaderTable(csv2DbMap.getDbTable());
			// 4、读取dataFile文件表头
			String encoding = csv2DbMap.getEncoding();
			if (encoding != null)
			{
				in = new FileInputStream(middleFile);
				csvReader = new CSVReader(new InputStreamReader(in, encoding), csv2DbMap.getSeparator().charAt(0),
						csv2DbMap.getQuotechar().charAt(0));
			} else
			{
				//new CSVReader(reader, ',', '"');
				char c=(csv2DbMap.getSeparator()==null||csv2DbMap.getSeparator().length()<1)?',':csv2DbMap.getSeparator().charAt(0);
				char c1=(csv2DbMap.getQuotechar()==null||csv2DbMap.getQuotechar().length()<1)?'"':csv2DbMap.getQuotechar().charAt(0);
				csvReader = new CSVReader(new FileReader(middleFile), c, c1);
			}

			String[] csvFields = csvReader.readNext();
			// 5、根据dataFile文件表头的顺序或者列位置排列数据库对应的列，无需入库的列进行过滤
			List<DbLoaderColumn> dbLoaderColumns = new ArrayList<DbLoaderColumn>();
			if (csvFields != null)
			{
				if (csv2DbMap.getHasTitle() != null && csv2DbMap.getHasTitle().equalsIgnoreCase("true"))
				{// 有表头，根据列头名称获取列映射信息
					for (int i = 0; i < csvFields.length; i++)
					{
						String csvField = csvFields[i];
						// 配置文件中已配置该字段且对应的数据库字段名不为空，则需要从csv文件中加载该列，则加到列表中
						Csv2DbColumn csv2DbColumn = null;
						csv2DbColumn = csv2DbMap.getCsv2DbColumnByCsvField(csvField);
						if (csv2DbColumn != null && !StringUtil.isEmpty(csv2DbColumn.getDbColName()))
						{
							dbLoaderColumns.add(csv2DbColumn.getDbLoderColumn());
						} else
						{// 配置文件中未配置该字段，则不需要从csv文件中加载该列，则过滤之
							DbLoaderColumn dbloaderColumn = new DbLoaderColumn();
							dbloaderColumn.setName(csvField);
							dbloaderColumn.setFiller(true);
							dbLoaderColumns.add(dbloaderColumn);
						}
					}
				} else
				{// 没有表头，根据列位置获取列映射信息(配置文件中csvField存储的位置)
					for (int i = 0; i < csvFields.length; i++)
					{
						// 配置文件中已配置该字段且对应的数据库字段名不为空，则需要从csv文件中加载该列，则加到列表中
						Csv2DbColumn csv2DbColumn = null;
						csv2DbColumn = csv2DbMap.getCsv2DbColumnByCsvField(String.valueOf(i));
						if (csv2DbColumn != null && !StringUtil.isEmpty(csv2DbColumn.getDbColName()))
						{
							dbLoaderColumns.add(csv2DbColumn.getDbLoderColumn());
						} else
						{// 配置文件中未配置该字段，则不需要从csv文件中加载该列，则过滤之
							DbLoaderColumn dbloaderColumn = new DbLoaderColumn();
							dbloaderColumn.setName("col"+String.valueOf(i));
							dbloaderColumn.setFiller(true);
							dbLoaderColumns.add(dbloaderColumn);
						}
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
		//constantValue: to_date('$startTime$','yyyymmddhh24miss')
		if(constantValue.contains("$")){
			
			String [] keyList = constantValue.split("\\$");
			for(int i=0;i<keyList.length;i++){
				String key = keyList[i];
				Object valueObj = paramMap.get(key);
				if (valueObj != null){
					constantValue = constantValue.replace("$"+key+"$", valueObj.toString());
				}
			}
		}
		
		return constantValue;
	}
}
