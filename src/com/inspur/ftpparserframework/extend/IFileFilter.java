package com.inspur.ftpparserframework.extend;

import java.io.File;

/**
 * �ļ��������˽ӿ�
 * @author �����
 *
 */
public interface IFileFilter
{
	/**
	 * �ж��ļ��Ƿ���Ҫ������
	 * ������ж�����Ϊ���Ѿ��������Ĳ��ظ��������ļ�����10���Ž�����
	 * �ⲿ���������Ҫ�����µ������������ͨ��ʵ��shouldParseExtend��������������������
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public boolean shouldParseExtend(File file) throws Exception;
}
