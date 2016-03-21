package com.inspur.ftpparserframework.transformator;

import java.util.ArrayList;
import java.util.List;

import com.inspur.ftpparserframework.config.ConfigReader;
import com.inspur.ftpparserframework.util.StringUtil;

/**
 * 文件转换器工具类
 * @author 武玉刚
 *
 */
public class TransformatUtil
{
	/**
	 * 从SystemConfig.xml配置文件中获取extend.transformators并返回所有的实现类的实例
	 * @return
	 * @throws Exception
	 */
	public static List<ITransformator> getAllTransformators() throws Exception
	{
		List<ITransformator> transList = new ArrayList<ITransformator>();

		String transformatorsClz = ConfigReader.getProperties("extend.transformators");

		List<String> clzList = StringUtil.getStringListByString(transformatorsClz);

		for (int i = 0; i < clzList.size(); i++)
		{
			ITransformator trans = (ITransformator) Thread.currentThread().getContextClassLoader()
					.loadClass(clzList.get(i).trim()).newInstance();
			transList.add(trans);
		}

		return transList;
	}
}
