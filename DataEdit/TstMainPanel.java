package poc.dataedit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import utils.gwt.GValidator;
import utils.dataedit.*;

public class TstMainPanel extends JPanel
			 implements ActionListener
{
	private JFrame parentFrame;

	private JMenuBar m_menuBar;

	private TstAddressInfoManager m_addrMgr = new TstAddressInfoManager();
	private TstProjectInfoManager m_projMgr = new TstProjectInfoManager();
	private TstEmployeeInfoManager m_empMgr = new TstEmployeeInfoManager();

    public TstMainPanel(JFrame frame)
	{
		parentFrame = frame;

		setLayout(new BorderLayout());

		//frame.getContentPane().add(this, "Center");

		//add(makeMenuBar(), "North");
		m_menuBar = makeMenuBar();

	    add(makeControlPanel(), "Center");

	    //Put sample test data
	    TstAddress Address = new TstAddress("Lehman", "70, Hudson Street", null, null,
							"New Jersey", "New Jersey", "USA", "NJ 070703");
		m_addrMgr.addValue(Address);

		TstProject Project = new TstProject("Lehman NH Website", "LEHMAN-017", "D", true, 10, 33, 150,
							GValidator.StringToDate("01-20-2003"), GValidator.StringToDate("06-30-2003"),
							"Pune", 12076);
		m_projMgr.addValue(Project);
   }

	private JComponent makeControlPanel()
	{
		JPanel panel = new JPanel();

		//TBD : Panel contents, if any, should be added here.

		return panel;
    }

	public JMenuBar getMenuBar()
	{
		return m_menuBar;
	}

    private JMenuBar makeMenuBar()
    {
		JMenuBar menuBar;
		JMenu menu;
		JMenuItem menuItem;

		menuBar = new JMenuBar();

		//File menu
		menu = new JMenu("File");

		menuItem = new JMenuItem("Exit");
		menuItem.setActionCommand("Exit");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuBar.add(menu);

		//Address related feaures
		menu = new JMenu("Address");

		menuItem = new JMenuItem("Add Address");
		menuItem.setActionCommand("AddAddress");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Edit Address");
		menuItem.setActionCommand("EditAddress");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuBar.add(menu);

		//Project related feaures
		menu = new JMenu("Project");

		menuItem = new JMenuItem("Add Project");
		menuItem.setActionCommand("AddProject");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Edit Project");
		menuItem.setActionCommand("EditProject");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuBar.add(menu);

		//Employee related feaures
		menu = new JMenu("Employee");

		menuItem = new JMenuItem("Add Employee");
		menuItem.setActionCommand("AddEmployee");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Edit Employee");
		menuItem.setActionCommand("EditEmployee");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuBar.add(menu);

		//Help menu
		menu = new JMenu("Help");

		menuItem = new JMenuItem("Contents and Index");
		menuItem.setActionCommand("HelpContent");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menu.add(new JSeparator());

		menuItem = new JMenuItem("About Test App");
		menuItem.setActionCommand("About");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuBar.add(menu);

		return menuBar;
	}

	public void actionPerformed(ActionEvent ev)
	{
		Object oSrc = ev.getSource();

		if (oSrc instanceof JMenuItem)
		{
			String strCmd = ((JMenuItem)oSrc).getActionCommand();

			if (strCmd.equals("Exit"))
				doExit();
			else if (strCmd.equals("AddAddress"))
				doAddAddress();
			else if (strCmd.equals("EditAddress"))
				doEditAddress();
			else if (strCmd.equals("AddProject"))
				doAddProject();
			else if (strCmd.equals("EditProject"))
				doEditProject();
			else if (strCmd.equals("AddEmployee"))
				doAddEmployee();
			else if (strCmd.equals("EditEmployee"))
				doEditEmployee();
			else if (strCmd.equals("About"))
				showMessage(TstApp.ABOUT_STR, TstApp.APPNAME_STR);
			else
				showMessage("Sorry! Not implemented yet.", TstApp.APPNAME_STR);
		}
	}

	private void showMessage(String strMsg, String strTitle)
	{
		JOptionPane.showMessageDialog(this, strMsg,strTitle,JOptionPane.INFORMATION_MESSAGE);
	}

	private void doExit()
	{
		TstApp.getApp().exitMainWindow();
	}

	private void doAddInfo(String strTitle, ValueInfoManager infoManager)
	{
		Vector vMapValueNames, vListMaps;
		Vector vPageNames, vValueNames;

		vMapValueNames = new Vector();
		vListMaps = new Vector();
		infoManager.getValueItemListMap(vMapValueNames, vListMaps);

		vPageNames = new Vector();
		vValueNames = new Vector();
		infoManager.getValueGroupMap(vPageNames, vValueNames);

		ValueInfoDialog dlgValue = new ValueInfoDialog(parentFrame, strTitle, true);
		dlgValue.addListMap(vMapValueNames, vListMaps);
		dlgValue.addPageMap(vPageNames, vValueNames);

		ValueInfo valueInfo = infoManager.newValue(); //create new instance
		dlgValue.setValueInfo(valueInfo.getValues());

		if (dlgValue.doDialog())
		{
			valueInfo.setValues(dlgValue.getValueInfo());
			infoManager.addValue(valueInfo);
		}
	}

	private void doEditInfo(String strTitle, ValueInfoManager infoManager)
	{
		ValueListDialog dlgValueList = new ValueListDialog(parentFrame, strTitle, true);

		dlgValueList.setValueInfoManager(infoManager);
		dlgValueList.setValueList(infoManager.getValueList());

		dlgValueList.doDialog();
	}

	private void doAddAddress()
	{
		doAddInfo("Add Address", m_addrMgr);
	}

	private void doEditAddress()
	{
		doEditInfo("Edit Address", m_addrMgr);
	}

	private void doAddProject()
	{
		doAddInfo("Add Project", m_projMgr);
	}

	private void doEditProject()
	{
		doEditInfo("Edit Project", m_projMgr);
	}

	private void doAddEmployee()
	{
		doAddInfo("Add Employee", m_empMgr);
	}

	private void doEditEmployee()
	{
		doEditInfo("Edit Employee", m_empMgr);
	}
}
