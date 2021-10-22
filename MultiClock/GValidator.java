package utils.gwt;

import java.util.*;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class GValidator
{
	/**
	* validates the integer value specified in the given text
	*/
	public static int validateInteger(String strText, int nMin, int nMax, boolean bOptional)
	{
		if (strText == null || strText.length() <= 0)
			return (bOptional ? 1 : 0); //Valid or missing text

		int nValue;

		try
		{
		  nValue = Integer.parseInt(strText);
		}
		catch(NumberFormatException e)
		{
		  return -1; //Incorrect format
		}

		if (nValue >= nMin && nValue <= nMax)
			return 1; //Valid

		return -2; //Invalid range
	}

	/**
	* validates the double value specified in the given text
	*/
	public static int validateDouble(String strText, double dMin, double dMax, boolean bOptional)
	{
		if (strText == null || strText.length() <= 0)
			return (bOptional ? 1 : 0); //Valid or missing text

		double dValue;

		try
		{
		  dValue = Double.parseDouble(strText);
		}
		catch(NumberFormatException e)
		{
		  return -1; //Incorrect format
		}

		if (dValue >= dMin && dValue <= dMax)
			return 1; //Valid

		return -2; //Invalid range
	}

	/**
	* validates the given text
	*/
	public static int validateText(String strText, int nMaxLen, boolean bOptional)
	{
		if (strText == null || strText.length() <= 0)
			return (bOptional ? 1 : 0); //Valid or missing text

		if (strText.length() > nMaxLen)
			return -1; //Text exceeds the length limit

		return 1; //Valid text
	}

	/**
	* validates the date value specified in the given text
	*/
	public static int validateDate(String strText, String strSeparator, boolean bOptional)
	{
		if (strText == null || strText.length() <= 0)
			return (bOptional ? 1 : 0); //Valid or missing text

		int DaysInMonth[] = {
			31, //Jan
			29, //Feb
			31, //Mar
			30, //Apr
			31, //May
			30, //Jun
			31, //Jul
			31, //Aug
			30, //Sep
			31, //Oct
			30, //Nov
			31 //Dec
		};

		//Check if data is in mm-dd-yyyy format
		StringTokenizer tok = new StringTokenizer(strText, strSeparator);
		if (tok.countTokens() != 3)
			return -1; //invalid date format

		String strMonth, strDay, strYear;
		int nYear = 0, nMonth = 0, nDay = 0;
		int nRet;

		strMonth = tok.nextToken();
		strDay = tok.nextToken();
		strYear = tok.nextToken();

		//Check for month value and its range
		nRet = validateInteger(strMonth, 1, 12, false);
		if (nRet == -1)
			return -1; //invalid date format
		else if (nRet == -2)
			return -2; //invalid date

		//Special validation for the month Feb
		try
		{
		  nMonth = Integer.parseInt(strMonth);
		}
		catch(NumberFormatException e)
		{
		}

		//Check for day value and its range
		nRet = validateInteger(strDay, 1, DaysInMonth[nMonth-1], false);
		if (nRet == -1)
			return -1; //invalid date format
		else if (nRet == -2)
			return -2; //invalid date

		//Check for year value and its range
		nRet = validateInteger(strYear, 1900, 2100, false);
		if (nRet == -1)
			return -1; //invalid date format
		else if (nRet == -2)
			return -2; //invalid date

		try
		{
		  nYear = Integer.parseInt(strYear);
		  nDay = Integer.parseInt(strDay);
		}
		catch(NumberFormatException e)
		{
		}

		//Special validation for the month Feb
		if (nMonth == 2 && nDay == 29 && (nYear%4) != 0)
			return -2; //invalid date

		//TBD : Date range (start to end) should also be supported
		//return -3; //invalid date range

		return 1; //Valid date
	}

	/**
	* validates the file path specified in the given text
	*/
	public static int validatePath(String strText, int nMaxLen, boolean bCheckExistence, boolean bDirectory, boolean bOptional)
	{
		int nRet = validateText(strText, nMaxLen, bOptional);
		if (nRet != 1)
			return nRet;

		if (bCheckExistence)
		{
			File file = new File(strText);

			if(!file.exists())
				return -2; //path does not exists
			else if (bDirectory && !file.isDirectory())
				return -3; //not a directory
			else if (!bDirectory && !file.isFile())
				return -4; //not a file
		}

		return 1;
	}

	/**
	* returns a data after converting the given string to the date
	*/
	public static Date StringToDate(String strText)
	{
		//Assumes that date format is valid

		if (strText == null || strText.length() <= 0)
			return null;

		//Get date in mm-dd-yyyy format
		StringTokenizer tok = new StringTokenizer(strText, "-");

		String strTemp;
		int nYear = 0, nMonth = 0, nDay = 0;

		try
		{
		  strTemp = tok.nextToken(); //Month
		  nMonth = Integer.parseInt(strTemp);

		  strTemp = tok.nextToken(); //Day
		  nDay = Integer.parseInt(strTemp);

		  strTemp = tok.nextToken(); //Year
		  nYear = Integer.parseInt(strTemp);
		}
		catch(NumberFormatException e)
		{
		}

		Calendar cal = Calendar.getInstance(); //Get calender
        cal.set(nYear, nMonth-1, nDay);
        Date date = cal.getTime();

		return date;
	}

	/**
	* returns a string after converting the given date to the string
	*/
	public static String DateToString(Date date)
	{
		if (date == null)
			return null;

		Calendar cal = Calendar.getInstance(); //Get calender
		cal.setTime(date); //Set given date

		//Get date specific values
		int nDay, nMonth, nYear;
		nDay = cal.get(Calendar.DAY_OF_MONTH);
		nMonth = cal.get(Calendar.MONTH);
		nYear = cal.get(Calendar.YEAR);

		return Integer.toString(nMonth+1) + "-" + Integer.toString(nDay) + "-" + Integer.toString(nYear);
	}
}
