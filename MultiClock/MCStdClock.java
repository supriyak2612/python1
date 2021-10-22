package tools.multiclock;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.text.*;
import java.util.*;

/**
 * This class uses the AWT to display a clock.
 *
 */
public class MCStdClock extends JComponent implements MCClockField
{
	private Color m_bkgColorAM = null; //background color for AM time
	private Color m_bkgColorPM = null; //background color for PM time

	private Color m_brdrColor = null; //border color
	private Color m_textColor = null; //text color
	private Font  m_textFont = null; //text font

	private Image m_imageLogo = null; //Logo image

  private static boolean bDebugMsg = false;

  private static final int DEF_CLOCK_SIZE = 150;

  private int hourHandLength;     // Length of hour hand
  private int minuteHandLength;   // Length of minute hand
  private int secondHandLength;   // Length of second hand
  private int numberDistance;     // Where the numbers are placed
  private int circleRadius;       // Radius of the clock face

  TimeZone m_timeZone;
  Calendar m_cal;

  /**
   * Creates a new <code>Clock</code> of a given size.
   */
   public MCStdClock()
   {
	   init(DEF_CLOCK_SIZE);
   }

  public MCStdClock(Image image)
  {
	  init(DEF_CLOCK_SIZE);
	  m_imageLogo = image;
  }

  public MCStdClock(int size, Image image)
  {
	  init(size);
	  m_imageLogo = image;
  }

  public void init(int size)
  {
    //this.setSize(size, size);

    // Calculate various lengths based on size
    reSize(new Dimension(size, size));

	// When the window is resized, update the clock
    this.addComponentListener(new ComponentAdapter() {
	public void componentResized(ComponentEvent e) {
	  MCStdClock.this.reSize();
	  MCStdClock.this.repaint();
	}
      });

    this.repaint();
  }

	/**
	* sets/gets the time zone of the clock
	*/
	public void setTimeZone(TimeZone timeZone) { m_timeZone = timeZone; }
	public TimeZone getTimeZone() { return m_timeZone; }

	public void setEditable(boolean bEditable) { }

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

	private void reSize()
	{
		reSize(this.getSize());
	}

  /**
   * Sets various length values based on the size of the clock.
   */
  private void reSize(Dimension d)
  {
    int size = (d.height > d.width ? d.width : d.height);
/* OLD_CODE (for size = 400)
    this.circleRadius = 95 * size / 200;
    this.numberDistance = 90 * size / 200;
    this.minuteHandLength = 85 * size / 200;
    this.secondHandLength = 80 * size / 200;
    this.hourHandLength = 50 * size / 200;
 */
 	this.circleRadius = 95 * (size/2) / 100;
    this.numberDistance = 90 * (size/2) / 100;
    this.minuteHandLength = 85 * (size/2) / 100;
    this.secondHandLength = 80 * (size/2) / 100;
    this.hourHandLength = 50 * (size/2) / 100;
  }

  /**
   * Draw the clock.
   */
  public void paintComponent(Graphics g)
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

    Dimension d = this.getSize();
    int size = (d.height > d.width ? d.width : d.height);

    Point center = new Point(size / 2, size / 2);

	int hourDay = m_cal.get(Calendar.HOUR_OF_DAY);

	int nTopX, nTopY;
	int nWidth, nHeight;

	nTopX = center.x - this.circleRadius;
	nTopY = center.y - this.circleRadius;
	nWidth = this.circleRadius * 2;
	nHeight = this.circleRadius * 2;

	//Draw background
	g.setColor((hourDay < 12) ? m_bkgColorAM : m_bkgColorPM);

	g.fillOval(nTopX, // upper left X
		   nTopY,     // upper left Y
		   nWidth,    // width
	       nHeight);  // height

	//Draw Logo
	if (m_imageLogo != null)
	{
		int nLogoWidth = size/4;
		int nLogoHeight = size/4;
		g.drawImage(m_imageLogo, center.x-nLogoWidth/2, center.y-nLogoHeight/2,
					nLogoWidth, nLogoHeight, null);
	}

    // Draw the clock face, a circle centered in the middle of the
    // clock with radius
    g.setColor(m_brdrColor);
    //Draw main clock circle
    g.drawOval(nTopX, // upper left X
			   nTopY,     // upper left Y
			   nWidth,    // width
	           nHeight);  // height

	//Draw additional lines to provide thick border
	//Draw first extra line
	g.drawOval(nTopX-1, // upper left X
			   nTopY-1,     // upper left Y
			   nWidth+2,    // width
	           nHeight+2);  // height

	//Draw second extra line
	g.drawOval(nTopX-2, // upper left X
			   nTopY-2,     // upper left Y
			   nWidth+4,    // width
	           nHeight+4);  // height

    //Set text font
	g.setColor(m_textColor);
	g.setFont(m_textFont);

    // Draw the numbers on the clock face

	FontMetrics fm = g.getFontMetrics();
	int x, y, nQuad;
	int nNumEndX, nNumEndY;
	int nNumWidth, nNumHeight;
	String strNum;
	Point end;

	/***********
	The logic to center the text w.r.t the end point on the circle, is described here.
	Let us divide the clock circle into 4 quatradants, as shown below.
	+-----+-----+
	+ Q4  | Q1  |
	+-----+-----+
	+ Q3  | Q2  |
	+-----+-----+
	Q1 - contains numbers 1, 2, 3; number to be drawn left, below; X+, Y-
	Q2 - contains numbers 4, 5, 6; number to be drawn left, above; X+, Y+
	Q3 - contains numbers 7, 8, 9; number to be drawn right, above, X-, Y+
	Q4 - contains numbers 10, 11, 12; number to be drawn right, below, X-, Y-
	*******/

    for (int i = 1; i <= 12; i++)
    {
	  strNum = Integer.toString(i);
      end = this.pointAtTime(center, 5 * i, this.numberDistance);

	  // Attempt to center the string nicely

      nNumWidth = fm.stringWidth(strNum);
      nNumHeight = fm.getHeight();

	  //center position for the number
      x = end.x - (nNumWidth / 2);
      y = end.y + (nNumHeight / 2);

	  ///////// START NEW_CODE
	  //apply constraints to position the number within the circle
	  if (i == 1 || i == 2 || i == 3) //Q1
	  	nQuad = 1;
	  else if (i == 4 || i == 5 || i == 6) //Q2
	  	nQuad = 2;
	  else if (i == 7 || i == 8 || i == 9) //Q3
	  	nQuad = 3;
	  else //Q4
	  	nQuad = 4;

	  nNumEndX = x+nNumWidth;
	  nNumEndY = y+nNumHeight;

	  if (!pointXInRange(end, nNumEndX, nQuad))
	  {
    	x = x - Math.abs(nNumEndX-end.x);
    	if (bDebugMsg)
	  		System.out.println("Adjusted End X for Number " + i);
	  }

	  if (!pointYInRange(end, nNumEndY, nQuad))
	  {
	  	y = y + Math.abs(nNumEndY-end.y);
	  	if (bDebugMsg)
	  		System.out.println("Adjusted End Y for Number " + i);
  	  }

	  if (!pointXInRange(end, x, nQuad))
	  {
		if (nQuad == 1 || nQuad == 2)
			x = end.x - nNumWidth;
		else
			x = end.x;
		if (bDebugMsg)
			System.out.println("Adjusted X for Number " + i);
	  }

	  if (!pointYInRange(end, y, nQuad))
	  {
		if (nQuad == 1 || nQuad == 4)
			y = end.y + nNumHeight;
		else
			y = end.y;
		if (bDebugMsg)
			System.out.println("Adjusted Y for Number " + i);
	  }
  	  ///////// END NEW_CODE

      g.drawString(strNum, x, y);
    }

    bDebugMsg = false;

	g.setColor(m_brdrColor); //restore border color

    // Draw the minute hand
    int minute = m_cal.get(Calendar.MINUTE);
    Point minuteEnd = this.pointAtTime(center, minute,
				       this.minuteHandLength);
    g.drawLine(center.x, center.y, minuteEnd.x, minuteEnd.y);

    // Draw the hour hand
    int hour = m_cal.get(Calendar.HOUR);
    Point hourEnd = this.pointAtTime(center, 5 * hour + (minute / 12),
				     this.hourHandLength);
    g.drawLine(center.x, center.y, hourEnd.x, hourEnd.y);
    g.drawLine(center.x+1, center.y, hourEnd.x+1, hourEnd.y); //for thick line

    // Draw the second hand in red
    int second = m_cal.get(Calendar.SECOND);
    Point secondEnd = this.pointAtTime(center, second,
				       this.secondHandLength);
    g.setColor(Color.red);
    g.drawLine(center.x, center.y, secondEnd.x, secondEnd.y);

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

  private boolean pointXInRange(Point refPt, int x,int nQuad)
  {
	/***
		Q1: X+, Y-
		Q2: X+, Y+
		Q3: X-, Y+
		Q4: X-, Y-
	***/
	if (nQuad == 1 || nQuad == 2) //Q1, Q2
		return (x <= refPt.x);
	else //Q3, Q4
		return (x >= refPt.x);
  }

  private boolean pointYInRange(Point refPt, int y, int nQuad)
  {
	/***
		Q1: X+, Y-
		Q2: X+, Y+
		Q3: X-, Y+
		Q4: X-, Y-
	***/
	if (nQuad == 1 || nQuad == 4) //Q1, Q4
		return (y >= refPt.y);
	else //Q2, Q3
		return (y <= refPt.y);
  }

  /**
   * Returns the point along the clock's edge corresponding to a given
   * minute and radius.
   *
   * @param center
   *        The center of the clock
   * @param minutes
   *        The number of minutes past the hour
   * @param radius
   *        How far away from the center should the point be?
   *
   * @return The point radius distance from the center point towards a
   *         given minute.
   */
  private Point pointAtTime(Point center, int minutes, int radius) {
    // Each minute counts for 6 degrees
    double angle = -1.0 * Math.toRadians(minutes * 6.0 + 180.0);
    double x = radius * Math.sin(angle) + center.x;
    double y = radius * Math.cos(angle) + center.y;

    return new Point((int) x, (int) y);
  }

  public Dimension getPreferredSize() {
    // As I recall, it is a good idea to override this method
    //return this.getSize();
    return new Dimension(DEF_CLOCK_SIZE, DEF_CLOCK_SIZE);
  }

  /*public Dimension getMinimumSize() {
    return this.getPreferredSize();
  }*/
}

