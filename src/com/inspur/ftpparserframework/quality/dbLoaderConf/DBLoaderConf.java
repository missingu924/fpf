package com.inspur.ftpparserframework.quality.dbLoaderConf;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "dbLoader")
public class DBLoaderConf implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5212894284703368193L;
	private String user;
	private String password;
	private String tnsname;
	private String ip;
	private String port;

	public String getUser()
	{
		return user;
	}

	@XmlElement(name = "user")
	public void setUser(String user)
	{
		this.user = user;
	}

	public String getPassword()
	{
		return password;
	}

	@XmlElement(name = "password")
	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getTnsname()
	{
		return tnsname;
	}

	@XmlElement(name = "tnsname")
	public void setTnsname(String tnsname)
	{
		this.tnsname = tnsname;
	}

	public String getIp()
	{
		return ip;
	}

	@XmlElement(name = "ip")
	public void setIp(String ip)
	{
		this.ip = ip;
	}

	public String getPort()
	{
		return port;
	}

	@XmlElement(name = "port")
	public void setPort(String port)
	{
		this.port = port;
	}
}
