package utils.gwt;

import java.awt.*;

public class GTableCell
{
	public static final int STRING_TYPE = 0;
	public static final int INT_TYPE = 1;
	public static final int FLOAT_TYPE = 2;
	public static final int DOUBLE_TYPE = 3;
	public static final int BOOLEAN_TYPE = 4;

	private Object m_oValue;
	private int m_iValueType;

	private boolean m_bEditable = false;
	private boolean m_bDirty = false;

	private Object m_oUserData;
	private Image m_image;
	private Font m_font;
	private Color m_colCell;

	/**
	* Table cell constructor - generic object as value
	*/
	public GTableCell(Object oValue, int nType)
	{
		initData(oValue, nType);
	}

	/**
	* Table cell constructor - string value
	*/
	public GTableCell(String strValue)
	{
		initData(new String(strValue), STRING_TYPE);
	}

	/**
	* Table cell constructor - integer value
	*/
	public GTableCell(Integer oiValue)
	{
		initData(new Integer(oiValue.intValue()), INT_TYPE);
	}

	/**
	* Table cell constructor - integer value
	*/
	public GTableCell(int nValue)
	{
		initData(new Integer(nValue), INT_TYPE);
	}

	/**
	* Table cell constructor - float value
	*/
	public GTableCell(Float ofValue)
	{
		initData(new Float(ofValue.floatValue()), FLOAT_TYPE);
	}

	/**
	* Table cell constructor - float value
	*/
	public GTableCell(float fValue)
	{
		initData(new Float(fValue), FLOAT_TYPE);
	}

	/**
	* Table cell constructor - double value
	*/
	public GTableCell(Double odValue)
	{
		initData(new Double(odValue.doubleValue()), DOUBLE_TYPE);
	}

	/**
	* Table cell constructor - double value
	*/
	public GTableCell(double dValue)
	{
		initData(new Double(dValue), DOUBLE_TYPE);
	}

	/**
	* Table cell constructor - boolean value
	*/
	public GTableCell(Boolean obValue)
	{
		initData(new Boolean(obValue.booleanValue()), BOOLEAN_TYPE);
	}

	/**
	* Table cell constructor - boolean value
	*/
	public GTableCell(boolean bValue)
	{
		initData(new Boolean(bValue), BOOLEAN_TYPE);
	}

	/**
	* intialize cell data
	*/
	public void initData(Object oValue, int nType)
	{
		setValue(oValue);
		setValueType(nType);
	}

	/**
	* set/get the value of the table cell
	*/
	public void setValue(Object p_oValue) { m_oValue = p_oValue; }
   	public Object getValue() { return m_oValue;	}

	/**
	* set/get the value type of the table cell
	*/
   	public void setValueType(int p_iValueType) { m_iValueType = p_iValueType; }
	public int getValueType() { return m_iValueType; }

	/**
	* set/get the user data of the table cell
	*/
	public void setUserData(Object p_oUserData) { m_oUserData = p_oUserData; }
   	public Object getUserData() { return m_oUserData;	}

	/**
	* set/get the editable flag of the table cell
	*/
    public void setEditable(boolean p_bEditable) { m_bEditable = p_bEditable; }
	public boolean getEditable() { return m_bEditable; }

	/**
	* set/get the dirty flag of the table cell
	*/
	public void setDirty(boolean p_bDirty) { m_bDirty = p_bDirty; }
	public boolean getDirty() { return m_bDirty; }

	/**
	* set/get the icon image of the table cell
	*/
	public void setImage(Image p_image) { m_image = p_image; }
	public Image getImage() { return m_image; }

	/**
	* set/get the color of the table cell
	*/
	public void setColor(Color p_colCell) { m_colCell = p_colCell; }
	public Color getColor() { return m_colCell; }

	/**
	* set/get the font of the table cell
	*/
	public void setFont(Font p_font) { m_font = p_font; }
	public Font getFont() { return m_font; }

	/**
	* returns the string equivalent of the cell value
	*/
	public String toString()
	{
		if (m_oValue != null)
			return m_oValue.toString();
		else
			return "";
	}
}
