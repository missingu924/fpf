package com.inspur.ftpparserframework.test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.inspur.ftpparserframework.transformator.Csv2DbTransformator;
import com.inspur.ftpparserframework.transformator.TransformatEngine;
import com.inspur.ftpparserframework.util.Constant;

public class TransformatorTest
{

	public static void main(String[] args)
	{
		try
		{
			TransformatEngine engine =new TransformatEngine();
//			engine.getTransformatorClz().add(GunzipTransformator.class.getName());
//			engine.getTransformatorClz().add(Xml2SimpleXmlTransformator.class.getName());		
//			engine.getTransformatorClz().add(SimpleXml2DbTransformator.class.getName());	
//			engine.getTransformatorClz().add(Csv2DbTransformator.class.getName());

			
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("VENDOR_NAME", "ZTE");
			paramMap.put("debug", "true");
			
			
			paramMap.put(Csv2DbTransformator.csv2dbmap, "Csv2DbMap.xml");
			paramMap.put(Csv2DbTransformator.csv2dbtable, "TD_MR_PccpchRscp");
			File srcFile = new File(Constant.DATA_INPUT_DIR+"/test/csv/TD-SCDMA_MRS_ZTE_OMCR1_1798_20121122190000.xml.gz_Td_Mr_PccpchRscp.txt");

			
//			paramMap.put(Xml2SimpleXmlTransformator.xslt, "D:/workspace1/td-mr-xml/conf/TD-MRS-ZTE-NEW.xslt");
//			File srcFile = new File(
//					"D:/workspace1/td-mr-xml/data/input/ZTE/TD-SCDMA_MRS_ZTE_OMCR1_1798_20121122190000.xml");
			
//			File srcFile = new File(
//					"/home/oracle/wuyg/td-mr-xml/data/input/JN_ZTE_OMC/ZTE/TD-SCDMA_MRS_ZTE_OMCR1_1798_20121122190000.xml.gz");
//			paramMap.put(Xml2SimpleXmlTransformator.xslt, "/home/oracle/wuyg/td-mr-xml/conf/TD-MRS-ZTE-NEW.xslt");
			

			engine.transformat(srcFile, paramMap);
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
