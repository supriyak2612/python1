package utils.dataedit;

import java.util.Vector;

public class ValueManager implements ValueInfoManager
{
	protected Vector m_vValues;
	protected int nKey = 0;

	public ValueManager()
	{
	}

	public ValueInfo newValue()
	{
		return new ValueInfo();
	}

	public boolean addValue(ValueInfo valueInfo)
	{
		if (m_vValues == null)
			m_vValues = new Vector();

		valueInfo.setKey(new Integer(nKey));
		m_vValues.add(valueInfo);
		nKey++;

		return (true); //success of failure
	}

	public boolean updateValue(Object oKey, ValueInfo valueInfo)
	{
		int nIndex = findValue(oKey);

		if (nIndex != -1) //if already exists
		{
			valueInfo.setKey(oKey);
			m_vValues.setElementAt(valueInfo, nIndex);
		}

		return (nIndex != -1); //success of failure
	}

	public boolean deleteValue(Object oKey)
	{
		int nIndex = findValue(oKey);

		if (nIndex != -1)
			m_vValues.remove(nIndex);

		return (nIndex != -1); //success of failure
	}

	protected int findValue(Object oKey)
	{
		if (m_vValues == null)
			return -1;

		int nIndex = -1;

		ValueInfo vinfo;
		for (int i = 0; i < m_vValues.size(); i++)
		{
			vinfo = (ValueInfo)m_vValues.get(i);
			if (oKey.equals(vinfo.getKey()))
			{
				nIndex = i;
				break;
			}
		}

		return nIndex;
	}

	public Vector getValueList()
	{
		return m_vValues;
	}

	public boolean getValueItemListMap(Vector vValueNames, Vector vMaps)
	{
			return false;
	}

	public boolean getValueGroupMap(Vector vGroupNames, Vector vValueNames)
	{
		return false;
	}
}
