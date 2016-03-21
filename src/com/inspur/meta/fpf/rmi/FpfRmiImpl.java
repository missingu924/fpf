package com.inspur.meta.fpf.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

import com.inspur.ftpparserframework.Main;

public class FpfRmiImpl extends UnicastRemoteObject implements IFpfRmi
{

	public FpfRmiImpl() throws RemoteException
	{
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2040138563900476423L;

	@Override
	public String addTask(String publishtTaskId) throws RemoteException
	{
		try
		{
			return Main.addorUpdateTask(publishtTaskId);
		} catch (Exception e)
		{
			e.printStackTrace();
			throw new RemoteException(e.getMessage());
		}
	}

	@Override
	public String deleteTask(String publishtTaskId) throws RemoteException
	{
		return Main.delTask(publishtTaskId);
	}

	@Override
	public String runTask(String publishtTaskId, String publishTaskName, Map<String, String> varMap)
			throws RemoteException
	{
		return null;
	}

	@Override
	public String testStage(Map<String, String> varMap, Map<String, String> xmlMap) throws RemoteException
	{
		return null;
	}

	@Override
	public String testTask(String publishTaskName, Map<String, String> varMap, Map<String, String> xmlMap)
			throws RemoteException
	{
		return null;
	}

	@Override
	public String updateTask(String publishtTaskId) throws RemoteException
	{
		return this.addTask(publishtTaskId);
	}

}
