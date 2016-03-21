package com.inspur.ftpparserframework.ftp;

/** 
 * @ClassName: FileCount 
 * @Description: �̰߳�ȫ�ļ�¼��
 * @author lex
 * @date 2014-6-20 ����12:37:05 
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
