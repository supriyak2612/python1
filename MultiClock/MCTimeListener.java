package tools.multiclock;

import java.util.Date;

public interface MCTimeListener
{
	/**
	* callback method for alarm notification
	*/
	public void alarmAlert(int nID, MCAlarmInfo alarmInfo, Date curDate);
}
