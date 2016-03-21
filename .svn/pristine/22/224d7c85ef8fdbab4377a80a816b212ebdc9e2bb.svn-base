package com.inspur.ftpparserframework.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.log4j.Logger;

public class FuncUtil
{
	private static Logger log = Logger.getLogger(FuncUtil.class);

	public static String invokeCustomMethed(String line)
	{
		try
		{
			List<String> funcList = getFuncList(line);
			for (int i = 0; i < funcList.size(); i++)
			{
				String function = funcList.get(i);
				Pattern p = Pattern.compile("\\s*(\\w+)\\((\\S+)\\)\\s*");
				Matcher m = p.matcher(funcList.get(i));
				if (m.matches())
				{
					log.info("扩展函数调用-原始输入:" + function);
					String methodName = m.group(1);
					String param = m.group(2);

					List<String> paramList = StringUtil.getStringListByString(param, ',', '"');// 切分参数
					String[] params = new String[paramList.size()];
					for (int j = 0; j < params.length; j++)
					{
						String paramWithQuotation = paramList.get(j);
						params[j] = paramList.get(j).substring(1, paramWithQuotation.length() - 1);//去除参数两边的引号
					}

					String value = (String) MethodUtils.invokeMethod(FuncUtil.class.newInstance(), methodName, params);

					line = line.replace("#" + function + "#", value);
				}
			}
		} catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}

		return line;
	}

	private static List<String> getFuncList(String line)
	{
		char separator = '#';
		List<String> varList = new ArrayList<String>();
		char[] chars = line.toCharArray();

		for (int i = 0; i < chars.length; i++)
		{
			if (chars[i] == separator)
			{
				String varName = "";

				for (i++; i < chars.length; i++)
				{
					if (chars[i] != separator)
					{
						varName += chars[i];
					} else
					{
						break;
					}
				}

				if (!varList.contains(varName))
				{
					varList.add(varName);
				}
			}
		}
		return varList;
	}

	public String test(String p1, String p2, String p3)
	{
		return p1 + "-" + p2 + "-" + p3;
	}

	public String subString(String str, String beginIndex, String endIndex)
	{
		int beginIdx = Integer.parseInt(beginIndex);
		int endIdx = Integer.parseInt(endIndex);

		return str.substring(beginIdx, endIdx);
	}

	public static void main(String[] args)
	{
		try
		{
			System.out.println(invokeCustomMethed("#subString(\"RNC288\",\"2\",\"5\")#"));

			//		String line ="$test(\"3\",\"18329\",\"46\")$ a bc d,1234$test1(\"3\",\"18329\",\"46\")$";
			//		StringTokenizer tk = new StringTokenizer(line, "$", false);
			//		while (tk.hasMoreTokens())
			//		{
			//			System.out.println(tk.nextToken());
			//		}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
