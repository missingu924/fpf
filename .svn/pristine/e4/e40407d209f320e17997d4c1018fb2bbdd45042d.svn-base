package com.inspur.ftpparserframework.report;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

public class ReporSocketServer extends Thread
{
	private Logger log = Logger.getLogger(ReporSocketServer.class);
	private ServerSocket server = null;

	@Override
	public void run()
	{
		try
		{
			boolean serverOk = false;
			for (int i = 2000; i < 10000; i++)
			{
				try
				{
					if (!serverOk)
					{
						server = new ServerSocket(i);
					}

					serverOk = true;
				} catch (Exception e)
				{
				}
			}

			log.info("¶Ë¿ÚºÅ£º" + server.getLocalPort());
			while (true)
			{
				Socket socket = server.accept();				
				ReportThread t = new ReportThread(socket);
				t.start();
			}
		} catch (Exception ex)
		{
			log.error(ex.getMessage(), ex);
		} finally
		{
			if (server != null)
			{
				try
				{
					server.close();
				} catch (IOException ex1)
				{
					log.error(ex1.getMessage(), ex1);
				}
			}
		}

	}

	public static void main(String[] args)
	{
		ReporSocketServer r = new ReporSocketServer();
		r.run();
	}
}
