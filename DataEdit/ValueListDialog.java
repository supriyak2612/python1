package utils.dataedit;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.table.*;

import utils.gwt.*;

public class ValueListDialog extends GDialog
{
	//GUI attributes
	private JButton buttonAdd, buttonModify, buttonDelete;
	private JButton buttonOk, buttonCancel;

	protected Vector m_ValueList;
	protected JTable m_tableValues;
	protected ValueInfoManager m_valueInfoMgr;

	protected Vector m_vMapValueNames, m_vListMaps;
	protected Vector m_vPageNames, m_vPageValueNames;

	protected boolean m_bUIInitialized = false;

	protected String m_colNames[];

	protected static final int DEF_DLG_WIDTH = 400;
	protected static final int DEF_DLG_HEIGHT = 250;

	public ValueListDialog(Frame owner, String title,	boolean modal)
	{
		super(owner, title, modal);

		getContentPane().setLayout(new BorderLayout());

		//getContentPane().add(makeControlPanel(), "Center");
		//m_bUIInitialized = true;

		setSize(DEF_DLG_WIDTH, DEF_DLG_HEIGHT);
	}

	public boolean doDialog(boolean bCenter)
	{
		if (!m_bUIInitialized)
		{
			getContentPane().add(makeControlPanel(), "Center");
			setSize(DEF_DLG_WIDTH, DEF_DLG_HEIGHT);
			m_bUIInitialized = true;
		}

		return super.doDialog(bCenter);
	}

	public void setValueInfoManager(ValueInfoManager valueInfoMgr)
	{
		m_valueInfoMgr = valueInfoMgr;

		//Initialize column headers
		ValueInfo emptyValue = valueInfoMgr.newValue();
		ValueDesc [] valueDesc = emptyValue.getValueDescriptors();

		m_colNames = new String [valueDesc.length];
		for (int i = 0; i < valueDesc.length; i++)
			m_colNames[i] = valueDesc[i].getCaption();

		//Initialize Value Maps
		m_vMapValueNames = new Vector();
		m_vListMaps = new Vector();
		m_valueInfoMgr.getValueItemListMap(m_vMapValueNames, m_vListMaps);

		//Initialize Page Maps
		m_vPageNames = new Vector();
		m_vPageValueNames = new Vector();
		m_valueInfoMgr.getValueGroupMap(m_vPageNames, m_vPageValueNames);
	}

	public void setValueList(Vector values)
	{
		m_ValueList = new Vector();

		if (values == null)
			return;

		ValueInfo vInfo, vNewInfo;
		for (int i = 0; i < values.size(); i++)
		{
			vInfo = (ValueInfo)values.get(i);

			vNewInfo = (ValueInfo)vInfo.makeCopy();

			m_ValueList.add(vNewInfo);
		}
	}

	public Vector getValueList()
	{
		return m_ValueList;
	}

	protected Object [][] getListMap(String strValueName)
	{
		if (m_vMapValueNames != null && m_vListMaps != null)
		{
			for (int i = 0; i < m_vMapValueNames.size(); i++)
				if (m_vMapValueNames.get(i).equals(strValueName))
					return ((Object [][])m_vListMaps.get(i));
		}

		return null;
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

	protected Object getListMapCaption(Object [][] ListMap, Object oValue)
	{
		int nIndex = getListMapIndex(ListMap, oValue);
		if (nIndex != -1)
		{
			Object oCaption = ListMap[nIndex][1];
			if (oCaption == null)
				oCaption = ListMap[nIndex][0];

			return oCaption;
		}
		else
			return null;
	}

	protected Object getListMapValue(Object [][] ListMap, int nIndex)
	{
		return (ListMap[nIndex][0]);
	}

	private void initValues()
	{
		if (m_ValueList.size() <= 0)
			return;

		String rowData[][] = new String[m_ValueList.size()][m_colNames.length];
		ValueInfo vi;

		for (int i = 0; i < m_ValueList.size(); i++)
		{
			vi = (ValueInfo)m_ValueList.get(i);

			rowData[i] = createValueRow(vi);
		}

		//Set new data
		DefaultTableModel dtm = (DefaultTableModel)m_tableValues.getModel();
		dtm.setDataVector(rowData, m_colNames);
	}

	private String objectToString(Object oValue)
	{
		String strValue = "";

		if (oValue != null)
		{
			if (oValue instanceof Date)
				strValue = GValidator.DateToString((Date)oValue);
			else
				strValue = oValue.toString();
		}

		return strValue;
	}

	public String [] createValueRow(ValueInfo valueInfo)
	{
		String rowData[];
		String str;

		rowData = new String [m_colNames.length];

		ValueDesc [] valueDesc = valueInfo.getValues();
		Object oValue;
		Object [][] ListMap;

		for (int i = 0; i < valueDesc.length; i++)
		{
			oValue = valueDesc[i].getValue();
			ListMap = getListMap(valueDesc[i].getName());

			if (ListMap != null)
				oValue = getListMapCaption(ListMap, oValue);

			str = objectToString(oValue);
			rowData[i] = str;
		}
		return rowData;
	}

  /*******************************************************************
   *  Makes the User Interface.
   *******************************************************************/

  private JPanel makeControlPanel()
  {
	 if (m_ValueList == null)
		return null;

    JPanel dataPanel, controlPanel, tempPanel;

    controlPanel = new JPanel();
    controlPanel.setLayout(new BorderLayout());

	// Table label
    tempPanel = new JPanel();
    tempPanel.setLayout(new FlowLayout());

    tempPanel.add(new JLabel("List of Items :"));
	controlPanel.add(tempPanel, "North");

	// Broker list table
	String rowData[][] = new String[0][m_colNames.length];

	DefaultTableModel tm = new DefaultTableModel(rowData, m_colNames);
	m_tableValues = new JTable(tm);
	m_tableValues.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	m_tableValues.getTableHeader().setReorderingAllowed(false);

	//initValues(); //init deferred

	JScrollPane scrollpane = new JScrollPane(m_tableValues);
	//scrollpane.setPreferredSize(m_tableBrokers.getPreferredSize());

	controlPanel.add(scrollpane, "Center");

	//Command Panel
	JPanel commandPanel = new JPanel();

    buttonAdd = new JButton("Add");
    buttonAdd.addActionListener(this);
    commandPanel.add(buttonAdd);

    buttonModify = new JButton("Modify");
    buttonModify.addActionListener(this);
    commandPanel.add(buttonModify);

    buttonDelete = new JButton("Delete");
	buttonDelete.addActionListener(this);
    commandPanel.add(buttonDelete);

	/*buttonOk = new JButton("Ok");
	buttonOk.addActionListener(this);
    commandPanel.add(buttonOk);*/

    buttonCancel = new JButton("Close");
	buttonCancel.addActionListener(this);
    commandPanel.add(buttonCancel);

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


  /*******************************************************************
   *   Listens for and responds to button presses.
   *   @param ev action event
   *******************************************************************/

  public void actionPerformed(ActionEvent ev)
  {
	  Object obj = ev.getSource();

	if (obj == buttonAdd)
      onAdd();
    else if (obj == buttonModify)
      onModify();
    else if (obj == buttonDelete)
      onDelete();
    else if (obj == buttonOk)
      onOk();
    else if (obj == buttonCancel)
      onCancel();
  }

  protected void setDataToUI()
  {
	initValues();
  }

  protected void getDataFromUI()
  {
	  //Do nothing
  }

  public boolean validateUIData()
  {
	//do validation and display error msg

	return true;
  }

  public void onAdd()
  {
	ValueInfo valueInfo = null;
	ValueInfoDialog dlgValues = new ValueInfoDialog(this, "Add Data", true);

	dlgValues.addListMap(m_vMapValueNames, m_vListMaps);
	dlgValues.addPageMap(m_vPageNames, m_vPageValueNames);

	valueInfo = m_valueInfoMgr.newValue(); //create new instance
	dlgValues.setValueInfo(valueInfo.getValues());

	if (dlgValues.doDialog())
	{
		valueInfo.setValues(dlgValues.getValueInfo());
		boolean bRet = true;
		if (m_valueInfoMgr != null)
			bRet = m_valueInfoMgr.addValue(valueInfo);

		if (!bRet)
			showErrorMsg("Add failed.");
		else
		{
			m_ValueList.add(valueInfo);

			DefaultTableModel dtm = (DefaultTableModel)m_tableValues.getModel();
	  		dtm.addRow(createValueRow(valueInfo));
		}
	}
  }

  public void onModify()
  {
	int nSel = m_tableValues.getSelectedRow();
	if (nSel == -1)
		return;

	ValueInfo valueInfo = (ValueInfo)m_ValueList.get(nSel);
	Object oKey = valueInfo.getKey();

	ValueInfoDialog dlgValues = new ValueInfoDialog(this, "Edit Data", true);

	dlgValues.addListMap(m_vMapValueNames, m_vListMaps);
	dlgValues.setValueInfo(valueInfo.getValues());

	if (dlgValues.doDialog())
	{
		ValueInfo newInfo = m_valueInfoMgr.newValue();
		newInfo.setKey(oKey);
		newInfo.setValues(dlgValues.getValueInfo());

		boolean bRet = true;
		if (m_valueInfoMgr != null)
			bRet = m_valueInfoMgr.updateValue(oKey, newInfo);

		if (bRet)
		{
			m_ValueList.setElementAt(newInfo, nSel);

			DefaultTableModel dtm = (DefaultTableModel)m_tableValues.getModel();
			dtm.removeRow(nSel);
			dtm.insertRow(nSel, createValueRow(newInfo));
		}
		else
			showErrorMsg("Update failed.");
	}
  }

  public void onDelete()
  {
	  int nSel = m_tableValues.getSelectedRow();

	  if (nSel != -1)
	  {
		ValueInfo valueInfo = (ValueInfo)m_ValueList.get(nSel);

		boolean bRet = true;
		if (m_valueInfoMgr != null)
			bRet = m_valueInfoMgr.deleteValue(valueInfo.getKey());

		if (bRet)
		{
			m_ValueList.remove(nSel);

			DefaultTableModel dtm = (DefaultTableModel)m_tableValues.getModel();
			dtm.removeRow(nSel);
		}
		else
			showErrorMsg("Delete failed.");
	  }
  }

  public void onOk()
  {
    setReturnFlag(true);
    dispose();
  }

  public void onCancel()
  {
	  setReturnFlag(false);
	  dispose();
  }
}
