package utils.dataedit;

import java.io.*;
import java.sql.*;
import java.util.Vector;
import java.util.Date;

import utils.gwt.GValidator;

public class DBAccessHelper
{
	//public static String NEW_LINE = "";
	public static String NEW_LINE = "\r\n";

	public static boolean insertValueInfo(String strTableName, ValueInfo vInfo, DBConnectionMgr dbConMgr)
	{
		Connection con;
		boolean bRet = false;

		con = dbConMgr.openConnection();

		if (con == null)
		{
			System.out.println("Error : Could not open the database.");
			return false;
		}

		ValueDesc [] valueDesc = vInfo.getValues();
		String strValueNames, strValues;

		String strName;
		Object oValue;
		int i;

		strValueNames = new String("");
		strValues = new String("");

		for (i = 0; i < valueDesc.length; i++)
		{
			strName = valueDesc[i].getName();
			oValue = valueDesc[i].getValue();
			if (i != 0)
			{
				strValueNames += ",";
				strValues += ",";
			}
			strValueNames += strName;
			strValues += "?";
		}

		try
		{
			//Format : INSERT INTO <table_name> (<name_list> VALUES (<value_holder>)
			//Example : "INSERT INTO EmpTable (Name, Age, Salary) VALUES (?,?,?)"
			String strStmt = "INSERT INTO "+ strTableName + " ("+strValueNames+") VALUES ("+strValues+")";

			PreparedStatement pstmt = con.prepareStatement(strStmt);

			for (i = 0; i < valueDesc.length; i++)
			{
				oValue = valueDesc[i].getValue();

				if (oValue != null && valueDesc[i].getValueType() == ValueDesc.VT_DATE)
				{ //Convert to SQL date
					java.sql.Date oSQLDate;

					oSQLDate = new java.sql.Date( ((java.util.Date)oValue).getTime() );
					oValue = oSQLDate;
				}

				pstmt.setObject(i+1, oValue);
			}

			pstmt.executeUpdate();

			bRet = true;

			dbConMgr.closeConnection(con);

			pstmt = null;

		}
		catch (Exception e)
		{
			String msg = "insertValueInfo : Exception " + e.getClass().getName();
			msg = msg + " - " + e.getMessage();
			System.out.println(msg);

			dbConMgr.closeConnection(con);
		}

		return bRet;
	}

	public static boolean updateValueInfo(String strTableName, ValueInfo vInfo, ValueInfo vInfoOld,
								DBConnectionMgr dbConMgr)
	{
		String strUpdate = "";
		String strTemp;

		ValueDesc [] valueDesc = null;
		ValueDesc [] oldValueDesc = null;

		Object oValue, oOldValue;

		valueDesc = vInfo.getValues();
		if (vInfoOld != null)
			oldValueDesc = vInfoOld.getValues();

		String strName;
		int i;

		for (i = 0; i < valueDesc.length; i++)
		{
			strName = valueDesc[i].getName();
			oValue = valueDesc[i].getValue();
			oOldValue = (oldValueDesc != null)?(oldValueDesc[i].getValue()):(null);

			strTemp = getUpdateExpr(oValue, oOldValue, strName);
			strUpdate = addUpdateExpr(strUpdate, strTemp);
		}

		if (strUpdate != null && strUpdate.length() > 0)
		{
			String strKey = vInfo.getKey().toString();
			String strSQL = "UPDATE " + strTableName + " SET " + strUpdate + " WHERE Key = " + strKey;

			return executeUpdate(strSQL, dbConMgr);
		}
		else
			return true;
	}

	protected static String addUpdateExpr(String strStmt, String strExpr)
	{
		if (strExpr != null && strExpr.length() > 0)
		{
			if (strStmt.length() > 0)
				strStmt += ",";
			strStmt += strExpr;
		}

		return strStmt;
	}

	protected static String objectToString(Object oValue)
	{
		String strValue = null;

		if (oValue != null)
		{
			if (oValue instanceof Date)
				strValue = GValidator.DateToString((Date)oValue);
			else
				strValue = oValue.toString();
		}

		return strValue;
	}

	protected static String getUpdateExpr(Object oValue, Object oOldValue, String strFieldName)
	{
		if (oValue != null && oOldValue != null)
		{
			if (!oValue.equals(oOldValue))
				return getUpdateExpr(objectToString(oValue), objectToString(oOldValue), strFieldName);
			else
				return null;
		}
		else
			return getUpdateExpr(objectToString(oValue), objectToString(oOldValue), strFieldName);
	}

	protected static String getUpdateExpr(String str, String strOld, String strFieldName)
	{
		String strUpdate = null;

		if (!equalsField(str, strOld))
		{
			strUpdate = strFieldName + " = '";
			if (str != null)
				strUpdate += str;
			strUpdate += "'";
		}

		return strUpdate;
	}

	public static boolean equalsField(String strField1, String strField2)
	{
		boolean bEquals = false;

		if (strField1 != null && strField2 != null)
			bEquals = strField1.equals(strField2);
		else
		{
			if (strField1 == null && strField2 == null)
				bEquals = true;
			//one of the field is null
			else if ( (strField1 == null && strField2.length() == 0) || (strField2 == null && strField1.length() == 0))
				bEquals = true;
		}

		return bEquals;
	}

	public static boolean equalsField(Object oValue1, Object oValue2)
	{
		if (oValue1 == null && oValue2 == null)
			return true;
		else if (oValue1 != null && oValue2 != null)
			return oValue1.equals(oValue2);
		else
			return false;
	}

	/*public static BugInfo getBugInfo(String strBugNum)
	{
		String strSQL = "SELECT * FROM BugTable WHERE BugNum = "  + strBugNum ;
		BugInfo bInfo = null;

		Vector vBugs = getBugList(strSQL);
		if (vBugs != null && vBugs.size() > 0)
			bInfo = (BugInfo)vBugs.get(0);

		return bInfo;
	}*/

	public static Vector getValueList(String strTableName, String strSQLQuery,
									ValueInfoManager valueMgr, DBConnectionMgr dbConMgr)
	{
		String strDefSQL = "SELECT * FROM " + strTableName;

		if (strSQLQuery == null || strSQLQuery.length() == 0)
			strSQLQuery = strDefSQL;

		ResultSet rs = executeQuery(strSQLQuery, dbConMgr);

		if (rs == null)
			return null;

		Vector vValues = null;

		try
		{
			if (!rs.next())
			{
				System.out.println("No matching records found.");
				return new Vector(); //return empty vector
			}

			ValueInfo vInfo;
			int i, nCols = -1;
			Object oValue;
			String [] FieldNames = null;

			do
			{
				if (nCols == -1)
				{
					ResultSetMetaData rsm;

					rsm = rs.getMetaData();
					nCols = rsm.getColumnCount();
					FieldNames = new String [nCols];
					for (i = 0; i < nCols; i++)
						FieldNames[i] = rsm.getColumnName(i+1);
				}

				vInfo = valueMgr.newValue(); //Create a new instance

				for (i = 0; i < nCols; i++)
				{
					try
					{
						oValue = rs.getObject(i+1);

						if (oValue != null && oValue instanceof java.sql.Date)
						{
							java.util.Date oDate;

							oDate = new java.util.Date( ((java.sql.Date)oValue).getTime() );
							oValue = oDate;
						}
						vInfo.setProperty(FieldNames[i], oValue);
					}
					catch(SQLException e) {}
				}

				if (vValues == null)
					vValues = new Vector();

				vValues.add(vInfo);

			} while (rs.next());
		}
		catch (Exception e)
		{
			String msg = "getValueList : Exception " + e.getClass().getName();
			msg = msg + " - " + e.getMessage();
			System.out.println(msg);
			e.printStackTrace();
		}

		return vValues;
	}

	public static boolean deleteValueInfo(String strTableName, Object oKey, String strWhereClause,
								DBConnectionMgr dbConMgr)
	{
		if (oKey != null || strWhereClause != null)
		{
			String strSQL = "delete from " + strTableName;

			if ( (strWhereClause == null || strWhereClause.length() == 0) && oKey != null)
				strWhereClause = "Key = " + oKey.toString();

			strSQL += " where " + strWhereClause;

			return executeUpdate(strSQL, dbConMgr);
		}
		else
			return false;
	}

	public static ResultSet executeQuery(String strSQL, DBConnectionMgr dbConMgr)
	{
		Connection con;
		ResultSet rs = null;

		con = dbConMgr.openConnection();

		//System.out.println("### Executing Query : " + strSQL);

		if (con == null)
		{
			System.out.println("Error : Could not open the database.");
			return null;
		}

		try
		{
			Statement stmt = con.createStatement();

			rs = stmt.executeQuery( strSQL );

			dbConMgr.closeConnection(con);

			stmt = null;
		}
		catch (Exception e)
		{
			String msg = "executeQuery : Exception " + e.getClass().getName();
			msg = msg + " - " + e.getMessage();
			System.out.println(msg);

			dbConMgr.closeConnection(con);
		}

		return rs;
	}

	public static boolean executeUpdate(String strSQL, DBConnectionMgr dbConMgr)
	{
		Connection con;
		boolean bRet = false;

		con = dbConMgr.openConnection();

		//System.out.println("### Executing Update Query : " + strSQL);

		if (con == null)
		{
			System.out.println("Error : Could not open the database.");
			return false;
		}
		try
		{
			Statement stmt = con.createStatement();

			stmt.executeUpdate( strSQL );

			bRet = true;

			dbConMgr.closeConnection(con);

			stmt = null;

		}
		catch (Exception e)
		{
			String msg = "executeUpdate : Exception " + e.getClass().getName();
			msg = msg + " - " + e.getMessage();
			System.out.println(msg);

			dbConMgr.closeConnection(con);
		}

		return bRet;
	}

	public static Vector getColumnValues(String strColName, String strTable, String strWhereCondn,
										 DBConnectionMgr dbConMgr)
	{
		String strSQL = "SELECT " + strColName + " FROM " + strTable;
		if (strWhereCondn != null)
			strSQL = strSQL + " " + strWhereCondn;

		ResultSet rs = executeQuery( strSQL, dbConMgr );

		Vector vValues = null;

		try
		{
			if (!rs.next())
			{
				System.out.println("Error : Could not fetch column data.");
				return null;
			}

			String strValue;
			do
			{

				strValue = null;

				//Fix for JDBC-ODBC driver bug, when memo field is null
				//Catch the exception thrown due to this bug
				try
				{
					strValue = rs.getString(strColName);
				}
				catch(SQLException e) {}

				if (vValues == null)
					vValues = new Vector();

				vValues.add(strValue);

			} while (rs.next());

		}
		catch (Exception e)
		{
			String msg = "getColumnValues : Exception " + e.getClass().getName();
			msg = msg + " - " + e.getMessage();
			System.out.println(msg);
			e.printStackTrace();
		}

		return vValues;
	}

}
