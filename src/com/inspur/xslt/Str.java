package com.inspur.xslt;

/**
 * @Description: XSLT转换器字符串处理类
 * @author lex
 * @date 2014-3-12 下午19:46:39
 * 
 */
public class Str
{
	/**
	 * 扩展自带函数substring-after,原函数弱爆了。
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static String substringAfterLast(String str1, String str2)
	{
		if (str1 == null || str2 == null)
		{
			return "";
		}
		int idx = str1.lastIndexOf(str2);
		if (idx < 0)
		{
			return "";
		}
		return str1.substring(idx + str2.length(), str1.length());
	}

	public static void main(String[] args)
	{
		String str1 = "DC=www.zte.com.cn,SubNetwork=ZTE_EUTRAN_SYSTEM,SubNetwork=ltetdd4,ManagedElement=ENODEBME78778,EnbFunction=78778,EutranCellTdd=1";
		String str2 = "SubNetwork=12";
		System.out.println(Str.substringAfterLast(str1, str2));
	}

}
