package tools.multiclock;

import java.util.*;

public interface MCClockField
{
	public void setCalendarTime(Calendar cal);
	public void setTimeZone(TimeZone tz);
	public void enableToolTip(boolean bEnable);
	public void setEditable(boolean bEditable);
}
