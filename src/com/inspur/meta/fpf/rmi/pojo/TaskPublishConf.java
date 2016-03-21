package com.inspur.meta.fpf.rmi.pojo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.bind.annotation.XmlRootElement;

import com.inspur.ftpparserframework.util.XMLFactory;
import com.sun.xml.internal.txw2.annotation.XmlValue;

@XmlRootElement(name = "conf")
public class TaskPublishConf
{
	private String ftp_id;
	private String db_id;

	public String getFtp_id()
	{
		return ftp_id;
	}

	@XmlValue
	public void setFtp_id(String ftp_id)
	{
		this.ftp_id = ftp_id;
	}

	public String getDb_id()
	{
		return db_id;
	}

	@XmlValue
	public void setDb_id(String db_id)
	{
		this.db_id = db_id;
	}

	public static void main(String[] args)
	{
		String str="D:/workspace/conf/quality/ftpServers/task_publish_id_1/conf.xml";
		XMLFactory taskPublishConfFactory = null;
		InputStream fis=null;
		try
		{
			fis = new FileInputStream(str);
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		taskPublishConfFactory = new XMLFactory(TaskPublishConf.class);
		TaskPublishConf taskPublishConf = taskPublishConfFactory.unmarshal(fis);
		System.out.println(taskPublishConf.getFtp_id());
		System.out.println(taskPublishConf.getDb_id());
	}

}
