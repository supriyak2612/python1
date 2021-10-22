package utils.dataedit;

import java.io.*;
import java.sql.*;
import java.util.*;

public class DBConnectionMgr
{
	private Connection [] m_nDBCons;
	private boolean [] m_bInUseFlags;

	private String m_strDriverClassName;
	private String m_strDatabaseURL;
	private String m_strUserName;
	private String m_strPassword;

	private static final int MAX_POOL_SIZE = 8;

	public DBConnectionMgr(String strDriverClassName, String strDatabaseURL)
	{
		initialize(MAX_POOL_SIZE, strDriverClassName, strDatabaseURL, null, null);
	}

	public DBConnectionMgr(int nMaxPoolSize, String strDriverClassName, String strDatabaseURL,
							 String strUserName, String strPassword)
	{
		initialize(nMaxPoolSize, strDriverClassName, strDatabaseURL,
			strUserName, strPassword);
	}

	private void initialize(int nMaxPoolSize, String strDriverClassName, String strDatabaseURL,
							String strUserName, String strPassword)
	{
		m_nDBCons = new Connection [nMaxPoolSize];

		m_bInUseFlags = new boolean [nMaxPoolSize];

		for (int i = 0; i < m_bInUseFlags.length; i++)
			m_bInUseFlags[i] = false;

		m_strDriverClassName = strDriverClassName;
		m_strDatabaseURL = strDatabaseURL;
		m_strUserName = strUserName;
		m_strPassword = strPassword;
	}

	public Connection openConnection()
	{
		return allocConnection();
	}

	public void closeConnection(Connection con)
	{
		freeConnection(con);
	}

	public void shutdown()
	{
		cleanupConnections();
	}

	public Connection allocConnection()
	{
		int i;

		i = getFreeConnectionIndex();
		if (i != -1)
		{
			m_bInUseFlags[i] = true;
			return m_nDBCons[i];
		}

		i = getFreeSlot();
		if (i == -1)
			return null;

		m_nDBCons[i] = getDBConnection(); //Get a new connection

		if (m_nDBCons[i] != null)
			m_bInUseFlags[i] = true;

		return m_nDBCons[i];
	}

	public void freeConnection(Connection con)
	{
		for (int i = 0; i < m_nDBCons.length; i++)
			if (m_nDBCons[i] == con)
			{
				m_bInUseFlags[i] = false;
				break;
			}
	}

	private int getFreeConnectionIndex()
	{
		for (int i = 0; i < m_bInUseFlags.length; i++)
			if (m_bInUseFlags[i] == false && m_nDBCons[i] != null)
				return i;

		return -1;
	}

	private int getFreeSlot()
	{
		for (int i = 0; i < m_bInUseFlags.length; i++)
			if (m_bInUseFlags[i] == false)
				return i;

		return -1;
	}

	private void cleanupConnections()
	{
		for (int i = 0; i < m_nDBCons.length; i++)
			if (m_nDBCons[i] != null)
			{
				boolean bSuccess = true;
				try
				{
					m_nDBCons[i].close();
				}
				catch(SQLException e)
				{
					bSuccess = false;
				}
				if (bSuccess)
				{
					m_nDBCons[i] = null;
					m_bInUseFlags[i] = false;
				}
			}
	}

	/**
	 * returns a Database connection to the NetMem database
	 */
	private Connection getDBConnection()
	{
		Connection con = null;

		try
		{
			//Driver Example : "sun.jdbc.odbc.JdbcOdbcDriver"
			Class.forName(m_strDriverClassName);

			//con = DriverManager.getConnection("jdbc:subprotocol:subname", username, pwd);
			//URL Example : "jdbc:odbc:TradeDB"
			if (m_strUserName == null)
				con = DriverManager.getConnection(m_strDatabaseURL);
			else
				con = DriverManager.getConnection(m_strDatabaseURL, m_strUserName, m_strPassword);
		}
		catch (Exception e)
		{
			String msg = "getDBConnection : Exception " + e.getClass().getName();
			msg = msg + " - " + e.getMessage();
			System.out.println(msg);
		}

		return con;
	}
}
