package utils.dataedit;

import java.util.Vector;

public class DBTableInfoManager implements ValueInfoManager
{
	protected DBConnectionMgr m_dbConMgr;

	protected String m_strTableName;
	protected String m_strDriverClassName;
	protected String m_strDatabaseURL;

	public DBTableInfoManager(String strDriverClassName, String strDatabaseURL, String strTableName)
	{
		m_strDriverClassName = strDriverClassName;
		m_strDatabaseURL = strDatabaseURL;
		m_strTableName = strTableName;

		m_dbConMgr = new DBConnectionMgr(m_strDriverClassName, m_strDatabaseURL);
	}

	public ValueInfo newValue()
	{
		return null;
	}

	public boolean addValue(ValueInfo vInfo)
	{
		return DBAccessHelper.insertValueInfo(m_strTableName, vInfo, m_dbConMgr);
	}

	public boolean updateValue(Object oKey, ValueInfo vInfo)
	{
		return DBAccessHelper.updateValueInfo(m_strTableName, vInfo, null, m_dbConMgr);
	}

	public boolean deleteValue(Object oKey)
	{
		return DBAccessHelper.deleteValueInfo(m_strTableName, oKey, null, m_dbConMgr);
	}

	public Vector getValueList()
	{
		Vector vRecords;

		vRecords = DBAccessHelper.getValueList(m_strTableName, null, this, m_dbConMgr);

		return vRecords;
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
