package com.inspur.ftpparserframework.extend;

import java.io.File;

import org.apache.log4j.Logger;

import com.inspur.ftpparserframework.log.obj.BasicInfo;

/**
 * �ļ�������Ϣ��ȡ�ӿڵ�Ĭ��ʵ�֣������κδ�����
 * @author �����
 *
 */
public class DefaultBasicInfoParser implements IBasicInfoParser
{
	private Logger log = Logger.getLogger(this.getClass());


//	@Override
	public BasicInfo parseBasicInfo(File srcFile) throws Exception
	{
		// Ĭ��ʵ�֣����κ�ҵ���߼���
		log.debug(srcFile.getCanonicalPath() + " ʹ��Ĭ�ϵĻ�����Ϣ�������򣬲����κδ�����");
		
		return new BasicInfo();
	}

}