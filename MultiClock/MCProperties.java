package tools.multiclock;

import java.util.*;
import java.io.*;

import utils.gwt.GToolkit;

public class MCProperties
{
	public String m_strDefaultZone;

	public int m_nRefreshTime;
	public int m_nClockType;

	public int m_nMaxClocks;

	public Vector m_vDisplayZones;
	public Vector m_vAlarms;

	public static String DEF_ZONE = "%%%"; //Use System Default
	public static int DEF_REF_TIME = 5*1000; //5 seconds
	public static int DEF_CLK_TYPE = 0;
	public static int MAX_CLOCKS = 20;

	private String m_strPropFile;

	private Properties m_props;

	/**
	* Constructor
	*/
	public MCProperties(String strPropFile)
	{
		m_strPropFile = strPropFile; //set property file name

		//set default property values
		m_strDefaultZone = DEF_ZONE;
		m_nRefreshTime = DEF_REF_TIME;
		m_nClockType = 0;
		m_vDisplayZones = new Vector();
		m_vAlarms = new Vector();
	}

	/**
	* loads the specified property resource
	*/
	public boolean loadResource(Properties props, String strResName)
		throws IOException
	{
		boolean bLoaded = false;

		try
		{
			//Load from local file
			props.load(new FileInputStream(strResName));
			bLoaded = true;
		}
		catch(IOException ex)
		{
			//Loaded from class path
			InputStream is;

			is = GToolkit.getPropertyResource(strResName);
			if (is != null)
			{
				props.load(is);
				bLoaded = true;
			}
		}

		return bLoaded;
	}

	/**
	* loads the properties from the persistent media and initializes the attributes
	*/
	public boolean load()
	{
		m_props = new Properties();

		boolean bRet = true;

		try
		{
			loadResource(m_props, m_strPropFile);

			//Default Time Zone
			m_strDefaultZone = m_props.getProperty("DefaultZone", DEF_ZONE);

			String strTemp;

			//Refresh Time
			strTemp = m_props.getProperty("RefreshTime", Integer.toString(DEF_REF_TIME));
			try
			{
				m_nRefreshTime = Integer.parseInt(strTemp);
			}
			catch(NumberFormatException e1)
			{
				m_nRefreshTime = DEF_REF_TIME;
			}

			//Clock Type
			strTemp = m_props.getProperty("ClockType", Integer.toString(DEF_CLK_TYPE));
			try
			{
				m_nClockType = Integer.parseInt(strTemp);
			}
			catch(NumberFormatException e1)
			{
				m_nClockType = DEF_CLK_TYPE;
			}

			//Maximum no. of Clocks
			strTemp = m_props.getProperty("MaxClocks", Integer.toString(MAX_CLOCKS));
			try
			{
				m_nMaxClocks = Integer.parseInt(strTemp);
			}
			catch(NumberFormatException e1)
			{
				m_nMaxClocks = MAX_CLOCKS;
			}

			//Other Time Zones
			int i = 1;
			do
			{
				strTemp = m_props.getProperty("ZoneDisplay"+Integer.toString(i));
				if (strTemp != null)
					m_vDisplayZones.add(strTemp);
				i++;
			} while (strTemp != null);

			//Alarms
			i = 1;
			do
			{
				strTemp = m_props.getProperty("Alarm"+Integer.toString(i));
				MCAlarmInfo alarmInfo = MCAlarmInfo.StringToAlarmInfo(strTemp);
				if (alarmInfo != null)
					m_vAlarms.add(alarmInfo);
				i++;
			} while (strTemp != null);

		}
		catch(IOException e)
		{
			bRet = false;
		}

		return bRet;
	}

	/**
	* save the properties to the persistent media
	*/
	public boolean save()
	{
		m_props.setProperty("DefaultZone", m_strDefaultZone);
		m_props.setProperty("RefreshTime", Integer.toString(m_nRefreshTime));
		m_props.setProperty("ClockType", Integer.toString(m_nClockType));
		m_props.setProperty("MaxClocks", Integer.toString(m_nMaxClocks));

		int i;

		//Save other time zones
		for (i = 0; i < m_vDisplayZones.size(); i++)
		{
			m_props.setProperty("ZoneDisplay"+Integer.toString(i+1), (String)m_vDisplayZones.get(i));
		}

		//Delete extra elements (Time Zones)
		String strKey;
		while (true)
		{
			strKey = "ZoneDisplay"+Integer.toString(i+1);
			if (m_props.getProperty(strKey) == null)
				break;
			m_props.remove(strKey);
		}

		//Save Alarms
		for (i = 0; i < m_vAlarms.size(); i++)
		{
			m_props.setProperty("Alarm"+Integer.toString(i+1), ((MCAlarmInfo)m_vAlarms.get(i)).toString());
		}

		//Delete extra elements (Alarms)
		while (true)
		{
			strKey = "Alarm"+Integer.toString(i+1);
			if (m_props.getProperty(strKey) == null)
				break;
			m_props.remove(strKey);
		}

		boolean bRet = true;

		try
		{
			m_props.store(new FileOutputStream(m_strPropFile), "MultiClock Options");
		}
		catch(IOException e)
		{
			bRet = false;
		}

		return bRet;
	}
}
