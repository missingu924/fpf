package com.inspur.ftpparserframework.transformator;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;

import com.inspur.ftpparserframework.exception.GetParameterException;
import com.inspur.ftpparserframework.util.FPFXmlWriter;

/**
 * 复杂格式的Xml文件转换为简单格式(规定好的格式)的Xml文件
 * 
 * @author 武玉刚
 * 
 */
public class Xml2SimpleXmlTransformator implements ITransformator
{
	public static final String xslt = "xslt";

	private static Logger log = Logger.getLogger(Xml2SimpleXmlTransformator.class);

	// @Override
	public String getTransformatorName()
	{
		return "Xml2SimpleXml文件转换";
	}

	// @Override
	public String getDestFileSuffix()
	{
		return ".xml2xml";
	}

	// @Override
	public void transformat(File srcFile, File middleFile, File destFile, Map<String, Object> paramMap)
			throws Exception
	{

		PrintWriter destFileWriter = null;
		FPFXmlWriter xmlWriter = null;// 使用自己定义的xmlwriter类
		try
		{
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

			destFile = new File(middleFile.getCanonicalPath() + ".xml2xml");
			SAXReader reader = new SAXReader();
			Document document = reader.read(middleFile);
			document = styleDocument(document, xsltFile, paramMap);

			// 2、 美化格式
			destFileWriter = new PrintWriter(destFile, "UTF-8");
			OutputFormat format = OutputFormat.createPrettyPrint();
			xmlWriter = new FPFXmlWriter(destFileWriter, format);
			xmlWriter.setAddSpaceWhileTrimText(false);// xml文件中多行数据合并时不增加空格进行间隔。
			format.setEncoding("UTF-8");
			xmlWriter.write(document);
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

	protected static Document styleDocument(Document document, File stylesheet, Map<String, Object> paramMap)
			throws Exception
	{
		// load the transformer using JAXP
		// TransformerFactory factory = TransformerFactory.newInstance();
		// Transformer transformer = factory.newTransformer(new StreamSource(stylesheet));
		Transformer transformer = getTransformer(stylesheet);

		// now lets style the given document
		DocumentSource source = new DocumentSource(document);
		DocumentResult result = new DocumentResult();

		if (paramMap != null && paramMap.size() > 0)
		{
			Set<Entry<String, Object>> entrySet = paramMap.entrySet();
			for (Entry<String, Object> entry : entrySet)
			{
				transformer.setParameter(entry.getKey(), entry.getValue());
			}
		}
		transformer.transform(source, result);

		// return the transformed document
		Document transformedDoc = result.getDocument();
		return transformedDoc;
	}

	private static Map<String, List<Object>> cachedXsltMap = new HashMap<String, List<Object>>();

	private static Transformer getTransformer(File stylesheet) throws TransformerConfigurationException, IOException
	{
		// synchronized
		synchronized (cachedXsltMap)
		{
			String key = stylesheet.getCanonicalPath();
			long lastModified = stylesheet.lastModified();
			if (cachedXsltMap.containsKey(key) && cachedXsltMap.get(key).size() > 1
					&& (Long) cachedXsltMap.get(key).get(0) == lastModified)
			{
				// do nothing
			} else
			{
				// load the transformer using JAXP
				log.info("重新加载转换文件:" + stylesheet.getCanonicalPath());
				Source xsltSource = new StreamSource(stylesheet);
				TransformerFactory factory = TransformerFactory.newInstance();
				Templates cachedXSLT = factory.newTemplates(xsltSource);
				List<Object> list = new ArrayList<Object>();
				list.add(lastModified);
				list.add(cachedXSLT);
				cachedXsltMap.put(key, list);
			}
			return ((Templates) cachedXsltMap.get(key).get(1)).newTransformer();
		}

	}
}
