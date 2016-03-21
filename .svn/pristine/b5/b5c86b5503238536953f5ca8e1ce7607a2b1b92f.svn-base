package com.inspur.ftpparserframework.transformator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;

import com.inspur.ftpparserframework.util.FileUtil;
import com.inspur.ftpparserframework.util.StringUtil;

/**
 * 文件解析转换器
 * 
 * @author 武玉刚
 * 
 */
public class GunzipTransformator implements ITransformator
{
	private Logger log = Logger.getLogger(getClass());

	// @Override
	public String getTransformatorName()
	{
		return "文件解压";
	}

	// @Override
	public String getDestFileSuffix()
	{
		return ".unzip";
	}

	// @Override
	public void transformat(File srcFile, File middleFile, File destFile, Map<String, Object> paramMap)
			throws Exception
	{
		try
		{
			if (middleFile.getName().endsWith(".gz"))
			{
				// 使用java自带的解压程序进行文件解压
				gunzipByJava(middleFile, destFile);
			} else if (middleFile.getName().endsWith(".zip"))
			{
				// 使用java自带的解压程序进行文件解压
				unzipByJava(middleFile, destFile);
			} else
			{
				log.debug("文件不是以.gz或.zip结尾的不解压.");
				FileUtil.copy(middleFile, destFile);
			}
		} catch (Exception e)
		{
			// 有的文件用java自带的解析代码解析报Not in GZIP format的错误，但是用linux的gunzip可以解。
			if ("Not in GZIP format".equals(e.getMessage()))
			{
				gunzipByNative(middleFile, destFile);
			} else
			{
				throw e;
			}
		}
	}

	public void unzipByJava(File zipFile, File destFile) throws Exception
	{
		FileInputStream fin = null;
		ZipInputStream zin = null;
		FileOutputStream fout = null;

		try
		{
			destFile.createNewFile();

			// 打开文件流
			fin = new FileInputStream(zipFile);
			zin = new ZipInputStream(fin);
			fout = new FileOutputStream(destFile);
			// add by lex 20130718 start
			ZipEntry entry;
			// 只处理压缩包中的第一个文件
			while ((entry = zin.getNextEntry()) != null)
			{
				if (entry.isDirectory())
				{
					// donothing
				} else
				{
					byte[] buf = new byte[1024];
					int num;
					while ((num = zin.read(buf, 0, buf.length)) != -1)
					{
						fout.write(buf, 0, num);
					}
					break;
				}

			}
			// add by lex 20130718 end
			// byte[] buf = new byte[1024];
			// int num;
			// while ((num = zin.read(buf, 0, buf.length)) != -1)
			// {
			// fout.write(buf, 0, num);
			// }
		} finally
		{
			// 三个文件流都必须关闭
			if (fin != null)
			{
				fin.close();
			}
			if (zin != null)
			{
				zin.close();
			}
			if (fout != null)
			{
				fout.close();
			}
		}
	}

	public void gunzipByJava(File gzipFile, File destFile) throws Exception
	{
		FileInputStream fin = null;
		GZIPInputStream gzin = null;
		FileOutputStream fout = null;

		try
		{
			destFile.createNewFile();

			// 打开文件流
			fin = new FileInputStream(gzipFile);
			gzin = new GZIPInputStream(fin);
			fout = new FileOutputStream(destFile);

			byte[] buf = new byte[1024];
			int num;
			while ((num = gzin.read(buf, 0, buf.length)) != -1)
			{
				fout.write(buf, 0, num);
			}
		} finally
		{
			// 三个文件流都必须关闭
			if (fin != null)
			{
				fin.close();
			}
			if (gzin != null)
			{
				gzin.close();
			}
			if (fout != null)
			{
				fout.close();
			}
		}
	}

	public void gunzipByNative(File gzipFile, File destFile) throws Exception
	{
		String errorMessage = "";

		File tmpGzFile = new File(destFile.getCanonicalPath() + ".gz"); // a.gz -> a.gz.unzip.gz

		String shell = "cp " + gzipFile.getCanonicalPath() + " " + tmpGzFile.getCanonicalPath(); // cp a.gz
																									// a.unzip.gz
		log.debug(shell);
		String exeResult;
		Process process = Runtime.getRuntime().exec(shell);
		BufferedReader out = new BufferedReader(new InputStreamReader(process.getInputStream()));
		BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		while ((exeResult = out.readLine()) != null)
		{
			if (exeResult.length() > 0)
			{
				// log.debug(exeResult);
			}
		}
		while ((exeResult = error.readLine()) != null)
		{
			if (exeResult.length() > 0)
			{
				errorMessage += exeResult;
			}
		}
		out.close();
		error.close();
		process.waitFor();

		shell = "gunzip -f " + tmpGzFile.getCanonicalPath(); // gunzip -f a.gz.unzip.gz
		log.debug(shell);
		process = Runtime.getRuntime().exec(shell);
		out = new BufferedReader(new InputStreamReader(process.getInputStream()));
		error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		while ((exeResult = out.readLine()) != null)
		{
			if (exeResult.length() > 0)
			{
				// log.debug(exeResult);
			}
		}
		while ((exeResult = error.readLine()) != null)
		{
			if (exeResult.length() > 0)
			{
				errorMessage += exeResult;
			}
		}
		out.close();
		error.close();
		process.waitFor();

		// 出错，抛出异常
		if (!StringUtil.isEmpty(errorMessage))
		{
			throw new Exception(errorMessage);
		}
	}

	public static void main(String[] args)
	{
		try
		{
			File s = new File("C:\\Users\\lex\\Desktop\\902\\TD-SCDMA_MRO_DATANG_OMCR_1281_20130704050000.zip");
			File d = new File("C:\\Users\\lex\\Desktop\\902\\TD-SCDMA_MRO_DATANG_OMCR_1281_20130704050000.zip.xml");
			GunzipTransformator t = new GunzipTransformator();
			t.transformat(s, s, d, null);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
