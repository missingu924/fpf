package com.inspur.ftpparserframework.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 字符串工具类
 * @author 武玉刚
 *
 */
public class StringUtil
{

	/**
	 * 根据String List获得用 号分割的字符串,每个数据项都用单引号引起来
	 * 
	 * @param stringList
	 * @return
	 */
	public static String getStringByListWithQuotation(List stringList)
	{
		return getStringByList(stringList, true);
	}

	/**
	 * 根据String List获得用逗号分割的字符串,每个数据项都不用单引号引起来
	 * 
	 * @param stringList
	 * @return
	 */
	public static String getStringByListNoQuotation(List stringList)
	{
		return getStringByList(stringList, false);
	}

	/**
	 * 根据String List获得用逗号分割的字符串，如果withQuotation=true则每个数据项都用单引号引起来
	 * 
	 * 
	 * @param stringList
	 * @param withQuotation
	 * @return
	 */
	public static String getStringByList(List stringList, boolean withQuotation)
	{
		if (stringList == null || stringList.size() == 0)
		{
			return "";
		}

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < stringList.size(); i++)
		{
			if (withQuotation)
			{
				sb.append("'");
			}
			sb.append(stringList.get(i));
			if (withQuotation)
			{
				sb.append("'");
			}
			if (i != stringList.size() - 1)
			{
				sb.append(",");
			}
		}

		return sb.toString();
	}

	/**
	 * 根据逗号或分号将字符串拆分为list，注意：逗号和分号都会认作是分割符
	 * 
	 * @param listString
	 * @return
	 */
	public static List<String> getStringListByString(String listString)
	{
		List<String> stringList = new ArrayList<String>();
		if (!isEmpty(listString))
		{
			String[] temp = listString.split(",|;|\\|");
			for (int i = 0; i < temp.length; i++)
			{
				if (!isEmpty(temp[i]))
				{
					stringList.add(temp[i]);
				}
			}
		}

		return stringList;
	}

	public static List<String> getStringListByString(String listString, char separator, char quoter)
	{
		List<String> stringList = new ArrayList<String>();
		if (!isEmpty(listString))
		{
			//			String[] temp = listString.split(separator);
			//
			//			String columnStr = "";
			//			boolean inQuotation = false;
			//			for (int i = 0; i < temp.length; i++)
			//			{
			//				columnStr += temp[i];
			//				if (temp[i].trim().contains(quoter))
			//				{
			//					if (inQuotation == false)
			//					{
			//						inQuotation = true;
			//					} else
			//					{
			//						inQuotation = false;
			//					}
			//				}
			//
			//				if (inQuotation == true)
			//				{
			//					columnStr += separator;
			//				} else
			//				{
			//					if (!isEmpty(columnStr))
			//					{
			//						stringList.add(columnStr);
			//					}
			//					columnStr = "";
			//				}
			//			}

			char[] chars = listString.toCharArray();
			String s = "";
			boolean inQuotation = false;
			for (int i = 0; i < chars.length; i++)
			{
				if (i == chars.length - 1)
				{
					s += chars[i];
					if (!isEmpty(s.trim()))
					{
						stringList.add(s);
					}
					break;
				}

				if (chars[i] == separator)
				{
					if (inQuotation)
					{
						s += chars[i];
					} else if (!isEmpty(s.trim()))
					{
						stringList.add(s);
						s = "";
					}
				} else
				{
					s += chars[i];
					
					if (chars[i] == quoter)
					{
						if (inQuotation)
						{
							inQuotation = false;
							if (!isEmpty(s.trim()))
							{
								stringList.add(s);
								s = "";
							}
						} else
						{
							inQuotation = true;
						}
					}
				}
			}
		}

		return stringList;
	}

	/**
	 * 判断ListA中的字符串是否都在ListB??
	 * 
	 * @param listA
	 * @param listB
	 * @return
	 */
	public static boolean isListAInListB(List<String> listA, List<String> listB)
	{
		for (int i = 0; i < listA.size(); i++)
		{
			boolean in = false;
			for (int j = 0; j < listB.size(); j++)
			{
				if (listB.get(j).equalsIgnoreCase(listA.get(i)))
				{
					in = true;
					break;
				}
			}
			if (!in)
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断字符串是否为null或为??
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str)
	{
		return str == null || str.trim().equals("");
	}

	public static String trim(String str)
	{
		if (isEmpty(str))
		{
			return "";
		} else
		{
			return str.trim();
		}
	}

	/**
	 * 根据String List获得用separator分割的字符串
	 * 
	 * @param stringList
	 * @param withQuotation
	 * @return
	 */
	public static String getStringByList(List stringList, String separator)
	{
		if (stringList == null || stringList.size() == 0)
		{
			return "";
		}

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < stringList.size(); i++)
		{
			sb.append(stringList.get(i));
			if (i != stringList.size() - 1)
			{
				sb.append(separator);
			}
		}

		return sb.toString();
	}

	public static void main(String[] args)
	{
		List<String> l = getStringListByString("\"a c\" YEAR \"to_char(to_date( :START_TIME,'yyyymmddhh24miss')\" ab \"db c\"", ' ', '"');
		for (int i = 0; i < l.size(); i++)
		{
			System.out.println(l.get(i));
		}
	}

}