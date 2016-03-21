package com.inspur.ftpparserframework.transformator.obj;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Csv2DbMapConfig
{
	private String version;
	private String description;
	private List<Csv2DbMap> csv2DbMaps = new ArrayList<Csv2DbMap>();

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public List<Csv2DbMap> getCsv2DbMaps()
	{
		return csv2DbMaps;
	}

	public void setCsv2DbMaps(List<Csv2DbMap> csv2DbMaps)
	{
		this.csv2DbMaps = csv2DbMaps;
	}

	public void addCsv2DbMap(Csv2DbMap csv2DdMap)
	{
		this.csv2DbMaps.add(csv2DdMap);
	}

	public Csv2DbMap getCsv2DbMapByTable(String table)
	{
		Csv2DbMap m = null;
		for (int i = 0; i < this.csv2DbMaps.size(); i++)
		{
			m = this.csv2DbMaps.get(i);
			if (table.equalsIgnoreCase(m.getDbTable()))
			{
				return m;
			}
		}
		return m;
	}

	// add by Ðì¶÷Áú 20130930
	public Csv2DbMap getCsv2DbMapBySrcFileName(String srcFileName)
	{
		Csv2DbMap ret=null;
		Pattern pattern;
		for (int i = 0; i < this.csv2DbMaps.size(); i++){
			Csv2DbMap csv2DbMap=csv2DbMaps.get(i);
			pattern = Pattern.compile(csv2DbMap.getFileRegext());
			Matcher matcher = pattern.matcher(srcFileName);
			if (matcher.find()) {
				ret = csv2DbMap;
				break;
			}
		}
		return ret;

	}

}
