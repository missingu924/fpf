package com.inspur.ftpparserframework.extend;

import java.io.File;

import com.inspur.ftpparserframework.log.obj.BasicInfo;

/**
 * �ļ�������Ϣ��ȡ�ӿ�
 * @author �����
 *
 */
public interface IBasicInfoParser
{
	/**
	 * ��ȡ�ļ�������Ϣ
	 * @param srcFile
	 * @return
	 */
	public BasicInfo parseBasicInfo(File srcFile) throws Exception;
}
