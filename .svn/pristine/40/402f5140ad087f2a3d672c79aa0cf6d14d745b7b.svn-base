package com.inspur.ftpparserframework.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 时间处理工具类
 * @author 武玉刚
 *
 */
public class TimeUtil
{

	/**
	 * 将时间转换为"yyyy-MM-dd HH:mm:ss"格式的字符串
	 * 
	 * @return String
	 */
	public static String date2str(long time)
	{
		return date2str(new Date(time));
	}

	/**
	 * 将时间转换为"yyyy-MM-dd HH:mm:ss"格式的字符串
	 * 
	 * @return String
	 */
	public static String date2str(long time, String format)
	{
		return date2str(new Date(time), format);
	}

	/**
	 * 将时间转换为"yyyy-MM-dd HH:mm:ss"格式的字符串
	 * 
	 * @return String
	 */
	public static String date2str(Date date)
	{
		return date2str(date, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 将时间转换为"yyyy-MM-dd HH:mm:ss"格式的字符串
	 * 
	 * @return String
	 */
	public static String date2str(Date date, String format)
	{
		if (date == null || StringUtil.isEmpty(format))
		{
			return "";
		} else
		{
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			String str = sdf.format(date);
			return str;
		}
	}

	/**
	 * 将当前时间转换为"yyyy-MM-dd HH:mm:ss"格式的字符串
	 * 
	 * @return String
	 */
	public static String nowTime2str()
	{
		return nowTime2str("yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 将当前时间转换为"yyyy-MM-dd HH:mm:ss"格式的字符串
	 * 
	 * @return String
	 */
	public static String nowTime2str(String format)
	{
		Date now = new Date();
		return date2str(now, format);
	}

	/**
	 * 使用默认格式将字符串转换为Date 默认格式为yyyy-MM-dd HH:mm:ss
	 * 
	 * @param timeStr
	 *            String
	 * @return Date
	 * @throws ParseException
	 */
	public static Date str2date(String timeStr) throws ParseException
	{
		return str2date(timeStr, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 使用指定格式将字符串转换为Date
	 * 
	 * @param timeStr
	 *            String
	 * @return Date
	 * @throws ParseException
	 */
	public static Date str2date(String timeStr, String format) throws ParseException
	{
		if (StringUtil.isEmpty(timeStr) || StringUtil.isEmpty(format))
		{
			return null;
		} else
		{
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			Date dt = sdf.parse(timeStr);
			return dt;
		}
	}

	/**
	 * 将时间字符串转换为TimeStamp
	 * 
	 * @param timeStr
	 *            String
	 * @param format
	 *            String
	 * @return Timestamp
	 * @throws ParseException
	 */
	public static Timestamp getTimeStamp(String timeStr, String format) throws ParseException
	{
		if (timeStr == null || "".equals(timeStr) || format == null || "".equals(format))
		{
			return null;
		}
		return new Timestamp(str2date(timeStr, format).getTime());
	}

	/**
	 * 将时间字符串转换为TimeStamp 时间字符串格式为 "yyyy-MM-dd HH:mm:ss"
	 * 
	 * @param timeStr
	 *            String
	 * @return Timestamp
	 * @throws ParseException
	 */
	public static Timestamp getTimeStamp(String timeStr) throws ParseException
	{
		return getTimeStamp(timeStr, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 判断时间是否在指定时间范围内
	 * 
	 * @param time
	 *            String
	 * @param fromTime
	 *            String
	 * @param toTime
	 *            String
	 * @return boolean
	 * @throws ParseException
	 */
	public static boolean timeIn(String time, String fromTime, String toTime) throws ParseException
	{
		Date date = str2date(time);
		Date fromDate = str2date(fromTime);
		Date toDate = str2date(toTime);

		return date.after(fromDate) && date.before(toDate);
	}

	public static long getToday()
	{
		Calendar cal = Calendar.getInstance();
		cal.set(cal.HOUR_OF_DAY, 0);
		cal.set(cal.MINUTE, 0);
		cal.set(cal.SECOND, 0);
		cal.set(cal.MILLISECOND, 0);

		return cal.getTimeInMillis();
	}

	public static long getYesterday()
	{
		return getToday() - 1000 * 3600 * 24;
	}

	public static long getDayBefore(int interval)
	{
		return getToday() - 1000 * 3600 * 24 * interval;
	}

	public static String computeDate(String condition)
	{
		// -1D2H 一天两小时前
		// -H2 两小时前
		// 1D2H 一天两小时后
		// H2 两小时后

		int day = 0;
		int hour = 0;
		int minute = 0;
		if (condition.indexOf("D") > 0)
		{
			String d = condition.substring(0, condition.indexOf("D"));
			day = Integer.parseInt(d);
			if (condition.indexOf("H") > 0)
			{
				String h = condition.substring(condition.indexOf("D") + 1, condition.indexOf("H"));
				hour = Integer.parseInt(h);
				if (condition.indexOf("-") == 0)
				{
					hour = -hour;
				}
			}
		} else if (condition.indexOf("H") > 0)
		{
			String h = condition.substring(condition.indexOf("H") + 1, condition.length());
			hour = Integer.parseInt(h);
			if (condition.indexOf("-") == 0)
			{
				hour = -hour;
			}
		} else if (condition.indexOf("M") > 0)
		{
			String h = condition.substring(condition.indexOf("M") + 1, condition.length());
			minute = Integer.parseInt(h);
			if (condition.indexOf("-") == 0)
			{
				minute = -minute;
			}
		}

		Calendar cal = Calendar.getInstance();
		cal.add(cal.DAY_OF_MONTH, day);
		cal.add(cal.HOUR_OF_DAY, hour);
		cal.set(cal.MINUTE, 0);
		cal.set(cal.SECOND, 0);

		return date2str(cal.getTimeInMillis());
	}

	public static String computeDate(String format, String condition)
	{
		// -1D2H 一天两小时前
		// -H2 两小时前
		// 1D2H 一天两小时后
		// H2 两小时后

		int day = 0;
		int hour = 0;
		if (condition.indexOf("D") > 0)
		{
			String d = condition.substring(0, condition.indexOf("D"));
			day = Integer.parseInt(d);
			if (condition.indexOf("H") > 0)
			{
				String h = condition.substring(condition.indexOf("D") + 1, condition.indexOf("H"));
				hour = Integer.parseInt(h);
				if (condition.indexOf("-") == 0)
				{
					hour = -hour;
				}
			}
		} else if (condition.indexOf("H") > 0)
		{
			String h = condition.substring(condition.indexOf("H") + 1, condition.length());
			hour = Integer.parseInt(h);
			if (condition.indexOf("-") == 0)
			{
				hour = -hour;
			}
		}

		Calendar cal = Calendar.getInstance();
		cal.add(cal.DAY_OF_MONTH, day);
		cal.add(cal.HOUR_OF_DAY, hour);
		cal.set(cal.MINUTE, 0);
		cal.set(cal.SECOND, 0);

		return date2str(cal.getTimeInMillis(), format);
	}

	/**
	 * 将当前时间转换为"yyyy-MM-dd HH:mm:ss"格式的字符串
	 * 
	 * @return String
	 */
	public static String today2str()
	{
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String str = sdf.format(now);
		return str;
	}
	
	/**
	 * 将时间间隔转换为"x天x小时x分x秒"的格式
	 * @param timeInterval 时间间隔，单位毫秒
	 * @return
	 */
	public static String timeInerval2str(long timeInterval)
	{
		long oneDayInMills = 24*60*60*1000;
		long oneHourInMills = 60*60*1000;
		long oneMinuteInMills = 60*1000;
		long oneSecondInMills = 1000;
		
		long days = timeInterval/oneDayInMills;
		long hours = timeInterval%oneDayInMills/oneHourInMills;
		long minutes = timeInterval%oneDayInMills%oneHourInMills/oneMinuteInMills;
		long seconds = timeInterval%oneDayInMills%oneHourInMills%oneMinuteInMills/oneSecondInMills;
		
		return (days+"天"+hours+"小时"+minutes+"分"+seconds+"秒");
	}
	public static void main(String[] args)
	{
		// System.out.println(computeDate("yyyyMMdd HH", "-H2"));
		//		
		// String filter="$TIME(yyyyMMddHH,-1D2H)$_*.xml";
		// Pattern p=Pattern.compile("(.*)\\$TIME\\((.+)\\)\\$(.*)");
		// Matcher m=p.matcher(filter);
		// if (m.matches())
		// {
		// String tmp=m.group(2);
		// System.out.println(tmp);
		// String[] inputs=tmp.split(",");
		// if (inputs.length==2)
		// {
		// System.out.println(computeDate(inputs[0],inputs[1]));
		// }
		// else
		// {
		// System.out.println(computeDate(inputs[0]));
		// }
		// }
		String filter = "*_$TIME(yyyyMMdd)$_*";

		String realFilter = filter;

		Pattern p = Pattern.compile("(.*)\\$TIME\\((.+)\\)\\$(.*)");
		Matcher m = p.matcher(filter);
		if (m.matches())
		{
			String tmp = m.group(2);
			String[] inputs = tmp.split(",");

			String time = "";
			if (inputs.length == 2)
			{
				time = TimeUtil.computeDate(inputs[0], inputs[1]);
			} else if (inputs.length == 1)
			{
				time = TimeUtil.computeDate(inputs[0], "");
			}

			realFilter = filter.replaceAll("\\$TIME\\((.+)\\)\\$", time);
		}

		System.out.println(realFilter);
		//		
	}
}
