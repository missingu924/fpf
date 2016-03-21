package com.inspur.ftpparserframework.extend;

import java.io.File;

/**
 * 文件解析过滤接口
 * @author 武玉刚
 *
 */
public interface IFileFilter
{
	/**
	 * 判断文件是否需要解析。
	 * 框架已判断条件为：已经解析过的不重复解析、文件生成10秒后才解析；
	 * 外部程序如果需要增加新的条件，则可以通过实现shouldParseExtend方法来增加限制条件。
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public boolean shouldParseExtend(File file) throws Exception;
}
