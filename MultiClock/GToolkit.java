package utils.gwt;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

public class GToolkit
{
	public static final String DEF_RES_LOC = "res";
	public static final String DEF_IMAGE_LOC = "res/images";
	public static final String DEF_PROP_LOC = "res/props";

	private static final int BUF_SIZE = 512;

	/**
	* loads the specified resource
	*/
	public static InputStream getResource(String strResName)
	{
		return getResource(strResName, DEF_RES_LOC);
	}

	/**
	* loads the specified image resource
	*/
	public static InputStream getImageResource(String strImageName)
	{
		return getResource(strImageName, DEF_IMAGE_LOC);
	}

	/**
	* loads the specified property resource
	*/
	public static InputStream getPropertyResource(String strPropName)
	{
		return getResource(strPropName, DEF_PROP_LOC);
	}

	/**
	* returns the specified image
	*/
	public static Image getImage(String strImageName)
	{
		Image image = null;
		ImageIcon imageIcon = loadImageResource(strImageName);

		if (imageIcon != null)
			image = imageIcon.getImage();

		return image;
	}

	/**
	* returns the specified image icon
	*/
	public static ImageIcon getIcon(String strImageName)
	{
		return loadImageResource(strImageName);
	}

	/**
	* loads the specified resource
	*/
	public static InputStream getResource(String strResName, String strBase)
	{
		InputStream is = null;

		//is = this.getClass().getResourceAsStream(strImageName);

		String strResPath = strResName;
		if (strResPath.indexOf('/') == -1)
			strResPath = strBase + "/" + strResPath;

		is = ClassLoader.getSystemResourceAsStream(strResPath);

		return is;
	}

	/**
	* loads the specified image resource
  	*/
	protected static ImageIcon loadImageResource(String strImageName)
	{
		ImageIcon imageIcon = null;

		InputStream is;

		is = getImageResource(strImageName);

		if (is == null)
			return null;

		byte [] bufData = getByteStream(is);

		if (bufData != null)
			imageIcon = new ImageIcon(bufData);

		return imageIcon;
	}

	/**
	* returns the byte content of the input stream
  	*/
	public static byte [] getByteStream(InputStream is)
	{
		byte [] bufData = null;
		byte [] bufRead;
		byte [] bufStream;
		int nSizeInBuf, nSizeRead, i;

		bufRead = new byte[BUF_SIZE];
		bufStream = new byte[BUF_SIZE];
		nSizeInBuf = 0;

		try
		{
			while (true)
			{
				nSizeRead = is.read(bufRead, 0, BUF_SIZE);
				if (nSizeRead == -1)
					break; //EOF input stream or error
				if (nSizeInBuf+nSizeRead > bufStream.length)
				{ //not enough space; resize the buffer
					byte [] bufTemp = new byte[Math.max(nSizeInBuf+nSizeRead, nSizeInBuf+BUF_SIZE)];
					for (i = 0; i < nSizeInBuf; i++)
						bufTemp[i] = bufStream[i];
					bufStream = bufTemp;
				}
				for (i = 0; i < nSizeRead; i++)
					bufStream[i+nSizeInBuf] = bufRead[i];
				nSizeInBuf = nSizeInBuf + nSizeRead;
			}
			if (nSizeInBuf > 0)
			{
				bufData = new byte[nSizeInBuf];
				for (i = 0; i < nSizeInBuf; i++)
						bufData[i] = bufStream[i];
			}
		}
		catch(IOException e)
		{
			return null;
		}

		return bufData;
	}
}
