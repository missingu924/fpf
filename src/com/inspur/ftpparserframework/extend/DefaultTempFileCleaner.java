package com.inspur.ftpparserframework.extend;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import com.inspur.ftpparserframework.transformator.ITransformator;
import com.inspur.ftpparserframework.transformator.TransformatUtil;
import com.inspur.ftpparserframework.util.StringUtil;

/**
 * ��ʱ�ļ�����ӿڵ�Ĭ��ʵ�֡�
 * <br/><br/>
 * Ĭ���������Transformator���ɵ���ʱ�ļ���<br/>
 * Ĭ������������ѳ���10���.tmp�ļ���.temp�ļ���
 * @author �����
 *
 */
public class DefaultTempFileCleaner implements ItempFileCleaner
{
	private Logger log = Logger.getLogger(getClass());

//	@Override
	public void clean(File inputDir) throws Exception
	{
		// Ĭ��ʵ�֣�ɾ��.tmp��.temp��׺���ļ���
		File[] files = inputDir.listFiles();
		if (files != null)
		{
			for (int i = 0; i < files.length; i++)
			{
				if (files[i].isDirectory())
				{
					clean(files[i]);
				} else
				{
					File f = files[i];
					boolean deleteSucc = false;

					List<ITransformator> transList = TransformatUtil.getAllTransformators();
					for (int j = 0; j < transList.size(); j++)
					{
						String suffix = transList.get(j).getDestFileSuffix();
						if (!StringUtil.isEmpty(suffix))
						{
							if (f.getName().endsWith(suffix))
							{
								deleteSucc = f.delete();
								log.info("�ļ�����, ɾ����" + suffix + "Ϊ��׺���ļ�, ɾ��[" + (deleteSucc ? "�ɹ�" : "ʧ��") + "]��"
										+ f.getCanonicalPath());
							}
						}
					}

					if ((f.getName().endsWith(".tmp") || f.getName().endsWith(".temp"))
							&& (System.currentTimeMillis() - f.lastModified()) > 10 * 1000l)
					{
						deleteSucc = f.delete();
						log.info("�ļ�����, ɾ����.tmp��.tempΪ��׺���ļ�, ɾ��[" + (deleteSucc ? "�ɹ�" : "ʧ��") + "]��"
								+ f.getCanonicalPath());
					}
				}
			}
		}
	}
}
