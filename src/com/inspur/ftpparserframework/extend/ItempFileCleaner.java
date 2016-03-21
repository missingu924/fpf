package com.inspur.ftpparserframework.extend;

import java.io.File;

/**
 * 临时文件清理接口
 * @author 武玉刚
 *
 */
public interface ItempFileCleaner
{
	/**
	 * 清除解析过程中产生的临时文件，避免程序重启时一些过程文件被解析，引发错误
	 * @param file
	 * @throws Exception
	 */
	public void clean(File inputDir) throws Exception;
}
