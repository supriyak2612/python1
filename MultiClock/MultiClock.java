package tools.multiclock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import utils.gwt.GToolkit;

public class MultiClock
{

  public static MultiClock m_theApp = null;

  private MCDisplayPanel m_panelClock;

  Image m_imageLogo;

  private JFrame m_frameMain;

  private MCProperties m_appProps;
  private MCTimeKeeper m_timeKeeper;

  String [] m_timeZoneIDs;

  //static final String m_IconDir = "Icons\\";
  static final String m_IconDir = "";

  /**
  * class constructor
  */
  public MultiClock()
  {
  }

  /**
  * returns the main application
  */
  public static MultiClock getApp()
  {
	  return m_theApp;
  }

  /**
  * returns the clock application properties
  */
  public MCProperties getProperties()
  {
	  return m_appProps;
  }

  /**
  * returns the time keeper
  */
  public MCTimeKeeper getTimeKeeper()
  {
  	return m_timeKeeper;
  }

  /**
  * exits the main application
  */
  public void exit()
  {
	  System.exit(0);
  }

  /**
  * centers the given frame within the desktop screen area
  */
  public void centerFrame(JFrame frame)
  {
  	Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
 	int screenWidth = dim.width;
  	int screenHeight = dim.height;
  	int frameWidth = frame.getWidth();
  	int frameHeight = frame.getHeight();
  	frame.setBounds((screenWidth/2) - (frameWidth/2) ,(screenHeight/2) -  (frameHeight/2), frameWidth, frameHeight );
  }

  /**
  * intializes and launches the main window of the application
  */
  public void launchClockDisplay()
  {
    m_frameMain = new JFrame("Multi-Clock");
    m_frameMain.setForeground(Color.black);
    m_frameMain.setBackground(Color.lightGray);
    m_frameMain.getContentPane().setLayout(new BorderLayout());
    Image image = getImage("Globe.gif");
	if (image != null)
	    m_frameMain.setIconImage(image);


    try {
      m_panelClock = new MCDisplayPanel(m_frameMain, m_timeZoneIDs,
      		m_appProps.m_strDefaultZone, m_appProps.m_nRefreshTime,
      		m_appProps.m_nClockType);

      WindowListener l = new MCWinAdapter(this);
      m_frameMain.addWindowListener(l);
    }
    catch (Exception e) {
      System.out.println("Exception has occured : " + e.getMessage());
      e.printStackTrace();
      System.exit(0);
    }

	m_frameMain.setResizable(true);
    m_frameMain.pack();
    centerFrame(m_frameMain);
    m_frameMain.setVisible(true);
  }

  /**
  * handler for window closing event
  */
  public void windowClosing(Object oWindow)
  {
	if (oWindow == m_frameMain)
	{
	  m_panelClock = null;
	 }
  	 exit();
  }

  /**
  * exit the main application
  */
  public void exitClockDisplay()
  {
  	  if (m_frameMain != null)
  	  {
  		  m_frameMain.dispose();
  		  windowClosing(m_frameMain);
  	  }
  }

  /**
  * returns the specified image
  */
  public static Image getImage(String strImageName)
  {
	  return GToolkit.getImage(strImageName);
  }

  /**
  * returns the specified image icon
  */
  public static ImageIcon getIcon(String strImageName)
  {
  	  return GToolkit.getIcon(strImageName);
  }

  /**
  * Application main method
  */
  public void Main(String argv [])
  {
	m_imageLogo = getImage("MultiClock.gif");

	m_appProps = new MCProperties("MultiClock.props");
	m_appProps.load();

	m_timeKeeper = new MCTimeKeeper();
	m_timeKeeper.initialize(m_appProps.m_vAlarms);

	int nCount = m_appProps.m_vDisplayZones.size();
	if (nCount > 0)
	{
		int i;

		m_timeZoneIDs = new String[nCount+argv.length];
		for (i = 0; i < nCount; i++)
			m_timeZoneIDs[i] = new String((String)m_appProps.m_vDisplayZones.get(i));

		for (i = 0; i < argv.length; i++)
			m_timeZoneIDs[i+nCount] = argv[i];
	}
	else if (argv.length > 0)
		m_timeZoneIDs = argv;

	launchClockDisplay();

	m_timeKeeper.startWatch();
  }

 /**
  *  standard main method that loads the application
  */
  public static void main(String argv [])
  {
	  m_theApp = new MultiClock();
	  m_theApp.Main(argv);
  }

  public class MCWinAdapter extends WindowAdapter
  {
		public MultiClock m_App;

		MCWinAdapter(MultiClock theApp) {m_App = theApp; }

		public void windowClosing(WindowEvent e)
		{
			m_App.windowClosing(e.getSource());
		}
   }
}

