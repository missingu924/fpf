package com.inspur.ftpparserframework.transformator;

import java.io.File;
import java.util.Map;

/**
 * �ļ�ת�����ӿ�
 * @author �����
 *
 */
public interface ITransformator
{
	
	/**
	 * ��ȡ��ת����������
	 * @return
	 */
	public String getTransformatorName();
	
	/**
	 * ��ȡת�����ļ��ĺ�׺
	 * @return
	 */
	public String getDestFileSuffix();
	
	/**
	 * �ļ���ʽת��
	 * @param srcFile ԭʼ�ļ�
	 * @param middleFile ���δ�������м��ļ�
	 * @param middleFile ���δ��������ļ�
	 * @param paramMap ��������Map
	 * @return
	 * @throws Exception
	 */
	public void transformat(File srcFile, File middleFile, File destFile, Map<String, Object> paramMap) throws Exception;
}
