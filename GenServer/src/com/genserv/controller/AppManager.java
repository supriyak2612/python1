package com.genserv.controller;

import java.util.*;
import java.io.*;

public class AppManager
{
	protected static String m_strPropFile = null;
	protected static Properties m_props;
	protected static FileInputStream m_fisProp;

	public static boolean init(String strPropFile)
	{
		boolean bRet;

		bRet = loadPropertyFile(strPropFile);
		if (!bRet)
			return false;

		return true;
	}

	public static boolean loadPropertyFile(String strPropFile)
	{
		boolean bRet = true;

		m_props = new Properties();

		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream(strPropFile);
			m_props.load(fis);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			bRet = false;
		}

		if (!bRet)
			return false;

		m_fisProp = fis;
		m_strPropFile = strPropFile; //Save the path of the currently loaded property file

		return true;
	}

	public static boolean savePropertyFile()
	{
		boolean bRet = true;

		try
		{
			if (m_fisProp != null)
			{ //close the current input stream, as we are writing into the same file
				m_fisProp.close();
				m_fisProp = null;
			}

			//Save the output to the destination file
			BufferedWriter wr;

			wr = new BufferedWriter(new FileWriter(m_strPropFile));

			//wr.write(strOutText, 0, strOutText.length());
			//wr.write("\r\n", 0, 2);

			wr.flush();
			wr.close();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			bRet = false;
		}

		return bRet;
	}
	public static String getProperty(String strKey)
	{
		if (m_props != null)
			return m_props.getProperty(strKey);
		return null;
	}

	public static String getProperty(String strKey, String strDefaultValue)
	{
		if (m_props != null)
			return m_props.getProperty(strKey, strDefaultValue);
		return null;
	}

	public static void setProperty(String strKey, String strValue)
	{
		if (m_props != null)
			m_props.setProperty(strKey, strValue);
	}
}
