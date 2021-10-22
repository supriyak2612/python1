package tools.multiclock;

import java.util.*;

public class MCTimeKeeper implements Runnable
{
	private Thread m_alarmTicker;

	protected Vector m_vAlarms;

	protected Calendar [] m_cal;
	protected boolean [] m_AlarmGiven;

	protected boolean m_bRunning = false;

	protected Vector m_vListeners = new Vector();

	protected int m_nID = -1;

	/**
	* Constructor
	*/
	public MCTimeKeeper()
	{
	}

	/**
	* Constructor, with specific ID
	*/
	public MCTimeKeeper(int nID)
	{
		m_nID = nID;
	}

	/**
	* Intializes the Alarms and Alarm Keeper
	*/
	public void initialize(Vector vAlarms)
	{
		setAlarms(vAlarms);

		m_alarmTicker = new Thread(this);
		m_alarmTicker.start();
	}

	/*
	* Sets the given alarms for time keeping
	*/
	public void setAlarms(Vector vAlarms)
	{
		boolean bSaveRunning = m_bRunning;

		m_bRunning = false; //supsend

		m_vAlarms = new Vector();

		m_cal = new Calendar[vAlarms.size()];
		m_AlarmGiven = new boolean[vAlarms.size()];

		MCAlarmInfo alarmInfo;
		TimeZone zone;
		Date curDate = new Date();

		for (int i = 0 ; i < vAlarms.size(); i++)
		{
			alarmInfo = new MCAlarmInfo((MCAlarmInfo)vAlarms.get(i));
			m_vAlarms.add(alarmInfo);
			zone = TimeZone.getTimeZone(alarmInfo.getTimeZone());
			m_cal[i] = Calendar.getInstance(zone);
			m_cal[i].setTime(curDate);
			m_AlarmGiven[i] = false;
		}

		m_bRunning = bSaveRunning; //resume
	}

	/**
	* Start background alarm checking
	*/
	public void startWatch() { m_bRunning = true; }

	/**
	* Stop background alarm checking
	*/
	public void stopWatch() { m_bRunning = false; }

	/**
	* add time listener
	*/
	public void addTimeListener(MCTimeListener l)
	{
		m_vListeners.add(l);
	}

	/**
	* remove time listener
	*/
	public void removeTimeListener(MCTimeListener l)
	{
		m_vListeners.remove(l);
	}

	/**
	* notify alarm event to the listeners
	*/
	public void notifyAlarmBeep(MCAlarmInfo alarmInfo, Date curDate)
	{
		int i;
		MCTimeListener l;

		for (i = 0; i < m_vListeners.size(); i++)
		{
			l = (MCTimeListener)m_vListeners.get(i);
			l.alarmAlert(m_nID, alarmInfo, curDate);
		}
	}

	/**
	* Check the alarms and decide if alarm events need to be triggered
	*/
	protected void checkAlarms()
	{
		int i;
		int nHour, nMin, nSec;
		int nDay, nMonth, nYear, nWeekDay;

		int DaysInMonth[] = {
					31, //Jan
					28, //Feb
					31, //Mar
					30, //Apr
					31, //May
					30, //Jun
					31, //Jul
					31, //Aug
					30, //Sep
					31, //Oct
					30, //Nov
					31 //Dec
		};

		Date curDate = new Date();
		MCAlarmInfo alarmInfo;
		int nType;
		Calendar cal;

		for (i = 0; i < m_vAlarms.size(); i++)
		{
			//System.out.println("Checking Alarm...");

			if (m_AlarmGiven[i])
				continue;

			alarmInfo = new MCAlarmInfo((MCAlarmInfo)m_vAlarms.get(i));
			cal = m_cal[i];

			cal.setTime(curDate);

			nDay = cal.get(Calendar.DAY_OF_MONTH);
			nMonth = cal.get(Calendar.MONTH);
			nYear = cal.get(Calendar.YEAR);
			nWeekDay = cal.get(Calendar.DAY_OF_WEEK);

			nType = alarmInfo.getType();

			if (nType == MCAlarmInfo.AT_WEEK_DAY && nWeekDay != alarmInfo.getWeekDay())
				continue;

			if (nType == MCAlarmInfo.AT_MONTH_DATE && nDay != alarmInfo.getMonthDate())
				continue;

			if (nType == MCAlarmInfo.AT_MONTH_START && nDay != 1)
				continue;

			if (nType == MCAlarmInfo.AT_MONTH_END)
			{
				int nLastDay;

				nLastDay = DaysInMonth[nMonth];
				if (nMonth == 1 && (nYear%4) == 0)
					nLastDay++; //for feb

				if (nDay != nLastDay)
					continue;
			}

			if (nType == MCAlarmInfo.AT_DATE)
			{
				Date date = alarmInfo.getDate();
				TimeZone zone = TimeZone.getTimeZone(alarmInfo.getTimeZone());
				Calendar cal1 = Calendar.getInstance(zone);

				cal1.setTime(date);

				int nDay1, nMonth1, nYear1;

				nDay1 = cal1.get(Calendar.DAY_OF_MONTH);
				nMonth1 = cal1.get(Calendar.MONTH);
				nYear1 = cal1.get(Calendar.YEAR);

				if (nYear != nYear1 || nMonth != nMonth1 || nDay != nDay1)
					continue;
			}

			nHour = cal.get(Calendar.HOUR_OF_DAY);
			nMin = cal.get(Calendar.MINUTE);
			nSec = cal.get(Calendar.SECOND);

			//System.out.println("Checking Time...");
			if (nHour == alarmInfo.getHour() && nMin == alarmInfo.getMinute())
			{
				notifyAlarmBeep(alarmInfo, curDate);
				m_AlarmGiven[i] = true;
			}
		}
	}

	/**
	* run method for the background thread
	*/
	public void run()
	{
		while (true)
		{
			if (m_bRunning)
			{
				checkAlarms(); //Check for alarms
			}

			try
			{
				Thread.currentThread().sleep(5*1000);
			}
			catch(InterruptedException e) {}
		}
	}
}
