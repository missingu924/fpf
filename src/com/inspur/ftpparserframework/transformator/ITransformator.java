package com.inspur.ftpparserframework.transformator;

import java.io.File;
import java.util.Map;

/**
 * 文件转换器接口
 * @author 武玉刚
 *
 */
public interface ITransformator
{
	
	/**
	 * 获取本转换器的名字
	 * @return
	 */
	public String getTransformatorName();
	
	/**
	 * 获取转换后文件的后缀
	 * @return
	 */
	public String getDestFileSuffix();
	
	/**
	 * 文件格式转换
	 * @param srcFile 原始文件
	 * @param middleFile 本次待处理的中间文件
	 * @param middleFile 本次待处理后的文件
	 * @param paramMap 参数传递Map
	 * @return
	 * @throws Exception
	 */
	public void transformat(File srcFile, File middleFile, File destFile, Map<String, Object> paramMap) throws Exception;
}
