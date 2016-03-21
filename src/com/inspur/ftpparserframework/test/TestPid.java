package com.inspur.ftpparserframework.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;

import com.inspur.ftpparserframework.util.TimeUtil;

public class TestPid
{
	public static void main(String[] args)
	{
		try
		{
			RuntimeMXBean r = ManagementFactory.getRuntimeMXBean();
			//			System.out.println(r.getBootClassPath());
			//			System.out.println(r.getClassPath());
			//			System.out.println(r.getLibraryPath());
			//			System.out.println(r.getManagementSpecVersion());
			//			System.out.println(r.getSpecName());
			//			System.out.println(r.getSpecVendor());
			//			System.out.println(r.getSpecVersion());
			//			System.out.println(r.getInputArguments());
			//			System.out.println(r.getName());
			//			System.out.println(TimeUtil.date2str(r.getStartTime()));
			//			System.out.println(r.getUptime());
			//			System.out.println(r.getVmName());
			//			System.out.println(r.getVmVendor());
			//			System.out.println(r.getVmVersion());
			//			System.out.println(r.getSystemProperties());
			//			System.out.println();

			String cmd = "java ";
			List<String> inputArgs = r.getInputArguments();
			for (int i = 0; i < inputArgs.size(); i++)
			{
				cmd += inputArgs.get(i) + " ";
			}

			cmd += "-classpath " + r.getClassPath() + " ";

			cmd += TestPid.class.getCanonicalName() + " ";

			for (int i = 0; i < args.length; i++)
			{
				cmd += args[i] + " ";
			}

			System.out.println("I'm up,pid=" + r.getName() + " dir=" + new File("./").getCanonicalPath());

			String exeResult;
			Process process = Runtime.getRuntime().exec(cmd);
			BufferedReader out = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			while ((exeResult = out.readLine()) != null)
			{
				System.out.println(exeResult);
			}
			while ((exeResult = error.readLine()) != null)
			{
				System.out.println(exeResult);
			}
			out.close();
			error.close();
			process.waitFor();
			Thread.sleep(3000l);
			System.out.println("I'm down,pid="+r.getName());
		} catch (Exception e)
		{
			// TODO: handle exception
		}
	}
}
