package com.inspur.ftpparserframework.transformator;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;

import com.inspur.ftpparserframework.exception.GetParameterException;
import com.inspur.ftpparserframework.util.Constant;
import com.inspur.ftpparserframework.util.FPFXmlWriter;
import com.inspur.ftpparserframework.util.XmlBranchMergeUtil;

public class Xml2SimpleXmlTransformatorPro extends Xml2SimpleXmlTransformator
{
	private static Logger log = Logger.getLogger(Xml2SimpleXmlTransformatorPro.class);

	@Override
	public void transformat(File srcFile, File middleFile, File destFile, Map<String, Object> paramMap)
			throws Exception
	{
		PrintWriter destFileWriter = null;
		FPFXmlWriter xmlWriter = null;// 使用自己定义的xmlwriter类

		// 1、按xslt解析原始的xml文件
		Object xsltFileObj = paramMap.get(xslt);
		if (paramMap.get(xslt) == null)
		{
			throw new GetParameterException("无法获取" + xslt + "参数值,该参数值用于指定" + getTransformatorName()
					+ "的配置文件,对应的待处理文件为:" + middleFile.getCanonicalPath());
		}
		File xsltFile = new File(xsltFileObj.toString());
		if (!xsltFile.exists())
		{
			throw new GetParameterException("未找到" + getTransformatorName() + "转换所需的" + xslt + "文件:"
					+ xsltFile.getCanonicalPath());
		}

		log.info("Xml2SimpleXml解析文件" + middleFile.getCanonicalPath() + "所使用的配置文件为" + xsltFile.getCanonicalPath());

		// 1.1拆分文件
		List<File> divedList = new ArrayList<File>();
		Object divTag = paramMap.get(Constant.PARAM_DIVTAG);
		if (divTag == null)
		{// 不拆分
			divedList.add(middleFile);
		} else
		{// 拆分
			Object divNum = paramMap.get(Constant.PARAM_DIVNUM);
			Object paramDivTagDeep = paramMap.get(Constant.PARAM_DIVTAG_DEEP);
			int divTagDeep = 0;
			if (paramDivTagDeep != null)
			{
				divTagDeep = Integer.parseInt(paramDivTagDeep.toString());
			}
			if (divNum == null)
			{
				throw new Exception(Constant.PARAM_DIVTAG + "和" + Constant.PARAM_DIVNUM + "需同时定义");
			} else
			{
				divedList = XmlBranchMergeUtil.branchXml(middleFile, divTag.toString(), Integer.parseInt(divNum
						.toString()), divTagDeep);
			}
		}
		List<File> destFileList = new ArrayList<File>();
		// 1.2解析文件
		for (int i = 0; i < divedList.size(); i++)
		{
			try
			{
				File destPartFile = new File(divedList.get(i).getCanonicalPath() + ".xml2xml");
				SAXReader reader = new SAXReader();
				Document document = reader.read(divedList.get(i));
				document = styleDocument(document, xsltFile, paramMap);
				// 美化格式
				destFileWriter = new PrintWriter(destPartFile, "UTF-8");
				OutputFormat format = OutputFormat.createPrettyPrint();
				xmlWriter = new FPFXmlWriter(destFileWriter, format);
				xmlWriter.setAddSpaceWhileTrimText(false);// xml文件中多行数据合并时不增加空格进行间隔。
				format.setEncoding("UTF-8");
				xmlWriter.write(document);
				destFileList.add(destPartFile);
			} finally
			{
				if (destFileWriter != null)
				{
					destFileWriter.close();
				}
				if (xmlWriter != null)
				{
					xmlWriter.close();
				}
			}

		}
		// 1.3合并解析后的文件
		destFile = new File(middleFile.getCanonicalPath() + ".xml2xml");
		if (destFileList.size() == 1)
		{
			destFileList.get(0).renameTo(destFile);
		} else
		{
			// 合并合并
			XmlBranchMergeUtil.mergeXml(destFileList, destFile);
		}

	}

}
