package com.inspur.ftpparserframework.ftp;

/** 
 * @ClassName: FileCount 
 * @Description: 线程安全的记录器
 * @author lex
 * @date 2014-6-20 上午12:37:05 
 *  
 */
public class FileCount
{
	private int num=0;

	public synchronized int getNum()
	{
		return num;
	}

	public synchronized void addOne()
	{
		this.num++;
	}
	public synchronized void addOtherFileCount(FileCount fileCount)
	{
		this.num=this.num+fileCount.getNum();
	}
	

}
