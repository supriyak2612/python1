package utils.dataedit;

import java.lang.reflect.*;

public class ValueInfo
{
	protected ValueDesc [] m_valueDesc; //Value Descriptors

	protected Object m_oKey;

	public ValueInfo()
	{
	}

	public ValueInfo(ValueDesc [] valueDesc, Object oKey)
	{
		m_valueDesc = valueDesc;
		m_oKey = oKey;
	}

	public ValueInfo(ValueInfo src)
	{
		m_valueDesc = src.m_valueDesc;
		m_oKey = src.m_oKey;
	}

	public Object makeCopy()
	{
		return new ValueInfo(this);
	}

	public Object getKey() { return m_oKey; }
	public  void setKey(Object oKey) { m_oKey = oKey; }

	public ValueDesc [] getValueDescriptors() { return m_valueDesc; }

	public boolean hasGetSetMethods() { return true; }

	public Object getProperty(String strPropertyName) { return null; }
	public void setProperty(String strPropertyName, Object oValue) { }

	public ValueDesc [] getValues()
	{
		if (m_valueDesc == null)
		{
			System.out.println("Error : No value descriptors");
			return null;
		}

		ValueDesc values [] = new ValueDesc[m_valueDesc.length];

		int i;
		Method method;
		String strMethodName;
		Object oValue;

		Class [] args1 = { null };
		Object [] params1 = { null };

		boolean bGetSet = hasGetSetMethods();

		if (!bGetSet)
		{
			try
			{
				args1[0] = Class.forName("java.lang.String");
			}
			catch(ClassNotFoundException ex1)
			{
				return null;
			}
		}

		for (i = 0; i < m_valueDesc.length; i++)
		{
			values[i] = new ValueDesc(m_valueDesc[i]);

			if (bGetSet)
				strMethodName = "get" + m_valueDesc[i].getName();
			else
				strMethodName = "getProperty";
			oValue = null;

			try
			{
				if (bGetSet)
				{ //call getXXX
					method = this.getClass().getMethod(strMethodName, null);
					oValue = method.invoke(this, null);
				}
				else
				{ //call getProperty("XXX")
					method = this.getClass().getMethod(strMethodName, args1);
					params1[0] = m_valueDesc[i].getName();
					oValue = method.invoke(this, params1);
				}
			}
			catch(Exception ex)
			{
				System.out.println("Exception: Could not invoke get Method" + ex.getClass().getName());
			}

			values[i].setValue(oValue);
		}

		return values;
	}

	public  boolean setValues(ValueDesc [] values)
	{
		if (m_valueDesc == null)
			return false;

		int i;
		Method method;
		String strMethodName;
		Object oValue;

		Class [] args = { null };
		Object [] params = { null };

		Class [] args1 = { null, null };
		Object [] params1 = { null, null };

		boolean bGetSet = hasGetSetMethods();

		if (!bGetSet)
		{
			try
			{
				args1[0] = Class.forName("java.lang.String");
				args1[1] = Class.forName("java.lang.Object");
			}
			catch(ClassNotFoundException ex1)
			{
				return false;
			}
		}

		for (i = 0; i < m_valueDesc.length; i++)
		{
			if (bGetSet)
				strMethodName = "set" + m_valueDesc[i].getName();
			else
				strMethodName = "setProperty";
			oValue = values[i].getValue();

			try
			{
				if (bGetSet)
				{ //call setXXX(oValue)
					args[0] = m_valueDesc[i].getValue().getClass();
					method = this.getClass().getMethod(strMethodName, args);

					params[0] = oValue;
					method.invoke(this, params);
				}
				else
				{ //call setProperty("XXX", oValue)
					method = this.getClass().getMethod(strMethodName, args1);

					params1[0] = m_valueDesc[i].getName();
					params1[1] = oValue;
					method.invoke(this, params1);
				}
			}
			catch(Exception ex)
			{
				System.out.println("Exception: Could not invoke set Method");
			}
		}

		return true;
	}
}
