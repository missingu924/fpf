package com.inspur.ftpparserframework.transformator;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.inspur.ftpparserframework.dbloader.DbLoaderUtil;
import com.inspur.ftpparserframework.dbloader.obj.DbLoaderColumn;
import com.inspur.ftpparserframework.dbloader.obj.DbLoaderTable;
import com.inspur.ftpparserframework.exception.GetParameterException;
import com.inspur.ftpparserframework.transformator.obj.excel2DbMap.ColumnInfo;
import com.inspur.ftpparserframework.transformator.obj.excel2DbMap.ExcelInfo;
import com.inspur.ftpparserframework.transformator.obj.excel2DbMap.SheetInfo;
import com.inspur.ftpparserframework.util.Constant;
import com.inspur.ftpparserframework.util.StringUtil;
import com.inspur.ftpparserframework.util.XMLFactory;

public class Excel2DbTransformator implements ITransformator
{

	public static final String EXCEL2DBMAPFILE = "__excel2DbMapFile";
	private static Logger log = Logger.getLogger(Excel2DbTransformator.class);
	public static String LINESEPARATOR = java.security.AccessController
			.doPrivileged(new sun.security.action.GetPropertyAction("line.separator"));

	@Override
	public String getDestFileSuffix()
	{
		return "Excel2SimpleXml�ļ�ת��";
	}

	@Override
	public String getTransformatorName()
	{
		return ".excel2xml";
	}

	@Override
	public void transformat(File srcFile, File middleFile, File destFile, Map<String, Object> paramMap)
			throws Exception
	{
		// 1����ȡ�����ļ���Ϣ
		Object excel2DbMapFile = paramMap.get(EXCEL2DBMAPFILE);
		if (excel2DbMapFile == null)
		{
			throw new GetParameterException("�޷���ȡ" + EXCEL2DBMAPFILE + "����ֵ,�ò���ֵ����ָ��" + getTransformatorName()
					+ "�������ļ�,��Ӧ�Ĵ������ļ�Ϊ:" + middleFile.getCanonicalPath());
		}
		File xmlFile = new File(excel2DbMapFile.toString());
		if (!xmlFile.exists())
		{
			throw new GetParameterException("δ�ҵ�" + getTransformatorName() + "ת�������" + EXCEL2DBMAPFILE + "�ļ�:"
					+ xmlFile.getCanonicalPath());
		}

		log.info("Excel2SimpleXml�����ļ�" + middleFile.getCanonicalPath() + "��ʹ�õ������ļ�Ϊ" + xmlFile.getCanonicalPath());

		InputStream fis = null;
		XMLFactory excelInfoFactory = null;
		ExcelInfo excelInfo = null;

		InputStream is = null;
		Workbook wb = null;
		PrintWriter txtWriter = null;
		try
		{
			// 2�������ļ�ʵ����
			fis = new FileInputStream(xmlFile);
			excelInfoFactory = new XMLFactory(ExcelInfo.class);
			excelInfo = excelInfoFactory.unmarshal(fis);
			// 3��excel�ļ�ʵ����
			try
			{
				// xls��ʽ�ļ�
				is = new FileInputStream(middleFile);
				wb = new HSSFWorkbook(is);
			} catch (OfficeXmlFileException e)
			{// xlsx��ʽ�ļ�
				is.close();
				is = new FileInputStream(middleFile);
				wb = new XSSFWorkbook(is);
			}
			// 4����֯����
			int dataPartNum = 0;// ���ݷֲ����
			File dataFile = null;
			List<Integer> excelSheetIdx = new ArrayList<Integer>();// ��¼sheet����
			// 4.1��ȡ�����ļ���excel�ļ���sheetӳ���ϵ
			this.organizeSheetIdx(excelInfo, wb, xmlFile, srcFile, excelSheetIdx);
			List<SheetInfo> sheetInfos = excelInfo.getSheetInfos();
			// 4.2 ��excel�ļ�sheet��������
			for (int i = 0; i < excelSheetIdx.size(); i++)
			{
				SheetInfo sheetInfo = sheetInfos.get(i);
				int dataRowStartNum = sheetInfo.getDataRowStartNum();
				String tableName = sheetInfo.getTableName();
				Sheet sheet = wb.getSheetAt(excelSheetIdx.get(i));
				if (sheet != null && sheet.getLastRowNum() - dataRowStartNum > 0)
				{
					List<DbLoaderColumn> columns = new ArrayList<DbLoaderColumn>();
					StringBuffer columnsSB = new StringBuffer();
					// 4.2.1����֯��ͷ
					this.organizeColumn(sheetInfo, paramMap, columns, columnsSB);
					dataPartNum++;
					dataFile = new File(Constant.ORACLE_TXT + "/" + srcFile.getName() + "_" + tableName + ".part"
							+ new DecimalFormat("00000").format(dataPartNum) + ".txt");
					if (!dataFile.getParentFile().exists())
					{
						dataFile.getParentFile().mkdirs();
					}
					txtWriter = new PrintWriter(dataFile, "gb2312");
					// 4.2.2��д���ͷ
					txtWriter.write(columnsSB.toString() + LINESEPARATOR);
					// 4.2.3����֯������˳��
					List<Object[]> colIdx = new ArrayList<Object[]>();// ��¼�����
					this.organizeColIdx(sheetInfo, sheet, xmlFile, srcFile, i, colIdx);
					// 4.2.4����ʼд������
					for (int j = dataRowStartNum; j <= sheet.getLastRowNum(); j++)
					{
						Row row = sheet.getRow(j);
						StringBuffer sb=new StringBuffer();
						for (int k = 0; k < colIdx.size(); k++)
						{
							Cell cell = row.getCell((Integer)colIdx.get(k)[0]);
							
							// ���������ļ�����Date�ĵ�Ԫ������
							ColumnInfo column = (ColumnInfo)colIdx.get(k)[1];
							String cellType = column.getDbColType();
							
							if(cellType!=null){
								cellType = cellType.toLowerCase();
							}
							
							String value = "";
							if(cellType!=null && "date".equals(cellType)){
								String cellFormat = column.getDbColFormat();
								if(cellFormat==null){
									cellFormat = "yyyy/MM/dd HH:mm:ss";
								}
								if(cellFormat.length()>10){
									cellFormat = "yyyy/MM/dd HH:mm:ss";
								}
								Date dtValue=cell.getDateCellValue();
								SimpleDateFormat format = new SimpleDateFormat(cellFormat);
								value = format.format(dtValue);
							}else{
								 value=this.getValue(cell, (ColumnInfo)colIdx.get(k)[1]);
							}
						
							sb.append(value+",");
						}
						txtWriter.write(sb.substring(0, sb.length()-1)+LINESEPARATOR);
					}
					txtWriter.close();
					// 4.2.5�����
					DbLoaderUtil.load2db(srcFile, dataFile, new DbLoaderTable(tableName), columns);
				}
			}

		} catch (Exception e)
		{
			throw e;
		} finally
		{
			if (fis != null)
			{
				fis.close();
			}
			if (is != null)
			{
				is.close();
			}
			if (txtWriter != null)
			{
				txtWriter.close();
			}
		}
	}

	private String getValue(Cell cell, ColumnInfo columnInfo)
	{
		int cellType = cell.getCellType();
		String dbColType = columnInfo.getDbColType();
		if (cellType == Cell.CELL_TYPE_BLANK)
		{
			return "";
		} else if (dbColType != null && dbColType.trim().equalsIgnoreCase("date"))
		{
			
			if (cellType==Cell.CELL_TYPE_NUMERIC)
			{
				String dbColFormat = columnInfo.getDbColFormat();
				SimpleDateFormat sdf = new SimpleDateFormat(dbColFormat);
				return sdf.format(cell.getDateCellValue());
			} else
			{
				return cell.getStringCellValue();
			}
		}
		if (cellType == Cell.CELL_TYPE_BOOLEAN)
		{
			return String.valueOf(cell.getBooleanCellValue());
		} else if (cellType == Cell.CELL_TYPE_NUMERIC)
		{
			double numericCellValue=cell.getNumericCellValue();
			DecimalFormat df = new DecimalFormat();  
			df.setMaximumFractionDigits(10); // �������С��λ  
			df.setGroupingUsed(false);
			String result = df.format(numericCellValue); 
			return result;
		} else
		{
			return cell.getStringCellValue();
		}
	}

	/**
	 * ��ȡ�������
	 * 
	 * @param sheetInfo
	 * @param paramMap
	 * @param columns
	 * @param columnsStr
	 */
	private void organizeColumn(SheetInfo sheetInfo, Map<String, Object> paramMap, List<DbLoaderColumn> columns,
			StringBuffer columnsStr)
	{
		List<ColumnInfo> columnInfos = sheetInfo.getColumnInfos();
		for (int j = 0; j < columnInfos.size(); j++)
		{
			ColumnInfo columnInfo = columnInfos.get(j);
			String dbColName = columnInfo.getDbColName();// ����
			String dbColType = columnInfo.getDbColType();// ������
			String dbColFormat = columnInfo.getDbColFormat();// �и�ʽ
			String constantValue = columnInfo.getConstantValue();// ��̬����
			String columnStr = dbColName + ",";// �ֶζ�Ӧ�ı�ͷ
			DbLoaderColumn column = new DbLoaderColumn(dbColName);
			if (!StringUtil.isEmpty(dbColType))
			{
				column.setType(dbColType);
			}
			if (!StringUtil.isEmpty(dbColFormat))
			{
				column.setFormat(dbColFormat);
			}
			if (!StringUtil.isEmpty(constantValue))
			{
				constantValue = constantValue.trim();
				if (constantValue.startsWith("$") && constantValue.endsWith("$"))
				{// ͨ���������뾲̬����
					constantValue = (String) paramMap.get(constantValue.substring(1, constantValue.length() - 1));
				}
				column.setConstantValue(constantValue);
				columnStr = "";// �ֶζ�Ӧ�ı�ͷ����ȡ��ֵ̬ʱ�����������ݱ�ͷ��ʾ
			}
			columns.add(column);
			columnsStr.append(columnStr);
		}
	}

	/**
	 * ��֯excel�ж�Ӧ��sheet˳��
	 * @param excelInfo
	 * @param wb
	 * @param xmlFile
	 * @param srcFile
	 * @param excelSheetIdx
	 * @throws Exception
	 */
	private void organizeSheetIdx(ExcelInfo excelInfo, Workbook wb, File xmlFile, File srcFile,
			List<Integer> excelSheetIdx) throws Exception
	{
		String sheetIdentifiedBy = excelInfo.getIdentifiedBy();
		List<SheetInfo> sheetInfos = excelInfo.getSheetInfos();
		if (sheetIdentifiedBy != null && sheetIdentifiedBy.trim().equals("name"))
		{// ����sheetҳ����������
			for (int i = 0; i < sheetInfos.size(); i++)
			{
				String sheetName = sheetInfos.get(i).getName();
				if (StringUtil.isEmpty(sheetName))
				{
					throw new Exception("Excel2Db�����ļ����������ļ�" + xmlFile.getCanonicalPath() + "����" + i
							+ "��sheet������name����Ϊ��");
				}
				int sheetIndex = wb.getSheetIndex(sheetName.trim());
				if (sheetIndex > -1)
				{
					excelSheetIdx.add(sheetIndex);
				} else
				{
					throw new Exception("δ�ҵ������ļ�" + xmlFile.getCanonicalPath() + "����" + i + "��sheet(name=\""
							+ sheetName + "\")��Ӧexcel�е�sheet��Դ�ļ�Ϊ��" + srcFile.getCanonicalPath());
				}

			}
		} else
		{
			for (int i = 0; i < sheetInfos.size(); i++)
			{
				int sheetidx = sheetInfos.get(i).getIdx();
				try
				{
					wb.getSheetAt(sheetidx);
					excelSheetIdx.add(sheetidx);
				} catch (IllegalArgumentException e)
				{
					if (e.getMessage().startsWith("Sheet index ("))
					{
						e.printStackTrace();
						throw new Exception("δ�ҵ������ļ�" + xmlFile.getCanonicalPath() + "����" + i + "��sheet(idx=\""
								+ sheetidx + "\")��Ӧexcel�е�sheet��Դ�ļ�Ϊ��" + srcFile.getCanonicalPath());
					} else
					{
						throw e;
					}
				}
			}

		}

	}

	/**
	 * ��֯excel�ж�Ӧ����˳��
	 * @param sheetInfo
	 * @param sheet
	 * @param xmlFile
	 * @param srcFile
	 * @param sheetInfoIdx
	 * @param colIdx
	 * @throws Exception
	 */
	private void organizeColIdx(SheetInfo sheetInfo, Sheet sheet, File xmlFile, File srcFile, int sheetInfoIdx,
			List<Object[]> colIdx) throws Exception
	{
		String coldentifiedBy = sheetInfo.getIdentifiedBy();
		List<ColumnInfo> columnInfos = sheetInfo.getColumnInfos();
		int titleRowNum = sheetInfo.getTitleRowNum();
		Row titleRow = sheet.getRow(titleRowNum);
		int lastCellNum = titleRow.getLastCellNum();
		if (coldentifiedBy != null && coldentifiedBy.equals("field"))
		{// ������ͷ׼��˳��
			for (int j = 0; j < columnInfos.size(); j++)
			{
				ColumnInfo columnInfo = columnInfos.get(j);
				String field = columnInfo.getField();
				String constantValue=columnInfo.getConstantValue();
				if (!StringUtil.isEmpty(constantValue))
				{
					continue;
				}
				if (StringUtil.isEmpty(field))
				{
					throw new Exception("Excel2Db�����ļ����������ļ�" + xmlFile.getCanonicalPath() + "����" + sheetInfoIdx
							+ "��sheet��" + j + "��column������field����Ϊ��");
				}
				int k = 0;
				for (; k < lastCellNum; k++)
				{
					Cell cell = titleRow.getCell(k);
					String cellValue = cell.getStringCellValue();
					if (cellValue.trim().equalsIgnoreCase(field.trim()))
					{
//						colIdx.add(columnInfos.get(j).getIdx());
						Object[] col={columnInfos.get(j).getIdx(),columnInfo};
						colIdx.add(col);
						break;
					}
				}
				if (k == lastCellNum)
				{// δ�ҵ���Ӧ�ֶΣ��׳�����
					throw new Exception("δ�ҵ������ļ�" + xmlFile.getCanonicalPath() + "����" + sheetInfoIdx + "��sheet��" + j
							+ "��column������field(" + field + ")��Ӧ�С�Դ�ļ�Ϊ��" + srcFile.getCanonicalPath());
				}
			}
		} else
		{// ������˳��׼��˳��
			for (int j = 0; j < columnInfos.size(); j++)
			{
				ColumnInfo columnInfo=columnInfos.get(j);
				int idx = columnInfo.getIdx();
				String constantValue=columnInfo.getConstantValue();
				if (!StringUtil.isEmpty(constantValue))
				{
					continue;
				}
				if (idx > lastCellNum)
				{
					throw new Exception("δ�ҵ������ļ�" + xmlFile.getCanonicalPath() + "����" + sheetInfoIdx + "��sheet��" + j
							+ "��column������idx(" + idx + ")��Ӧ�С�Դ�ļ�Ϊ��" + srcFile.getCanonicalPath());
				} else
				{
//					colIdx.add(columnInfos.get(j).getIdx());
					Object[] col={columnInfos.get(j).getIdx(),columnInfo};
					colIdx.add(col);
				}
			}
		}
	}

	public static void main(String[] args)
	{
		File dataInfoFile = new File("C:\\Users\\lex\\Desktop\\725\\Desktop\\�ҷ�2013-07-23���&����.xlsx");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(Excel2DbTransformator.EXCEL2DBMAPFILE, "C:\\Users\\lex\\Desktop\\731\\Excel2DbMp.xml");
		Excel2DbTransformator testTransformator = new Excel2DbTransformator();
		try
		{
			testTransformator.transformat(dataInfoFile, dataInfoFile, dataInfoFile, paramMap);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}