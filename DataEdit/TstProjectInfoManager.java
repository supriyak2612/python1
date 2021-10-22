package poc.dataedit;

import java.util.Vector;

import utils.dataedit.*;

public class TstProjectInfoManager extends ValueManager
{
	public TstProjectInfoManager()
	{
	}

	public ValueInfo newValue()
	{
		return new TstProject();
	}

	public boolean getValueItemListMap(Vector vValueNames, Vector vMaps)
	{
		if (vValueNames == null || vMaps == null)
			return false;

		String models [][] =
		{
			{"D", "Development"},
			{"M", "Maintenance"},
			{"C", "Conversion"},
			{"M", "Service"},
			{"H", "Hybrid"}
		};

		vValueNames.add("Model");
		vMaps.add(models);

		String locations [][] =
		{
			{"Bangalore", null},
			{"Chennai", null},
			{"Hydrabad", null},
			{"Pune", null}
		};

		vValueNames.add("Location");
		vMaps.add(locations);

		Object managers [][] =
		{
			{new Integer(11082), "Adam Edmonds"},
			{new Integer(80715), "Karina"},
			{new Integer(12076), "Marlyn"},
		};

		vValueNames.add("ManagerID");
		vMaps.add(managers);

		return true;
	}

	public boolean getValueGroupMap(Vector vGroupNames, Vector vValueNames)
	{
		if (vGroupNames == null || vValueNames == null)
			return false;

		String General [] =
		{
			"Name",
			"Code",
			"Model",
			"IPAT",
			"StartDate",
			"EndDate",
			"Location",
			"ManagerID"
		};

		vGroupNames.add("General");
		vValueNames.add(General);

		String Metrics [] =
		{
			"TeamSize",
			"Effort",
			"Cost"
		};

		vGroupNames.add("Metrics");
		vValueNames.add(Metrics);

		return true;
	}
}
