package com.inspur.ftpparserframework.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * FPF实例状态信息报告程序
 * 
 * @author 武玉刚
 * 
 */

public interface IStatusReporter extends Remote
{
	/**
	 * RMI注册名
	 */
	public static final String RMI_NAME = "RStatusReporter";
	
	/**
	 * 获取FPF状态
	 * @return
	 * @throws RemoteException
	 */
	public StatusObj getStatus() throws RemoteException;
}
