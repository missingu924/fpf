package com.inspur.ftpparserframework.transformator;

import java.io.File;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Ĭ���ļ�ת�������޾��������
 * @author �����
 *
 */
public class DefaultTransformator implements ITransformator
{
	Logger log = Logger.getLogger(getClass());
	
//	@Override
	public String getTransformatorName()
	{
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
	public String getDestFileSuffix()
	{
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
	public void transformat(File srcFile, File middleFile, File destFile, Map<String, Object> paramMap)
			throws Exception
	{
		log.info(srcFile.getCanonicalPath()+": ����Ĭ���ļ�ת�����������κδ���");
	}

}
