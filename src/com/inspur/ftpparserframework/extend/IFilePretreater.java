package com.inspur.ftpparserframework.extend;

import java.io.File;
import java.util.Map;

/*
* 文件预处理-进入文件解析多线程之前调用，用于文件合并等全局性预处理动作接口
 * @author 徐恩龙
 */
public interface IFilePretreater
{
	public static final String SUFFIX = "._pretreater";
	public File filePretreater(File file,Map<String, Object> threadSafeParamMap) throws Exception;
	public void setEnd() throws Exception;
	//add by lex 20140526 重置缓存
	public void reset();

}
