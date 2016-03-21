package com.inspur.ftpparserframework.transformator;

import java.io.File;
import java.util.Map;

import com.inspur.ftpparserframework.dbloader.DbLoaderUtil;
import com.inspur.ftpparserframework.dbloader.obj.DbLoaderTable;

/**
 * TXT入库到GBase
 * @author 郭启刚
 *
 */
public class Txt2GbaseTransformator implements ITransformator
{
	public static final String txt2gbasetable = "txt2gbasetable";

//	@Override
	public String getDestFileSuffix()
	{
		return "";
	}

//	@Override
	public String getTransformatorName()
	{
		return "Txt2Gbase";
	}

//	@Override
	public void transformat(File srcFile, File middleFile, File destFile, Map<String, Object> paramMap)
			throws Exception
	{
		// 1、构造表
		String tableName = (String)paramMap.get(txt2gbasetable);
		
		DbLoaderTable table = new DbLoaderTable(tableName);
		
		// 2、入库
		DbLoaderUtil.load2db(srcFile, middleFile, table, null);		
	}

}
