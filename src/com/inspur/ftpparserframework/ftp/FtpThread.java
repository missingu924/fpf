package com.inspur.ftpparserframework.ftp;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;

import com.inspur.ftpparserframework.ftp.obj.FtpServer;
import com.inspur.ftpparserframework.util.Constant;
import com.inspur.ftpparserframework.util.TimeUtil;

/**
 * FTP�����߳�
 * 
 * @author �����
 * 
 */
public class FtpThread extends Thread
{
	private Logger log = Logger.getLogger(getClass());
	private FtpServer ftpServer = null;
	private long interval;// ��λ��
	private long fileLastModifyTime;// ��λ��
	private Date startTime = new Date();
	private String mode = null;
	private FTPClient ftp = null;// add by lex 20140519 ����fpt���ӣ����ڳ�ʱ��أ�����NOOP����

	/**
	 * ���췽��
	 * 
	 * @param ftpServer
	 *            ftp��������Ϣ
	 * @param interval
	 *            ftp��ѯ�������λ��
	 * @param fileLastModifyTime
	 *            ftp�ɼ�����೤ʱ�������ɵ��ļ�,��λ��
	 * @param mode
	 *            ftpģʽ������(download|upload|redownload����ӦΪ�ϴ� |����|����)
	 */
	public FtpThread(FtpServer ftpServer, long interval, long fileLastModifyTime, String mode)
	{
		this.ftpServer = ftpServer;
		this.interval = interval;
		this.fileLastModifyTime = fileLastModifyTime;
		this.mode = mode;
		this.setName(ftpServer.getName());// lex 20130823 ����,�����ѯftpServer��Ӧ���߳�
	}

	@Override
	public void run()
	{

		while (true)
		{
			try
			{
				this.setStartTime(new Date());// ���赱ǰʱ�䣬���ڼ�ⳬʱ��

				log.info("====================FTP������" + ftpServer.getName() + "�ļ�" + mode + "��ʼ====================");
				log.info("FTP������" + ftpServer.getName() + "�Ļ�����Ϣ���£�\n" + ftpServer.toString());
				// ����
				if (Constant.FTP_DOWNLOAD.equalsIgnoreCase(mode))
				{
					FtpUtil.downloadNested(this, ftpServer.getIp(), ftpServer.getPort(), ftpServer.getUser(), ftpServer
							.getPassword(), ftpServer.getDir(), ftpServer.getStoredir(), ftpServer.getRealFilter(),
							fileLastModifyTime, false);
				}
				// �ϴ�
				else if (Constant.FTP_UPLOAD.equalsIgnoreCase(mode))
				{
					FtpUtil.uploadNested(this, ftpServer.getIp(), ftpServer.getPort(), ftpServer.getUser(), ftpServer
							.getPassword(), ftpServer.getDir(), ftpServer.getStoredir());
				}
				// ����
				else if (Constant.FTP_RE_DOWNLOAD.equalsIgnoreCase(mode))
				{
					FtpUtil.downloadNested(this, ftpServer.getIp(), ftpServer.getPort(), ftpServer.getUser(), ftpServer
							.getPassword(), ftpServer.getDir(), ftpServer.getStoredir(), ftpServer.getRealFilter(),
							fileLastModifyTime, true);
				}
				log.info("====================������" + ftpServer.getName() + "�ļ�" + mode + "����====================");

				// this.setStartTime(new Date((new Date().getTime()) + interval * 1000l));//�����������ڼ䱻��ⳬʱ����
				this.setStartTime(new Date((new Date().getTime())));// modify by lex 20140620��ȥ������ʱ�䣬����ʱ�����ʱӦ�ñ���⵽����������
				sleep(interval * 1000l);
			} catch (Exception e)
			{
				log.error(e.getMessage(), e);
				try
				{
					// ftp �������򽫴�ftp�̵߳Ŀ�ʼʱ����Ϊ1900-1-1�����ftp��ʱ����̻߳��Զ�������ftp�߳�
					this.setStartTime(TimeUtil.str2date("1900-1-1 0:0:0"));
				} catch (ParseException e1)
				{
				}
			}
		}

	}

	public long getInterval()
	{
		return interval;
	}

	public void setInterval(long interval)
	{
		this.interval = interval;
	}

	public String getMode()
	{
		return mode;
	}

	public void setMode(String mode)
	{
		this.mode = mode;
	}

	public FtpServer getFtpServer()
	{
		return ftpServer;
	}

	public void setFtpServer(FtpServer ftpServer)
	{
		this.ftpServer = ftpServer;
	}

	public Date getStartTime()
	{
		return startTime;
	}

	public void setStartTime(Date startTime)
	{
		this.startTime = startTime;
	}

	public long getFileLastModifyTime()
	{
		return fileLastModifyTime;
	}

	public void setFileLastModifyTime(long fileLastModifyTime)
	{
		this.fileLastModifyTime = fileLastModifyTime;
	}

	public synchronized FTPClient getFtp()
	{
		return ftp;
	}

	public synchronized void setFtp(FTPClient ftp)
	{
		this.ftp = ftp;
	}

}