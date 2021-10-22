package poc.dataedit;

import java.util.Date;

import utils.dataedit.*;

public class TstProject extends ValueInfo
{
	private String m_strName;
	private String m_strCode;
	private String m_strModel;
	private Boolean m_obIPAT;

	private Integer m_oiTeamSize;
	private Integer m_oiEffort;
	private Double m_odCost;

	private Date m_dateStart, m_dateEnd;

	private String m_strLocation;
	private Integer m_oiManagerID;

	private static ValueDesc [] m_valueDesc = {
		new ValueDesc(ValueDesc.OBJ_STR, "Name", "Project Name", 64, false, true),
		new ValueDesc(ValueDesc.OBJ_STR, "Code", "Project Code", 32, false, true),
		new ValueDesc(ValueDesc.OBJ_STR, "Model", "Process Model", 1, false, true),
		new ValueDesc(ValueDesc.OBJ_BOOL, "IPAT", "Tracked in iPAT"),
		new ValueDesc(ValueDesc.OBJ_INT, "TeamSize", "Team Size"),
		new ValueDesc(ValueDesc.OBJ_INT, "Effort", "Estimated Effort"),
		new ValueDesc(ValueDesc.OBJ_DOUBLE, "Cost", "Cost"),
		new ValueDesc(ValueDesc.OBJ_DATE, "StartDate", "Start Date"),
		new ValueDesc(ValueDesc.OBJ_DATE, "EndDate", "End Date"),
		new ValueDesc(ValueDesc.OBJ_STR, "Location", "Development Center"),
		new ValueDesc(ValueDesc.OBJ_INT, "ManagerID", "Project Manager")
	};

	public TstProject()
	{
		super(m_valueDesc, null);
	}

	public TstProject(String strName, String strCode, String strModel, boolean bIPAT,
					 int nTeamSize, int nEffort, double dCost, Date dateStart, Date dateEnd,
					 String strLocation, int nManagerID)
	{
		super(m_valueDesc, null);

		setName(strName);
		setCode(strCode);
		setModel(strModel);
		setIPAT(new Boolean(bIPAT));

		setTeamSize(new Integer(nTeamSize));
		setEffort(new Integer(nEffort));
		setCost(new Double(dCost));

		setStartDate(dateStart);
		setEndDate(dateEnd);

		setLocation(strLocation);
		setManagerID(new Integer(nManagerID));
	}

	public TstProject(TstProject src)
	{
		super(src);

		setName(src.getName());
		setCode(src.getCode());
		setModel(src.getModel());
		setIPAT(src.getIPAT());

		setTeamSize(src.getTeamSize());
		setEffort(src.getEffort());
		setCost(src.getCost());

		setStartDate(src.getStartDate());
		setEndDate(src.getEndDate());

		setLocation(src.getLocation());
		setManagerID(src.getManagerID());
	}

	public Object makeCopy()
	{
		return new TstProject(this);
	}

	public String getName() { return m_strName; }
	public void setName(String strName) { m_strName = strName; }

	public String getCode() { return m_strCode; }
	public void setCode(String strCode) { m_strCode = strCode; }

	public String getModel() { return m_strModel; }
	public void setModel(String strModel) { m_strModel = strModel; }

	public Boolean getIPAT() { return m_obIPAT; }
	public void setIPAT(Boolean obIPAT) { m_obIPAT = obIPAT; }

	public Integer getTeamSize() { return m_oiTeamSize; }
	public void setTeamSize(Integer oiTeamSize) { m_oiTeamSize = oiTeamSize; }

	public Integer getEffort() { return m_oiEffort; }
	public void setEffort(Integer oiEffort) { m_oiEffort = oiEffort; }

	public Double getCost() { return m_odCost; }
	public void setCost(Double odCost) { m_odCost = odCost; }

	public Date getStartDate() { return m_dateStart; }
	public void setStartDate(Date dateStart) { m_dateStart = dateStart; }

	public Date getEndDate() { return m_dateEnd; }
	public void setEndDate(Date dateEnd) { m_dateEnd = dateEnd; }

	public String getLocation() { return m_strLocation; }
	public void setLocation(String strLocation) { m_strLocation = strLocation; }

	public Integer getManagerID() { return m_oiManagerID; }
	public void setManagerID(Integer oiManagerID) { m_oiManagerID = oiManagerID; }
}
