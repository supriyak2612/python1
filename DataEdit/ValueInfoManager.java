package utils.dataedit;

import java.util.Vector;

public interface ValueInfoManager
{
	public ValueInfo newValue();

	public boolean addValue(ValueInfo vInfo);
	public boolean updateValue(Object oKey, ValueInfo vInfo);
	public boolean deleteValue(Object oKey);

	public Vector getValueList();
	public boolean getValueItemListMap(Vector vValueNames, Vector vMaps);
	public boolean getValueGroupMap(Vector vGroupNames, Vector vValueNames);
}
