package tools.multiclock;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

import java.util.*;

public class MCDigitalClock extends JComponent implements MCClockField
{
	protected TimeZone m_timeZone;
	protected Calendar m_cal;

	protected Color m_bkgColorAM = null; //background color for AM time
	protected Color m_bkgColorPM = null; //background color for PM time

	protected Color m_brdrColor = null; //border color
	protected Color m_textColor = null; //text color
	protected Font  m_textFont = null; //text font

	/**
	* Clock component constructor
	*/
	public MCDigitalClock()
	{
	}

	/**
	* sets/gets the time zone of the clock
	*/
	public void setTimeZone(TimeZone timeZone) { m_timeZone = timeZone; }
	public TimeZone getTimeZone() { return m_timeZone; }

	/**
	* enables of disables the tooltip
	*/
	public void enableToolTip(boolean bEnable)
	{
		if (bEnable)
			setToolTipText("TIME");
		else
			setToolTipText(null);
		ToolTipManager.sharedInstance().registerComponent(this);
	}

	/**
	* returns the tooltip of the clock component
	*/
	public String getToolTipText()
	{
		if (m_timeZone == null)
			return super.getToolTipText();

		int nOffSec = m_timeZone.getRawOffset() / 1000;

		int nHour, nMin;

		nHour = Math.abs(nOffSec) / 3600;
		nMin = (Math.abs(nOffSec) % 3600) / 60;

		String strTime = Integer.toString(nHour) + ":" + Integer.toString(nMin);

		String strToolTip = "GMT";
		if (nOffSec < 0)
			strToolTip = strToolTip + "-" + strTime;
		else
			strToolTip = strToolTip + "+" + strTime;

		if (m_cal != null)
		{
			//Date curDate = new Date();
			Date curDate = m_cal.getTime();

			if (m_timeZone.inDaylightTime(curDate))
				strToolTip = strToolTip + " (Daylight)";

			int hourDay = m_cal.get(Calendar.HOUR_OF_DAY);
			if (hourDay < 12)
				strToolTip = strToolTip + ", AM";
			else
				strToolTip = strToolTip + ", PM";
		}

		return strToolTip;
	}

	/**
	* returns the tooltip for the given mouse position
	*/
	public String getToolTipText(MouseEvent me)
	{
		if (m_timeZone == null)
			return super.getToolTipText(me);

		return getToolTipText();
	}

	/**
	* Sets the display text
	*/
    public void setCalendarTime(Calendar cal)
    {
		setTime(cal, true);
	}

	/**
	* Sets the display text
	*/
	private void setTime(Calendar cal, boolean bRedraw)
    {
    	m_cal = cal;
    	if (bRedraw)
    	{
    		revalidate();
    		repaint();
    	}
    }

	/**
	* Sets the editable flag
	*/
	public void setEditable(boolean bFlag)
	{
		//do nothing
	}

	/**
	* overriden method that paints the component
	*/
    protected void paintComponent(Graphics g)
	{
		if (m_bkgColorAM == null)
			m_bkgColorAM = new Color(204, 255, 204);
		if (m_bkgColorPM == null)
			m_bkgColorPM = new Color(255, 255, 179);
		if (m_brdrColor == null)
			m_brdrColor = Color.black;
		if (m_textColor == null)
			m_textColor = new Color(128, 0, 0);
		if (m_textFont == null)
		{
			m_textFont = g.getFont().deriveFont(Font.BOLD);
		}

		int hourDay = m_cal.get(Calendar.HOUR_OF_DAY);

		//Draw background
		g.setColor((hourDay < 12) ? m_bkgColorAM : m_bkgColorPM);

		g.fillOval(0, 0, getWidth()-1, getHeight()-1);
		g.setColor(m_brdrColor);
		g.drawOval(0, 0, getWidth()-1, getHeight()-1);

		String strText = getTimeString(m_cal);

		if (strText != null)
		{
			g.setColor(m_textColor);
			g.setFont(m_textFont);
			g.drawString(strText, 12, getHeight()-8);
		}
	}

	/**
	* overriden method that paints the border
	*/
	public void paintBorder(Graphics g)
	{
		/*
		if (m_brdrColor == null)
			m_brdrColor = Color.black;
		g.setColor(m_brdrColor);
		g.drawOval(0, 0, getWidth()-1, getHeight()-1);
		*/
	}

	/**
	* returns the preferred size of the control
	*/
	public Dimension getPreferredSize()
	{
		return new Dimension(75, 25);
	}

	/**
	*  returns the calendar time as string
	*/
	private String getTimeString(Calendar cal)
	{
	 return getTimeString(cal, true);
	}

	/**
	* returns the calendar time as string
	*/
	private String getTimeString(Calendar cal, boolean bIncludeSec)
	{
		int nHour, nMin, nSec;

		nHour = cal.get(Calendar.HOUR_OF_DAY);
		nMin = cal.get(Calendar.MINUTE);
		nSec = cal.get(Calendar.SECOND);

		String strTime = Integer.toString(nHour) + ":" + Integer.toString(nMin);
		if (bIncludeSec)
			strTime = strTime + ":" + Integer.toString(nSec);

		return strTime;
	}
}
