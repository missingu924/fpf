package com.inspur.ftpparserframework.parser;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.inspur.ftpparserframework.config.ConfigReader;
import com.inspur.ftpparserframework.exception.GetParameterException;
import com.inspur.ftpparserframework.rmi.StatusCenter;
import com.inspur.ftpparserframework.transformator.TransformatEngine;
import com.inspur.ftpparserframework.util.Constant;
import com.inspur.ftpparserframework.util.FileUtil;
import com.inspur.ftpparserframework.util.FilterUtil;
import com.inspur.ftpparserframework.util.TimeUtil;

/**
 * �ļ������̣߳�����һ���ļ�������һ�������߳�ʵ����
 * @author �����
 *
 */
public class ParseThread extends Thread
{
	private Logger log = Logger.getLogger(getClass());
	private File file = null;
	private Map<String, Object> paramMap = new HashMap<String, Object>();

	//	private static String deleteOldFilesDay = "";

	private static Map<String, String> deleteOldFileMap = new HashMap<String, String>();

	public ParseThread(File file, Map<String, Object> paramMap)
	{
		this.file = file;
		this.paramMap = paramMap;
	}

	@Override
	public void run()
	{
		String fileInfo = "";
		long startTime = System.currentTimeMillis();
		try
		{
			fileInfo = "[Դ�ļ�=" + file.getCanonicalPath() + ",��С=" + file.length() + "Byte,ʱ��="
					+ TimeUtil.date2str(file.lastModified()) + "]";

			if (FilterUtil.shouldParse(file))
			{
				// ��־��ʽ��#�ļ��������#-[��ʼ]-[Դ�ļ�=xx,��С=xxByte,ʱ��=yyyy-MM-dd HH:mm:ss]
				log.info("#�ļ��������#-[��ʼ]-" + fileInfo);

				// step1:�����ļ�
				new TransformatEngine().transformat(file, paramMap);

				// step2:�ļ��б��¼����¼�����Ѵ�������ļ��������ظ�����
				// modify by wuyg 2013-01-17,ȥ�����Ѵ����ļ��ļ�¼��
				// ��Ϊ�ļ�������ɺ��ת��bakĿ¼��,������ʱ�ǲ�����bakĿ¼�µ��ļ���,��˲���Ҫ�������¼��
				// ����Ҫ�������ļ��ǳ����ʱ��,�˴�Ƶ���Ĵ򿪺͹ر��ļ��ǱȽϺķ�ʱ��ġ�

				//				FilterUtil.recordParsedSuccFile(file);

				// step3:����ԭʼ�ļ�
				File bakDir = new File(file.getParent() + "/bak/");
				File bakFile = new File(bakDir, file.getName());
				if (!bakFile.getParentFile().exists())
				{
					bakFile.getParentFile().mkdirs();
				}
				boolean bakOk = file.renameTo(bakFile);
				log.info("�ļ�����[" + (bakOk ? "�ɹ�" : "ʧ��") + "]��Դ�ļ�=" + file.getCanonicalPath() + ", �����ļ�="
						+ bakFile.getCanonicalPath());

				// step4:ɾ�����ڵ�ԭʼ�ͱ����ļ�,Ĭ�ϱ���3��
				int srcfileDays = 3;
				try
				{
					srcfileDays = Integer.parseInt(ConfigReader.getProperties("parser.fileSavedDays"));
					log.debug("SystemConfig.xml����ԭʼ�ļ�����ʱ��(parser.fileSavedDays):" + srcfileDays + "��");
				} catch (Exception e)
				{
					log.debug("SystemConfig.xmlδԭʼ�ļ�����ʱ��(parser.fileSavedDays)��ʹ��ϵͳĬ��ʱ��:"
							+ Constant.PARSER_FILE_SAVED_DAYS + "��");
					srcfileDays = Constant.PARSER_FILE_SAVED_DAYS;
					log.error(e.getMessage(), e);
				}

				// step5:ɾ��n��ǰ�����ݣ�Ϊ����ÿ�����ж������ļ������ķѽ϶�ʱ�䣬�޶�ֻ��ÿ���0,6,12,18������ļ�ɾ������
				String day = TimeUtil.nowTime2str("dd");
				String hour = TimeUtil.nowTime2str("HH");
				if (!day.equals(deleteOldFileMap.get(bakDir.getCanonicalPath()))
						&& ("00".equals(hour) || "06".equals(hour) || "12".equals(hour) || "16".equals(hour) || "18"
								.equals(hour)))
				{
					FileUtil.deleteFilesNdaysAgo(file.getParentFile(), srcfileDays);
					FileUtil.deleteFilesNdaysAgo(bakDir, srcfileDays);

					// add by wuyg 2013-1-17 ɾ��3��ǰ����־�ļ����������ﲻ̫���ʣ������ܱ�û�кá�
					FileUtil.deleteFilesNdaysAgo(Constant.LOGS_DIR, 3);

					//					deleteOldFilesDay = day;
					deleteOldFileMap.put(bakDir.getCanonicalPath(), day);
				}

				long endTime = System.currentTimeMillis();
				// ��־��ʽ��#�ļ��������#-[����]-[Դ�ļ�=xx,��С=xxByte,ʱ��=yyyy-MM-dd HH:mm:ss]-[��ʱ=n����] 
				log.info("#�ļ��������#-[����]-" + fileInfo + "-[��ʱ=" + (endTime - startTime) + "����]");
				
				// ��¼�����ɹ��ļ���
				StatusCenter.oneFileParseSucc();
			}
		} catch (Exception e)
		{
			try
			{
				// ��־��ʽ��#�ļ��������#-[����]-[Դ�ļ�=xx,��С=xxByte,ʱ��=yyyy-MM-dd HH:mm:ss]-[��ʱ=n����]-[������Ϣ=xx]
				log.error("#�ļ��������#-[����]-" + fileInfo + "-[��ʱ=" + (System.currentTimeMillis() - startTime)
						+ "����]-[������Ϣ=" + e.getMessage() + "]");
				
				// ��¼����ʧ���ļ���
				StatusCenter.oneFileParseError();
				
				log.error(e.getMessage(), e);

				if (!(e instanceof GetParameterException))
				{
					// step6:����ʧ�ܵ��ļ��ŵ�bad��Ŀ¼�£�������ǲ���Ҫ�ƶ��ļ��Ĵ�������Ҫ�ƶ���badĿ¼��
					File badDir = new File(file.getParent() + "/bad/");
					File badFile = new File(badDir, file.getName());
					if (!badFile.getParentFile().exists())
					{
						badFile.getParentFile().mkdirs();
					}
					file.renameTo(badFile);
				}

				// �ļ��б��¼����¼���촦��ʧ�ܵ��ļ�
				FilterUtil.recordParsedFailFile(file);
			} catch (Exception e1)
			{
			}
		}
	}
}
