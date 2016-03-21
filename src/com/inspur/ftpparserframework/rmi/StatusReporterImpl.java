package com.inspur.ftpparserframework.rmi;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.apache.log4j.Logger;

/**
 * FPF状态信息获取实现类
 * @author 武玉刚
 *
 */
public class StatusReporterImpl extends UnicastRemoteObject implements IStatusReporter
{
	Logger log = Logger.getLogger(getClass());

	public StatusReporterImpl() throws RemoteException
	{
		super();
	}

//	@Override
	public StatusObj getStatus() throws RemoteException
	{
		StatusObj status = new StatusObj();

		log.info(status.toString());

		return status;
	}

	public static void main(String[] args)
	{
		try
		{
			int port = -1;
			if (args.length != 2)
			{
				usage();
			} else
			{
				if (args[0].equals("-rmiport"))
				{
					port = Integer.parseInt(args[1]);
				} else
				{
					usage();
				}
			}

			//在RMI服务注册表中查找名称为RStatusReporter的对象，并调用其上的方法 
			IStatusReporter RStatusReporter = (IStatusReporter) Naming.lookup("rmi://localhost:" + port + "/"
					+ RMI_NAME);

			System.out.println(RStatusReporter.getStatus().toString());
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	private static void usage()
	{
		System.out.println("命令说明: ./statusrmi.sh -rmiport portNo");
		System.exit(0);
	}

}
