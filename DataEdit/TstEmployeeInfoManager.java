package poc.dataedit;

import java.util.Vector;

import utils.dataedit.*;

public class TstEmployeeInfoManager extends DBTableInfoManager
{
	public TstEmployeeInfoManager()
	{
		super("sun.jdbc.odbc.JdbcOdbcDriver", "jdbc:odbc:POCTest", "Employee");
	}

	public ValueInfo newValue()
	{
		return new TstEmployee();
	}
}
