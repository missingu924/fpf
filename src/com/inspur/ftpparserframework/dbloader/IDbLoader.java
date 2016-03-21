package com.inspur.ftpparserframework.dbloader;

import java.io.File;
import java.util.List;

import com.inspur.ftpparserframework.dbloader.obj.DbLoaderColumn;
import com.inspur.ftpparserframework.dbloader.obj.DbLoaderDatabase;
import com.inspur.ftpparserframework.dbloader.obj.DbLoaderTable;

/**
 * 数据入库接口
 * @author 武玉刚
 *
 */
public interface IDbLoader
{
	public static final String DEFAULT_LOAD_MODE = "APPEND";
	public static final String DEFAULT_FIELDS_TERMINATED_BY = ",";
	public static final String DEFAULT_OPTIONALLY_ENCLOSED_BY = "\"";
	/**
	 * 数据加载入库。数据加载模式、数据文件分割字符、可选闭包字符采用默认值，具体默认值由实现类定义。
	 * 
	 * @param srcFile
	 *            最原始的文件
	 * @param dataFile
	 *            数据文件
	 * @param db
	 *            数据库信息
	 * @param table
	 *            表信息
	 * @param columns
	 *            列信息
	 * @throws Exception
	 */
	public void load(File srcFile, File dataFile, DbLoaderDatabase db, DbLoaderTable table, List<DbLoaderColumn> columns) throws Exception;

	/**
	 * 数据加载入库。数据文件分割字符、可选闭包字符采用默认值，具体默认值由实现类定义。
	 * 
	 * @param srcFile
	 *            最原始的文件
	 * @param dataFile
	 *            数据文件
	 * @param db
	 *            数据库信息
	 * @param table
	 *            表信息
	 * @param columns
	 *            列信息
	 * @param loadMode
	 *            加载模式
	 * @throws Exception
	 */
	public void load(File srcFile, File dataFile, DbLoaderDatabase db, DbLoaderTable table, List<DbLoaderColumn> columns, String loadMode)
			throws Exception;

	/**
	 * 数据加载入库
	 * 
	 * @param srcFile
	 *            最原始的文件
	 * @param dataFile
	 *            数据文件
	 * @param db
	 *            数据库信息
	 * @param table
	 *            表信息
	 * @param columns
	 *            列信息
	 * @param loadMode
	 *            加载模式
	 * @param fieldsTeminatedBy
	 *            数据文件分割字符
	 * @param optionallyEnclosedBy
	 *            可选闭包字符
	 * @throws Exception
	 */
	public void load(File srcFile, File dataFile, DbLoaderDatabase db, DbLoaderTable table, List<DbLoaderColumn> columns, String loadMode,
			String fieldsTeminatedBy, String optionallyEnclosedBy) throws Exception;
}
