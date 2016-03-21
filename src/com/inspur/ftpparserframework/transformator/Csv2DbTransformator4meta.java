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
 * CSV�ļ����ʽ��׼��TXT�ļ����ת����
 * 
 * @author �����
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

		// 0������ӳ���ļ�
		String srcFilePath = srcFile.getCanonicalPath().replaceAll("\\\\", "/");
		if (srcFilePath.contains("input/~") && srcFilePath.contains("~/"))
		{
			String publisth_task_id = srcFilePath.substring(srcFilePath.indexOf("input/~") + 7, srcFilePath
					.indexOf("~/"));
			csvMapFile = new File("../conf/quality/ftpServers/" + publisth_task_id + "/" + CSV2DBMAPFILE);
			if (!csvMapFile.exists())
			{
				throw new GetParameterException("û�ҵ�����[" + publisth_task_id + "]��Ӧ���ֶ�ӳ���ļ�["
						+ csvMapFile.getCanonicalPath() + "]����˲飡");
			}
		} else
		{
			csvMapFile = new File(DEFAULT_CSV2DBMAPFILE);
			if (!csvMapFile.exists())
			{
				throw new GetParameterException("û�ҵ�Ĭ���ֶ�ӳ���ļ�[" + csvMapFile.getCanonicalPath() + "]����˲飡");
			}
		}
		try
		{
			// 1��ӳ���ļ�ʵ����
			Csv2DbMapConfig config = (Csv2DbMapConfig) DigesterHelper.parseFromXmlFile(Csv2DbMapConfig.class,
					csvMapFile.getCanonicalPath());
			// 2�������ļ���ƥ��ӳ���ļ����������ӳ��
			Csv2DbMap csv2DbMap = config.getCsv2DbMapBySrcFileName(srcFile.getName());
			if (csv2DbMap==null)
			{
				throw new Exception("δ�ҵ��ļ�"+srcFile.getName()+"��Ӧ�����������Ϣ");
			}
			// 3�������
			DbLoaderTable table = new DbLoaderTable(csv2DbMap.getDbTable());
			// 4����ȡdataFile�ļ���ͷ
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
			// 5������dataFile�ļ���ͷ��˳�������λ���������ݿ��Ӧ���У����������н��й���
			List<DbLoaderColumn> dbLoaderColumns = new ArrayList<DbLoaderColumn>();
			if (csvFields != null)
			{
				if (csv2DbMap.getHasTitle() != null && csv2DbMap.getHasTitle().equalsIgnoreCase("true"))
				{// �б�ͷ��������ͷ���ƻ�ȡ��ӳ����Ϣ
					for (int i = 0; i < csvFields.length; i++)
					{
						String csvField = csvFields[i];
						// �����ļ��������ø��ֶ��Ҷ�Ӧ�����ݿ��ֶ�����Ϊ�գ�����Ҫ��csv�ļ��м��ظ��У���ӵ��б���
						Csv2DbColumn csv2DbColumn = null;
						csv2DbColumn = csv2DbMap.getCsv2DbColumnByCsvField(csvField);
						if (csv2DbColumn != null && !StringUtil.isEmpty(csv2DbColumn.getDbColName()))
						{
							dbLoaderColumns.add(csv2DbColumn.getDbLoderColumn());
						} else
						{// �����ļ���δ���ø��ֶΣ�����Ҫ��csv�ļ��м��ظ��У������֮
							DbLoaderColumn dbloaderColumn = new DbLoaderColumn();
							dbloaderColumn.setName(csvField);
							dbloaderColumn.setFiller(true);
							dbLoaderColumns.add(dbloaderColumn);
						}
					}
				} else
				{// û�б�ͷ��������λ�û�ȡ��ӳ����Ϣ(�����ļ���csvField�洢��λ��)
					for (int i = 0; i < csvFields.length; i++)
					{
						// �����ļ��������ø��ֶ��Ҷ�Ӧ�����ݿ��ֶ�����Ϊ�գ�����Ҫ��csv�ļ��м��ظ��У���ӵ��б���
						Csv2DbColumn csv2DbColumn = null;
						csv2DbColumn = csv2DbMap.getCsv2DbColumnByCsvField(String.valueOf(i));
						if (csv2DbColumn != null && !StringUtil.isEmpty(csv2DbColumn.getDbColName()))
						{
							dbLoaderColumns.add(csv2DbColumn.getDbLoderColumn());
						} else
						{// �����ļ���δ���ø��ֶΣ�����Ҫ��csv�ļ��м��ظ��У������֮
							DbLoaderColumn dbloaderColumn = new DbLoaderColumn();
							dbloaderColumn.setName("col"+String.valueOf(i));
							dbloaderColumn.setFiller(true);
							dbLoaderColumns.add(dbloaderColumn);
						}
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
