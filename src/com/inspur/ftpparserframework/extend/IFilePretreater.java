package com.inspur.ftpparserframework.extend;

import java.io.File;
import java.util.Map;

/*
* �ļ�Ԥ����-�����ļ��������߳�֮ǰ���ã������ļ��ϲ���ȫ����Ԥ�������ӿ�
 * @author �����
 */
public interface IFilePretreater
{
	public static final String SUFFIX = "._pretreater";
	public File filePretreater(File file,Map<String, Object> threadSafeParamMap) throws Exception;
	public void setEnd() throws Exception;
	//add by lex 20140526 ���û���
	public void reset();

}
