package com.inspur.ftpparserframework.extend;

import java.io.File;
import java.util.LinkedHashMap;

public interface IFileParser
{
	public abstract void parse(File paramFile, LinkedHashMap<String, String> paramLinkedHashMap)
    throws Exception;
}
