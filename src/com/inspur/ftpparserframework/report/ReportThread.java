package com.inspur.ftpparserframework.report;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.inspur.ftpparserframework.Main;

public class ReportThread extends Thread
{
	private Logger log = Logger.getLogger(getClass());

	private Socket socket = null;

	private final String help = "Usage :\r\n" + "\thelp          :help\r\n"
			+ "\tstatus        :report the status of the threads.\r\n" + "\tq             :quit\r\n";

	public ReportThread(Socket socket)
	{
		this.socket = socket;
	}

	@Override
	public void run()
	{
		try
		{
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

			boolean quit = false;
			String command = null;

			output.println("\n===============Welcome to FPF Reporter===============\r");
			output.println(help);
			while (!quit)
			{
				command = input.readLine(); // 接收客户端请求

				if (command == null)
				{
					break;
				}
				
				if (command.equalsIgnoreCase("help"))
				{
					output.println(help);
				} else if (command.equalsIgnoreCase("status"))
				{
//					output.println("ftp = " + Main.ftp + " parse = " + Main.parse + " server = " + Main.serverStr);

				} else if (command.equalsIgnoreCase("q"))
				{
					output.println("Goodbye!");
					output.close();
					input.close();
					socket.close();
					quit = true;
				} else
				{
					output.println("can't process the request\r");
				}
				command = "";
			}

		} catch (Exception e)
		{
			log.error(e.getMessage(), e);
		} finally
		{
			if (socket != null)
			{
				try
				{
					socket.close();
				} catch (IOException e)
				{
				}
			}
		}
	}

}
