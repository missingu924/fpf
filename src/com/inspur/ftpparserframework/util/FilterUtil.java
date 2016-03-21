package com.inspur.ftpparserframework.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.inspur.ftpparserframework.config.ConfigReader;
import com.inspur.ftpparserframework.extend.IFileFilter;

/**
 * ������������
 * 
 * @author �����
 * 
 */
public class FilterUtil
{
	private static Logger log = Logger.getLogger(FilterUtil.class);
	public static final String PREFIX_PARSE_SUCC = "parse_succ";// ��¼�����ɹ����ļ�
	public static final String PREFIX_PARSE_FAIL = "parse_fail";// ��¼����ʧ�ܵ��ļ�
	public static final String PREFIX_DOWNLOAD = "ftp_dl"; // ��¼���سɹ����ļ�
	public static final String PREFIX_UPLOADLOAD = "ftp_ul"; // ��¼�ϴ��ɹ����ļ�
	public static final String PREFIX_DOWNLOAD_OLD = "ftp";// �����ϰ汾���ϰ汾û���ϴ���������û������dl��ul

	/**
	 * �ж��ļ��Ƿ���Ҫ����
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static synchronized boolean shouldParse(File file) throws Exception
	{
		// 1:����ļ��ڡ������ѽ������ļ�����¼���Ѿ����ڣ����ظ�����
		// modify by wuyg 2013-01-17,ȥ���ļ����ļ���¼���ļ�⡣
		// ��Ϊ�ļ�������ɺ��ת��bakĿ¼��,������ʱ�ǲ�����bakĿ¼�µ��ļ���,��˲���Ҫ���������ظ��жϡ�
		// ����Ҫ�������ļ��ǳ����ʱ��,�˴�Ƶ���Ĵ򿪺͹ر��ļ��ǱȽϺķ�ʱ��ġ�

		// if (recordContainsFile(file, PREFIX_PARSE_SUCC))
		// return false;

		// 2:�ļ�������޸�ʱ���뵱ǰʱ��������ٴ���5�루�����ļ������ɵĹ����б�������
		if ((System.currentTimeMillis() - file.lastModified()) <= 5 * 1000l)
		{
			log.debug("�ļ����ˣ�" + file.getCanonicalPath() + "������޸�ʱ���뵱ǰʱ�����С��10��,Ϊ�����ļ����ɹ����б�����,�����ݲ�����");
			return false;
		}

		// 3:����ļ���СΪ0���򲻴���
		if (file.length() == 0)
		{
			// log.debug("�ļ����ˣ�" + file.getCanonicalPath() + "�ļ���СΪ0,�����ݲ�����");
			// add by lex 20140520 start
			if ((System.currentTimeMillis() - file.lastModified()) > 30 * 1000l)
			{// �ļ�������޸�ʱ���뵱ǰʱ����ȴ���30��,���ļ���СΪ0����Ϊ�ļ������⣬�Ƴ�֮
				File badDir = new File(file.getParent() + "/bad/");
				File badFile = new File(badDir, file.getName());
				if (!badFile.getParentFile().exists())
				{
					badFile.getParentFile().mkdirs();
				}
				if (badFile.exists())
				{
					boolean isDelete = badFile.delete();
					log.debug("badĿ¼����ͬ���ļ���ɾ��:" + isDelete);
				}
				boolean renameOk = file.renameTo(badFile);
				log.debug("�ļ����ˣ�" + file.getCanonicalPath() + "�ļ���СΪ0������޸�ʱ���뵱ǰʱ����ȴ���30��,�Ƴ���badĿ¼��:" + renameOk);
			} else
			{
				log.debug("�ļ����ˣ�" + file.getCanonicalPath() + "�ļ���СΪ0,�����ݲ�����");
			}
			// add by lex 20140520 end
			return false;
		}

		// 4:��.tmp��.temp��β���ļ�
		if (file.getName().endsWith(".tmp") || file.getName().endsWith(".temp"))
		{
			log.debug("�ļ����ˣ�" + file.getCanonicalPath() + "��.tmp��.temp��β,�����ݲ�����");
			return false;
		}

		// 5:�ⲿ���������Ҫ�����µ������������ͨ��ʵ��shouldParseExtend��������������������
		String fileFilterClass = ConfigReader.getProperties("extend.fileFilter");
		if (!StringUtil.isEmpty(fileFilterClass))
		{
			log.debug(file.getCanonicalPath() + " �ļ����˳���Ϊ " + fileFilterClass);

			IFileFilter fileFilter = (IFileFilter) Thread.currentThread().getContextClassLoader().loadClass(
					fileFilterClass).newInstance();
			boolean shouldParse = fileFilter.shouldParseExtend(file);
			if (!shouldParse)
			{
				log.debug("�ļ����ˣ��Զ�Ӧ������" + fileFilterClass + "��⵽�ļ�" + file.getCanonicalPath() + "�����,�����ݲ�����");
			}
			return shouldParse;
		}

		return true;
	}

	/**
	 * ��¼�Ѿ����������ļ�
	 * 
	 * @param file
	 * @throws IOException
	 */
	public static synchronized void recordParsedSuccFile(File file) throws IOException
	{
		record(file.getCanonicalPath() + "-" + TimeUtil.date2str(file.lastModified(), "yyyyMMddHHmmss"),
				PREFIX_PARSE_SUCC);
	}

	/**
	 * ��¼����ʧ�ܵ��ļ�
	 * 
	 * @param file
	 * @throws IOException
	 */
	public static synchronized void recordParsedFailFile(File file) throws IOException
	{
		record(file.getCanonicalPath() + "-" + TimeUtil.date2str(file.lastModified(), "yyyyMMddHHmmss"),
				PREFIX_PARSE_FAIL);
	}

	/**
	 * �ж��ļ��Ƿ���Ҫ����
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static synchronized boolean shouldDownload(File file) throws Exception
	{
		return !recordContainsFile(file, PREFIX_DOWNLOAD);
	}

	/**
	 * �ж��ļ��Ƿ���Ҫ����
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static synchronized boolean shouldReDownload(String recordInfo) throws Exception
	{
		File[] recordFiles = Constant.DATA_RECORD_DIR.listFiles();
		if (recordFiles != null)
		{
			for (int i = 0; i < recordFiles.length; i++)
			{
				if ((recordFiles[i].getName().contains(PREFIX_DOWNLOAD) || recordFiles[i].getName().contains(
						PREFIX_DOWNLOAD_OLD + "_"))
						&& recordContainsFile(recordInfo, PREFIX_DOWNLOAD, recordFiles[i]))
				{
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * ��¼�Ѿ����ع����ļ�
	 * 
	 * @param file
	 */
	public static synchronized void recordDownloadedFile(String recordInfo)
	{
		record(recordInfo, PREFIX_DOWNLOAD);
	}

	/**
	 * �ж��ļ��Ƿ���Ҫ�ϴ�
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static synchronized boolean shouldUpload(File file) throws Exception
	{
		return !recordContainsFile(file, PREFIX_UPLOADLOAD);
	}

	/**
	 * ��¼�Ѿ��ϴ������ļ�
	 * 
	 * @param file
	 * @throws IOException
	 */
	public static synchronized void recordUploadedFile(File file) throws IOException
	{
		record(file.getCanonicalPath() + "-" + TimeUtil.date2str(file.lastModified(), "yyyyMMddHHmmss"),
				PREFIX_UPLOADLOAD);
	}

	private static synchronized void record(String recordInfo, String preFix)
	{
		PrintWriter pw = null;
		FileWriter fw = null;
		try
		{
			File recordFile = getRecordFile(preFix, 0);
			fw = new FileWriter(recordFile, true);
			pw = new PrintWriter(fw);
			pw.println(TimeUtil.nowTime2str() + " " + recordInfo);
		} catch (Exception e)
		{
			log.error(e.getMessage(), e);
		} finally
		{
			try
			{
				if (pw != null)
				{
					pw.close();
				}
				if (fw != null)
				{
					fw.close();
				}
			} catch (Exception e)
			{
			}
		}
	}

	public static synchronized boolean recordContainsFile(File file, String preFix) throws FileNotFoundException,
			IOException
	{
		// ���ڼ�¼�ļ���ÿ��һ���������ڽ��м������Ҫͬʱ�����������ļ�¼�ļ�����Ҫ�Ǳ������賿0���Ժ��ʱ�����ظ����������Ѿ����������ʼ�

		String recordInfo = file.getCanonicalPath() + "-" + TimeUtil.date2str(file.lastModified(), "yyyyMMddHHmmss");
		File recordFileToday = getRecordFile(preFix, 0);// ����ļ�¼�ļ�
		if (recordContainsFile(recordInfo, preFix, recordFileToday))
		{
			return true;
		}

		File recordFileYestoday = getRecordFile(preFix, 1);// ����ļ�¼�ļ�
		if (recordContainsFile(recordInfo, preFix, recordFileYestoday))
		{
			return true;
		}

		return false;
	}

	public static boolean recordContainsFile(String recordInfo, String preFix, File recordFile)
			throws FileNotFoundException, IOException
	{
		BufferedReader bf = null;
		FileReader fr = null;
		try
		{
			fr = new FileReader(recordFile);
			bf = new BufferedReader(fr);
			String line = null;
			while ((line = bf.readLine()) != null)
			{
				// String[] names = line.split("\\s+");//�����ϰ汾���ϰ汾û�м�¼ʱ��
				String file = line.substring(10).trim();// modify by lex 20140620 ���ټ����ϰ汾�����ļ������пո�ʱԭд��������
				// for (int i = 0; i < names.length; i++)
				// {

				if (file.equalsIgnoreCase(recordInfo))// ��ʽ�磺2012-11-20 16:27:37
				// /home/oracle/wuyg/td-mr-xml/data/input/DATANG/TD-SCDMA_MRS_DATANG_OMCR_905_20121120150000.xml
				{
					log.debug(preFix + "�ļ����ˣ�" + recordInfo + "�Ѵ�����,���ٴ���,��鿴�ļ��б�:" + recordFile.getCanonicalPath());
					return true;
				}
				// }
			}
		} catch (Exception e)
		{
			log.error(e.getMessage(), e);
		} finally
		{
			if (fr != null)
			{
				fr.close();
			}
			if (bf != null)
			{
				bf.close();
			}
		}
		return false;
	}

	public static Set<String> getRecordFileList(String preFix) throws Exception
	{
		// ���ڼ�¼�ļ���ÿ��һ���������ڽ��м������Ҫͬʱ�����������ļ�¼�ļ�����Ҫ�Ǳ������賿0���Ժ��ʱ�����ظ����������Ѿ����������ʼ�
		Set<String> files = new HashSet<String>();

		File recordFileToday = getRecordFile(preFix, 0);// ����ļ�¼�ļ�
		files.addAll(getRecordFileList(recordFileToday));

		File recordFileYestoday = getRecordFile(preFix, 1);// ����ļ�¼�ļ�
		files.addAll(getRecordFileList(recordFileYestoday));

		return files;
	}

	public static Set<String> getRecordFileList(File recordFile) throws Exception
	{
		Set<String> files = new HashSet<String>();
		BufferedReader bf = null;
		FileReader fr = null;
		try
		{
			fr = new FileReader(recordFile);
			bf = new BufferedReader(fr);
			String line = null;
			while ((line = bf.readLine()) != null)
			{
				// String[] names = line.split("\\s+");//�����ϰ汾���ϰ汾û�м�¼ʱ��
				// files.add(names[names.length - 1]);
				// 2014-06-20 01:22:11 D:\workspace\data\input\test\test3\*.*-20140619234500
				String file = line.substring(19).trim();// modify by lex 20140620 ���ټ����ϰ汾�����ļ������пո�ʱԭд��������
				files.add(file);
			}
		} catch (Exception e)
		{
			log.error(e.getMessage(), e);
		} finally
		{
			if (fr != null)
			{
				fr.close();
			}
			if (bf != null)
			{
				bf.close();
			}
		}
		return files;
	}

	private static File getRecordFile(String preFix, int nDaysAgo)
	{
		try
		{
			String recordFileName = preFix + "_" + TimeUtil.date2str(TimeUtil.getDayBefore(nDaysAgo), "yyyyMMdd")
					+ ".txt";

			File recordFile = new File(Constant.DATA_RECORD_DIR, recordFileName);

			if (!recordFile.getParentFile().exists())
			{
				recordFile.getParentFile().mkdirs();
			}
			if (!recordFile.exists())
			{
				recordFile.createNewFile();
			}

			return recordFile;
		} catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public static void main(String[] args)
	{
		try
		{
			List<String> ll = new ArrayList<String>();
			for (int i = 0; i < 30000; i++)
			{
				ll.add("ss");
			}
			long startTime = System.currentTimeMillis();
			File s = new File("c:/a.txt");
			for (int i = 0; i < 1000000; i++)
			{
				recordDownloadedFile(System.currentTimeMillis() + "");

				shouldReDownload(System.currentTimeMillis() + "");
				if (i % 100 == 0)
				{
					System.out.println(i + "==" + (System.currentTimeMillis() - startTime));
				}
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}