package poc.dataedit;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import utils.gwt.GToolkit;

public class TstApp
{
  public static TstApp m_theApp = null;

  private JFrame m_frameMain;
  private JPanel panelFrame;

  private JMenuBar menuBar;

  private TstMainPanel m_panelCMS;

  public static final String ABOUT_STR = "This is a test application developed for as a Proof Of Concept (POC).";
  public static final String APPNAME_STR = "POC Test Application";

  Image m_imageLogo;

  public TstApp()
  {
  }

  public static TstApp getApp()
  {
	  return m_theApp;
  }

  public void exit()
  {
	  System.exit(0);
  }

 public void centerFrame(JFrame frame)
 {
  	Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
 	int screenWidth = dim.width;
  	int screenHeight = dim.height;
  	int frameWidth = frame.getWidth();
  	int frameHeight = frame.getHeight();
  	frame.setBounds((screenWidth/2) - (frameWidth/2) ,(screenHeight/2) -  (frameHeight/2), frameWidth, frameHeight );
 }

  public void launchMainWindow()
  {
	  m_frameMain = new JFrame("POC Test Application");
	  m_frameMain.setForeground(Color.black);
	  m_frameMain.setBackground(Color.lightGray);
	  m_frameMain.getContentPane().setLayout(new BorderLayout());
	  if (m_imageLogo != null)
	    m_frameMain.setIconImage(m_imageLogo);

	  try {

		panelFrame = new JPanel();
		panelFrame.setLayout(new BorderLayout());

		m_panelCMS = new TstMainPanel(m_frameMain);
		menuBar = m_panelCMS.getMenuBar();

		panelFrame.add(menuBar, "North");

		m_frameMain.getContentPane().add(panelFrame, "Center");

		WindowListener l = new CMSWinAdapter(this);
		m_frameMain.addWindowListener(l);
	  }
	  catch (Exception e) {
		System.out.println("Exception has occured : " + e.getMessage());
		e.printStackTrace();
		System.exit(0);
	  }

	  //m_frameMain.setResizable(false);
	  m_frameMain.setSize(650, 500);
	  //m_frameMain.pack();
	  centerFrame(m_frameMain);
	  m_frameMain.setVisible(true);
  }

  public void windowClosing(Object oWindow)
  {
  	  if (oWindow == m_frameMain)
	  {
	   	  m_frameMain = null;
	  }

  	  //if (m_frameStatus == null && m_frameBooker == null && m_frameMain == null)
  	    exit();
  }

  public void exitMainWindow()
  {
	  if (m_frameMain != null)
	  {
		  m_frameMain.dispose();
		  windowClosing(m_frameMain);
	  }
  }

  public void Main(String argv [])
  {
	m_imageLogo = GToolkit.getImage("TstApp.gif");

	launchMainWindow();
  }

 /*******************************************************************
  *  main() method.
  *******************************************************************/
  public static void main(String argv [])
  {
	m_theApp = new TstApp();
	m_theApp.Main(argv);
  }

  public class CMSWinAdapter extends WindowAdapter
  {
		public TstApp m_App;

		CMSWinAdapter(TstApp theApp) {m_App = theApp; }

		public void windowClosing(WindowEvent e)
		{
			m_App.windowClosing(e.getSource());
		}
   }
}

