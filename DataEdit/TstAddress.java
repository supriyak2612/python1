package poc.dataedit;

import utils.dataedit.*;

public class TstAddress extends ValueInfo
{
	private String m_strName;
	private String m_strAddr1, m_strAddr2, m_strAddr3;
	private String m_strCity, m_strState, m_strCountry;
	private String m_strZip;

	private static ValueDesc [] m_valueDesc = {
		new ValueDesc(ValueDesc.OBJ_STR, "Name", "Company Name", 64, false, true),
		new ValueDesc(ValueDesc.OBJ_STR, "Address1", "Address-1"),
		new ValueDesc(ValueDesc.OBJ_STR, "Address2", "Address-2"),
		new ValueDesc(ValueDesc.OBJ_STR, "Address3", "Address-3"),
		new ValueDesc(ValueDesc.OBJ_STR, "City", "City", 32, false, true),
		new ValueDesc(ValueDesc.OBJ_STR, "State", "State", 32, false, true),
		new ValueDesc(ValueDesc.OBJ_STR, "Country", "Country", 32, false, true),
		new ValueDesc(ValueDesc.OBJ_STR, "ZipCode", "Zip Code"),
	};

	public TstAddress()
	{
		super(m_valueDesc, null);
	}

	public TstAddress(String strName, String strAddr1, String strAddr2, String strAddr3,
					  String strCity, String strState, String strCountry, String strZip)
	{
		super(m_valueDesc, null);

		setName(strName);

		setAddress1(strAddr1);
		setAddress2(strAddr2);
		setAddress3(strAddr3);

		setCity(strCity);
		setState(strState);
		setCountry(strCountry);

		setZipCode(strZip);
	}

	public TstAddress(TstAddress src)
	{
		super(src);

		setName(src.getName());

		setAddress1(src.getAddress1());
		setAddress2(src.getAddress2());
		setAddress3(src.getAddress3());

		setCity(src.getCity());
		setState(src.getState());
		setCountry(src.getCountry());

		setZipCode(src.getZipCode());
	}

	public Object makeCopy()
	{
		return new TstAddress(this);
	}

	public String getName() { return m_strName; }
	public void setName(String strName) { m_strName = strName; }

	public String getAddress1() { return m_strAddr1; }
	public void setAddress1(String strAddr1) { m_strAddr1 = strAddr1; }

	public String getAddress2() { return m_strAddr2; }
	public void setAddress2(String strAddr2) { m_strAddr2 = strAddr2; }

	public String getAddress3() { return m_strAddr3; }
	public void setAddress3(String strAddr3) { m_strAddr3 = strAddr3; }

	public String getCity() { return m_strCity; }
	public void setCity(String strCity) { m_strCity = strCity; }

	public String getState() { return m_strState; }
	public void setState(String strState) { m_strState = strState; }

	public String getCountry() { return m_strCountry; }
	public void setCountry(String strCountry) { m_strCountry = strCountry; }

	public String getZipCode() { return m_strZip; }
	public void setZipCode(String strZip) { m_strZip = strZip; }
}
