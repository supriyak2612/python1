package poc.dataedit;

import java.util.Date;

import utils.dataedit.*;

public class TstEmployee extends PropertyInfo
{
	private static ValueDesc [] m_valueDesc = {
		new ValueDesc(ValueDesc.OBJ_STR, "Name", "Employee Name", 64, false, true),
		new ValueDesc(ValueDesc.OBJ_DATE, "DateJoined", "Date of Joining"),
		new ValueDesc(ValueDesc.OBJ_STR, "Education", "Qualifications"),
		new ValueDesc(ValueDesc.OBJ_INT, new Integer(1), new Integer(35), new Integer(1), "YearsOfExp", "Experience (in Years)")
	};

	public TstEmployee()
	{
		super(m_valueDesc, null);
	}

	public TstEmployee(TstEmployee src)
	{
		super(src);
	}

	public Object makeCopy()
	{
		return new TstEmployee(this);
	}
}
