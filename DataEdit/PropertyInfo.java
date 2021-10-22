package utils.dataedit;

import java.util.*;

public class PropertyInfo extends ValueInfo
{
	protected Hashtable m_hProps;

	public PropertyInfo()
	{
		super();
	}

	public PropertyInfo(ValueDesc [] valueDesc, Object oKey)
	{
		super(valueDesc, oKey);
	}

	public PropertyInfo(PropertyInfo src)
	{
		super(src);

		if (src.m_hProps != null)
		{
			Enumeration eKeys = src.m_hProps.keys();
			String strPropName;
			Object oValue;

			Hashtable hProps = new Hashtable();

			while (eKeys.hasMoreElements())
			{
				strPropName = (String)eKeys.nextElement();
				oValue = src.m_hProps.get(strPropName);
				hProps.put(strPropName, oValue);
			}

			m_hProps = hProps;
		}
		else
			m_hProps = null;
	}

	public Object makeCopy()
	{
		return new PropertyInfo(this);
	}

	public boolean hasGetSetMethods() { return false; }

	public Object getProperty(String strPropertyName)
	{
		if (strPropertyName.equals("Key"))
			return getKey();

		Object oValue = null;

		if (m_hProps != null)
			oValue = m_hProps.get(strPropertyName);

		return oValue;
	}

	public void setProperty(String strPropertyName, Object oValue)
	{
		if (strPropertyName.equals("Key"))
		{
			setKey(oValue);
			return;
		}

		if (m_hProps == null)
			m_hProps = new Hashtable();

		m_hProps.put(strPropertyName, oValue);
	}
}
