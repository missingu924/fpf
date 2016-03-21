package com.inspur.ftpparserframework.extend;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import com.inspur.ftpparserframework.transformator.ITransformator;
import com.inspur.ftpparserframework.transformator.TransformatUtil;
import com.inspur.ftpparserframework.util.StringUtil;

/**
 * 临时文件清理接口的默认实现。
 * <br/><br/>
 * 默认清理掉各Transformator生成的临时文件。<br/>
 * 默认清理掉生成已超过10秒的.tmp文件及.temp文件。
 * @author 武玉刚
 *
 */
public class DefaultTempFileCleaner implements ItempFileCleaner
{
	private Logger log = Logger.getLogger(getClass());

//	@Override
	public void clean(File inputDir) throws Exception
	{
		// 默认实现，删除.tmp或.temp后缀的文件。
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
								log.info("文件清理, 删除以" + suffix + "为后缀的文件, 删除[" + (deleteSucc ? "成功" : "失败") + "]："
										+ f.getCanonicalPath());
							}
						}
					}

					if ((f.getName().endsWith(".tmp") || f.getName().endsWith(".temp"))
							&& (System.currentTimeMillis() - f.lastModified()) > 10 * 1000l)
					{
						deleteSucc = f.delete();
						log.info("文件清理, 删除以.tmp或.temp为后缀的文件, 删除[" + (deleteSucc ? "成功" : "失败") + "]："
								+ f.getCanonicalPath());
					}
				}
			}
		}
	}
}
