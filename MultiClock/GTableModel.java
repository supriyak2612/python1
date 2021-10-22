package utils.gwt;

import javax.swing.table.*;
import java.util.*;

public class GTableModel extends DefaultTableModel
{
	private boolean m_bDirty = false;

	private int m_nSortColIndex = -1;
	private boolean m_bSortAscending = true;
	private boolean m_bResort = false;
	private int m_SortColFlags[] = null;

	private Vector m_vColNames; //Saved copy of col names
	private Vector m_vRowData; //Saved copy of table data

	//Extended sort flags / options (for individual columns)
	public static final int SORT_NONE = -1;
	public static final int SORT_DEFAULT = 0;
	public static final int SORT_STR = 1;
	public static final int SORT_INT = 2;
	public static final int SORT_REAL = 3;

	/**
	* Table model constructor
	*/
	public GTableModel()
	{
	    super();
	}

	/**
	* Table model constructor
	*/
	public GTableModel(int numRows, int numCols)
	{
	    super(numRows, numCols);
	}

	/**
	* Table model constructor
	*/
	public GTableModel(Object[][] data, Object[] columnNames)
    {
        super(data, columnNames);
    }

	/**
	* Table model constructor
	*/
    public GTableModel(Object[] columnNames, int numRows)
    {
        super(columnNames, numRows);
    }

	/**
	* Table model constructor
	*/
    public GTableModel(Vector columnNames, int numRows)
    {
        super(columnNames, numRows);
    }

	/**
	* Table model constructor
	*/
    public GTableModel(Vector data, Vector columnNames)
    {
        super(data, columnNames);
    }

	/**
	* sets the dirty flag (used to determine if data was modified)
	*/
    public void setDirty(boolean bDirty) { m_bDirty = bDirty; }

    /**
    * returns the dirty flag
    */
    public boolean getDirty() { return m_bDirty; }

	/**
	* Sets table cell value at the given row, col position
	*/
    public void setValueAt(Object oNewValue, int r, int c)
	{
		GTableCell cell = (GTableCell)getValueAt(r, c);

		Object oOldValue = cell.getValue();

		boolean bDataChanged = false;
		if (oOldValue != null)
		{
			if (oNewValue != null)
			{
				String strOldValue = oOldValue.toString();
				String strNewValue = oNewValue.toString();
				bDataChanged = (strOldValue.compareTo(strNewValue) != 0);
			}
			else //new value is null
				bDataChanged = true;
		}
		else //Old value is null
			bDataChanged = (oNewValue != null); //new value is not null

		if (bDataChanged)
		{
			cell.setValue((String)oNewValue);
			cell.setDirty(true);

			setDirty(true);

			fireTableCellUpdated(r, c);
		}
	}

	/**
	* initializes the table data
	*/
	public void initDataVector(Vector data, Vector columnNames)
	{
		//Save column names and row data
		m_vColNames = columnNames;
		m_vRowData = (Vector)data.clone();
	}

	/**
	* Set table data (data is sorted automatically, if required)
	*/
	public void setDataVector(Object[][] data, Object[] columnNames)
    {
		initDataVector(convertToVector(data), convertToVector(columnNames));

		int nCurSortCol = getSortColumn();
		boolean bCurSortOrder = getSortOrder();

		resetSort();
		super.setDataVector(data, columnNames);

		if (nCurSortCol != -1 && nCurSortCol < columnNames.length)
			sort(nCurSortCol, bCurSortOrder);
	}

	/**
	* Set table data (data is sorted automatically, if required)
	*/
	public void setDataVector(Vector newData, Vector columnNames)
    {
		initDataVector(newData, columnNames);

    	int nCurSortCol = getSortColumn();
		boolean bCurSortOrder = getSortOrder();

    	resetSort();
    	super.setDataVector(newData, columnNames);

    	if (nCurSortCol != -1 && nCurSortCol < columnNames.size())
			sort(nCurSortCol, bCurSortOrder);
    }

	/**
	* inserts a table row
	*/
    public void insertRow(int row, Vector rowData)
	{
		m_bResort = true;
		m_vRowData.insertElementAt(rowData, row);

		super.insertRow(row, rowData);
	}

	/**
	* inserts a table row
	*/
	public void insertRow(int row, Object [] rowData)
	{
		m_bResort = true;
		m_vRowData.insertElementAt(convertToVector(rowData), row);

		super.insertRow(row, rowData);
	}

	/**
	* adds a table row
	*/
	public void addRow(Vector rowData)
	{
		m_bResort = true;
		m_vRowData.addElement(rowData);

		super.addRow(rowData);
	}

	/**
	* adds a table row
	*/
	public void addRow(Object [] rowData)
	{
		m_bResort = true;
		m_vRowData.addElement(convertToVector(rowData));

		super.addRow(rowData);
	}

	/**
	* removes a table row
	*/
	public void removeRow(int row)
	{
		m_vRowData.removeElementAt(row);

		super.removeRow(row);
	}

	/**
	* returns if the table is sorted
	*/
	public boolean isSorted() { return m_nSortColIndex != -1; }

	/**
	* Return the index of sorting column
	*/
	public int getSortColumn() { return m_nSortColIndex; }

	/**
	* Decides the sorting order to be ascending or descending
	*/
	public boolean getSortOrder() { return m_bSortAscending; }

	/**
    * Reset the sorting order to ascending as original one
    */
    private void resetSort()
	{
		m_nSortColIndex = -1;
    	m_bSortAscending = true;
    }

    /**
    * Stores the type of sorting(i.e. Default, String, Int, Real) for all columns on which sorting can be done
    */
	public void setSortColumnFlags(int [] sortColFlags) { m_SortColFlags = sortColFlags; }

	/**
	* Gets the all sorting column's sorting type values
	*/
	public int [] getSortColumnFlags() { return m_SortColFlags; }

	/**
	* Check whether the sorting can be performed on particular column
	*/
	public boolean canSortColumn(int nColIndex)
	{
		if (m_SortColFlags == null)
			return true;

		if (nColIndex >= m_SortColFlags.length)
			return true;

		return (m_SortColFlags[nColIndex] != SORT_NONE);
	}

	/**
	* Get the sorting type value for a particular column
	*/
	public int getSortColumnType(int nColIndex)
	{
		if (m_SortColFlags == null)
			return SORT_DEFAULT;

		if (nColIndex >= m_SortColFlags.length)
			return SORT_DEFAULT;

		return (m_SortColFlags[nColIndex]);
	}

    /* Do sorting for table model columns using indices
	 * and if columnis already sorted then toggle between
	 * ascending and descending.
	 */
    public void sort(int nColIndex)
    {
    	if (nColIndex == -1)
    	{ //reset sorting to none
    		resetSort();
    		super.setDataVector(m_vRowData, m_vColNames);
    		fireTableDataChanged(); //notify the listeners of table update
    		return;
    	}

		if (!canSortColumn(nColIndex))
    		return; //sorting is not allowed for this column; do nothing

        if (!m_bResort && isSorted() && nColIndex == m_nSortColIndex)
        { //table is already sorted with same col, then toggle
          //between ascending & decending
           	m_bSortAscending = !m_bSortAscending;

			Vector vData = getDataVector();
            int nCount;
            Object oTemp;
            nCount = vData.size();
            for (int i = 0; i < nCount/2; i++)
            {
                oTemp = vData.get(i);
                vData.setElementAt(vData.get(nCount-i-1), i);
                vData.setElementAt(oTemp, nCount-i-1);
            }

            super.setDataVector(vData, m_vColNames);
        }
        else if (m_bResort || !isSorted() || m_nSortColIndex != nColIndex)
        { //not sorted or sort key (column) has changed
			sortData(nColIndex, true);
			m_bResort = false;
        }

        m_nSortColIndex = nColIndex; //set current sort column
        fireTableDataChanged(); //notify the listeners of table update
    }

    /**
    * Perform sorting on a column depending upon it's order
    */
    public void sort(int nColIndex, boolean bAscending)
    {
    	if (nColIndex == -1)
    	{ //reset sorting to none
    		resetSort();
    		super.setDataVector(m_vRowData, m_vColNames);
    	}
    	else if (!canSortColumn(nColIndex))
    		return; //sorting is not allowed for this column; do nothing
       	else
    	{ //Sort by the specified column
    		sortData(nColIndex, bAscending);
    	   	m_nSortColIndex = nColIndex; //set current sort column
    	}

        fireTableDataChanged(); //notify the listeners of table update
    }

    /**
    * Sort the data internally in data model
    */
    protected void sortData(int nColIndex, boolean bAscending)
    {
		m_bSortAscending = bAscending;

        int nRowCount = getRowCount();

		int nColType = getSortColumnType(nColIndex);

        Vector vTblData = getDataVector();
        Vector rowData1, rowData2;
        GTableCell tblCell1, tblCell2;
		int nCmpValue = 0;
        for (int j = 0; j < nRowCount; j++)
		{
			for (int i = 0; i < nRowCount-j-1; i++)
			{
			    rowData1 = (Vector)vTblData.elementAt(i);
				rowData2 = (Vector)vTblData.elementAt(i+1);
				tblCell1 = (GTableCell)rowData1.elementAt(nColIndex);
				tblCell2 = (GTableCell)rowData2.elementAt(nColIndex);

                if (nColType == SORT_INT)
                {
                	int nVal1, nVal2;
                	try
                	{
                		nVal1 = Integer.parseInt(tblCell1.toString());
                		nVal2 = Integer.parseInt(tblCell2.toString());
                		if (nVal1 == nVal2)
                			nCmpValue = 0;
                		else if (nVal1 < nVal2)
                			nCmpValue = -1;
                		else
                			nCmpValue = 1;
                	}
                	catch(Exception e) { nCmpValue = 0; }
                }
                else if (nColType == SORT_REAL)
                {
                	double dVal1, dVal2;
                	try
                	{
                		dVal1 = Double.valueOf(tblCell1.toString()).doubleValue();
                		dVal2 = Double.valueOf(tblCell2.toString()).doubleValue();
                		if (dVal1 == dVal2)
                			nCmpValue = 0;
                		else if (dVal1 < dVal2)
                			nCmpValue = -1;
                		else
                			nCmpValue = 1;
                	}
                	catch(Exception e) { nCmpValue = 0; }
                }
            	else if(tblCell1.getValue() != null && tblCell2.getValue() != null)
                	nCmpValue = tblCell1.toString().compareTo(tblCell2.toString());

                if ( (bAscending && nCmpValue > 0) || (!bAscending && nCmpValue < 0))
				{ //Cell-1 is greater than Cell-2 - Swap the row elements
					Object oTemp;

					oTemp = vTblData.get(i);
					vTblData.setElementAt(vTblData.get(i+1), i);
					vTblData.setElementAt(oTemp, i+1);
				}
			}
		}

		super.setDataVector(vTblData, m_vColNames);
	}
}
