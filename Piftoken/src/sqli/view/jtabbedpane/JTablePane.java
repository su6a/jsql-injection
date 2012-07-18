package sqli.view.jtabbedpane;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class JTablePane extends JPanel implements MouseListener {
	private static final long serialVersionUID = 4505998197469263100L;
	
	private JCustomTable newJTable;
	
	public JTablePane(String[][] data, String[] columnNames, JTabbedPane newJTabbedPane){
		super(new GridLayout(1,0));
		newJTable = new JCustomTable(data, columnNames);
		newJTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		newJTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		newJTable.setColumnSelectionAllowed(true);
		newJTable.setRowSelectionAllowed(true);
		newJTable.setCellSelectionEnabled(true);
		
		newJTable.getColumnModel().getColumn(0).setResizable(false);
		newJTable.getColumnModel().getColumn(0).setPreferredWidth(34);
		newJTable.getColumnModel().getColumn(0).setMinWidth(34);
		newJTable.getColumnModel().getColumn(0).setMaxWidth(34);
		
		newJTable.getColumnModel().getColumn(1).setResizable(false);
		newJTable.getColumnModel().getColumn(1).setPreferredWidth(60);
		newJTable.getColumnModel().getColumn(1).setMinWidth(60);
		newJTable.getColumnModel().getColumn(1).setMaxWidth(60);

		DefaultTableCellRenderer centerHorizontalAlignment = new CenterRenderer();
		newJTable.getColumnModel().getColumn(0).setCellRenderer(centerHorizontalAlignment);
		newJTable.getColumnModel().getColumn(1).setCellRenderer(centerHorizontalAlignment);
		
		newJTable.getTableHeader().addMouseListener(this);
		
		newJTable.setTableHeader(new DisableReordering(newJTable.getColumnModel()));
		
		this.add(new JScrollPane(newJTable));
	}
	
	private class CenterRenderer extends DefaultTableCellRenderer{
		private static final long serialVersionUID = -3624608585496119576L;

		public CenterRenderer(){
			this.setHorizontalAlignment(JLabel.CENTER);
		}
	}
	
	private class DisableReordering extends JTableHeader{
		private static final long serialVersionUID = -906296661480210371L;

		public DisableReordering(TableColumnModel tableColumnModel){
			super(tableColumnModel);
			this.setReorderingAllowed(false);
		}
	}
	
	private class JCustomTable extends JTable{
		private static final long serialVersionUID = 6180838060982831586L;

		public JCustomTable(String[][] data, String[] columnNames){
			super(data, columnNames);
			
			TableCellRenderer renderer = this.getTableHeader().getDefaultRenderer();

		    for (int i = 0; i < getColumnCount(); ++i)
		        getColumnModel().getColumn(i).setPreferredWidth(
		        	renderer.getTableCellRendererComponent(this,
		                this.getModel().getColumnName(i), false, false, 0, i)
		                	.getPreferredSize().width);
		}
		
		@Override
		public Component prepareRenderer(TableCellRenderer renderer,
		        int row, int column) {
		    Component prepareRenderer = super.prepareRenderer(renderer, row, column);
		    TableColumn tableColumn = this.getColumnModel().getColumn(column);

		    tableColumn.setPreferredWidth(Math.max(
		            prepareRenderer.getPreferredSize().width,
		            tableColumn.getPreferredWidth()));

		    return prepareRenderer;
		}
		
		@Override
		public boolean getScrollableTracksViewportWidth(){
            return this.getPreferredSize().width < this.getParent().getWidth();
        }
		
		@Override
		public boolean isCellEditable(int row,int column){  
			return column != 0 && column != 1;
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}
	@Override
	public void mousePressed(MouseEvent arg0) {}
	@Override
	public void mouseReleased(MouseEvent arg0) {}
	@Override
	public void mouseClicked(MouseEvent arg0) {}
		
//	@Override
//	public void mouseClicked(MouseEvent e)
//	{
//	    JTableHeader header = newJTable.getTableHeader();
//	    TableColumnModel columns = header.getColumnModel();
//
//	    if (!columns.getColumnSelectionAllowed())
//	        return;
//
//	    int column = header.columnAtPoint(e.getPoint());
//	    if (column == -1)
//	        return;
//
//	    int count = newJTable.getRowCount();
//	    if (count != 0)
//	        newJTable.setRowSelectionInterval(0, count - 1);
//
//	    ListSelectionModel selection = columns.getSelectionModel();
//
//	    if (e.isShiftDown())
//	    {
//	        int anchor = selection.getAnchorSelectionIndex();
//	        int lead = selection.getLeadSelectionIndex();
//
//	        if (anchor != -1)
//	        {
//	            boolean old = selection.getValueIsAdjusting();
//	            selection.setValueIsAdjusting(true);
//
//	            boolean anchorSelected = selection.isSelectedIndex(anchor);
//
//	            if (lead != -1)
//	            {
//	                if (anchorSelected)
//	                    selection.removeSelectionInterval(anchor, lead);
//	                else
//	                    selection.addSelectionInterval(anchor, lead);
//	                // The latter is quite unintuitive.
//	            }
//
//	            if (anchorSelected)
//	                selection.addSelectionInterval(anchor, column);
//	            else
//	                selection.removeSelectionInterval(anchor, column);
//
//	            selection.setValueIsAdjusting(old);
//	        }
//	        else
//	            selection.setSelectionInterval(column, column);
//	    }
//	    else if (e.isControlDown())
//	    {
//	        if (selection.isSelectedIndex(column))
//	            selection.removeSelectionInterval(column, column);
//	        else
//	            selection.addSelectionInterval(column, column);
//	    }
//	    else
//	    {
//	        selection.setSelectionInterval(column, column);
//	    }
//	}
}

