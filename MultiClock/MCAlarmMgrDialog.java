package tools.multiclock;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import utils.gwt.*;

public class MCAlarmMgrDialog extends GDialog
  implements ListSelectionListener
{
  //GUI attributes
  private JButton buttonAdd, buttonModify, buttonDelete, buttonDate;

  private JTextField m_textTitle, m_textTime, m_textDate;
  private JComboBox m_comboType, m_comboZone, m_comboWeekDay, m_comboMonthDay;

  private Vector m_alarms;
  private GTable m_tableAlarms;
  private MCAlarmInfo m_curAlarm;
  private String m_colNames[] = {"Title", "Type", "Options", "Alarm Time"};
  private Image m_imageAlarm;

  /**
  * Dialog constructor
  */
  public MCAlarmMgrDialog(Frame owner, String title,	boolean modal)
  {
	super(owner, title, modal);

	getContentPane().setLayout(new BorderLayout());

	getContentPane().add(makeControlPanel(), "Center");

	setSize(425, 350);

	m_imageAlarm = MultiClock.getImage("Time.gif");
  }

	/**
	* Sets the alarms to the dialog
	*/
	public void setAlarmList(Vector alarms)
	{
		MCAlarmInfo alarm;

		m_alarms = new Vector();
		for (int i = 0; i < alarms.size(); i++)
		{
			alarm = (MCAlarmInfo)alarms.get(i);
			m_alarms.add(new MCAlarmInfo(alarm));
		}
	}

	/**
	* returns the alarms from the dialog
	*/
	public Vector getAlarmList()
	{
		return m_alarms;
	}

	/**
	* intializes the alarms
	*/
	private void initAlarms()
	{
		if (m_alarms.size() <= 0)
			return;

		GTableCell rowData[][] = new GTableCell[m_alarms.size()][m_colNames.length];
		MCAlarmInfo alarm;
		int i;

		for (i = 0; i < m_alarms.size(); i++)
		{
			alarm = (MCAlarmInfo)m_alarms.get(i);

			rowData[i] = createAlarmRow(alarm);
		}

		//Set new data
		GTableModel dtm = (GTableModel)m_tableAlarms.getModel();
		dtm.setDataVector(rowData, m_colNames);

		for (i = 0; i < m_alarms.size(); i++)
		{
			if (m_imageAlarm != null)
				m_tableAlarms.setRowImage(i, m_imageAlarm);
		}
	}

	/**
	* creates a table row from the given alarm info
	*/
	public GTableCell [] createAlarmRow(MCAlarmInfo aInfo)
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

		return rowData;
	}

  /**
   *  Makes the User Interface.
   */
  private JPanel makeControlPanel()
  {
    JPanel dataPanel, controlPanel, tempPanel;

    controlPanel = new JPanel();
    controlPanel.setLayout(new BorderLayout());

    dataPanel = new JPanel();
    dataPanel.setLayout(new GridLayout(0, 1, 1, 10));

	//Title
	tempPanel = new JPanel();
    tempPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	tempPanel.add(new JLabel("Title :"));
	m_textTitle = new JTextField("", 20);
	tempPanel.add(m_textTitle);
	dataPanel.add(tempPanel);

	//Alarm Type
	m_comboType = new JComboBox();
	m_comboType.addItem(new String("Daily"));
	m_comboType.addItem(new String("Weekly"));
	m_comboType.addItem(new String("Monthly"));
	m_comboType.addItem(new String("Month Start"));
	m_comboType.addItem(new String("Month End"));
	m_comboType.addItem(new String("Date"));
	m_comboType.addActionListener(this);

	tempPanel = new JPanel();
	tempPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	tempPanel.add(new JLabel("Type :"));
	tempPanel.add(m_comboType);
	dataPanel.add(tempPanel);

	//Time

	String [] timeZoneIDs = TimeZone.getAvailableIDs();
	int i, j;
	Vector vTimeZones;
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

	m_comboZone = new JComboBox();

	TimeZone defZone = TimeZone.getDefault();

	//comboZone.addItem(new String("<Local Time>"));
	for (i = 0; i < vTimeZones.size(); i++)
	{
		m_comboZone.addItem(vTimeZones.get(i));
	}

	m_comboZone.setSelectedItem(defZone.getID());

	tempPanel = new JPanel();
	tempPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	tempPanel.add(new JLabel("Time (hh:mm) :"));
	m_textTime = new JTextField("", 5);
	tempPanel.add(m_textTime);
	tempPanel.add(new JLabel("Zone :"));
	tempPanel.add(m_comboZone);
	dataPanel.add(tempPanel);

	//Week Day
	m_comboWeekDay = new JComboBox();
	m_comboWeekDay.addItem(new String("Sunday"));
	m_comboWeekDay.addItem(new String("Monday"));
	m_comboWeekDay.addItem(new String("Tuesday"));
	m_comboWeekDay.addItem(new String("Wednesday"));
	m_comboWeekDay.addItem(new String("Thursday"));
	m_comboWeekDay.addItem(new String("Friday"));
	m_comboWeekDay.addItem(new String("Saturday"));

	tempPanel = new JPanel();
	tempPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	tempPanel.add(new JLabel("Week Day :"));
	tempPanel.add(m_comboWeekDay);
	//dataPanel.add(tempPanel);

	//Month Day
	m_comboMonthDay = new JComboBox();
	for (i = 1; i <= 31; i++)
		m_comboMonthDay.addItem(Integer.toString(i));

	/*tempPanel = new JPanel();
	tempPanel.setLayout(new FlowLayout(FlowLayout.LEFT));*/
	tempPanel.add(new JLabel("Month Day :"));
	tempPanel.add(m_comboMonthDay);
	dataPanel.add(tempPanel);

	//Date
	tempPanel = new JPanel();
	tempPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	tempPanel.add(new JLabel("Date (mm-dd-yyyy) :"));
	m_textDate = new JTextField("", 7);
	tempPanel.add(m_textDate);

	buttonDate = new JButton("");
	buttonDate.setIcon(MultiClock.getIcon("Date.gif"));
	buttonDate.setMargin(new Insets(0, 0, 0, 0));
	buttonDate.addActionListener(this);
	tempPanel.add(buttonDate);

	dataPanel.add(tempPanel);

	controlPanel.add(dataPanel, "North");

	// Alarm list table
	GTableCell rowData[][] = new GTableCell[0][m_colNames.length];

	GTableModel tm = new GTableModel(rowData, m_colNames);
	m_tableAlarms = new GTable(tm);
	m_tableAlarms.setSortFlag(false);
	m_tableAlarms.getSelectionModel().addListSelectionListener(this);

	//initAlarms(); //init deferred

	JScrollPane scrollpane = new JScrollPane(m_tableAlarms);
	scrollpane.setPreferredSize(m_tableAlarms.getPreferredSize());

	controlPanel.add(scrollpane, "Center");

	//Command Panel
	JPanel commandPanel = new JPanel();
	commandPanel.setLayout(new BorderLayout());

	JPanel leftPanel, rigthPanel;

	leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    buttonAdd = new JButton("Add");
    buttonAdd.addActionListener(this);
    leftPanel.add(buttonAdd);

    buttonModify = new JButton("Modify");
    buttonModify.addActionListener(this);
    leftPanel.add(buttonModify);

    buttonDelete = new JButton("Delete");
	buttonDelete.addActionListener(this);
    leftPanel.add(buttonDelete);

	rigthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

	JButton buttonOk = new JButton("OK");
	buttonOk.addActionListener(this);
    rigthPanel.add(buttonOk);

    JButton buttonCancel = new JButton("Cancel");
	buttonCancel.addActionListener(this);
    rigthPanel.add(buttonCancel);

	commandPanel.add("West", leftPanel);
	commandPanel.add("East", rigthPanel);

	registerOKButton(buttonOk);
    registerCancelButton(buttonCancel);

    controlPanel.add(commandPanel, "South");

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
  * overriden action listener to handle custom button click events and combo selection
  */
  public void actionPerformed(ActionEvent ev)
  {
	  Object obj = ev.getSource();

	if (obj == buttonAdd)
      onAdd();
    else if (obj == buttonModify)
      onModify();
    else if (obj == buttonDelete)
      onDelete();
    else if (obj == buttonDate)
      onDate(m_textDate);
    else if (obj == m_comboType)
    {
		int nType = m_comboType.getSelectedIndex();
		setCtrlStates(nType);
	}
    else
      super.actionPerformed(ev);
  }

  /**
  * sets the state of the UI controls
  */
  protected void setCtrlStates(int nType)
  {
	m_comboWeekDay.setEnabled(nType == MCAlarmInfo.AT_WEEK_DAY);
	m_comboMonthDay.setEnabled(nType == MCAlarmInfo.AT_MONTH_DATE);
	m_textDate.setEnabled(nType == MCAlarmInfo.AT_DATE);
	buttonDate.setEnabled(nType == MCAlarmInfo.AT_DATE);
  }

  /**
  * sets the data to the dialog controls
  */
  protected void setDataToUI()
  {
	initAlarms();
	m_comboType.setSelectedIndex(MCAlarmInfo.AT_DAILY);
	setCtrlStates(MCAlarmInfo.AT_DAILY); //enable / disable controls
  }

  /**
  * sets the current alarm info to the dialog controls
  */
  private void setAlarmDataToUI()
  {
	int nType;

	nType = m_curAlarm.getType();

	m_textTitle.setText(m_curAlarm.getTitle()); //Title
	m_comboType.setSelectedIndex(nType); //Type

	setCtrlStates(nType); //enable / disable controls

	//Options
	switch(nType)
	{
		case MCAlarmInfo.AT_WEEK_DAY :
		{
			Integer oWeekDay = (Integer)m_curAlarm.getData();
			m_comboWeekDay.setSelectedIndex(oWeekDay.intValue()-1);
			break;
		}
		case MCAlarmInfo.AT_MONTH_DATE :
		{
			Integer oMonthDay = (Integer)m_curAlarm.getData();
			m_comboMonthDay.setSelectedIndex(oMonthDay.intValue()-1);
			break;
		}
		case MCAlarmInfo.AT_DATE :
		{
			Date date = (Date)m_curAlarm.getData();
			setDate(m_textDate, date);
			break;
		}
	}

	//Time
	String strTime = Integer.toString(m_curAlarm.getHour());
	strTime = strTime + ":";
	strTime = strTime + Integer.toString(m_curAlarm.getMinute());
	setText(m_textTime, strTime);

	//Time Zone
	m_comboZone.setSelectedItem(m_curAlarm.getTimeZone());
  }

  /**
  * gets the alarm info from the dialog controls
  */
  protected void getDataFromUI()
  {
	if (m_curAlarm == null)
		m_curAlarm = new MCAlarmInfo();

	int nType;

	nType = m_comboType.getSelectedIndex();

	m_curAlarm.setType(nType);
	m_curAlarm.setTitle(getText(m_textTitle));

	switch(nType)
	{
		case MCAlarmInfo.AT_WEEK_DAY :
		{
			Integer oWeekDay = new Integer(m_comboWeekDay.getSelectedIndex()+1);
			m_curAlarm.setData(oWeekDay);
			break;
		}
		case MCAlarmInfo.AT_MONTH_DATE :
		{
			Integer oMonthDay = new Integer(m_comboMonthDay.getSelectedIndex()+1);
			m_curAlarm.setData(oMonthDay);
			break;
		}
		case MCAlarmInfo.AT_DATE :
		{
			Date date = getDate(m_textDate);
			m_curAlarm.setData(date);
			break;
		}
	}

	StringTokenizer tokTime = new StringTokenizer(getText(m_textTime), ":");

	String strHour, strMin;
	int nHour, nMin;

	strHour = tokTime.nextToken();
	strMin = tokTime.nextToken();

	try
	{
		nHour = Integer.parseInt(strHour);
		nMin = Integer.parseInt(strMin);
	}
	catch(NumberFormatException e)
	{
		nHour = nMin = 0;
	}

	m_curAlarm.setHour(nHour);
	m_curAlarm.setMinute(nMin);

	m_curAlarm.setTimeZone((String)m_comboZone.getSelectedItem());
  }

  /**
  * clears the data from some of the dialog controls
  */
  private void clearUIData()
  {
	  m_textTitle.setText("");
	  m_textTime.setText("");
	  m_textDate.setText("");
	  /*
	  m_comboType.setSelectedIndex(MCAlarmInfo.AT_DAILY);
	  setCtrlStates(MCAlarmInfo.AT_DAILY); //enable / disable controls
	  */
  }

  /**
  * validates the alarm options specified in the dialog controls
  */
  public boolean validateData()
  {
	//do validation and display error msg
  	boolean bRet;

	bRet = validateTextField(m_textTitle, 30, false, "Title");
	if (!bRet)
		return false;

	String strTime = getText(m_textTime);
	if (strTime == null || strTime.length() == 0)
	{
		showErrorMsg("Time can not be empty.");
		return false;
	}

	StringTokenizer tokTime = new StringTokenizer(strTime, ":");
	if (tokTime.countTokens() != 2)
	{
		showErrorMsg("Invalid Time format. Time must be in (hh:mm) format.");
		return false;
	}

	String strHour, strMin;

	strHour = tokTime.nextToken();
	strMin = tokTime.nextToken();

	int nRet;
	nRet = GValidator.validateInteger(strHour, 0, 23, false);
	if (nRet == -1)
		showErrorMsg("Invalid Time (Hour). You must specify a valid numeric value.");
	else if (nRet == -2)
		showErrorMsg("Invalid Time. Hour should be in the range (0-23)");
	if (nRet != 1)
		return false;

	nRet = GValidator.validateInteger(strMin, 0, 59, false);
	if (nRet == -1)
		showErrorMsg("Invalid Time (Minute). You must specify a valid numeric value.");
	else if (nRet == -2)
		showErrorMsg("Invalid Time. Minute should be in the range (0-59)");
	if (nRet != 1)
		return false;

	int nType;

	nType = m_comboType.getSelectedIndex();

	if (nType == MCAlarmInfo.AT_DATE)
	{
		bRet = validateDateField(m_textDate, false, "Date");
		if (!bRet)
			return false;
	}

	return true;
  }

  /**
  * handler to display date dialog
  */
  protected void onDate(JTextField textDate)
  {
	  String strText = textDate.getText();

	  Date date = null;
	  if (strText != null && strText.length() > 0)
	  {
		int nRet = GValidator.validateDate(strText, "-", true);

		if (nRet == 1)
			date = GValidator.StringToDate(strText);
		else
			showWarningMsg("Specified date is invalid.");
	  }

  	  GDateDialog dlgDate = new GDateDialog(this, "Select Date", true);
  	  dlgDate.setDate(date);
  	  if (dlgDate.doDialog())
  	  {
  		date = dlgDate.getDate();
  		setDate(textDate, date);
  	  }
  }

  /**
  * handler to add a new Alarm info entry
  */
  public void onAdd()
  {
	  if (!validateData())
		return;

	  getDataFromUI();

	  m_alarms.add(m_curAlarm);

	  GTableModel dtm = (GTableModel)m_tableAlarms.getModel();
	  dtm.addRow(createAlarmRow(m_curAlarm));

	  int nRow = dtm.getRowCount()-1;
	  if (m_imageAlarm != null)
	  	m_tableAlarms.setRowImage(nRow, m_imageAlarm);

	  m_curAlarm = null;
	  clearUIData();
  }

  /**
  * handler to modify an existing Alarm info entry
  */
  public void onModify()
  {
	  if (m_curAlarm == null)
	  {
		  showErrorMsg("You must select a row in the table.");
		  return;
	  }

	  if (!validateData())
  		return;

	  int nSel = m_tableAlarms.getSelectedRow();
	  if (nSel == -1)
	  	return;

  	  getDataFromUI();

  	  m_alarms.setElementAt(m_curAlarm, nSel);

	  GTableModel dtm = (GTableModel)m_tableAlarms.getModel();
	  dtm.removeRow(nSel);
	  dtm.insertRow(nSel, createAlarmRow(m_curAlarm));
	  if (m_imageAlarm != null)
	  	m_tableAlarms.setRowImage(nSel, m_imageAlarm);

	  m_curAlarm = null;
	  clearUIData();
  }

  /**
  * handler to delete an existing Alarm info entry
  */
  public void onDelete()
  {
	  int nSel = m_tableAlarms.getSelectedRow();
	  if (nSel != -1)
	  {
		  m_alarms.remove(nSel);

		  GTableModel dtm = (GTableModel)m_tableAlarms.getModel();
		  dtm.removeRow(nSel);
	  }

	  m_curAlarm = null;
	  clearUIData();
  }

  /**
  * overriden OK button handler to skip default validation
  */
  public void onOK()
  {
	//Override to skip validate and getData
	/*if (!validateUIData())
		return;*/

	setReturnFlag(true);
	//getDataFromUI();
	dispose();
  }

  /*
  * handler for selection change event of the alarm info table
  */
  public void valueChanged(ListSelectionEvent e)
  {
	  int nSel = m_tableAlarms.getSelectedRow();
	  if (nSel == -1)
	  	return;

	  m_curAlarm = new MCAlarmInfo((MCAlarmInfo)m_alarms.get(nSel));
	  setAlarmDataToUI();
  }
}
