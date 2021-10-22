package utils.gwt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class GDialog extends JDialog
						implements ActionListener
{
	private boolean m_bOK = false;
	private JButton m_buttonOK, m_buttonCancel;

	/**
	* Dialog constructor
	*/
	public GDialog(Frame owner, String title, boolean modal)
	{
		super(owner, title, modal);

		setResizable(false);
	}

	/**
	* Dialog constructor
	*/
	public GDialog(Dialog owner, String title, boolean modal)
	{
		super(owner, title, modal);

		setResizable(false);
	}

	/**
	* Sets Dialog return code
	*/
	public void setReturnFlag(boolean bOK) { m_bOK = bOK; }

	/**
	* Gets Dialog return code
	*/
	public boolean getReturnFlag() { return m_bOK; }

	/**
	* Registers OK button for the dialog
	*/
	protected void registerOKButton(JButton button)
	{
		m_buttonOK = button;
	}

	/**
	* Registers Cancel button for the dialog
	*/
	protected void registerCancelButton(JButton button)
	{
		m_buttonCancel = button;
	}

	/**
	* Overridable method for setting data to the dialog controls
	*/
	protected void setDataToUI()
	{
	}

	/**
	* Overridable method for getting data from the dialog controls
	*/
	protected void getDataFromUI()
	{
	}

	/**
	* Overridable method for validating the data specified in the dialog controls
	*/
	protected boolean validateUIData()
	{
		return true;
	}

	/**
	* Overridable action event handler; handles default actions such as OK, Cancel
	*/
	public void actionPerformed(ActionEvent ev)
	{
	    if (ev.getSource() == m_buttonOK) {
	      onOK();
	    }
	    else if (ev.getSource() == m_buttonCancel) {
	      onCancel();
	    }
  	}

	/**
	* Overridable method for handling dialog OK event
	*/
	public void onOK()
	{
		if (!validateUIData())
			return;

		setReturnFlag(true);
		getDataFromUI();
		dispose();
	}

	/**
	* Overridable method for handling dialog CANCEL event
	*/
	public void onCancel()
	{
		setReturnFlag(false);
		dispose();
	}

	/**
	* Displays the dialog
	*/
	public boolean doDialog()
	{
		return doDialog(true);
	}

	/**
	* Displays the dialog
	*/
	public boolean doDialog(boolean bCenter)
	{
		setDataToUI();

		if (bCenter)
			centerDialog(this);

		show();
		return m_bOK;
	}

	/**
	* Centers the dialog within parent bounds
	*/
	public static void centerDialog(Component comp)
	{
	   	centerComponent(comp, comp.getParent());
	}

	/**
	* Centers the component within the given base component
	*/
	public static void centerComponent(Component comp, Component compBase)
	{
		Dimension parSize = compBase.getSize();

		int parWidth = (int)parSize.getWidth();
		int parHeight = (int)parSize.getHeight();

		Dimension dlgSize = comp.getSize();
		int dlgWidth = (int)dlgSize.getWidth();
		int dlgHeight = (int)dlgSize.getHeight();

		int nX, nY;
		nX = compBase.getX();
		nY = compBase.getY();

		comp.setBounds(nX+(parWidth-dlgWidth)/2, nY+(parHeight-dlgHeight)/2, dlgWidth,dlgHeight);
	}

	/**
	* Shows the given error message
	*/
	protected void showErrorMsg(String strMsg)
	{
		JOptionPane.showMessageDialog(this, strMsg,"Error",JOptionPane.ERROR_MESSAGE);
	}

	/**
	* Shows the given warning message
	*/
	protected void showWarningMsg(String strMsg)
	{
		JOptionPane.showMessageDialog(this, strMsg,"Warning",JOptionPane.WARNING_MESSAGE);
	}

	/**
	* Shows the given information message
	*/
	protected void showInformationMsg(String strMsg)
	{
		JOptionPane.showMessageDialog(this, strMsg,"Information",JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	* Validates the integer data specified in the control
	*/
	protected boolean validateIntegerField(JTextField textField, int nMin, int nMax, boolean bOptional, String strErrorPrefix)
	{
		int nRet = GValidator.validateInteger(textField.getText(), nMin, nMax, bOptional);

		if (nRet == 0)
			showErrorMsg(strErrorPrefix + " : Missing Value");
		else if (nRet == -1)
			showErrorMsg(strErrorPrefix + " : Invalid Numeric Value");
		else if (nRet == -2)
			showErrorMsg(strErrorPrefix + " : Numeric Value out of range (" + Integer.toString(nMin) + " - " + Integer.toString(nMax) + ")");

		//Set the focus to the component, in case of error
		if (nRet != 1)
			textField.requestFocus();

		return (nRet == 1);
	}

	/**
	* Validates the double data specified in the control
	*/
	protected boolean validateDoubleField(JTextField textField, double dMin, double dMax, boolean bOptional, String strErrorPrefix)
	{
		int nRet = GValidator.validateDouble(textField.getText(), dMin, dMax, bOptional);

		if (nRet == 0)
			showErrorMsg(strErrorPrefix + " : Missing Value");
		else if (nRet == -1)
			showErrorMsg(strErrorPrefix + " : Invalid Numeric Value");
		else if (nRet == -2)
			showErrorMsg(strErrorPrefix + " : Numeric Value out of range (" + Double.toString(dMin) + " - " + Double.toString(dMax) + ")");

		//Set the focus to the component, in case of error
		if (nRet != 1)
			textField.requestFocus();

		return (nRet == 1);
	}

	/**
	* Validates the text data specified in the control
	*/
	protected boolean validateTextField(JTextField textField, int nMaxLen, boolean bOptional, String strErrorPrefix)
	{
		int nRet = GValidator.validateText(textField.getText(), nMaxLen, bOptional);

		if (nRet == 0)
			showErrorMsg(strErrorPrefix + " : Missing Text");
		else if (nRet == -1)
			showErrorMsg(strErrorPrefix + " : Text length can not exceed " + Integer.toString(nMaxLen));

		//Set the focus to the component, in case of error
		if (nRet != 1)
			textField.requestFocus();

		return (nRet == 1);
	}

	/**
	* Validates the date specified in the control
	*/
	protected boolean validateDateField(JTextField textField, boolean bOptional, String strErrorPrefix)
	{
		int nRet = GValidator.validateDate(textField.getText(), "-", bOptional);

		if (nRet == 0)
			showErrorMsg(strErrorPrefix + " : Missing Date");
		else if (nRet == -1)
			showErrorMsg(strErrorPrefix + " : Invalid date format");
		else if (nRet == -2)
			showErrorMsg(strErrorPrefix + " : Invalid date");
		//TBD : Date range (start to end) should also be supported
		/* else if (nRet == -3)
			showErrorMsg(strErrorPrefix + " : Invalid date range");*/

		//Set the focus to the component, in case of error
		if (nRet != 1)
			textField.requestFocus();

		return (nRet == 1);
	}

	/**
	* Validates the file path specified in the control
	*/
	protected boolean validatePathField(JTextField textField, int nMaxLen, boolean bCheckExistence, boolean bDirectory, boolean bOptional, String strErrorPrefix)
	{
		int nRet = GValidator.validatePath(textField.getText(), nMaxLen, bCheckExistence, bDirectory, bOptional);

		if (nRet == 0)
			showErrorMsg(strErrorPrefix + " : Missing Path");
		else if (nRet == -1)
			showErrorMsg(strErrorPrefix + " : Path length can not exceed " + Integer.toString(nMaxLen));
		else if (nRet == -2)
			showErrorMsg(strErrorPrefix + " : Path does not exists");
		else if (nRet == -3)
			showErrorMsg(strErrorPrefix + " : Not a directory");
		else if (nRet == -4)
			showErrorMsg(strErrorPrefix + " : Not a File");

		//Set the focus to the component, in case of error
		if (nRet != 1)
			textField.requestFocus();

		return (nRet == 1);
	}

	/**
	* returns the integer data from the control
	*/
	public int getInteger(JTextField textField)
	{
		String strText = textField.getText();
		int nValue = 0;

		try
		{
		  nValue = Integer.parseInt(strText);
		}
		catch(NumberFormatException e)
		{
		}

		return nValue;
	}

	/**
	* returns the double data from the control
	*/
	public double getDouble(JTextField textField)
	{
		String strText = textField.getText();
		double dValue = 0;

		try
		{
		  dValue = Double.parseDouble(strText);
		}
		catch(NumberFormatException e)
		{
		}

		return dValue;
	}

	/**
	* returns the text data from the control
	*/
	public String getText(JTextField textField)
	{
		return textField.getText();
	}

	/**
	* returns the date from the control
	*/
	public Date getDate(JTextField textField)
	{
		String strText = textField.getText();

		return GValidator.StringToDate(strText);
	}

	/**
	* sets the integer data to the control
	*/
	public void setInteger(JTextField textField, int nValue)
	{
		textField.setText(Integer.toString(nValue));
	}

	/**
	* sets the double data to the control
	*/
	public void setDouble(JTextField textField, double dValue)
	{
		textField.setText(Double.toString(dValue));
	}

	/**
	* sets the text data to the control
	*/
	public void setText(JTextField textField, String strText)
	{
		textField.setText(strText);
	}

	/**
	* sets the date to the control
	*/
	public void setDate(JTextField textField, Date date)
	{
		if (date != null)
			textField.setText(GValidator.DateToString(date));
		else
			textField.setText("");
	}
}
