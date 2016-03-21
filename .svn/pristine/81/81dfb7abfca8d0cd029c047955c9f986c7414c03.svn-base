package com.inspur.ftpparserframework.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 
 * @ClassName: XmlBranchMergeUtil
 * @Description: Xml文档拆分合并工具
 * @author lex
 * @date 2013-6-17 下午04:42:58
 * 
 */
public class XmlBranchMergeUtil
{
	private static Logger log = Logger.getLogger(XmlBranchMergeUtil.class);
	// public static String LINESEPARATOR = System.getProperty("line.separator");
	public static String LINESEPARATOR = java.security.AccessController
			.doPrivileged(new sun.security.action.GetPropertyAction("line.separator"));

	// branch
	private static String CONTEXT_PRELUDE = "prelude";
	private static String CONTEXT_DIV = "div";
	private static String CONTEXT_LISTDATA = "listdata";
	private static String CONTEXT_END = "end";

	/**
	 * 仅限于把xml文件中多次出现的某一个节点类型拆分到不同的文件中去，其他公用上下文内容不发生变化，每个子文件都包含。
	 * 
	 * @param file
	 * @param divTag
	 * @param divNum
	 * @param deep
	 * @return
	 * @throws IOException
	 */
	public static List<File> branchXml(File file, String divTag, int divNum, int deep) throws IOException
	{
		// 分割后的文件
		List<File> divedList = new ArrayList<File>();
		List<PrintWriter> PrintList = new ArrayList<PrintWriter>();
		int partNum = 0;
		int dataNum = 0;
		BufferedReader reader = null;
		int divTagDeep = 0;// 记录divTag节点深度(仅统计同名接点) lex 20130809
		try
		{
			if (divTag == null || divTag.equals("") || divNum < 1)
			{
				divedList.add(file);
			} else
			{
				log.info("分隔文件：" + file.getCanonicalPath() + "，分隔标识：" + divTag + "，分隔行数：" + divNum);
				String encode = FileUtil.getXmlFileEncode(file);
				log.info("被分割文件编码格式:" + encode);
				String context = CONTEXT_PRELUDE;
				// 记录前奏
				StringBuilder preSB = new StringBuilder();
				String line = null;
				// BufferedReader reader = new BufferedReader(new FileReader(file));
				// reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encode));
				while ((line = reader.readLine()) != null)
				{
					if ("".equals(line.trim()))
					{
						continue;
					}
					if(context.equals(CONTEXT_END) && (line.contains("<" + divTag + " ") || line.contains("<" + divTag + ">") || line.contains("<" + divTag + "\t")||line.endsWith("<" + divTag)))
					{
						context = CONTEXT_LISTDATA;
					}
					// 前奏
					if (context.equals(CONTEXT_PRELUDE))
					{
//						if (line.contains("<" + divTag + " ") || line.contains("<" + divTag + ">"))
						if (line.contains("<" + divTag + " ") || line.contains("<" + divTag + ">") || line.contains("<" + divTag + "\t")||line.endsWith("<" + divTag))
						{
							if (deep < 2 || (deep >= 2 && deep == divTagDeep + 1))
							{
								// 前奏->数据遍历
								context = CONTEXT_LISTDATA;
							} else
							{
								// 前奏继续
								preSB.append(line + LINESEPARATOR);
								divTagDeep++;
							}
						} else
						{// 前奏继续
							preSB.append(line + LINESEPARATOR);
						}
					}
					// 音乐响起
					if (context.equals(CONTEXT_LISTDATA))
					{// 数据遍历
//						if (line.contains("<" + divTag + " ") || line.contains("<" + divTag + ">"))
						if (line.contains("<" + divTag + " ") || line.contains("<" + divTag + ">") || line.contains("<" + divTag + "\t")||line.endsWith("<" + divTag))
						{// 继续遍历
							dataNum++;
							context = CONTEXT_DIV;
							if (dataNum > partNum * divNum)
							{
								partNum++;
								String fileCanonicalPath = file.getCanonicalPath();
								File partFile = new File(fileCanonicalPath.substring(0, fileCanonicalPath
										.lastIndexOf("."))
										+ "_part"
										+ partNum
										+ fileCanonicalPath.substring(fileCanonicalPath.lastIndexOf(".")));
								// PrintWriter partFilePrintWriter = new PrintWriter(partFile, "GBK");
								PrintWriter partFilePrintWriter = new PrintWriter(partFile, encode);
								divedList.add(partFile);
								PrintList.add(partFilePrintWriter);
								PrintList.get(PrintList.size() - 1).write(preSB.toString());
							}
							PrintList.get(PrintList.size() - 1).write(line + LINESEPARATOR);
						} else
						{// 数据遍历->尾声
							context = CONTEXT_END;
						}
					} else if (context.equals(CONTEXT_DIV))
					{// 具体数据
						PrintList.get(PrintList.size() - 1).write(line + LINESEPARATOR);
						if (line.contains("</" + divTag + ">"))
						{// 具体数据-> 数据遍历
							context = CONTEXT_LISTDATA;
						}
					}

					// 尾声
					if (context.equals(CONTEXT_END))
					{
						for (int i = 0; i < PrintList.size(); i++)
						{
							PrintList.get(i).write(line + LINESEPARATOR);
						}
					}
				}
			}

		} catch (IOException e)
		{
			throw e;
		} finally
		{
			for (int i = 0; i < PrintList.size(); i++)
			{
				PrintList.get(i).close();
			}
			if (reader != null)
			{
				reader.close();
			}
		}
		log.info("文件分隔数量：" + partNum + "，数据行数：" + dataNum);
		return divedList;
	}

	/**
	 * 仅限于把xml文件中多次出现的某一个节点类型拆分到不同的文件中去，其他公用上下文内容不发生变化，每个子文件都包含。
	 * 
	 * @param file
	 * @param divTag
	 * @param divNum
	 * @return
	 * @throws IOException
	 * 
	 */
	public static List<File> branchXml(File file, String divTag, int divNum) throws IOException
	{
		return branchXml(file, divTag, divNum, 0);
	}

	/**
	 * 文件合并，仅限于经过com.inspur.ftpparserframework.util.FPFXmlWriter美化生成的xml文件
	 * 
	 * @param mergeFile
	 * @param destFile
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws
	 */
	public static void mergeXml(List<File> mergeFile, File destFile) throws Exception
	{
		if (mergeFile == null || mergeFile.size() == 0)
		{
			// do nothing
		} else if (mergeFile.size() == 1)
		{
			mergeFile.get(0).renameTo(destFile);
		} else
		{
			String line = null;
			BufferedReader reader = null;
			PrintWriter destFilePrintWriter = null;
			try
			{
				destFilePrintWriter = new PrintWriter(destFile, "UTF-8");
				for (int i = 0; i < mergeFile.size(); i++)
				{
					File file = mergeFile.get(i);
					// reader = new BufferedReader(new FileReader(file));
					reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
					boolean inBulkData = false;
					while ((line = reader.readLine()) != null)
					{
						String lineTrim = line.trim();
						if (lineTrim.startsWith("<table ") || lineTrim.startsWith("<columns>")
								|| lineTrim.startsWith("<v>") || lineTrim.startsWith("</v>")
								|| lineTrim.startsWith("</columns>") || lineTrim.startsWith("</table>"))
						{// 数据区
							destFilePrintWriter.write(line + LINESEPARATOR);
						} else if (i == 0 && lineTrim.startsWith("<bulkDataFile"))
						{// 进入数据区
							inBulkData = true;
							destFilePrintWriter.write(line + LINESEPARATOR);
						} else if (i == 0 && inBulkData == false)
						{// 前奏
							destFilePrintWriter.write(line + LINESEPARATOR);
						} else if (i == mergeFile.size() && lineTrim.startsWith("</bulkDataFile>"))
						{
							inBulkData = false;
							destFilePrintWriter.write(line + LINESEPARATOR);
						} else if (i == mergeFile.size() && inBulkData == false)
						{// 尾声
							destFilePrintWriter.write(line + LINESEPARATOR);
						}
					}
				}
			} finally
			{
				if (destFilePrintWriter != null)
				{
					destFilePrintWriter.close();
				}
			}

		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// File file = new File(
		// "C:\\Users\\lex\\Desktop\\614\\PM201306141642+0800168B20130614.1615+0800-1630+0800_job55.xml");
		// try
		// {
		// XmlBranchMergeUtil.branchXml(file, "measData", 1);
		// } catch (IOException e)
		// {
		// e.printStackTrace();
		// }
		File file1 = new File(
				"C:\\Users\\lex\\Desktop\\test\\PM201306141642+0800168B20130614.1615+0800-1630+0800_job55.xml.unzip.pre.xml2xml");
		File file2 = new File(
				"C:\\Users\\lex\\Desktop\\test\\PM201306141642+0800168B20130614.1615+0800-1630+0800_job55.xml.unzip_part2.pre.xml2xml");
		try
		{
			Document doc1 = new SAXReader().read(file1);
			Document doc2 = new SAXReader().read(file2);
			Element elSub1 = (Element) doc1.selectSingleNode("//" + "table");
			Element elSub2 = (Element) doc2.selectSingleNode("//" + "table");
			System.out.println("asdf");

		} catch (DocumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
