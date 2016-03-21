package com.inspur.ftpparserframework.extend;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * 文件解析过滤接口的默认实现，不做任何处理。
 * @author 武玉刚
 *
 */
public class DefaultFileFilter implements IFileFilter
{
	private Logger log=Logger.getLogger(getClass());
	
//	@Override
	public boolean shouldParseExtend(File file) throws Exception
	{
		// 默认实现，无任何业务逻辑。
		log.info(file.getCanonicalPath()+" 使用默认文件过滤程序，不做任何处理。");		
		return true;
	}
}
