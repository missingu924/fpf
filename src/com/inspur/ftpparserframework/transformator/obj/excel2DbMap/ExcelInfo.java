package com.inspur.ftpparserframework.transformator.obj.excel2DbMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.inspur.ftpparserframework.util.XMLFactory;

@XmlRootElement(name = "excel")
public class ExcelInfo
{
	private String identifiedBy;
	private String description;
	private List<SheetInfo> sheetInfos = new ArrayList<SheetInfo>();

	public String getIdentifiedBy()
	{
		return identifiedBy;
	}

	@XmlAttribute(name = "identifiedBy")
	public void setIdentifiedBy(String identifiedBy)
	{
		this.identifiedBy = identifiedBy;
	}

	public String getDescription()
	{
		return description;
	}

	@XmlAttribute(name = "description")
	public void setDescription(String description)
	{
		this.description = description;
	}

	public List<SheetInfo> getSheetInfos()
	{
		return sheetInfos;
	}

	@XmlElement(name = "sheet")
	public void setSheetInfos(List<SheetInfo> sheetInfos)
	{
		this.sheetInfos = sheetInfos;
	}

	public static void main(String[] args)
	{
		File dataInfoFile = new File("C:\\Users\\lex\\Desktop\\731\\Excel2DbMp.xml");
		InputStream fis = null;
		try
		{
			fis = new FileInputStream(dataInfoFile);
			XMLFactory excelInfoFactory = new XMLFactory(ExcelInfo.class);
			ExcelInfo excelInfo = excelInfoFactory.unmarshal(fis);
			System.out.println(excelInfo.getDescription());
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			if (fis != null)
			{
				try
				{
					fis.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}

	}

}
