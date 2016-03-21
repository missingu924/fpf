package com.inspur.ftpparserframework.transformator;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.inspur.ftpparserframework.util.StringUtil;
import com.inspur.ftpparserframework.util.TimeUtil;

/**
 * 文件转换器运行引擎
 * 
 * @author 武玉刚
 * 
 */
public class TransformatEngine
{
	private Logger log = Logger.getLogger(getClass());

	/**
	 * 文件转换处理
	 * 
	 * @param srcFile
	 *            源文件，指的是最原始的文件。
	 * @param paramMap
	 *            参数传递Map
	 * @throws Exception
	 */
	public void transformat(File srcFile, Map<String, Object> paramMap) throws Exception
	{
		// 定义转换的中间文件
		File middleFile = srcFile;
		File destFile = null;

		List<ITransformator> transList = TransformatUtil.getAllTransformators();

		for (int i = 0; i < transList.size(); i++)
		{
			// 获取文件转换器
			ITransformator transformator = transList.get(i);

			// 日志基本信息
			String transformatorName = transformator.getTransformatorName();
			String fileInfo = "[源文件=" + srcFile.getCanonicalPath() + ",大小=" + srcFile.length() + "Byte,时间="
					+ TimeUtil.date2str(srcFile.lastModified()) + "]-[待处理文件=" + middleFile.getCanonicalPath() + ",大小="
					+ middleFile.length() + "Byte,时间=" + TimeUtil.date2str(middleFile.lastModified()) + "]";
			long startTime = System.currentTimeMillis();

			try
			{
				// 定义转换后的文件，对于destFile的后最为空的转换器，则无需生成destFile
				if (!StringUtil.isEmpty(transformator.getDestFileSuffix()))
				{
					destFile = new File(middleFile.getCanonicalPath() + transformator.getDestFileSuffix());
				} else
				{
					destFile = middleFile;
				}

				// 日志格式：#文件转换#-[环节=xx]-[开始]-[源文件=xx,大小=xxByte,时间=yyyy-MM-dd HH:mm:ss][待处理文件=yy,大小=yyByte,时间=yyyy-MM-dd HH:mm:ss]
				log.info("#文件转换#-[环节=" + transformatorName + "]-[开始]-" + fileInfo);

				// 执行文件转换
				transformator.transformat(srcFile, middleFile, destFile, paramMap);

				// 日志格式：#文件转换#-[环节=xx]-[结束]-[源文件=xx,大小=xxByte,时间=yyyy-MM-dd HH:mm:ss]-[待处理文件=yy,大小=xxByte,时间=yyyy-MM-dd HH:mm:ss]-[处理后文件=zz,大小=zzByte,时间=yyyy-MM-dd HH:mm:ss]-[耗时=n毫秒]
				log.info("#文件转换#-[环节=" + transformatorName + "]-[结束]-" + fileInfo + "-[处理后文件="
						+ destFile.getCanonicalPath() + ",大小=" + destFile.length() + "Byte,时间="
						+ TimeUtil.date2str(destFile.lastModified()) + "]-[耗时="
						+ (System.currentTimeMillis() - startTime) + "毫秒]");
			} catch (Exception e)
			{
				// 日志格式：#文件转换#-[环节=xx]-[出错]-[源文件=xx,大小=xxByte,时间=yyyy-MM-dd HH:mm:ss]-[待处理文件=yy,大小=xxByte,时间=yyyy-MM-dd HH:mm:ss]-[错误信息=xx]
				log.error("#文件转换#-[环节=" + transformatorName + "]-[出错]-" + fileInfo + "-[耗时="
						+ (System.currentTimeMillis() - startTime) + "毫秒]-[错误信息=" + e.getMessage() + "]");
				throw e;
			} finally
			{
				Object debugFlagObj = paramMap.get("debug");
				if (debugFlagObj != null && "Y".equalsIgnoreCase(debugFlagObj.toString()))
				{
					log.debug("DEBUG状态，不删除中间过程产生的文件。");
				} else
				{
					// middleFile默认应该删除，除非middleFile==srcFile或者middleFile==destFile
					if (!middleFile.getCanonicalPath().equals(srcFile.getCanonicalPath())
							&& !middleFile.getCanonicalPath().equals(destFile.getCanonicalPath()))
					{
						log.debug("刪除过程文件:" + middleFile.getCanonicalPath());
						middleFile.delete();
					}

					// destFile默认不应该删除，所有转换都结束后，删除最终的目的文件；中间过程中不能删除目的文件，因为此时的目的文件是下一个环节的输入
					if (i == transList.size() - 1 && destFile != null && destFile.exists() && transList.size() != 1)
					{
						log.debug("刪除目的文件:" + destFile.getCanonicalPath());
						destFile.delete();
					}
				}

				// 转换完成后中间文件变更
				middleFile = destFile;
			}
		}
	}
}
