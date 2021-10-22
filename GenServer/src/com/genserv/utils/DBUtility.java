package com.genserv.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.genserv.controller.AppManager;


public class DBUtility {
 private static Connection connection = null;

	/*
	protected static String m_strPropFile = "/config.properties";

	public static void setPropertyFile(String strFile)
	{
		m_strPropFile = strFile;
	}
	*/

    public static Connection getConnection() {
        if (connection != null)
            return connection;
        else {
            try {

				/*
             	Properties prop = new Properties();

                InputStream inputStream = DBUtility.class.getClassLoader().getResourceAsStream(m_strPropFile);
                prop.load(inputStream);

                String driver = prop.getProperty("driver");
                String url = prop.getProperty("url");
                String user = prop.getProperty("user");
                String password = prop.getProperty("password");
                */
                String driver = AppManager.getProperty("driver");
				String url = AppManager.getProperty("url");
				String user = AppManager.getProperty("user");
                String password = AppManager.getProperty("password");

                if (user.equals("#"))
                	user = null;
                if (password.equals("#"))
                	password = null;

                Class.forName(driver);
                connection = DriverManager.getConnection(url, user, password);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return connection;
        }

    }

}


