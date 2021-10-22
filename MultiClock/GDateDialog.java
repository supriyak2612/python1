package utils.gwt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.border.*;

public class GDateDialog extends GDialog
                            implements MouseListener, ItemListener
{
	//GUI attributes
	private JTextField textDataSrcName, textDBAccessType;
	private JTextField textPDFViewer, textJPGViewer;

	private JComboBox comboYear, comboMonth;

    private Date m_date = null;
    private boolean m_bInSetData = false;

    private int m_nSelDay = -1;
    private JLabel m_labelSelDay = null;

    private String Months[] = {
        "Jan", "Feb", "Mar",
        "Apr", "May", "Jun",
        "Jul", "Aug", "Sep",
        "Oct", "Nov", "Dec"
    };

    private int DaysInMonth[] = {
        31, 28, 31,
        30, 31, 30,
        31, 31, 30,
        31, 30, 31
    };

    private JLabel m_Days[][];

    private Font m_selFont, m_defFont;
    private Color m_selColor, m_defColor;
    private Border m_selBorder, m_defBorder;

	/**
	* Dialog Constructor
	*/
	public GDateDialog(Frame owner, String title, boolean modal)
	{
		super(owner, title, modal);

		initDlg();
	}

	/**
	* Dialog Constructor
	*/
	public GDateDialog(Dialog owner, String title, boolean modal)
	{
		super(owner, title, modal);

		initDlg();
	}

	/**
	* Initializes the dialog User interface
	*/
	private void initDlg()
	{
		getContentPane().setLayout(new BorderLayout());

		getContentPane().add(makeControlPanel(), "Center");

		setSize(300, 250);
	}

	/**
	* sets the given date to the dialog
	*/
	public void setDate(Date date)
	{
	    if (date != null)
		    m_date = new Date(date.getTime());
		else
		    m_date = new Date();
	}

	/**
	* returns the selected date from the dialog
	*/
	public Date getDate()
	{
		return m_date;
	}

 /**
  * Makes the User Interface.
  */
  private JPanel makeControlPanel()
  {
    JPanel dataPanel, controlPanel;

    controlPanel = new JPanel();
    controlPanel.setLayout(new BorderLayout());

    dataPanel = new JPanel();
    dataPanel.add(new JLabel("Year :"));
    comboYear = new JComboBox();
    for (int nYear = 1970; nYear <= 2100; nYear++)
        comboYear.addItem(Integer.toString(nYear));
    comboYear.setSelectedIndex(0);
    comboYear.addItemListener(this);
    dataPanel.add(comboYear);

    dataPanel.add(new JLabel("Month :"));
    comboMonth = new JComboBox(Months);
    comboMonth.setSelectedIndex(0);
    comboMonth.addItemListener(this);
    dataPanel.add(comboMonth);

    controlPanel.add(dataPanel, "North");

    //m_defBorder = new BevelBorder(BevelBorder.RAISED);
    m_defBorder = null;
    m_selBorder = new BevelBorder(BevelBorder.LOWERED);

    dataPanel = new JPanel();
    dataPanel.setLayout(new GridLayout(0, 7, 1, 10));

    dataPanel.add(new JLabel("SUN"));
    dataPanel.add(new JLabel("MON"));
    dataPanel.add(new JLabel("TUE"));
    dataPanel.add(new JLabel("WED"));
    dataPanel.add(new JLabel("THU"));
    dataPanel.add(new JLabel("FRI"));
    dataPanel.add(new JLabel("SAT"));

	m_Days = new JLabel[5][7] ;

	int nDay = 1;
	JLabel labelDay;
	for (int nRow = 0; nRow < 5; nRow++)
	    for (int nCol = 0; nCol < 7; nCol++)
	    {
	        labelDay = new JLabel(Integer.toString(nDay));
	        m_Days[nRow][nCol] = labelDay;
	        if (m_defFont == null)
	        {
	            m_defFont = labelDay.getFont();
	            m_defColor = labelDay.getForeground();

	            if (m_selFont == null)
	                m_selFont = new Font(m_defFont.getName(), Font.BOLD, m_defFont.getSize());

	            if (m_selColor == null)
	                m_selColor = Color.blue;
	        }
	        labelDay.addMouseListener(this);
	        dataPanel.add(labelDay);
	        nDay++;
	    }

	controlPanel.add(dataPanel, "Center");

	JPanel commandPanel = new JPanel();

    JButton buttonOK = new JButton("OK");
    buttonOK.addActionListener(this);
    commandPanel.add(buttonOK);

    JButton buttonCancel = new JButton("Cancel");
    buttonCancel.addActionListener(this);
    commandPanel.add(buttonCancel);

    registerOKButton(buttonOK);
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
   *   Sets the data to the dialog controls
   */
  protected void setDataToUI()
  {
	if (m_date == null)
		return;

	m_bInSetData = true;

	int nDay, nMonth, nYear, nWeekDay;
	int nFirstWeekDay, nLastWeekDay;

	Calendar cal = Calendar.getInstance(); //Get calender
	cal.setTime(m_date); //Set given date

	//Get date specific values
	nDay = cal.get(Calendar.DAY_OF_MONTH);
	nMonth = cal.get(Calendar.MONTH);
	nYear = cal.get(Calendar.YEAR);
	nWeekDay = cal.get(Calendar.DAY_OF_WEEK);

	//Get First week day of month
	cal.set(Calendar.DAY_OF_MONTH, 1);
	nFirstWeekDay = cal.get(Calendar.DAY_OF_WEEK);

	//Calculate num of days in a month
	int nMonthDays = DaysInMonth[nMonth];
	if (nMonth == 1 && (nYear%4) == 0)
	    nMonthDays++; //29 days in Feb for leap year

	//Get Last week day of month
	cal.set(Calendar.DAY_OF_MONTH, nMonthDays);
	nLastWeekDay = cal.get(Calendar.DAY_OF_WEEK);

	int nRow, nCol;

	//Show or hide cells depending on if it is used
	for (nCol = 0; nCol < 7; nCol++)
	{
        m_Days[0][nCol].setVisible(nCol >= nFirstWeekDay-1);
        m_Days[0][nCol].setBorder(m_defBorder);
	}

    for (nCol = 0; nCol < 7; nCol++)
    {
        m_Days[4][nCol].setVisible(nCol <= nLastWeekDay-1);
        m_Days[4][nCol].setBorder(m_defBorder);
	}

    //Display days of month
    int date = 1;
    for (nRow = 0; nRow < 5 && date <= nMonthDays; nRow++)
    {
        for (nCol = (nRow == 0)?nFirstWeekDay-1:0; nCol < 7 && date <= nMonthDays; nCol++)
        {
            m_Days[nRow][nCol].setText(Integer.toString(date));

            m_Days[nRow][nCol].setFont((date == nDay)?m_selFont:m_defFont);
            m_Days[nRow][nCol].setForeground((date == nDay)?m_selColor:m_defColor);
            m_Days[nRow][nCol].setBorder((date == nDay)?m_selBorder:m_defBorder);

            if (date == nDay)
            { //Set cur select day
                m_labelSelDay = m_Days[nRow][nCol];
                m_nSelDay = nDay;
            }

            date++;
        }
    }

    comboMonth.setSelectedIndex(nMonth); //Select month

    comboYear.setSelectedIndex(nYear-1970); //Select Year

    m_bInSetData = false;
  }

  /**
  * gets the data from the dialog controls
  */
  protected void getDataFromUI()
  {
	if (m_date == null)
		m_date = new Date();

	Calendar cal = Calendar.getInstance(); //Get calender
	cal.set(comboYear.getSelectedIndex()+1970, comboMonth.getSelectedIndex(), m_nSelDay);
	m_date = cal.getTime();
  }

    public void mouseReleased(MouseEvent e){}

	public void mousePressed(MouseEvent e) { }

	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }

	/**
	* Handles mouse click event and sets currently selected date
	*/
	public void mouseClicked(MouseEvent e)
	{
		Object oSrc = e.getSource();

		int nRow, nCol;
		int nDay = 1;
		int nSelDay = -1;
		JLabel labelSelDay = null;
		for (nRow = 0; nRow < 5 && nSelDay == -1; nRow++)
		    for (nCol = 0; nCol < 7 && nSelDay == -1; nCol++)
		    {
		        if (!m_Days[nRow][nCol].isVisible())
		            continue;
		        if (m_Days[nRow][nCol] == oSrc)
		        {
		            nSelDay = nDay;
		            labelSelDay = m_Days[nRow][nCol];
		        }
		        nDay++;
		    }

		 //Reset font & color for prev selection
		 m_labelSelDay.setFont(m_defFont);
         m_labelSelDay.setForeground(m_defColor);
         m_labelSelDay.setBorder(m_defBorder);

         //Set font & color for current selection
         labelSelDay.setFont(m_selFont);
         labelSelDay.setForeground(m_selColor);
         labelSelDay.setBorder(m_selBorder);

         //Save selected day
         m_nSelDay = nSelDay;
         m_labelSelDay = labelSelDay;
	}

	/**
	* Handles combo box selection change event. Sets the data accordingly.
	*/
	public void itemStateChanged(ItemEvent e)
	{
	    Object oSrc = e.getSource();

	    if ( (oSrc == comboYear || oSrc == comboMonth) && !m_bInSetData)
	    {
	        int nMonth, nYear, nDay;

	        nYear = comboYear.getSelectedIndex()+1970;
	        nMonth = comboMonth.getSelectedIndex();
	        nDay = m_nSelDay;

	        //Calculate num of days in a month
	        int nMonthDays = DaysInMonth[nMonth];
	        if (nMonth == 1 && (nYear%4) == 0)
	            nMonthDays++; //29 days in Feb for leap year
	        if (nDay > nMonthDays)
	            nDay = nMonthDays;

	        Calendar cal = Calendar.getInstance(); //Get calender
	        cal.set(nYear, nMonth, nDay);
	        m_date = cal.getTime();

	        setDataToUI();
	    }
	}
}
