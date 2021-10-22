package tools.multiclock;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

import utils.gwt.*;

public class MCOptionsDialog extends GDialog implements ListSelectionListener
{
	public String m_strDefaultZone;
	public int m_nRefreshTime;
	public Vector m_vDisplayZones;
	public int m_nClockType;
	public int m_nMaxClocks = MCProperties.MAX_CLOCKS;

	//GUI attributes
	private JButton buttonAdd, buttonRemove, buttonClearAll;

	private JTextField textRefreshTime;
	private JCheckBox chkClockType;

	private JComboBox m_comboDefaultZone;
	private JList m_listDisplayZones, m_listSelDispZones;

	private static final int DLG_WIDTH = 475;
	private static final int DLG_HEIGHT = 270;

	/**
	* Dialog constructor
	*/
	public MCOptionsDialog(Frame owner, String title, boolean modal)
	{
		super(owner, title, modal);

		initDlg();
	}

	/**
	* Dialog constructor
	*/
	public MCOptionsDialog(Dialog owner, String title, boolean modal)
	{
		super(owner, title, modal);

		initDlg();
	}

	/**
	* Initializes the dialog
	*/
	private void initDlg()
	{
		getContentPane().setLayout(new BorderLayout());

		getContentPane().add(makeControlPanel(), "Center");

		setSize(DLG_WIDTH, DLG_HEIGHT);
	}

	/**
	* Sets default time zone
	*/
	public void setDefaultZone(String strDefaultZone)
	{
		m_strDefaultZone = new String(strDefaultZone);
	}

	/**
	* returns default time zone
	*/
	public String getDefaultZone()
	{
		return m_strDefaultZone;
	}

	/**
	* Sets refresh frequency/time
	*/
	public void setRefreshTime(int nRefreshTime)
	{
		m_nRefreshTime = nRefreshTime;
	}

	/**
	* returns refresh frequency/time
	*/
	public int getRefreshTime()
	{
		return m_nRefreshTime;
	}

	/**
	* Sets type of the clock
	*/
	public void setClockType(int nClockType)
	{
		m_nClockType = nClockType;
	}

	/**
	* returns refresh frequency/time
	*/
	public int getClockType()
	{
		return m_nClockType;
	}

	/**
	* Sets type of the clock
	*/
	public void setMaxClocks(int nMaxClocks)
	{
		m_nMaxClocks = nMaxClocks;
	}

	/**
	* sets the list of clock display zones
	*/
	public void setDisplayZones(Vector vDisplayZones)
	{
		String strZone;
		int i;

		m_vDisplayZones = new Vector();
		for (i = 0; i < vDisplayZones.size(); i++)
		{
			strZone = (String)vDisplayZones.get(i);
			m_vDisplayZones.add(new String(strZone));
		}
	}

	/**
	* returns the list of clock display zones
	*/
	public Vector getDisplayZones()
	{
		return m_vDisplayZones;
	}

  /**
   *  Makes the User Interface.
   */
  private JPanel makeControlPanel()
  {
    JPanel controlPanel, dataPanel, tempPanel;

	//Container Panel
    controlPanel = new JPanel();
    controlPanel.setLayout(new BorderLayout());

	//Top panel
    dataPanel = new JPanel();
    dataPanel.setLayout(new BorderLayout());

	JPanel tempPanel2 = new JPanel();
	tempPanel2.setLayout(new GridLayout(0, 1, 1, 1));

	//Refresh Time
    tempPanel = new JPanel();
    tempPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    tempPanel.add(new JLabel("Refresh Time (in seconds):"));
  	textRefreshTime = new JTextField("", 4);
  	tempPanel.add(textRefreshTime);

  	tempPanel2.add(tempPanel);

	//Clock Type
	tempPanel = new JPanel();
	tempPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    chkClockType = new JCheckBox("Digital Clock Display");
    tempPanel.add(chkClockType);

    tempPanel2.add(tempPanel);

  	dataPanel.add(tempPanel2, "North");

    m_comboDefaultZone = new JComboBox();

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

	DefaultListModel listModel = new DefaultListModel();

	m_comboDefaultZone.addItem(new String("<Local Time>"));
	for (i = 0; i < vTimeZones.size(); i++)
	{
		m_comboDefaultZone.addItem(vTimeZones.get(i));
		listModel.addElement(vTimeZones.get(i));
	}

    tempPanel = new JPanel();
    tempPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    tempPanel.add(new JLabel("Default Zone :"));
    tempPanel.add(m_comboDefaultZone);

    dataPanel.add(tempPanel, "South");

    controlPanel.add(dataPanel, "North");

	//Bottom Panel
	dataPanel = new JPanel();
    dataPanel.setLayout(new BorderLayout());

	tempPanel = new JPanel();
	tempPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	tempPanel.add(new JLabel("Display Zones :"));

	dataPanel.add(tempPanel, "North");

	JPanel btnPanel = new JPanel();
	btnPanel.setLayout(new BorderLayout());

	buttonAdd = new JButton(">");
	buttonAdd.addActionListener(this);
    btnPanel.add(buttonAdd, "North");

    buttonRemove = new JButton("<");
	buttonRemove.addActionListener(this);
    btnPanel.add(buttonRemove, "Center");

    buttonClearAll = new JButton("<<");
	buttonClearAll.addActionListener(this);
    btnPanel.add(buttonClearAll, "South");

	tempPanel = new JPanel();
	tempPanel.setLayout(new BorderLayout());
	m_listDisplayZones = new JList(listModel);
	m_listDisplayZones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	m_listDisplayZones.addListSelectionListener(this);
	m_listDisplayZones.setVisibleRowCount(4);

	JScrollPane scrollPane = new JScrollPane(m_listDisplayZones);
	scrollPane.setPreferredSize(new Dimension(200, 75));

	tempPanel.add(scrollPane, "West");

	tempPanel.add(btnPanel, "Center");

	m_listSelDispZones = new JList(new DefaultListModel());
	m_listSelDispZones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	m_listSelDispZones.addListSelectionListener(this);
	m_listSelDispZones.setVisibleRowCount(4);

	scrollPane = new JScrollPane(m_listSelDispZones);
	scrollPane.setPreferredSize(new Dimension(200, 75));

	tempPanel.add(scrollPane, "East");

	dataPanel.add(tempPanel, "South");

	controlPanel.add(dataPanel, "Center");

	//Setup command panel
	JPanel commandPanel = new JPanel();

    JButton buttonOK = new JButton("OK");
    buttonOK.addActionListener(this);
    commandPanel.add(buttonOK);

    JButton buttonCancel = new JButton("Cancel");
    buttonCancel.addActionListener(this);
    commandPanel.add(buttonCancel);

    controlPanel.add(commandPanel, "South");

    registerOKButton(buttonOK);
    registerCancelButton(buttonCancel);

    return controlPanel;
  }

  /**
   * Gets the dimensions.
   */
  /*public Dimension getPreferredSize()
  {
    return new Dimension(300,300);
  }*/


  /**
  * overriden action listener to handle custom button click events
  */
  public void actionPerformed(ActionEvent ev)
  {
	  Object oSrc = ev.getSource();

    if (oSrc == buttonAdd)
      onAddZone();
    else if (oSrc == buttonRemove)
      onRemoveZone();
    else if (oSrc == buttonClearAll)
      onClearZones();
     else
     	super.actionPerformed(ev);
  }

  /**
  * sets the data to the dialog controls
  */
  protected void setDataToUI()
  {
	setInteger(textRefreshTime, m_nRefreshTime/1000);

	chkClockType.setSelected(m_nClockType == 1);

	if (m_strDefaultZone.equals("%%%"))
		m_comboDefaultZone.setSelectedIndex(0);
	else
		m_comboDefaultZone.setSelectedItem(m_strDefaultZone);

	DefaultListModel lm = (DefaultListModel)m_listSelDispZones.getModel();

	for (int i = 0; i < m_vDisplayZones.size(); i++)
		lm.addElement(m_vDisplayZones.get(i));
  }

  /**
  * gets the alarm info from the dialog controls
  */
  protected void getDataFromUI()
  {
	  m_nRefreshTime = getInteger(textRefreshTime)*1000;

	  m_nClockType = (chkClockType.isSelected()) ? (1) : (0);

	  if (m_comboDefaultZone.getSelectedIndex() == 0)
	  	m_strDefaultZone = "%%%";
	  else
	  	m_strDefaultZone = (String)m_comboDefaultZone.getSelectedItem();

	  m_vDisplayZones.removeAllElements();
	  DefaultListModel lm = (DefaultListModel)m_listSelDispZones.getModel();
	  for (int i = 0; i < lm.size(); i++)
	  	m_vDisplayZones.add(lm.get(i));
  }

  /**
  * validates the options specified in the dialog controls
  */
  protected boolean validateUIData()
  {
	boolean bRet;

	//do validation and display error msg
	bRet = validateIntegerField(textRefreshTime, 1, 60*5, false, "Name");
	if (!bRet)
		return false;

	DefaultListModel lm = (DefaultListModel)m_listSelDispZones.getModel();
	if (lm.size() > m_nMaxClocks)
	{
		showErrorMsg("The maximum no. of clocks that can be selected is " + Integer.toString(m_nMaxClocks));
		return false;
	}

	return true;
  }

	/**
	* Command handler for Add action
	*/
	protected void onAddZone()
	{
		if (m_listDisplayZones.getSelectedIndex() != -1)
		{
			DefaultListModel lm = (DefaultListModel)m_listSelDispZones.getModel();
			Object obj = m_listDisplayZones.getSelectedValue();
			if (lm.indexOf(obj) == -1) //check for duplicate
				lm.addElement(obj);
		}
	}

	/**
	* Command handler for Remove action
	*/
	protected void onRemoveZone()
	{
		int nSel;

		nSel = m_listSelDispZones.getSelectedIndex();
		if (nSel != -1)
		{
			DefaultListModel lm = (DefaultListModel)m_listSelDispZones.getModel();
			lm.remove(nSel);
		}
	}

	/**
	* Command handler for Clear All action
	*/
	protected void onClearZones()
	{
		DefaultListModel lm = (DefaultListModel)m_listSelDispZones.getModel();
		lm.removeAllElements();
	}

	/**
	* Event handler - List Selection
	*/
	public void valueChanged(ListSelectionEvent e)
	{
		Object oSrc = e.getSource();

		if (oSrc instanceof JList)
		{
			JList list = (JList)oSrc;
			int nSelIndex = list.getSelectedIndex();
			if (nSelIndex != -1)
			{
				list.ensureIndexIsVisible(nSelIndex);
			}
		}
	}
}