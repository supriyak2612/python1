package tools.multiclock;

import java.util.*;

import utils.gwt.GValidator;

public class MCAlarmInfo
{
	public static final int AT_NONE = -1;
	public static final int AT_DAILY = 0;
	public static final int AT_WEEK_DAY = 1;
	public static final int AT_MONTH_DATE = 2;
	public static final int AT_MONTH_START = 3;
	public static final int AT_MONTH_END = 4;
	public static final int AT_DATE = 5;

	private int m_nType = AT_NONE;
	private String m_strTitle = "";
	private int m_nTimeHour, m_nTimeMin;
	private String m_strTimeZone = "%%%";
	private Object m_oData; //Date or Week day or Month date

	private static final String DATE_SEP = "-";
	private static final String TIME_SEP = "-";
	private static final String FIELD_SEP = ",";

	/**
	* Constructor - empty
	*/
	public MCAlarmInfo()
	{
	}

	/**
	* Constructor - using the given Alarm options
	*/
	public MCAlarmInfo(int nType, String strTitle, int nHour, int nMin,
						String strTimeZone, Object oData)
	{
		setType(nType);

		setTitle(strTitle);

		setHour(nHour);
		setMinute(nMin);
		setTimeZone(strTimeZone);

		setData(oData);
	}

	/**
	* Constructor - using the given Alarm Info
	*/
	public MCAlarmInfo(MCAlarmInfo srcInfo)
	{
		copyFrom(srcInfo);
	}

	/**
	* Constructor - using the given text
	*/
	public MCAlarmInfo(String strTextInfo)
	{
		MCAlarmInfo srcInfo = MCAlarmInfo.StringToAlarmInfo(strTextInfo);
		copyFrom(srcInfo);
	}

	/**
	* copies the data from the source alarm info
	*/
	protected void copyFrom(MCAlarmInfo srcInfo)
	{
		setType(srcInfo.getType());

		setTitle(srcInfo.getTitle());

		setHour(srcInfo.getHour());
		setMinute(srcInfo.getMinute());
		setTimeZone(srcInfo.getTimeZone());

		setData(srcInfo.getData());
	}

	/**
	* set/get the type of the alarm info
	*/
	public void setType(int nType) { m_nType = nType; }
	public int getType() { return m_nType; }

	/**
	* set/get the title of the alarm info
	*/
	public void setTitle(String strTitle) { m_strTitle = strTitle; }
	public String getTitle() { return m_strTitle; }

	/**
	* set/get the hour of the alarm info
	*/
	public void setHour(int nHour) { m_nTimeHour = nHour; }
	public int getHour() { return m_nTimeHour; }

	/**
	* set/get the minute of the alarm info
	*/
	public void setMinute(int nMinute) { m_nTimeMin = nMinute; }
	public int getMinute() { return m_nTimeMin; }

	/**
	* set/get the time zone of the alarm info
	*/
	public void setTimeZone(String strTimeZone) { m_strTimeZone = strTimeZone; }
	public String getTimeZone() { return m_strTimeZone; }

	/**
	* set/get the data of the alarm info
	*/
	public void setData(Object oData) { m_oData = oData; }
	public Object getData() { return m_oData; }

	/**
	* returns the day of the week, if applicable
	*/
	public int getWeekDay()
	{
		if (m_nType == AT_WEEK_DAY)
			return ((Integer)m_oData).intValue();
		else
			return -1;
	}

	/**
	* returns the day of the month, if applicable
	*/
	public int getMonthDate()
	{
		if (m_nType == AT_MONTH_DATE)
			return ((Integer)m_oData).intValue();
		else
			return -1;
	}

	/**
	* returns the date, if applicable
	*/
	public Date getDate()
	{
		if (m_nType == AT_DATE)
			return (Date)m_oData;
		else
			return null;
	}

	/**
	* sets daily alarm and options
	*/
	public void setDailyTime(int nHour, int nMin)
	{
		setType(AT_DAILY);
		setHour(nHour);
		setMinute(nMin);
		setData(null);
	}

	/**
	* sets weekly alarm and options
	*/
	public void setWeekDayTime(int nHour, int nMin, int nWeekDay)
	{
		setType(AT_WEEK_DAY);
		setHour(nHour);
		setMinute(nMin);
		setData(new Integer(nWeekDay));
	}

	/**
	* sets monthly alarm and options
	*/
	public void setMonthDateTime(int nHour, int nMin, int nMonthDate)
	{
		setType(AT_MONTH_DATE);
		setHour(nHour);
		setMinute(nMin);
		setData(new Integer(nMonthDate));
	}

	/**
	* sets alarm at month beginning
	*/
	public void setMonthStartTime(int nHour, int nMin)
	{
		setType(AT_MONTH_START);
		setHour(nHour);
		setMinute(nMin);
		setData(null);
	}

	/**
	* sets alarm at month end
	*/
	public void setMonthEndTime(int nHour, int nMin)
	{
		setType(AT_MONTH_END);
		setHour(nHour);
		setMinute(nMin);
		setData(null);
	}

	/**
	* sets alarm at a specified date and time
	*/
	public void setDateTime(int nHour, int nMin, Date date)
	{
		setType(AT_DATE);
		setHour(nHour);
		setMinute(nMin);
		setData(date);
	}

	/**
	* sets alarm at a specified date and time
	*/
	public void setDateTime(Date date)
	{
		int nHour, nMin;

		Calendar cal = Calendar.getInstance(); //Get calender
		cal.setTime(date); //Set given date
		nHour = cal.get(Calendar.HOUR_OF_DAY);
		nMin = cal.get(Calendar.MINUTE);

		setDateTime(nHour, nMin, date);
	}

	/**
	* returns the string equivalent of the Alarm Info
	*/
	public String toString()
	{
		if (m_nType == AT_NONE)
			return "ERROR";

		String str;

		//Format : <type>,<title>,<time>,<zone>,[<date>|<week day>|<month date>]
		//Time is in "hh:mm" format; Date in "mm-dd-yyyy" format

		str = Integer.toString(m_nType);
		str = str + FIELD_SEP;

		str = str + m_strTitle;
		str = str + FIELD_SEP;

		str = str + Integer.toString(m_nTimeHour);
		str = str + TIME_SEP;
		str = str + Integer.toString(m_nTimeMin);
		str = str + FIELD_SEP;
		str = str + m_strTimeZone;

		if (m_oData != null)
			str = str + FIELD_SEP;

		if (m_nType == AT_DATE)
			str = str + GValidator.DateToString((Date)m_oData);
		else if (m_nType == AT_WEEK_DAY || m_nType == AT_MONTH_DATE)
			str = str + ((Integer)m_oData).toString();

		return str;
	}

	/**
	* returns the Alarm Info after parsing and converting the given text format
	*/
	public static MCAlarmInfo StringToAlarmInfo(String strText)
	{
		if (strText == null || strText.length() <= 0)
			return null;

		StringTokenizer tok = new StringTokenizer(strText, FIELD_SEP);

		int nTokens = tok.countTokens();

		if (nTokens != 4 && nTokens != 5)
			return null;

		String strTitle, strType, strTime, strHour, strMin, strZone;
		String strData = null;

		strType = tok.nextToken();
		strTitle = tok.nextToken();
		strTime = tok.nextToken();
		strZone = tok.nextToken();
		if (nTokens == 5)
			strData = tok.nextToken();

		int nType, nHour, nMin;

		try
		{
			nType = Integer.parseInt(strType);
		}
		catch(NumberFormatException e)
		{
			return null;
		}

		StringTokenizer tokTime = new StringTokenizer(strTime, TIME_SEP);
		if (tokTime.countTokens() != 2)
			return null;

		strHour = tokTime.nextToken();
		strMin = tokTime.nextToken();

		try
		{
			nHour = Integer.parseInt(strHour);
			nMin = Integer.parseInt(strMin);
		}
		catch(NumberFormatException e)
		{
			return null;
		}

		int nData = 0;
		Date date = null;
		if (strData != null)
		{
			if (nType == AT_DATE)
			{
				date = GValidator.StringToDate(strData);
			}
			else
			{
				try
				{
					nData = Integer.parseInt(strData);
				}
				catch(NumberFormatException e)
				{
					return null;
				}
			}
		}

		MCAlarmInfo alarmInfo = null;

		switch(nType)
		{
			case AT_DAILY:
			{
				alarmInfo = new MCAlarmInfo();
				alarmInfo.setDailyTime(nHour, nMin);
				break;
			}
			case AT_WEEK_DAY:
			{
				alarmInfo = new MCAlarmInfo();
				alarmInfo.setWeekDayTime(nHour, nMin, nData);
				break;
			}
			case AT_MONTH_DATE:
			{
				alarmInfo = new MCAlarmInfo();
				alarmInfo.setMonthDateTime(nHour, nMin, nData);
				break;
			}
			case AT_MONTH_START:
			{
				alarmInfo = new MCAlarmInfo();
				alarmInfo.setMonthStartTime(nHour, nMin);
				break;
			}
			case AT_MONTH_END:
			{
				alarmInfo = new MCAlarmInfo();
				alarmInfo.setMonthEndTime(nHour, nMin);
				break;
			}
			case AT_DATE:
			{
				alarmInfo = new MCAlarmInfo();
				alarmInfo.setDateTime(nHour, nMin, date);
				break;
			}
			default:
				; //do nothing; null will be returned
		}

		if (alarmInfo != null)
		{
			alarmInfo.setTimeZone(strZone);
			alarmInfo.setTitle(strTitle);
		}

		return alarmInfo;
	}
}
