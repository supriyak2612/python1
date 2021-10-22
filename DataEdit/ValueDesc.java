package utils.dataedit;

import java.util.Date;

public class ValueDesc
{
	public static final int VT_UNKNOWN = -1;

	public static final int VT_STR = 0;
	public static final int VT_BOOL = 1;
	public static final int VT_INT = 2;
	public static final int VT_FLOAT = 3;
	public static final int VT_DOUBLE = 4;
	public static final int VT_DATE = 5;

	public static String OBJ_STR = new String();
	public static Boolean OBJ_BOOL = new Boolean(false);
	public static Integer OBJ_INT = new Integer(0);
	public static Float OBJ_FLOAT = new Float(0);
	public static Double OBJ_DOUBLE = new Double(0);
	public static Date OBJ_DATE = new Date();

	private int m_nValueType = VT_UNKNOWN;

	private Object m_oValue = null;

	private Object m_oMinValue = null;
	private Object m_oMaxValue = null;
	private Object m_oDefValue = null;

	private int m_nLength = -1;

	private boolean m_bOptional = true;
	private boolean m_bEditable = true;
	private boolean m_bDirty = false;

	private String m_strName;
	private String m_strCaption;

	public ValueDesc()
	{
	}

	public ValueDesc(ValueDesc oSrcVal)
	{
		Object oValue = oSrcVal.getValue();

		/*if (oValue != null)
		{
			try
			{
				oValue = oValue.clone();
			}
			catch(CloneNotSupportedException e)
			{
			}
		}*/

		initValue(oValue);

		setLength(oSrcVal.getLength());

		setMinValue(oSrcVal.getMinValue());
		setMaxValue(oSrcVal.getMaxValue());
		setDefaultValue(oSrcVal.getDefaultValue());

		setName(oSrcVal.getName());
		setCaption(oSrcVal.getCaption());

		setOptional(oSrcVal.isOptional());
		setEditable(oSrcVal.isEditable());

		setDirty(oSrcVal.isDirty());
	}

	public ValueDesc(Object oValue)
	{
		initValue(oValue);
	}

	public ValueDesc(Object oValue, String strName, String strCaption)
	{
		initValue(oValue);

		setName(strName);
		setCaption(strCaption);
	}

	public ValueDesc(Object oValue, String strName, String strCaption,
					int nLen, boolean bOptional, boolean bEditable)
	{
		initValue(oValue);

		setName(strName);
		setCaption(strCaption);
		setLength(nLen);

		setOptional(bOptional);
		setEditable(bEditable);
	}

	public ValueDesc(Object oValue, Object oMinValue, Object oMaxValue, Object mDefValue,
					String strName, String strCaption)
	{
		initValue(oValue);

		setMinValue(oMinValue);
		setMaxValue(oMaxValue);
		setDefaultValue(mDefValue);

		setName(strName);
		setCaption(strCaption);
	}

	public ValueDesc(Object oValue, Object oMinValue, Object oMaxValue, Object mDefValue,
					String strName, String strCaption, boolean bOptional, boolean bEditable)
	{
		initValue(oValue);

		setMinValue(oMinValue);
		setMaxValue(oMaxValue);
		setDefaultValue(mDefValue);

		setName(strName);
		setCaption(strCaption);

		setOptional(bOptional);
		setEditable(bEditable);
	}

	private void initValue(Object oValue)
	{
		if (oValue != null)
		{
			if (oValue instanceof String)
				setValueType(VT_STR);
			else if (oValue instanceof Boolean)
				setValueType(VT_BOOL);
			else if (oValue instanceof Integer)
				setValueType(VT_INT);
			else if (oValue instanceof Float)
				setValueType(VT_FLOAT);
			else if (oValue instanceof Double)
				setValueType(VT_DOUBLE);
			else if (oValue instanceof Date)
				setValueType(VT_DATE);
		}
		setValue(oValue);
	}

	//Value Type
	public int getValueType() { return m_nValueType; }
	public void setValueType(int nValueType) { m_nValueType = nValueType; }

	//Value
	public Object getValue() { return m_oValue; }
	public void setValue(Object oValue) { m_oValue = oValue; }

	//Minimum Value
	public Object getMinValue() { return m_oMinValue; }
	public void setMinValue(Object oMinValue) { m_oMinValue = oMinValue; }

	//Maximum Value
	public Object getMaxValue() { return m_oMaxValue; }
	public void setMaxValue(Object oMaxValue) { m_oMaxValue = oMaxValue; }

	//Default Value
	public Object getDefaultValue() { return m_oDefValue; }
	public void setDefaultValue(Object oDefValue) { m_oDefValue = oDefValue; }

	//Length
	public int getLength() { return m_nLength; }
	public void setLength(int nLength) { m_nLength = nLength; }

	//Optional or not
	public boolean isOptional() { return m_bOptional; }
	public void setOptional(boolean bOptional) { m_bOptional = bOptional;}

	//Editable or not
	public boolean isEditable() { return m_bEditable; }
	public void setEditable(boolean bEditable) { m_bEditable = bEditable;}

	//Modified or not
	public boolean isDirty() { return m_bDirty; }
	public void setDirty(boolean bDirty) { m_bDirty = bDirty;}

	//Unique Name of the value
	public String getName() { return m_strName; }
	public void setName(String strName) { m_strName = strName; }

	//Caption of the value
	public String getCaption() { return m_strCaption; }
	public void setCaption(String strCaption) { m_strCaption = strCaption; }
}
