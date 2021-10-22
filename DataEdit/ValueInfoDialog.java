package utils.dataedit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import utils.gwt.*;

public class ValueInfoDialog extends GDialog
{
	//GUI attributes
	private JButton buttonOK, buttonCancel;

	protected ValueDesc [] m_Values = null;
	protected Object [] m_valueUIElems = null;

	protected Hashtable m_hDateSelectorMap = new Hashtable();
	protected Hashtable m_hListMap = new Hashtable();

	protected Vector m_vPageNames, m_vPageValues;
	protected JPanel [] m_DataPanels;

	protected boolean m_bUIInitialized = false;

	protected static final int DEF_DLG_WIDTH = 325;
	protected static final int DEF_DLG_HEIGHT = 325;
	protected static final int MAX_DLG_HEIGHT = 350;
	protected static final int MAX_PANEL_HEIGHT = 300;

	protected static final int DLG_CAPTION_HT = 25;
	protected static final int DLG_CMD_PANEL_HT = 40;
	protected static final int DLG_ITEM_HT = 35;
	protected static final int VSCROLL_WT = 25;
	protected static final int TAB_TITLE_HT = 30;

	protected int m_nDlgWidth = DEF_DLG_WIDTH;
	protected int m_nDlgHeight = DEF_DLG_HEIGHT;

	public ValueInfoDialog(Frame owner, String title, boolean modal)
	{
		super(owner, title, modal);

		initDlg();
	}

	public ValueInfoDialog(Dialog owner, String title, boolean modal)
	{
		super(owner, title, modal);

		initDlg();
	}

	private void initDlg()
	{
		getContentPane().setLayout(new BorderLayout());

		//getContentPane().add(makeControlPanel(), "Center");
		//m_bUIInitialized = true;

		setSize(m_nDlgWidth, m_nDlgHeight);
	}

	public boolean doDialog(boolean bCenter)
	{
		if (!m_bUIInitialized)
		{
			getContentPane().add(makeControlPanel(), "Center");
			setSize(m_nDlgWidth, m_nDlgHeight);
			m_bUIInitialized = true;
		}

		return super.doDialog(bCenter);
	}

	public void addListMap(String strValueName, Object [][] ListMap)
	{
		m_hListMap.put(strValueName, ListMap);
	}

	public void addListMap(Vector vValueNames, Vector vListMaps)
	{
		if (vValueNames != null && vListMaps != null)
		{
			for (int i = 0; i < vValueNames.size(); i++)
				addListMap((String)vValueNames.get(i), (Object [][])vListMaps.get(i));
		}
	}

	protected int getListMapIndex(Object [][] ListMap, Object oValue)
	{
		if (oValue != null)
		{
			for (int i = 0; i < ListMap.length; i++)
				if (oValue.equals(ListMap[i][0]))
					return i;
		}

		return -1;
	}

	protected Object getListMapValue(Object [][] ListMap, int nIndex)
	{
		return (ListMap[nIndex][0]);
	}

	public void addPageMap(String strPageName, String [] ValueNames)
	{
		if (m_vPageNames == null)
		{
			m_vPageNames = new Vector();
			m_vPageValues = new Vector();
		}

		m_vPageNames.add(strPageName);
		m_vPageValues.add(ValueNames);
	}

	public void addPageMap(Vector vPageNames, Vector vValueNames)
	{
		if (vPageNames != null && vValueNames != null)
		{
			for (int i = 0; i < vPageNames.size(); i++)
				addPageMap((String)vPageNames.get(i), (String [])vValueNames.get(i));
		}
	}

	public int getPageIndex(String strValueName)
	{
		if (m_vPageNames == null)
			return -1;

		String [] ValueNames;
		for (int i = 0; i < m_vPageValues.size(); i++)
		{
			ValueNames = (String [])m_vPageValues.get(i);
			for (int j = 0; j < ValueNames.length; j++)
				if (strValueName.equals(ValueNames[j]))
					return i;
		}

		return -1;
	}

	public void setValueInfo(ValueDesc [] Values)
	{
		m_Values = Values;
	}

	public ValueDesc [] getValueInfo()
	{
		return m_Values;
	}

  /*******************************************************************
   *  Makes the User Interface.
   *******************************************************************/

  private JPanel makeControlPanel()
  {
	if (m_Values == null)
		return null;

    JPanel controlPanel, tempPanel;

    controlPanel = new JPanel();
    controlPanel.setLayout(new BorderLayout());

	boolean [] ScrollFlags;
	int [] PanelHeights;
	int i, nSize;

	nSize = (m_vPageNames != null)?(m_vPageNames.size()):(1);

	m_DataPanels = new JPanel [nSize];
	ScrollFlags = new boolean [nSize];
	PanelHeights = new int [nSize];

	for (i = 0; i < nSize; i++)
	{
		m_DataPanels[i] = null;
		ScrollFlags[i] = false;
		PanelHeights[i] = 0;
	}

	m_valueUIElems = new Object [m_Values.length];

	Component compUI;
	int nType;
	int nPage;
	JPanel dataPanel = null;

	for (i = 0; i < m_Values.length; i++)
    {
		nPage = getPageIndex(m_Values[i].getName());
		if (nPage == -1)
			nPage = 0;

		if (m_DataPanels[nPage] == null)
		{ //Create data panel
			m_DataPanels[nPage] = new JPanel();
			m_DataPanels[nPage].setLayout(new GridLayout(0, 2, 1, 1));
		}
		dataPanel = m_DataPanels[nPage];

		nType = m_Values[i].getValueType();

		if (nType != ValueDesc.VT_BOOL)
			dataPanel.add(new JLabel(m_Values[i].getCaption()));
		else
			dataPanel.add(new JLabel(""));

		tempPanel = new JPanel();
		tempPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		switch(nType)
		{
			case ValueDesc.VT_BOOL:
				compUI = new JCheckBox(m_Values[i].getCaption());
				break;
			case ValueDesc.VT_STR:
			case ValueDesc.VT_INT:
			case ValueDesc.VT_DOUBLE:
			{
				Object [][] ListMap = (Object [][])m_hListMap.get(m_Values[i].getName());
				if (ListMap == null)
					compUI = new JTextField("", 12);
				else
				{
					JComboBox combo = new JComboBox();
					compUI = combo;
					if (ListMap[0][1] != null)
					{
						for (int j = 0; j < ListMap.length; j++)
							combo.addItem(ListMap[j][1]);
					}
					else
					{
						for (int j = 0; j < ListMap.length; j++)
							combo.addItem(ListMap[j][0]);
					}
				}
				break;
			}
			case ValueDesc.VT_DATE:
				compUI = new JTextField("", 7);
				break;
			default:
				compUI = new JLabel();
				break;
		}

		m_valueUIElems[i] = compUI;
		tempPanel.add(compUI);
		if (!ScrollFlags[nPage])
		{
			PanelHeights[nPage] += DLG_ITEM_HT;
			if (PanelHeights[nPage] > MAX_PANEL_HEIGHT)
				ScrollFlags[nPage] = true;
		}

		//Add selector elements, if necessary
		if (nType == ValueDesc.VT_DATE)
		{
			JButton buttonDate = new JButton("");
			buttonDate.setIcon(GToolkit.getIcon("Date.gif"));
			buttonDate.setMargin(new Insets(0, 0, 0, 0));
			buttonDate.addActionListener(this);
			tempPanel.add(buttonDate);

			m_hDateSelectorMap.put(buttonDate, compUI);
		}

		dataPanel.add(tempPanel);
	}

	//Calculate dialog height and page heights
	m_nDlgHeight = DLG_CAPTION_HT + DLG_CMD_PANEL_HT;
	int nMaxPageHeight = 0;
	for (i = 0; i < PanelHeights.length; i++)
		if (nMaxPageHeight < PanelHeights[i])
			nMaxPageHeight = PanelHeights[i];

	m_nDlgHeight += nMaxPageHeight;
	if (m_vPageNames != null)
		m_nDlgHeight += TAB_TITLE_HT;

	//Create Panel containers
	if (m_vPageNames == null)
	{ //Simple dialog
		if (!ScrollFlags[0])
			controlPanel.add(m_DataPanels[0], "North");
		else
		{
			JScrollPane scrollpane = new JScrollPane(m_DataPanels[0]);
			scrollpane.setPreferredSize(
				new Dimension(m_nDlgWidth-VSCROLL_WT, m_nDlgHeight-DLG_CAPTION_HT - DLG_CMD_PANEL_HT));
			controlPanel.add(scrollpane, "North");
		}
	}
	else
	{ //Tab pane
		JTabbedPane tabPane = new JTabbedPane(JTabbedPane.TOP);
		Dimension dimPage = new Dimension(m_nDlgWidth-25, nMaxPageHeight);
		JPanel pagePanel;

		for (i = 0; i < m_vPageNames.size(); i++)
		{
			if (!ScrollFlags[i])
			{
				pagePanel = new JPanel();
				pagePanel.setLayout(new BorderLayout());
				pagePanel.setSize(dimPage);
				m_DataPanels[i].setSize(new Dimension(m_nDlgWidth-25, PanelHeights[i]));

				pagePanel.add(m_DataPanels[i], "Center");

				tabPane.addTab((String)m_vPageNames.get(i), pagePanel);
			}
			else
			{
				JScrollPane scrollpane = new JScrollPane(m_DataPanels[i]);
				scrollpane.setPreferredSize(
								new Dimension(m_nDlgWidth-25-VSCROLL_WT, nMaxPageHeight));
				tabPane.add(scrollpane);
			}
		}

		controlPanel.add(tabPane, "North");
	}

	//Setup command panel
	JPanel commandPanel = new JPanel();

    buttonOK = new JButton("OK");
    buttonOK.addActionListener(this);
    commandPanel.add(buttonOK);

    buttonCancel = new JButton("Cancel");
    buttonCancel.addActionListener(this);
    commandPanel.add(buttonCancel);

    controlPanel.add(commandPanel, "South");

    return controlPanel;
  }


  /**
   * Gets the dimensions.
   */

  /*public Dimension getPreferredSize()
  {
    return new Dimension(300,300);
  }*/


  /*******************************************************************
   *   Listens for and responds to button presses.
   *   @param ev action event
   *******************************************************************/

  public void actionPerformed(ActionEvent ev)
  {
	  Object oSrc = ev.getSource();

    if (oSrc == buttonOK)
      onOK();
    else if (oSrc == buttonCancel)
      onCancel();

      Object oComp;

      //Check if it is a selector
      oComp = m_hDateSelectorMap.get(oSrc);
      if (oComp != null)
      	onDate((JTextField)oComp);
  }

  protected void setDataToUI()
  {
	if (m_Values == null)
		return;

	Object [][] ListMap;

	for (int i = 0; i < m_Values.length; i++)
    {
		ListMap = (Object [][])m_hListMap.get(m_Values[i].getName());

		switch(m_Values[i].getValueType())
		{
			case ValueDesc.VT_BOOL:
			{
				Boolean oiValue = (Boolean)m_Values[i].getValue();
				boolean bFlag = (oiValue != null)?oiValue.booleanValue():false;

				((JCheckBox)m_valueUIElems[i]).setSelected(bFlag);
				break;
			}
			case ValueDesc.VT_STR:
			{
				if (ListMap == null)
					setText((JTextField)m_valueUIElems[i], (String)m_Values[i].getValue());
				break;
			}
			case ValueDesc.VT_INT:
			{
				if (ListMap == null)
				{
					Integer oiValue = (Integer)m_Values[i].getValue();
					int nValue = (oiValue != null)?oiValue.intValue():0;

					setInteger((JTextField)m_valueUIElems[i], nValue);
				}
				break;
			}
			case ValueDesc.VT_DOUBLE:
			{
				if (ListMap == null)
				{
					Double odValue = (Double)m_Values[i].getValue();
					double dValue = (odValue != null)?odValue.doubleValue():0;

					setDouble((JTextField)m_valueUIElems[i], dValue);
				}
				break;
			}
			case ValueDesc.VT_DATE:
				setDate((JTextField)m_valueUIElems[i], (Date)m_Values[i].getValue());
				break;
			default:
			{
				Object oValue = m_Values[i].getValue();
				String strVal = (oValue != null)?oValue.toString() : "NULL";

				((JLabel)m_valueUIElems[i]).setText(strVal);
				break;
			}
		}

		if (ListMap != null)
		{
			int nIndex = getListMapIndex(ListMap, m_Values[i].getValue());
			((JComboBox)m_valueUIElems[i]).setSelectedIndex(nIndex);
		}
	}

  }

  protected void getDataFromUI()
  {
	/* if (m_brokerInfo == null)
		m_brokerInfo = new CMSBrokerInfo();*/

	Object [][] ListMap;

	for (int i = 0; i < m_Values.length; i++)
	{
		ListMap = (Object [][])m_hListMap.get(m_Values[i].getName());

		switch(m_Values[i].getValueType())
		{
			case ValueDesc.VT_BOOL:
				m_Values[i].setValue(new Boolean(((JCheckBox)m_valueUIElems[i]).isSelected()));
				break;
			case ValueDesc.VT_STR:
				if (ListMap == null)
					m_Values[i].setValue(getText((JTextField)m_valueUIElems[i]));
				break;
			case ValueDesc.VT_INT:
			{
				if (ListMap == null)
				{
					int nValue = getInteger((JTextField)m_valueUIElems[i]);
					m_Values[i].setValue(new Integer(nValue));
				}
				break;
			}
			case ValueDesc.VT_DOUBLE:
			{
				if (ListMap == null)
				{
					double dValue = getDouble((JTextField)m_valueUIElems[i]);
					m_Values[i].setValue(new Double(dValue));
				}
				break;
			}
			case ValueDesc.VT_DATE:
				m_Values[i].setValue(getDate((JTextField)m_valueUIElems[i]));
				break;
			default:
				//do nothing
				break;
		}

		if (ListMap != null)
		{
			int nIndex = ((JComboBox)m_valueUIElems[i]).getSelectedIndex();
			Object oValue =	getListMapValue(ListMap, nIndex);
			m_Values[i].setValue(oValue);
		}
	}
  }

  public boolean validateUIData()
  {
	boolean bRet;

	//do validation and display error msg
	Object [][] ListMap;

	for (int i = 0; i < m_Values.length; i++)
	{
		ListMap = (Object [][])m_hListMap.get(m_Values[i].getName());

		switch(m_Values[i].getValueType())
		{
			case ValueDesc.VT_STR:
				if (ListMap == null)
					bRet = validateTextField((JTextField)m_valueUIElems[i], m_Values[i].getLength(),
								m_Values[i].isOptional(), m_Values[i].getCaption());
				else
					bRet = true;
				break;

			case ValueDesc.VT_INT:
			{
				if (ListMap == null)
				{
					int nMin, nMax;
					Integer oiValue;

					oiValue = (Integer)m_Values[i].getMinValue();
					nMin = (oiValue != null) ? oiValue.intValue() : Integer.MIN_VALUE;
					oiValue = (Integer)m_Values[i].getMaxValue();
					nMax = (oiValue != null) ? oiValue.intValue() : Integer.MAX_VALUE;

					bRet = validateIntegerField((JTextField)m_valueUIElems[i], nMin, nMax,
								m_Values[i].isOptional(), m_Values[i].getCaption());
				}
				else
					bRet = true;
				break;
			}
			case ValueDesc.VT_DOUBLE:
			{
				if (ListMap == null)
				{
					double dMin, dMax;
					Double odValue;

					odValue = (Double)m_Values[i].getMinValue();
					dMin = (odValue != null) ? odValue.doubleValue() : Double.MIN_VALUE;
					odValue = (Double)m_Values[i].getMaxValue();
					dMax = (odValue != null) ? odValue.doubleValue() : Double.MAX_VALUE;

					bRet = validateDoubleField((JTextField)m_valueUIElems[i], dMin, dMax,
								m_Values[i].isOptional(), m_Values[i].getCaption());
				}
				else
					bRet = true;
				break;
			}
			case ValueDesc.VT_DATE:
			{
				bRet = validateDateField((JTextField)m_valueUIElems[i],
							m_Values[i].isOptional(), m_Values[i].getCaption());
				break;
			}
			default :
				bRet = true; //assume that it is always valid
				break;
		}

		if (!bRet)
			return false;
	}

	return true;
  }

  public void onOK()
  {
	  if (!validateUIData())
		return;

	  setReturnFlag(true);
	  getDataFromUI();
	  dispose();
  }

  public void onCancel()
  {
	  setReturnFlag(false);
	  dispose();
  }

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
}
