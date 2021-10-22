package utils.gwt;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class GTable extends JTable
					implements MouseListener, KeyListener
{
	private boolean m_bAllowSorting = true;

	private static String m_strSort = null;
	private static String m_strAscending = null;
	private static String m_strDescending = null;
	private int m_iEditCellRow = -1, m_iEditCellCol = -1;

	/**
	* Table constructor
	*/
	public GTable()
	{
		super();
		initialize();
	}

	/**
	* Table constructor
	*/
	public GTable(Object[][] rowData, Object[] columnNames)
	{
		super(rowData, columnNames);
		initialize();
	}

	/**
	* Table constructor
	*/
	public GTable(TableModel tm)
	{
		super(tm);
		initialize();
	}

	/**
	* Table constructor
	*/
	public GTable(TableModel tm, TableColumnModel cm)
	{
		super(tm, cm);
		initialize();
	}

	/**
	* Table constructor
	*/
	public GTable(TableModel dm, TableColumnModel cm,ListSelectionModel sm)
	{
		super(dm, cm, sm);
		initialize();
	}

	/**
	* Table constructor
	*/
	public GTable(int numRows, int numColumns)
	{
        super(numRows, numColumns);
        initialize();
	}

	/**
	* Table constructor
	*/
	public GTable(Vector rowData, Vector columnNames)
	{
        super(rowData, columnNames);
        initialize();
	}

	/**
	* returns if the given cell is editable
	*/
	public boolean isCellEditable(int rowIndex, int ColumnIndex)
	{
		TableModel model = getModel();
		Object oCell = model.getValueAt(rowIndex, ColumnIndex);
		boolean bEditable = false;
		if (oCell instanceof GTableCell)
		{
			GTableCell cell = (GTableCell)oCell;
			bEditable = cell.getEditable();
		}

		return bEditable;
	}

	/**
	* returns the edited cells of the given row
	*/
	public Vector getEditedCells(int nRow)
	{
		int numCols = getColumnCount();
		TableModel model = getModel();
		Vector editedCellVec = new Vector();

		for (int jCol=0; jCol < numCols; jCol++)
		{
			GTableCell cellObj = (GTableCell)model.getValueAt(nRow,jCol);

			if(cellObj.getDirty())
			{
				editedCellVec.addElement(cellObj);
			}
		}

		return editedCellVec;
	}

	/**
	* returns all edited cells from the table
	*/
	public Vector getEditedCells()
	{
		int numRows = getRowCount();
		int numCols = getColumnCount();
		TableModel model = getModel();
		Vector editedCellVec = new Vector();

		for (int iRow = 0; iRow < numRows; iRow++)
		{
			for (int jCol = 0;jCol < numCols; jCol++)
			{
				GTableCell cellObj = (GTableCell)model.getValueAt(iRow,jCol);

				if(cellObj.getDirty())
				{
					editedCellVec.addElement(cellObj);
				}
			}
		}


		return editedCellVec;
	}

	/**
	* Sets custom user data to the table row
	*/
	public void setRowUserData(int nRow, Object oData)
	{
		TableModel model = getModel();

		GTableCell cell = (GTableCell)model.getValueAt(nRow,0);
		cell.setUserData(oData);
	}

	/**
	* returns the custom user data of the table row
	*/
	public Object getRowUserData(int nRow)
	{
		TableModel model = getModel();
		GTableCell firstRowCell = (GTableCell)model.getValueAt(nRow,0);
		return firstRowCell.getUserData();
	}

	/**
	* Sets the color of the table row
	*/
	public void setRowColor(int nRow, Color n_color)
	{
		TableModel model = getModel();

		GTableCell cellObj = (GTableCell)model.getValueAt(nRow,0);
		cellObj.setColor(n_color);
	}

	/**
	* returns the color of the table row
	*/
	public Color getRowColor(int nRow)
	{
		TableModel model = getModel();
		GTableCell firstRowCell = (GTableCell)model.getValueAt(nRow,0);
		Color rowColor=firstRowCell.getColor();
		return rowColor;
	}

	/**
	* Sets the image icon of the table row
	*/
	public void setRowImage(int nRow, Image n_image)
	{
		TableModel model = getModel();

		GTableCell cellObj = (GTableCell)model.getValueAt(nRow,0);
		cellObj.setImage(n_image);
	}

	/**
	* returns the image icon of the table row
	*/
	public Image getRowImage(int nRow)
	{
		TableModel model = getModel();
		GTableCell firstRowCell = (GTableCell)model.getValueAt(nRow,0);
		Image rowImage = firstRowCell.getImage();
		return rowImage;
	}

	/**
	* Sets the font of the table row
	*/
	public void setRowFont(int nRow, int n_fontStyle)
	{
		TableModel model = getModel();

		GTableCell cellObj = (GTableCell)model.getValueAt(nRow,0);

		if(n_fontStyle == -1)
			cellObj.setFont(null);
		else if(n_fontStyle == Font.PLAIN)
		{
			Font cellFont = new Font("Dialog", Font.PLAIN, 12);
			cellObj.setFont(cellFont);
		}
		else if(n_fontStyle == Font.BOLD)
		{
			Font cellFont = new Font("Dialog", Font.BOLD, 12);
			cellObj.setFont(cellFont);
		}

	}

	/**
	* returns the font of the table row
	*/
	public Font getRowFont(int nRow)
	{
		TableModel model = getModel();

		GTableCell cellObj = (GTableCell)model.getValueAt(nRow,0);
		Font cellFont = cellObj.getFont();

		return cellFont;
	}

	/**
	* enables/disables the sorting option
	*/
	public void setSortFlag(boolean bSort) { m_bAllowSorting = bSort; }

	/**
	* returns the sort option
	*/
	public boolean getSortFlag() { return m_bAllowSorting; }

	/**
	* Performs intialization of the table
	*/
	void initialize()
	{
		if (m_strSort == null)
			m_strSort = "Sorted";
		if (m_strAscending == null)
			m_strAscending = "Ascending";
		if (m_strDescending == null)
			m_strDescending = "Descending";

		GCellRenderer renderer = new GCellRenderer();
		setDefaultRenderer(Object.class,renderer);
		JTextField textField = new JTextField();
		textField.addKeyListener(this);
		//textField.addFocusListener(this);
		GCellEditor editor = new GCellEditor(textField);
		setDefaultEditor(Object.class,editor);

		JTableHeader tblHeader = getTableHeader();
		//// <BEGIN> : Header tool tip code
		tblHeader = new JTableHeader(tblHeader.getColumnModel())
		{
			public String getToolTipText(MouseEvent e)
			{
				String strTip = null;

				GTable table = (GTable)getTable();
				GTableModel tableModel = (GTableModel)table.getModel();
				int nSortCol = tableModel.getSortColumn();
				if (nSortCol != -1)
				{
					strTip = m_strSort + " : " + table.getColumnName(nSortCol);
					if (tableModel.getSortOrder())
						strTip = strTip + " , " + m_strAscending;
					else
						strTip = strTip + " , " + m_strDescending;
				}

				return strTip;
			}
		};
		setTableHeader(tblHeader);
		tblHeader.setToolTipText("Table Header");
		//// <END> : Header tooltip code
		tblHeader.addMouseListener(this);
		tblHeader.setReorderingAllowed(false);
		addMouseListener(this);
	}

	/**
	* returns the context menu (if any) of the table
	*/
	protected JPopupMenu getContextMenu(int nX, int nY)
	{
		return null;
	}

	/**
	* Displays the context menu
	*/
	protected void showPopupMenu(MouseEvent e)
	{
	   	int nX = e.getX();
        int nY = e.getY();

        JPopupMenu popupMenu = getContextMenu(nX, nY);
        if (popupMenu != null)
        {
			popupMenu.pack();
			popupMenu.show(this, nX, nY);

            popupMenu.requestFocus();
		}
	}

	/**
	* moused released handler to trigger the display of context menu
	*/
	public void mouseReleased(MouseEvent e)
	{
		if (e.isPopupTrigger())
	        showPopupMenu(e);
	}

	public void mousePressed(MouseEvent e) { }

	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }

	/**
	* moused clicked handler to trigger (a) Sorting (b) Validation for cell editing
	*/
	public void mouseClicked(MouseEvent e)
	{
		Object oSrc = e.getSource();
		if (oSrc instanceof JTableHeader)
		{
			if (!m_bAllowSorting)
				return;

			JTableHeader tblHeader = (JTableHeader)oSrc;

			GTableModel tblModel = (GTableModel)getModel();
			int nCol = tblHeader.columnAtPoint(e.getPoint());
			if (nCol != -1)
			{
				if (nCol == 0 && e.getClickCount() == 2)
					tblModel.sort(-1); //reset sorting
				else //sort based on the column clicked
					tblModel.sort(nCol);
			}
			else
			{
				 //no column returned from table header
			}

			return;
		}

		//Changes for Text Field validation on mouse single click
		if( e.getClickCount() == 1)
		{
			Point pt = e.getPoint();
			int rowIndex = rowAtPoint(pt);
			int colIndex  = columnAtPoint(pt);
			if(rowIndex == -1 || colIndex == -1)
			{
				//	Clicked outside table

			}
			else if (m_iEditCellRow != -1 && m_iEditCellCol != -1)
			{
				if(m_iEditCellRow != rowIndex || m_iEditCellCol != colIndex)
				{
					GTableCell tableCell = (GTableCell)getModel().getValueAt(m_iEditCellRow, m_iEditCellCol);
					int nValueType = tableCell.getValueType();
					String strValueText = tableCell.toString();
					boolean bValidChar = true;
					if (nValueType != GTableCell.STRING_TYPE)
					{
						bValidChar = validateTextField(strValueText, nValueType);
						if (bValidChar)
							m_iEditCellRow = m_iEditCellCol = -1;
					}
					if(!bValidChar)
					{
						tableCell.setValue("");
						setRowSelectionInterval(m_iEditCellRow,m_iEditCellRow);
						editCellAt(m_iEditCellRow, m_iEditCellCol);
						GCellEditor cellEditor = (GCellEditor)getDefaultEditor(Object.class);
						cellEditor.requestEditFocus();
					}
				}
			}
		}

	}

	public void keyTyped(KeyEvent evt){}

	/**
	* key release handler to save the edited cell value
	*/
	public void keyReleased(KeyEvent evt)
	{
		Object oSrc = evt.getSource();
		if (oSrc instanceof JTextField)
		{
			GTableCell tabelCell = (GTableCell)getModel().getValueAt(getSelectedRow(), getSelectedColumn());
			JTextField textField = (JTextField)oSrc;
			String strValueText = textField.getText();
			int nValueType = tabelCell.getValueType();
			if(nValueType == GTableCell.INT_TYPE && strValueText.length() > 8)
			{
				JOptionPane.showMessageDialog(this, "Invalid Length  - should be 0 to 8", "G Toolkit", JOptionPane.INFORMATION_MESSAGE);
				strValueText = strValueText.substring(0,8);
				textField.setText(strValueText);
			}
			else if((nValueType == GTableCell.FLOAT_TYPE || nValueType == GTableCell.DOUBLE_TYPE) && strValueText.length() > 16)
			{
				JOptionPane.showMessageDialog(this, "Invalid Length  - should be 0 to 16", "G Toolkit", JOptionPane.INFORMATION_MESSAGE);
				strValueText = strValueText.substring(0,16);
				textField.setText(strValueText);
			}
			else if(nValueType == GTableCell.STRING_TYPE && strValueText.length() > 256)
			{
				JOptionPane.showMessageDialog(this, "Invalid Length  - should be 0 to 256", "G Toolkit", JOptionPane.INFORMATION_MESSAGE);
				strValueText = strValueText.substring(0,256);
				textField.setText(strValueText);
			}
		}
	}

	/**
	* key pressed handled for validating the chars typed during cell editing
	*/
	public void keyPressed(KeyEvent evt)
	{
		Object oSrc = evt.getSource();
		if (oSrc instanceof JTextField)
		{
			GTableCell tabelCell = (GTableCell)getModel().getValueAt(getSelectedRow(), getSelectedColumn());
			JTextField textField = (JTextField)oSrc;
			String strValueText = textField.getText();
			int nValueType = tabelCell.getValueType();
			if (nValueType != GTableCell.STRING_TYPE)
			{ //Numeric validation
				char charVal = evt.getKeyChar();
				int nKeyCode = evt.getKeyCode();
				boolean bValidChar = true;
				if(nKeyCode == KeyEvent.VK_ENTER || nKeyCode == KeyEvent.VK_UP || nKeyCode == KeyEvent.VK_DOWN)
				{
					bValidChar =  validateTextField(strValueText, nValueType);
					if (bValidChar)
						m_iEditCellRow = m_iEditCellCol = -1;
				}

				if(!bValidChar)
				{
					textField.setText("");
					setRowSelectionInterval(m_iEditCellRow,m_iEditCellRow);
					editCellAt(m_iEditCellRow, m_iEditCellCol);
					GCellEditor cellEditor = (GCellEditor)getDefaultEditor(Object.class);
					cellEditor.requestEditFocus();
				}
			}
		}
	}

	/**
	* validates the cell value based on the type
	*/
	public boolean validateTextField(String p_strValueText, int nValueType)
	{
		//Numeric validation
		boolean bValidChar = true;
		for(int i=0; i < p_strValueText.length(); i++)
		{
			char charVal = p_strValueText.charAt(i);
			if (charVal == '-' || charVal == '+')
			{ //sign chars
				//Allows signs only at the beginning
				if (p_strValueText != null && p_strValueText.length() > 0)
					bValidChar = false;
			}
			else
			{
				if (nValueType == GTableCell.INT_TYPE)
					bValidChar = Character.isDigit(charVal);
				else if (nValueType == GTableCell.FLOAT_TYPE || nValueType == GTableCell.DOUBLE_TYPE)
				{
					if (charVal == '.')
					{
						//Allow only one decimal point
						if (p_strValueText.lastIndexOf(".") > p_strValueText.indexOf("."))
							bValidChar = false;
					}
					else
						bValidChar = Character.isDigit(charVal);
				}

			}

			if (!bValidChar && !p_strValueText.equals(""))
			{
				JOptionPane.showMessageDialog(this, "Invalid Numerical Value",
					"Error",JOptionPane.ERROR_MESSAGE);
				break;
			}

		}//for
		return bValidChar;
	}

	/**
	  * Class Name 				: 	GCellEditor
	  * Responsibilities		:	This is an extended class from DefaultCellEditor which make it
	  *								possible to edit the table cells using text field, combo-box etc.
	  *								1. Provides constructor for creating the own cell editor.
	  *								2. Gives the editable component for editing.
	  * Public Interface		:	1. GCellEditor(...)
	  *								2. Component getTableCellEditorComponent(....)
	  * Static Members      	:   Nil
	  * Hirarchy Description    : 	Base Class - DefaultCellEditor
	  *								The base class provides the implementation for TableCellEditor  *								used as user interface component to display data in table.
	  *								interface.This interface defines the methods any object that
	  *								would like to be an editor of values for components such as
	  *								ListBox, ComboBox, Tree, or Table, etc.
	  * External Dependencies	:	Nil
	  * Notes 					: 	This class is used by GTable for creating own custom editor.
	  *
  	**/
	private class GCellEditor extends DefaultCellEditor
	{
		JComponent m_editComp = null;

		public GCellEditor(JTextField textEdit)
		{
			super(textEdit);
			m_editComp = textEdit;
		}

		public GCellEditor(JCheckBox checkEdit)
		{
			super(checkEdit);
			m_editComp = checkEdit;
		}

		public GCellEditor(JComboBox comboEdit)
		{
			super(comboEdit);
			m_editComp = comboEdit;
		}

		/**
		* sets the focus to the cell editor
		*/
		public void requestEditFocus()
		{
			if (m_editComp != null)
				m_editComp.requestFocus();
		}

		/**
		* returns the cell value from the cell editor
		*/
		public Object getCellEditorValue()
		{
			Object oValue;
			if (m_editComp != null)
			{
				if (m_editComp instanceof JCheckBox)
					oValue = new Boolean( ((JCheckBox)m_editComp).isSelected());
				else
					oValue = super.getCellEditorValue();
			}
			else
				oValue = super.getCellEditorValue();
			return oValue;
		}

		/**
		 *  Method Name 		:	getTableCellEditorComponent
		 * 	Parameters			: 	JTable table, Object value, boolean isSelected, int row, int column
		 *  Return type			:	Component
		 *  Description			:   Sets an initial value for the editor. This will cause the
		 *							editor to stopEditing and lose any partially edited value
		 *							if the editor is editing when this method is called.
		 *	Exceptions  		:	Nil
		 *  Notes 				: 	Returns the component that should be added to the client's
		 *							Component hierarchy. Once installed in the client's
		 *							hierarchy this component will then be able to draw and
		 *							receive user input.
		 **/
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
		{
			Component editComponent = null;
			if(value != null && value instanceof GTableCell)
			{
				GTableCell cellValue = (GTableCell)value;
				String strValue = cellValue.toString();
				if (cellValue.getValueType() == -1)
				{
					//JCheckBox checkEditComp = new JCheckBox(table.getColumnName(column));
					JCheckBox checkEditComp = new JCheckBox();
					m_editComp = checkEditComp;
					editComponent = checkEditComp;
					checkEditComp.setBackground(getBackground());

					if (strValue.compareTo("TRUE") == 0)
					{
						checkEditComp.setSelected(true);
						checkEditComp.setEnabled(false);
					}
					else
					 {
						checkEditComp.setSelected(false);
					 }
				}
				else
				{
					editComponent = getComponent();
					((JTextField)editComponent).setText(strValue);
				}
			}
			m_iEditCellRow = row;
			m_iEditCellCol = column;
			return editComponent;
		}
	}

	/**
	  * Class Name 				: 	GCellRenderer
	  * Responsibilities		:	This is an extended class from DefaultCellRenderer which make it
	  *								possible to render(i.e. displaying image, color or text) the table
	  *								cells using JLabel.
	  *								1. Provides the facility for creating custom cell renderer.
	  *								2. Gives the renderer component for rendering.
	  * Public Interface		:	1. Component getTableCellRendererComponent(....)
	  * Static Members      	:   Nil
	  * Hirarchy Description    : 	Base Class - DefaultTableCellRenderer
	  *								The base class provides the inheritence from JLabel which is
	  *								used as user interface component to display data in table cell.
	  *								It provide implementation for interface DefaultTableCellRenderer
	  *								This interface defines the methods any object that
	  *								would like to be a renderer for cells in a GTable.
	  * External Dependencies	:	Nil
	  * Notes 					: 	This class is used by GTable for creating own custom renderer.
	  *
	**/
	private class GCellRenderer extends DefaultTableCellRenderer //implements MouseListener
	{
		JCheckBox m_checkCell = new JCheckBox();

		public Font m_defFont = null;
		public Image m_defEditableImage = null;
		public Image m_defEditedImage = null;

		/**
		 *  Method Name 		:	getTableCellRendererComponent
		 * 	Parameters			: 	JTable table, Object value, boolean isSelected, boolean isFocused, int row, int column)
		 *  Return type			:	Component
		 *
		 *  Description			:   This method is sent to the renderer by the drawing table
		 *							to configure the renderer appropriately before drawing.
		 *							Returns the Component used for drawing.
		 *	Exceptions  		:	Nil
		 *
		 *	Notes 				: 	Parameters -
		 *							table - the JTable that is asking the renderer to draw.
		 *							This parameter can be null.
		 *							value - the value of the cell to be rendered. It is up
		 *							to the specific renderer to interpret and draw the value.
		 *							eg. if value is the String "true", it could be rendered
		 *							as a string or it could be rendered as a check box that
		 *							is checked. null is a valid value.
		 *							isSelected - true is the cell is to be renderer with
		 *							selection highlighting
		 *							row - the row index of the cell being drawn. When drawing
		 *							the header the rowIndex is -1.
		 *							column - the column index of the cell being drawn
		 *
		**/
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean isFocused, int row, int column)
		{
			boolean checkboxSel = isSelected;
			Component comp = null;
			if(value != null && value instanceof GTableCell)
			{
				GTableCell cellValue = (GTableCell)value;

				if (cellValue.getValueType() == -1)
				{
					//comp = new JCheckBox(table.getColumnName(column));
					comp = m_checkCell; //comp = new JCheckBox();
					((JCheckBox)comp).setBackground(Color.white);
				}
			}

			if (comp == null)
				comp = super.getTableCellRendererComponent(table, value, isSelected, isFocused, row, column);

			//Set appropriate (user customized) font
			Font font = m_defFont;
			if (font != null)
				comp.setFont(font);

			if(value != null && value instanceof GTableCell)
			{
				GTableCell cellValue = (GTableCell)value;

				//1. Set the colors
				Color color = null;
				Image imageEdit = null;
				if (cellValue.getEditable())
				{ //check color for editing
					//comp = m_labelCell;
					if (cellValue.getDirty())
						//color = Color.red; //already edited
						imageEdit = m_defEditedImage;
					else
						//color = Color.green;
						imageEdit = m_defEditableImage;
				}
				else //get row color
				{
					color = ((GTable)table).getRowColor(row);
				}
				if (color != null )
				{
				   comp.setForeground(color);
				   setForeground(color);
				}
				else
				{
					if (table.isRowSelected(row))
					{
						if (isFocused && cellValue.getEditable())
							comp.setForeground(Color.black);
						else
							comp.setForeground(Color.white);
					}
					else
						comp.setForeground(Color.black);
				}

				//2. Set the image
				Image image=null;
				image = cellValue.getImage();
				if (image == null)
					image = imageEdit;
				if (image != null)
				{
					ImageIcon icon = new ImageIcon(image);
					if (comp instanceof JLabel)
						((JLabel)comp).setIcon(icon);
				}
				else
				{
					if (comp instanceof JLabel)
						((JLabel)comp).setIcon(null);
				}

				if (isSelected && comp instanceof JCheckBox)
				{
					comp.setForeground(Color.white);
					comp.setBackground(getBackground());
				}

				//3. Set font
				Font rowFont = null;

				//Sets the particular row font to Plain or bold.
				rowFont = getRowFont(row);
				if(rowFont != null)
					comp.setFont(rowFont);

				//4. Set value
				String strValue = cellValue.toString();

				if (cellValue.getValueType() == GTableCell.BOOLEAN_TYPE)
				{
					if (strValue.compareTo("TRUE") == 0)
					{
						((JCheckBox)comp).setSelected(true);
						((JCheckBox)comp).setEnabled(false);
					}
					else
						((JCheckBox)comp).setSelected(false);
				}
				else
					((JLabel)comp).setText(strValue);
			}
			return comp;
		}
	}
}
