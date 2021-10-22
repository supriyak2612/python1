package tools.multiclock;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

import java.util.*;
import java.io.*;

import utils.gwt.*;

public class MCDisplayPanel extends JPanel
  			implements ActionListener, ItemListener, Runnable, KeyListener, MCTimeListener
{
	private GTable m_tableAlerts;

	String m_colNames[] = {"Title", "Type", "Options", "Alarm Time", "Received Date"};

	Image m_imageAlertNew, m_imageAlertAck;

  	private Thread m_clockTicker;

	private JFrame m_parentFrame;
	private boolean m_bAutoRefresh = true;

	JPanel m_panelClock, m_controlPanel;
	JSplitPane m_splitPane;
	int m_nDividerLoc;

	MCClockField [] m_textTime;
	TimeZone [] m_timeZone;
	Calendar [] m_cal;

	MCTimeField m_textQuery, m_textResult;
	JComboBox m_comboQuery, m_comboResult;

	JButton m_buttonQuery;
	JButton m_btnAckAlert, m_btnDelAlert, m_btnClearAlert;

	JTextArea m_textAlerts;

	JCheckBoxMenuItem m_miAnalogClkView, m_miDigitalClkView, m_miAlertView;
	JPanel m_alertPanel;

	String [] m_strTimeZoneIDs;

	int m_nRefreshTime, m_nClockType;
	String m_strDefaultZone;

	static String DEF_QRY_ZONE = "EST";
	static String DEF_RES_ZONE = "IST";

  /**
  * Panel constructor
  */
  public MCDisplayPanel(JFrame frame, String [] strTimeZoneIDs,
  				String strDefaultZone, int nRefreshTime, int nClockType)
  {
	m_parentFrame = frame;

	m_strTimeZoneIDs = strTimeZoneIDs;

	m_nRefreshTime = nRefreshTime;

	m_nClockType = nClockType;

	m_strDefaultZone = strDefaultZone;

	setLayout(new BorderLayout());

	frame.getContentPane().add(this, "Center");

	frame.getContentPane().add(makeMenuBar(), "North");

	m_controlPanel = makeControlPanel();

	JScrollPane ctrlScrollPane = new JScrollPane(m_controlPanel);
	ctrlScrollPane.setPreferredSize(new Dimension(800, 215));

	JScrollBar sbar;
	sbar = ctrlScrollPane.getHorizontalScrollBar();
	if (sbar != null)
		sbar.setUnitIncrement(16);
	sbar = ctrlScrollPane.getVerticalScrollBar();
	if (sbar != null)
		sbar.setUnitIncrement(34);

	m_alertPanel = makeAlertPanel();

	JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	splitPane.setDividerSize(2);
	splitPane.setTopComponent(ctrlScrollPane);
	splitPane.setBottomComponent(m_alertPanel);
	splitPane.setDividerLocation(215);

	m_splitPane = splitPane;
	m_nDividerLoc = 215;

	frame.getContentPane().add(splitPane, "Center");

	m_clockTicker = new Thread(this);
	m_clockTicker.start();

	MultiClock.getApp().getTimeKeeper().addTimeListener(this);

	m_imageAlertNew = MultiClock.getImage("AlertNew.gif");
	m_imageAlertAck = MultiClock.getImage("AlertAck.gif");
  }

	/**
	* enable/disable background clock refresh
	*/
	private void enableAutoRefresh(boolean bEnable) { m_bAutoRefresh = bEnable; }

	/**
	* refresh the clock display
	*/
  	public void refreshClock(int clockID)
	{
		int i;

		Date curDate = new Date();
		long lTime = curDate.getTime();

		for (i = 0; i < m_cal.length; i++)
		{
			m_cal[i].setTime(curDate);
			m_textTime[i].setCalendarTime(m_cal[i]);
		}
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

	/**
	*  Makes the User Interface.
	*/
	private JPanel makeControlPanel()
	{
		JPanel controlPanel, dataPanel, whatIFPanel;
		int i;

		controlPanel = new JPanel();
		controlPanel.setLayout(new BorderLayout());

		m_comboQuery = new JComboBox();
		m_comboResult = new JComboBox();

		String [] timeZoneIDs = TimeZone.getAvailableIDs();

		Vector vTimeZones;
		int j;
		String strTemp;

		vTimeZones = new Vector();

		for (i = 0; i < timeZoneIDs.length; i++)
		{
			boolean bInserted = false;
			String strTimeID = (String)timeZoneIDs[i];
			for (j = 0; j < vTimeZones.size(); j++)
			{
				strTemp = (String)vTimeZones.get(j);
				if (strTemp.compareTo(strTimeID) >= 0)
				{
					vTimeZones.insertElementAt(strTimeID, j);
					bInserted = true;
					break;
				}
			}
			if (!bInserted)
				vTimeZones.add(strTimeID);
		}

		for (i = 0; i < vTimeZones.size(); i++)
		{
			m_comboQuery.addItem(vTimeZones.get(i));
			m_comboResult.addItem(vTimeZones.get(i));
		}

		m_comboQuery.setSelectedItem(DEF_QRY_ZONE);
		m_comboResult.setSelectedItem(DEF_RES_ZONE);

		m_comboQuery.addItemListener(this);
		m_comboResult.addItemListener(this);

		whatIFPanel = new JPanel();
		whatIFPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		whatIFPanel.add(m_comboQuery);

		m_textQuery = new MCTimeField("", 5);
		m_textQuery.addKeyListener(this);
		m_textQuery.setTimeZone(TimeZone.getTimeZone(DEF_QRY_ZONE));
		m_textQuery.enableToolTip(true);
		whatIFPanel.add(m_textQuery);

		whatIFPanel.add(new JLabel("Equal to:"));

		whatIFPanel.add(m_comboResult);

		m_buttonQuery = new JButton("Show Time");
		m_buttonQuery.addActionListener(this);
		m_buttonQuery.setIcon(MultiClock.getIcon("Time.gif"));
		whatIFPanel.add(m_buttonQuery);

		m_textResult = new MCTimeField("", 5);
		m_textResult.setEditable(false);
		m_textResult.setTimeZone(TimeZone.getTimeZone(DEF_RES_ZONE));
		m_textResult.enableToolTip(true);
		whatIFPanel.add(m_textResult);

		m_panelClock = makeClockPanel();

		controlPanel.add(whatIFPanel, "North");
		controlPanel.add(m_panelClock, "South");

		return controlPanel;
	}

	/**
	* makes user interface for Alarm Alerts display
	*/
	private JPanel makeAlertPanel()
	{
		//Alert Panel
		JPanel alertPanel = new JPanel();
		alertPanel.setLayout(new BorderLayout());

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		topPanel.add("West", new JLabel("Alerts:"));
		topPanel.add("East", makeToolbarPanel());
		alertPanel.add("North", topPanel);

		GTableCell rowData[][] = new GTableCell[0][m_colNames.length];

		GTableModel tm = new GTableModel(rowData, m_colNames);
		m_tableAlerts = new GTable(tm);

		JScrollPane scrollPane = new JScrollPane(m_tableAlerts);
		//scrollPane.setPreferredSize(m_tableAlerts.getPreferredSize());
		scrollPane.setPreferredSize(new Dimension(300, 100));
		alertPanel.add(scrollPane, "Center");

		return alertPanel;
	}

	/**
	* creates a table row from the given alarm info
	*/
	public GTableCell [] createAlertRow(MCAlarmInfo aInfo, Date curDate)
	{
		GTableCell rowData[];

		rowData = new GTableCell [m_colNames.length];

		String WeekDays [] = {
			"Sunday",
			"Monday",
			"Tuesday",
			"Wednesday",
			"Thursday",
			"Friday",
			"Saturday"
		};

		String strType = "Unknown";
		String strOptions = "";
		String strTime = "";
		switch(aInfo.getType())
		{
			case MCAlarmInfo.AT_DAILY:
				strType = "Daily";
				break;
			case MCAlarmInfo.AT_WEEK_DAY :
			{
				strType = "Weekly";
				Integer weekDay = (Integer)aInfo.getData();
				strOptions = WeekDays[weekDay.intValue()-1];
				break;
			}
			case MCAlarmInfo.AT_MONTH_DATE :
			{
				strType = "Monthly";
				Integer monthDay = (Integer)aInfo.getData();
				strOptions = monthDay.toString();
				break;
			}
			case MCAlarmInfo.AT_MONTH_START :
				strType = "Month Start";
				break;
			case MCAlarmInfo.AT_MONTH_END :
				strType = "Month End";
				break;
			case MCAlarmInfo.AT_DATE :
			{
				strType = "Dated";
				Date date = (Date)aInfo.getData();
				strOptions = GValidator.DateToString(date);
				break;
			}
		}

		String strData;

		strData = aInfo.getTitle();
		rowData[0] = new GTableCell(strData == null ? "" : strData);
		rowData[1] = new GTableCell(strType);
		rowData[2] = new GTableCell(strOptions);
		if (strType.length() > 0)
		{
			strTime = Integer.toString(aInfo.getHour()) + ":" + Integer.toString(aInfo.getMinute());
			strTime = strTime + " " + aInfo.getTimeZone();
		}
		rowData[3] = new GTableCell(strTime);
		rowData[4] = new GTableCell(GValidator.DateToString(curDate));

		return rowData;
	}

	/**
	* makes user interface for clock display
	*/
	private JPanel makeClockPanel()
	{
		JPanel dataPanel = new JPanel();
		int i;

		int nZoneCount = 1;

		if (m_strTimeZoneIDs != null)
			nZoneCount = nZoneCount + m_strTimeZoneIDs.length;

		if (m_nClockType == 1)
			dataPanel.setLayout(new GridLayout(0, 2, 1, 1));
		else
			dataPanel.setLayout(new GridLayout(0, 4 /*nZoneCount*/, 1, 1));

		m_timeZone = new TimeZone[nZoneCount];
		m_cal = new Calendar[nZoneCount];
		m_textTime = new MCClockField[nZoneCount];

		/* APIs
		String [] getAvailableIDs();
		tz.getID();
		tz.getDisplayName();
		getTimeZone(id);
		*/

		Date curDate = new Date();
		JLabel label;

		for (i = 0; i < nZoneCount; i++)
		{
			if (i == 0)
			{
				if (m_strDefaultZone.equals("%%%"))
					m_timeZone[i] = TimeZone.getDefault();
				else
					m_timeZone[i] = TimeZone.getTimeZone(m_strDefaultZone);
			}
			else
				m_timeZone[i] = TimeZone.getTimeZone(m_strTimeZoneIDs[i-1]);

			m_cal[i] = Calendar.getInstance(m_timeZone[i]);
			m_cal[i].setTime(curDate);

			String strText = m_timeZone[i].getDisplayName();
			strText = strText + "(" + m_timeZone[i].getID() + ")";

			label = new JLabel(strText);

			if (m_nClockType == 1)
				m_textTime[i] = new MCDigitalClock();
			else
				m_textTime[i] = new MCStdClock(GToolkit.getImage("WiproLogo.jpg"));

			m_textTime[i].setEditable(false);
			m_textTime[i].setTimeZone(m_timeZone[i]);
			m_textTime[i].enableToolTip(true);

			m_textTime[i].setCalendarTime(m_cal[i]);

			if (i == 0)
			{ //set color for default time
				label.setForeground(Color.blue);
			}

			if (m_nClockType == 1)
			{
				JPanel tempPanel = new JPanel();
				tempPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
				tempPanel.add((JComponent)m_textTime[i]);

				dataPanel.add(label);
				dataPanel.add(tempPanel);
			}
			else
			{
				JPanel tempPanel = new JPanel();
				tempPanel.setLayout(new BorderLayout());
				tempPanel.add(label, "North");
				tempPanel.add((JComponent)m_textTime[i], "South");

				dataPanel.add(tempPanel);
			}
		}

		return dataPanel;
	}

	/**
	* makes menu user interface
	*/
	private JMenuBar makeMenuBar()
	{
		JMenuBar menuBar;
		JMenu menu;
		JMenuItem menuItem;
		ImageIcon icon;

		menuBar = new JMenuBar();

		menu = new JMenu("File");

		menuItem = new JMenuItem("Exit");
		menuItem.setActionCommand("Exit");
		menuItem.addActionListener(this);
		icon = MultiClock.getIcon("Exit.gif");
		if (icon != null)
			menuItem.setIcon(icon);
		menu.add(menuItem);

		menuBar.add(menu);

		menu = new JMenu("View");

		menuItem = new JCheckBoxMenuItem("Analog Clock");
		menuItem.setActionCommand("AnalogClock");
		menuItem.addActionListener(this);
		((JCheckBoxMenuItem)menuItem).setState(m_nClockType == 0);
		menu.add(menuItem);
		m_miAnalogClkView = (JCheckBoxMenuItem)menuItem;

		menuItem = new JCheckBoxMenuItem("Digital Clock");
		menuItem.setActionCommand("DigitalClock");
		menuItem.addActionListener(this);
		((JCheckBoxMenuItem)menuItem).setState(m_nClockType == 1);
		menu.add(menuItem);
		m_miDigitalClkView = (JCheckBoxMenuItem)menuItem;

		menu.add(new JSeparator());

		menuItem = new JCheckBoxMenuItem("Alert View");
		menuItem.setActionCommand("AlertView");
		menuItem.addActionListener(this);
		((JCheckBoxMenuItem)menuItem).setState(true);
		menu.add(menuItem);
		m_miAlertView = (JCheckBoxMenuItem)menuItem;

		menuBar.add(menu);

		menu = new JMenu("Tools");

		menuItem = new JMenuItem("Alarms");
		menuItem.setActionCommand("Alarms");
		menuItem.addActionListener(this);
		icon = MultiClock.getIcon("Time.gif");
		if (icon != null)
			menuItem.setIcon(icon);
		menu.add(menuItem);

		menuItem = new JMenuItem("Customize");
		menuItem.setActionCommand("Customize");
		menuItem.addActionListener(this);
		icon = MultiClock.getIcon("Options.gif");
		if (icon != null)
			menuItem.setIcon(icon);
		menu.add(menuItem);

		menuBar.add(menu);

		menu = new JMenu("Help");

		menuItem = new JMenuItem("Help Contents");
		menuItem.setActionCommand("Help");
		menuItem.addActionListener(this);
		icon = MultiClock.getIcon("Help.gif");
		if (icon != null)
			menuItem.setIcon(icon);
		menu.add(menuItem);

		menuItem = new JMenuItem("About MultiClock");
		menuItem.setActionCommand("About");
		menuItem.addActionListener(this);
		icon = MultiClock.getIcon("About.gif");
		if (icon != null)
			menuItem.setIcon(icon);
		menu.add(menuItem);

		menuBar.add(menu);

		return menuBar;
	}

	/**
	* makes toolbar user interface
	*/
	private JPanel makeToolbarPanel()
	{
		JPanel controlPanel = new JPanel();

		controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		m_btnAckAlert = new JButton(); //new JButton("Acknowledge");
		m_btnAckAlert.setToolTipText("Acknowledge");
		m_btnAckAlert.setMargin(new Insets(0, 0, 0, 0));
		m_btnAckAlert.setIcon(MultiClock.getIcon("Ack.gif"));
		m_btnAckAlert.addActionListener(this);
    	controlPanel.add(m_btnAckAlert);

    	m_btnDelAlert = new JButton(); //new JButton("Delete");
    	m_btnDelAlert.setToolTipText("Delete");
		m_btnDelAlert.setMargin(new Insets(0, 0, 0, 0));
		m_btnDelAlert.setIcon(MultiClock.getIcon("Delete.gif"));
		m_btnDelAlert.addActionListener(this);
    	controlPanel.add(m_btnDelAlert);

    	m_btnClearAlert = new JButton(); //new JButton("Clear All");
    	m_btnClearAlert.setToolTipText("Clear All");
		m_btnClearAlert.setMargin(new Insets(0, 0, 0, 0));
		m_btnClearAlert.setIcon(MultiClock.getIcon("ClearAll.gif"));
		m_btnClearAlert.addActionListener(this);
    	controlPanel.add(m_btnClearAlert);

    	return controlPanel;
	}

  /**
   * Gets the dimensions.
   */
  public Dimension getPreferredSize()
  {
    return new Dimension(300,300);
  }


  /**
  * Action event handler to handle custom button/menu click events
  */
  public void actionPerformed(ActionEvent ev)
  {
	  Object oSrc = ev.getSource();

    if (oSrc == m_buttonQuery) {
      doTimeQuery();
    }
    else if (oSrc == m_btnAckAlert) {
	      doAckAlert();
    }
    else if (oSrc == m_btnDelAlert) {
	      doDeleteAlert();
    }
    else if (oSrc == m_btnClearAlert) {
	      doClearAllAlerts();
    }
    else if (oSrc instanceof JMenuItem)
	{
		String strCmd = ((JMenuItem)oSrc).getActionCommand();

		if (strCmd.equals("Exit"))
			doExit();
		else if (strCmd.equals("AnalogClock"))
			doClockView(0);
		else if (strCmd.equals("DigitalClock"))
			doClockView(1);
		else if (strCmd.equals("AlertView"))
			doAlertView();
		else if (strCmd.equals("Alarms"))
			doAlarms();
		else if (strCmd.equals("Customize"))
			doCustomize();
		else if (strCmd.equals("Help"))
			doHelp();
		else if (strCmd.equals("About"))
			doAbout();
		else
			showMessage("Sorry! Not implemented yet.", "MultiClock");
	}
  }

	/**
	* handler for combo selection events
	*/
	public void itemStateChanged(ItemEvent e)
	{
	 Object oSrc = e.getSource();

	 if (oSrc == m_comboQuery)
	 {
		 String strZone = (String)m_comboQuery.getSelectedItem();
		 m_textQuery.setTimeZone(TimeZone.getTimeZone(strZone));

		 //update the query result dynamically
		  if (validateQueryTime(true))
			showQueryResult();
	 }
	 else if (oSrc == m_comboResult)
	 {
		 String strZone = (String)m_comboResult.getSelectedItem();
		 m_textResult.setTimeZone(TimeZone.getTimeZone(strZone));

		 //update the query result dynamically
		 if (validateQueryTime(true))
			showQueryResult();
	 }
	}

	/**
	* Display message
	*/
	private void showMessage(String strMsg, String strTitle)
	{
		JOptionPane.showMessageDialog(m_parentFrame/*this*/, strMsg,strTitle,JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	* Help command handler
	*/
	private void doHelp()
	{
		MCHelpDialog dlgHelp = new MCHelpDialog(m_parentFrame, "Help", true);
		dlgHelp.doDialog();
	}

	/**
	* About command handler
	*/
	private void doAbout()
	{
		showMessage("MultiClock Application (ver 1.0)\nDeveloped at Wipro Technologies for internal use only.", "About MultiClock");
	}

	/**
	* Exit command handler
	*/
	private void doExit()
	{
		MultiClock.getApp().exitClockDisplay();
	}

	/**
	* Clock View (Analog/digital) command handler
	*/
	private void doClockView(int nClockType)
	{
		//Toggle between Analog and Digital
		if (m_nClockType == 0)
			m_nClockType = 1;
		else
			m_nClockType = 0;
		m_miAnalogClkView.setState(m_nClockType == 0);
		m_miDigitalClkView.setState(m_nClockType == 1);

		MCProperties props = MultiClock.getApp().getProperties();
		props.m_nClockType = m_nClockType;
		props.save(); //Save the options persistently

		enableAutoRefresh(false); //disable background refresh

		m_controlPanel.remove(m_panelClock);
		m_panelClock = makeClockPanel();
		m_controlPanel.add(m_panelClock, "Center");
		m_controlPanel.validate();
		repaint(0);

		enableAutoRefresh(true); //enable background refresh
	}

	/**
	* Alert View command handler
	*/
	private void doAlertView()
	{
		boolean bVisible = !m_alertPanel.isVisible();

		if (!bVisible)
			m_nDividerLoc = m_splitPane.getDividerLocation();

		m_alertPanel.setVisible(bVisible);
		((JCheckBoxMenuItem)m_miAlertView).setState(bVisible);

		if (bVisible)
			m_splitPane.setDividerLocation(m_nDividerLoc);
	}

	/**
	* Time Query command handler
	*/
	public void doTimeQuery()
	{
		if (!validateQueryTime(false))
			return;

		showQueryResult();
	}

	/**
	* Validates the query details
	*/
	protected boolean validateQueryTime(boolean bSilent)
	{
		String strTime =  m_textQuery.getText();

		if (strTime == null || strTime.length() == 0)
		{
			if (!bSilent)
			{
				showMessage("You must specify a time in the format (hh:mm).", "Error");
				m_textQuery.requestFocus();
			}
			return false;
		}

		//Check if data is in hh:mm format
		StringTokenizer tok = new StringTokenizer(strTime, ":");
		if (tok.countTokens() != 2)
		{
			if (!bSilent)
			{
				showMessage("Invalid Time. You must specify a valid time (hh:mm).", "Error");
				m_textQuery.requestFocus();
			}
			return false;
		}

		String strHour, strMin;
		int nHour, nMin;

		strHour = tok.nextToken();
		strMin = tok.nextToken();

		nHour = nMin = -1;

		try
		{
		  nHour = Integer.parseInt(strHour);
		  nMin = Integer.parseInt(strMin);
		}
		catch(NumberFormatException e)
		{
		}
		if (nHour == -1 || nMin == -1)
		{
			if (!bSilent)
			{
				showMessage("Invalid Number. You must specify a valid time (hh:mm).", "Error");
				m_textQuery.requestFocus();
			}
			return false;
		}

		if (!( (nHour >= 0 && nHour <= 23) && (nMin >= 0 && nMin <= 59)))
		{
			if (!bSilent)
			{
				showMessage("Time out of range. Valid Hour (hh) is 0 to 23, Minute (mm) is 0 to 59.", "Error");
				m_textQuery.requestFocus();
			}
			return false;
		}

		return true;
	}

	/**
	* displays the time based on the query (assuming time is already validated)
  	*/
	private boolean showQueryResult()
	{
		String strTime =  m_textQuery.getText();

		StringTokenizer tok = new StringTokenizer(strTime, ":");

		String strHour, strMin;
		int nHour, nMin;

		strHour = tok.nextToken();
		strMin = tok.nextToken();

		nHour = nMin = -1;

		try
		{
		  nHour = Integer.parseInt(strHour);
		  nMin = Integer.parseInt(strMin);
		}
		catch(NumberFormatException e)
		{
		}
		if (nHour == -1 || nMin == -1)
			return false;

		TimeZone queryZone, resultZone;
		Calendar queryCal, resultCal;
		String strQueryZoneID, strResultZoneID;

		Date curDate = new Date();

		strQueryZoneID = (String)m_comboQuery.getSelectedItem();
		queryZone = TimeZone.getTimeZone(strQueryZoneID);
		queryCal = Calendar.getInstance(queryZone);
		queryCal.setTime(curDate);

		strResultZoneID = (String)m_comboResult.getSelectedItem();
		resultZone = TimeZone.getTimeZone(strResultZoneID);
		resultCal = Calendar.getInstance(resultZone);
		resultCal.setTime(curDate);

		queryCal.set(Calendar.HOUR_OF_DAY, nHour);
		queryCal.set(Calendar.MINUTE, nMin);
		Date date = queryCal.getTime();

		resultCal.setTime(date);
		m_textResult.setText(getTimeString(resultCal, false));

		return true;
	}

  /**
  * Customize command handler
  */
  public void doCustomize()
  {
	  MCOptionsDialog dlgOptions;

	  MCProperties props = MultiClock.getApp().getProperties();

	  dlgOptions = new MCOptionsDialog(m_parentFrame, "Options", true);

	  dlgOptions.setRefreshTime(props.m_nRefreshTime);
	  dlgOptions.setClockType(props.m_nClockType);
	  dlgOptions.setMaxClocks(props.m_nMaxClocks);
	  dlgOptions.setDefaultZone(props.m_strDefaultZone);
	  dlgOptions.setDisplayZones(props.m_vDisplayZones);

	  if (dlgOptions.doDialog())
	  {
		  props.m_nRefreshTime = dlgOptions.getRefreshTime();
		  props.m_nClockType = dlgOptions.getClockType();
		  props.m_strDefaultZone = dlgOptions.getDefaultZone();
		  props.m_vDisplayZones = dlgOptions.getDisplayZones();

		  props.save(); //Save the options persistently

		  enableAutoRefresh(false); //disable background refresh

		  m_nRefreshTime = props.m_nRefreshTime;
		  m_nClockType = props.m_nClockType;
		  m_strDefaultZone = props.m_strDefaultZone;

		  if (props.m_vDisplayZones.size() == 0)
		  	m_strTimeZoneIDs = null;
		  else
		  {
			  m_strTimeZoneIDs = new String [props.m_vDisplayZones.size()];
			  for (int i = 0; i < props.m_vDisplayZones.size(); i++)
			  	m_strTimeZoneIDs[i] = new String((String)props.m_vDisplayZones.get(i));
	  	  }

	  	  m_controlPanel.remove(m_panelClock);
	  	  m_panelClock = makeClockPanel();
	  	  m_controlPanel.add(m_panelClock, "Center");
	  	  m_controlPanel.validate();
	  	  repaint(0);

	  	  enableAutoRefresh(true); //enable background refresh
	  }
  }

	/**
	* Alarm command handler
	*/
	public void doAlarms()
	{
		MCAlarmMgrDialog dlgAlarms;

		MCProperties props = MultiClock.getApp().getProperties();

		dlgAlarms = new MCAlarmMgrDialog(m_parentFrame, "Alarms", true);

		dlgAlarms.setAlarmList(props.m_vAlarms);

		if (dlgAlarms.doDialog())
		{
			props.m_vAlarms = dlgAlarms.getAlarmList();

			props.save(); //Save the options persistently

			MultiClock.getApp().getTimeKeeper().setAlarms(props.m_vAlarms);
		}
	}

	/**
	* Acknowledge Alert command handler
	*/
	public void doAckAlert()
	{
		GTableModel dtm = (GTableModel)m_tableAlerts.getModel();
		int nRow = m_tableAlerts.getSelectedRow();
		if (nRow != -1)
		{
			m_tableAlerts.setRowFont(nRow, -1);

			if (m_imageAlertAck != null)
				m_tableAlerts.setRowImage(nRow, m_imageAlertAck);

			m_tableAlerts.repaint(0);
		}
	}

	/**
	* Delete Alert command handler
	*/
	public void doDeleteAlert()
	{
		GTableModel dtm = (GTableModel)m_tableAlerts.getModel();
		int nSel = m_tableAlerts.getSelectedRow();
		if (nSel != -1)
		{
			dtm.removeRow(nSel);
			m_tableAlerts.repaint(0);
		}
	}

	/**
	* Clear All Alerts command handler
	*/
	public void doClearAllAlerts()
	{
		//Clear existing data
		GTableModel dtm = (GTableModel)m_tableAlerts.getModel();
		GTableCell rowData[][] = new GTableCell[0][m_colNames.length];
		dtm.setDataVector(rowData, m_colNames);

		m_tableAlerts.repaint(0);
	}

	/**
	* Alarm event handler that updates the alert to the table
	*/
	public void alarmAlert(int nID, MCAlarmInfo alarmInfo, Date curDate)
	{
		GTableCell [] tableRow;

		tableRow = createAlertRow(alarmInfo, curDate);
		GTableModel dtm = (GTableModel)m_tableAlerts.getModel();
		dtm.addRow(tableRow);
		int nRow = dtm.getRowCount()-1;
		m_tableAlerts.setRowFont(nRow, Font.BOLD);

		if (m_imageAlertNew != null)
			m_tableAlerts.setRowImage(nRow, m_imageAlertNew);

		m_tableAlerts.repaint(0);

		Toolkit.getDefaultToolkit().beep();
		Toolkit.getDefaultToolkit().beep();
	}

	/**
	* Thread method for background update of clock display
	*/
	public void run()
	{
		while (true)
		{
			if (m_bAutoRefresh)
				refreshClock(-1); //update all booker info

			try
			{
				Thread.currentThread().sleep(m_nRefreshTime);
			}
			catch(InterruptedException e) {}
		}
	}

	/**
	* Invoked when a key has been pressed.
	*/
	public void keyPressed(KeyEvent e)
	{
	}

	/**
	* Invoked when a key has been released.
	*/
	public void keyReleased(KeyEvent e)
	{
		if (e.getSource() == m_textQuery)
		{
			if (e.getKeyCode() == KeyEvent.VK_ENTER)
				doTimeQuery();
			else
			{
				//update the query result dynamically, when valid time is typed
				if (validateQueryTime(true))
					showQueryResult();
			}
		}
	}

	/**
	* Invoked when a key has been typed.
	*/
	public void keyTyped(KeyEvent e)
	{
		/*if (e.getSource() == m_textQuery)
		{
			//update the query result dynamically, when valid time is typed
			if (validateQueryTime(true))
				showQueryResult();
		}*/
	}

	////////////////////////////////////////////////////////////
	// class MCTimeField
	////////////////////////////////////////////////////////////
	public class MCTimeField extends JTextField
	{
		TimeZone m_timeZone;

		/**
		* component constructor
		*/
		public MCTimeField(String strText, int nCharWidth)
		{
			super(strText, nCharWidth);
		}

		/**
		* sets/gets the time zone of the component
		*/
		public void setTimeZone(TimeZone timeZone) { m_timeZone = timeZone; }
		public TimeZone getTimeZone() { return m_timeZone; }

		/**
		* enables or disables the tooltip
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
		* returns the tooltip of the component
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

			Date curDate = new Date();

			if (m_timeZone.inDaylightTime(curDate))
				strToolTip = strToolTip + " (Daylight)";

			return strToolTip;
		}

		/**
		* returns the tooltip based on the mouse position
		*/
		public String getToolTipText(MouseEvent me)
		{
			if (m_timeZone == null)
				return super.getToolTipText(me);

			return getToolTipText();
		}
	}

	////////////////////////////////////////////////////////////
	// class MCHelpDialog
	////////////////////////////////////////////////////////////
	public class MCHelpDialog extends GDialog
	{
		private static final int DLG_WIDTH = 475;
		private static final int DLG_HEIGHT = 235;

		/**
		* Dialog constructor
		*/
		public MCHelpDialog(Frame owner, String title, boolean modal)
		{
			super(owner, title, modal);

			initDlg();
		}

		/**
		* Dialog constructor
		*/
		public MCHelpDialog(Dialog owner, String title, boolean modal)
		{
			super(owner, title, modal);

			initDlg();
		}

		/**
		* intializes the dialog
		*/
		private void initDlg()
		{
			getContentPane().setLayout(new BorderLayout());

			getContentPane().add(makeControlPanel(), "Center");

			setSize(DLG_WIDTH, DLG_HEIGHT);
		}

		/**
		* makes the user interface
		*/
		private JPanel makeControlPanel()
	  	{
		    JPanel controlPanel;

			//Container Panel
		    controlPanel = new JPanel();
    		controlPanel.setLayout(new BorderLayout());

    		//Data Panel
    		JPanel dataPanel = new JPanel();
    		dataPanel.setLayout(new BorderLayout());

    		JTextArea text = new JTextArea(8, 30);
    		text.setEditable(false);
    		text.setLineWrap(true);
    		text.setWrapStyleWord(true);

    		InputStream is;

    		is = GToolkit.getResource("Help.txt");

    		String strLine;

    		BufferedReader br = new BufferedReader(new InputStreamReader(is));

			while (true)
			{
				try	{ strLine = br.readLine(); }
				catch(IOException e){ strLine = null; }

    			if (strLine == null)
    				break;
    			text.append(strLine);
    			text.append("\n");
			}
			text.setCaretPosition(0);

    		JScrollPane scrollPane = new JScrollPane(text);
    		dataPanel.add(scrollPane, "Center");

    		controlPanel.add(dataPanel, "Center");

			//Setup command panel
			JPanel commandPanel = new JPanel();

			JButton buttonOK = new JButton("OK");
			buttonOK.addActionListener(this);
			commandPanel.add(buttonOK);

			controlPanel.add(commandPanel, "South");

			registerOKButton(buttonOK);

			return controlPanel;
		}
	}
}
