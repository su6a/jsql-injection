package sqli.view.jtree;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;

import sqli.controller.InjectionController;
import database.model.Column;
import database.model.Database;
import database.model.Table;

public class TreeNodeEditor extends AbstractCellEditor implements TreeCellEditor, ActionListener, TreeSelectionListener, MouseListener{
	private static final long serialVersionUID = -190938126492801573L;

	private TreeNodeRenderer treeRenderer;
	private JTree databaseTree;
	private InjectionController controller;
	
	private TreeNodeModel<?> nodeData;
	private DefaultMutableTreeNode treeNode;

	public TreeNodeEditor(JTree newTree, InjectionController newController, JTabbedPane newTabbedPane) {
		treeRenderer = new TreeNodeRenderer();
		controller = newController;
		newTree.addTreeSelectionListener(this);
		newTree.addMouseListener(this);
		databaseTree = newTree;
	}

	@Override
	public Component getTreeCellEditorComponent(JTree tree, Object nodeRenderer,
			boolean selected, boolean expanded, boolean leaf, int row) {
		
		Component componentRenderer = treeRenderer.getTreeCellRendererComponent(tree, nodeRenderer, true, expanded, leaf,
			        row, true);
		
		if (nodeRenderer instanceof DefaultMutableTreeNode) {
			final DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) nodeRenderer;
			treeNode = currentNode;
			Object userObject = currentNode.getUserObject();
			if (userObject instanceof TreeNodeModel<?>) {
				nodeData = (TreeNodeModel<?>) userObject;
				TreeCellCustom c = (TreeCellCustom) componentRenderer;
				c.button.removeActionListener(this);
				c.checkBox.removeActionListener(this);
				c.button.addActionListener(this);
				c.checkBox.addActionListener(this);
			}
		}

		return componentRenderer;
	}
	
	@Override
	public Object getCellEditorValue() {
		return nodeData;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object source = arg0.getSource();

		if(source instanceof JButton){
			DefaultTreeModel treeModel = (DefaultTreeModel) databaseTree.getModel();
			DefaultMutableTreeNode tableNode = (DefaultMutableTreeNode) treeNode;
			List<Column> columnsToSearch = new ArrayList<Column>();
			
			int tableChildCount = treeModel.getChildCount(tableNode);
			for(int i=0; i < tableChildCount ;i++) {
				DefaultMutableTreeNode currentChild = (DefaultMutableTreeNode) treeModel.getChild(tableNode, i);
				if( currentChild.getUserObject() instanceof TreeNodeModel<?> ){
					TreeNodeModel<?> columnTreeNodeModel = (TreeNodeModel<?>) currentChild.getUserObject();
					if(columnTreeNodeModel.isSelected){
						columnsToSearch.add((Column) columnTreeNodeModel.dataObject);
					}
				}
			}
			controller.selectValues(columnsToSearch);
			
		}else if(source instanceof JCheckBox){
			JCheckBox columnCheckBox = (JCheckBox) source;
			nodeData.isSelected = columnCheckBox.isSelected();
			
			DefaultTreeModel treeModel = (DefaultTreeModel) databaseTree.getModel();
			DefaultMutableTreeNode tableNode = (DefaultMutableTreeNode) treeNode.getParent();
			
			int tableChildCount = treeModel.getChildCount(tableNode);
			boolean isOneChildSelected = false;
			for(int i=0; i < tableChildCount ;i++) {
				DefaultMutableTreeNode currentChild = (DefaultMutableTreeNode) treeModel.getChild(tableNode, i);
				if( currentChild.getUserObject() instanceof TreeNodeModel<?> ){
					TreeNodeModel<?> columnTreeNodeModel = (TreeNodeModel<?>) currentChild.getUserObject();
					if(columnTreeNodeModel.isSelected){
						isOneChildSelected = true;
						break;
					}
				}
			}
			
			TreeNodeModel<?> nodeUserObject = (TreeNodeModel<?>) tableNode.getUserObject();
			nodeUserObject.hasChildSelected = isOneChildSelected;
		}

		this.stopCellEditing();
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) databaseTree.getLastSelectedPathComponent();
		if(node == null) return; // get ride of java.lang.NullPointerException

		if(node.getUserObject() instanceof TreeNodeModel){
			TreeNodeModel<?> dataModel = (TreeNodeModel<?>) node.getUserObject();

			if(dataModel.isDatabase()){
				Database selectedDatabase = (Database) dataModel.dataObject;
				if(!dataModel.hasBeenSearched){
					controller.selectDatabase(selectedDatabase);
					dataModel.hasBeenSearched = true;
				}
			}else if(dataModel.isTable()){
				Table selectedTable = (Table) dataModel.dataObject;
				if(!dataModel.hasBeenSearched){
					controller.selectTable(selectedTable);
					dataModel.hasBeenSearched = true;
				}
			}
		}
	}

	@Override public void mouseClicked(MouseEvent arg0) {}
	@Override public void mouseEntered(MouseEvent arg0) {}
	@Override public void mouseExited(MouseEvent arg0) {}
	@Override public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent e) {
			
		if (e.isPopupTrigger()){
            JTree tree = (JTree)e.getSource();
            TreePath path = tree.getPathForLocation(e.getX(), e.getY());
            if (path == null)
                    return; 

            final DefaultMutableTreeNode currentTableNode = (DefaultMutableTreeNode) path.getLastPathComponent();

			if (currentTableNode.getUserObject() instanceof TreeNodeModel<?>) {
				final TreeNodeModel<?> currentTableModel = (TreeNodeModel<?>) currentTableNode.getUserObject();
				
				if(currentTableModel.isTable() && currentTableModel.hasBeenSearched){
				    JPopupMenu tablePopupMenu = new JPopupMenu();
				    JMenuItem checkAllMenu = new JMenuItem("Check All");
				    JMenuItem uncheckAllMenu = new JMenuItem("Uncheck All");
				    JMenuItem loadMenu = new JMenuItem("Load");
				    
				    checkAllMenu.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							DefaultTreeModel treeModel = (DefaultTreeModel) databaseTree.getModel();

							int tableChildCount = treeModel.getChildCount(currentTableNode);
							for(int i=0; i < tableChildCount ;i++) {
								DefaultMutableTreeNode currentChild = (DefaultMutableTreeNode) treeModel.getChild(currentTableNode, i);
								if( currentChild.getUserObject() instanceof TreeNodeModel<?> ){
									TreeNodeModel<?> columnTreeNodeModel = (TreeNodeModel<?>) currentChild.getUserObject();
									columnTreeNodeModel.isSelected = true;
									currentTableModel.hasChildSelected = true;
								}
							}

							treeModel.nodeChanged(currentTableNode);
						}
					});
				    
				    uncheckAllMenu.addActionListener(new ActionListener() {
				    	@Override
				    	public void actionPerformed(ActionEvent arg0) {
				    		DefaultTreeModel treeModel = (DefaultTreeModel) databaseTree.getModel();
				    		
				    		int tableChildCount = treeModel.getChildCount(currentTableNode);
				    		for(int i=0; i < tableChildCount ;i++) {
				    			DefaultMutableTreeNode currentChild = (DefaultMutableTreeNode) treeModel.getChild(currentTableNode, i);
				    			if( currentChild.getUserObject() instanceof TreeNodeModel<?> ){
				    				TreeNodeModel<?> columnTreeNodeModel = (TreeNodeModel<?>) currentChild.getUserObject();
				    				columnTreeNodeModel.isSelected = false;
				    				currentTableModel.hasChildSelected = false;
				    			}
				    		}
				    		
				    		treeModel.nodeChanged(currentTableNode);
				    	}
				    });
				    
				    tablePopupMenu.add(checkAllMenu);
				    tablePopupMenu.add(uncheckAllMenu);
				    tablePopupMenu.add(new JSeparator());
				    tablePopupMenu.add(loadMenu);
				    tablePopupMenu.show(tree, e.getX(), e.getY());
				}
            }
		}
	}
}