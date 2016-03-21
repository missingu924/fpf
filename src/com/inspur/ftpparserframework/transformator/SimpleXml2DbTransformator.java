package com.inspur.ftpparserframework.transformator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.inspur.ftpparserframework.dbloader.DbLoaderUtil;
import com.inspur.ftpparserframework.dbloader.obj.DbLoaderColumn;
import com.inspur.ftpparserframework.dbloader.obj.DbLoaderTable;
import com.inspur.ftpparserframework.util.Constant;
import com.inspur.ftpparserframework.util.StringUtil;

/**
 * �򵥸�ʽ(�涨�õĸ�ʽ)��Xml�ļ��������ת����
 * @author �����
 *
 */
public class SimpleXml2DbTransformator implements ITransformator
{
	private Logger log = Logger.getLogger(getClass());

//	@Override
	public String getTransformatorName()
	{
		return "SimpleXml2Db�ļ�ת�������";
	}

//	@Override
	public String getDestFileSuffix()
	{
		return "";
	}

//	@Override
	public void transformat(File srcFile, File middleFile, File destFile, Map<String, Object> paramMap)
			throws Exception
	{
		BufferedReader bf = null;
		PrintWriter txtWriter = null;
		try
		{
			bf = new BufferedReader(new InputStreamReader(new FileInputStream(middleFile), "UTF-8"));

			File dataFile = null;

			DbLoaderTable table = null;
			List<DbLoaderColumn> columns = new ArrayList<DbLoaderColumn>();
			StringBuffer columnsStr = new StringBuffer();
			StringBuffer value = new StringBuffer();
			int dataRows = 0;
			int dataPartNum=0;//���ݷֲ����

			String line = null;
			while ((line = bf.readLine()) != null)
			{
				if (isTableBegin(line))
				{
					String tableName = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
					table = new DbLoaderTable(tableName);
					dataRows = 0;

					dataPartNum++;
//					dataFile = new File(Constant.ORACLE_TXT + "/" + srcFile.getName() + "_" + tableName+".part"+new DecimalFormat("00000").format(dataPartNum) + ".txt");
					//���ⲻͬĿ¼��ͬ���ļ�ͬʱ�������³�ͻ
					dataFile = new File(Constant.ORACLE_TXT + "/" + srcFile.getName() + "." + tableName+"."+System.currentTimeMillis()+".part"+new DecimalFormat("00000").format(dataPartNum) + ".txt");
					if (!dataFile.getParentFile().exists())
					{
						dataFile.getParentFile().mkdirs();
					}

					txtWriter = new PrintWriter(dataFile, "gb2312");
				} else if (isTableEnd(line))
				{
					if (txtWriter != null)
					{
						txtWriter.close();

						// ���
						if (dataRows > 0)
						{
							try
							{
								DbLoaderUtil.load2db(srcFile, dataFile, table, columns);
							} catch (Exception e)
							{
								// modify by lex 2014-3-12 ,��ǰ�������ʧ�ܺ����׳��쳣���ڲ�����������ͬһ�ļ�������ʱ��һ�������º����޷���⡣
								log.error(e.getMessage(), e);
							}
						} else
						{
							log.debug("�����ļ������κ�����,�����أ�ֱ��ɾ�����ļ�:" + dataFile.getCanonicalPath());
							dataFile.delete();
						}
					}
				}

				else if (isColumnsBegin(line))
				{
					columnsStr = new StringBuffer();
					columns = new ArrayList<DbLoaderColumn>();

					if (isColumnsEnd(line))
					{
						columnsEnd(columnsStr, columns, txtWriter, line);
					}
				} else if (isColumnsEnd(line))
				{
					columnsEnd(columnsStr, columns, txtWriter, line);
				}

				else if (isValueBegin(line))
				{
					value = new StringBuffer();

					if (isValueEnd(line))
					{
						dataRows = valueEnd(txtWriter, value, dataRows, line);
					}
				} else if (isValueEnd(line))
				{
					dataRows = valueEnd(txtWriter, value, dataRows, line);
				}

				else
				{
					if (this.columns.equals(this.context))
					{
						columnsStr.append(line.trim());
					} else if (this.v.equals(this.context))
					{
						value.append(line);
					}
				}

			}
		} finally
		{
			if (bf != null)
			{
				bf.close();
			}
			if (txtWriter != null)
			{
				txtWriter.close();
			}
		}
	}

	private void columnsEnd(StringBuffer columnsStr, List<DbLoaderColumn> columns, PrintWriter txtWriter, String line)
	{
		columnsStr.append(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
		
		List<String> sl = StringUtil.getStringListByString(columnsStr.toString(), ',', '\"');

		for (int i = 0; i < sl.size(); i++)
		{
			columns.add(new DbLoaderColumn(sl.get(i)));
		}

		for (int i = 0; i < columns.size(); i++)
		{
			txtWriter.print(columns.get(i).getName());
			if (i != columns.size() - 1)
			{
				txtWriter.print(",");
			} else
			{
				txtWriter.print("\n");
			}
		}
	}

	private int valueEnd(PrintWriter txtWriter, StringBuffer value, int dataRows, String line)
	{
		value.append(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
		txtWriter.println(value);
		return ++dataRows;
	}

	private boolean isValueBegin(String line)
	{
		if (line.indexOf("<" + v + ">") >= 0 || line.indexOf("<" + v + "/>") >= 0)
		{
			this.context = this.v;
			return true;
		}
		return false;
	}

	private boolean isValueEnd(String line)
	{
		if (line.indexOf("</" + v + ">") >= 0)
		{
			this.context = "";
			return true;
		}
		return false;
	}

	private boolean isValuesEnd(String line)
	{
		if (line.indexOf("</" + values + ">") >= 0)
		{
			this.context = "";
			return true;
		}
		return false;
	}

	private boolean isValuesBegin(String line)
	{
		if (line.indexOf("<" + values + ">") >= 0)
		{
			this.context = this.values;
			return true;
		}
		return false;
	}

	private boolean isColumnsEnd(String line)
	{
		if (line.indexOf("</" + columns + ">") >= 0)
		{
			this.context = "";
			return true;
		}
		return false;
	}

	private boolean isColumnsBegin(String line)
	{
		if (line.indexOf("<" + columns + ">") >= 0)
		{
			this.context = this.columns;
			return true;
		}
		return false;
	}

	private boolean isTableEnd(String line)
	{
		if (line.indexOf("</" + table + ">") >= 0)
		{
			this.context = "";
			return true;
		}
		return false;
	}

	private boolean isTableBegin(String line)
	{
		if (line.indexOf("<" + table) >= 0)
		{
			this.context = this.table;
			return true;
		}
		return false;
	}

	private String context = "";

	private String table = "table";
	private String columns = "columns";
	private String values = "values";
	private String v = "v";
}
