package poc.dataedit;

import utils.dataedit.*;

public class TstAddressInfoManager extends ValueManager
{
	public TstAddressInfoManager()
	{
	}

	public ValueInfo newValue()
	{
		return new TstAddress();
	}
}
