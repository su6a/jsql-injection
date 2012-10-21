package mvc.view;

import java.awt.Cursor;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import mvc.view.component.TreeNodeEditor;
import mvc.view.component.TreeNodeRenderer;

public class OutputPanel extends JSplitPane{
	private static final long serialVersionUID = -5696939494054282278L;
	
	public OutputPanel(final GUI gui){
		super(JSplitPane.VERTICAL_SPLIT, true);
				
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Databases");
		gui.databaseTree = new JTree(root);
		
		TreeNodeRenderer renderer = new TreeNodeRenderer();
		gui.databaseTree.setCellRenderer(renderer);
		
	    TreeNodeEditor editor = new TreeNodeEditor(gui.databaseTree, gui.controller, gui.valuesTabbedPane);
	    gui.databaseTree.setCellEditor(editor);
	    
	    gui.databaseTree.setEditable(true);	// allows repaint nodes
	    gui.databaseTree.setShowsRootHandles(true);
	    gui.databaseTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	    
	    gui.databaseTree.getModel().addTreeModelListener(new TreeModelListener() { // allows repaint progressbar
			
			@Override public void treeStructureChanged(TreeModelEvent arg0) {}
			@Override public void treeNodesRemoved(TreeModelEvent arg0) {}
			@Override public void treeNodesInserted(TreeModelEvent arg0) {}
			@Override
			public void treeNodesChanged(TreeModelEvent arg0) {
				if(arg0 != null){
					gui.databaseTree.firePropertyChange(
						JTree.ROOT_VISIBLE_PROPERTY, 
						!gui.databaseTree.isRootVisible(), 
						gui.databaseTree.isRootVisible()
					);
				}
			}
			
		});
		
		JSplitPane treeAndTableSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		treeAndTableSplitPane.setLeftComponent( new JScrollPane(gui.databaseTree) );
		treeAndTableSplitPane.setRightComponent( gui.valuesTabbedPane );
		treeAndTableSplitPane.setDividerLocation(285);
		treeAndTableSplitPane.setDividerSize(5);
        
        JTabbedPane infoTabs = new JTabbedPane();
        infoTabs.addTab("Console", new ImageIcon(getClass().getResource("/console.gif")), new JScrollPane(gui.consoleArea));
        infoTabs.addTab("Chunk", new ImageIcon(getClass().getResource("/category.gif")), new JScrollPane(gui.chunks));
        infoTabs.addTab("Binary", new ImageIcon(getClass().getResource("/binary.gif")), new JScrollPane(gui.binaryArea));
        infoTabs.addTab("Header", new ImageIcon(getClass().getResource("/update.gif")), new JScrollPane(gui.headers));
        infoTabs.setFont(new Font(infoTabs.getFont().getName(),Font.PLAIN,infoTabs.getFont().getSize()));
        
        this.setTopComponent(treeAndTableSplitPane);
        this.setBottomComponent( infoTabs );
        this.setDividerLocation(280);
        this.setDividerSize(5);
        this.setResizeWeight(1); // defines left and bottom pane
        
        gui.consoleArea.setFont(new Font("Courier New",Font.PLAIN,gui.consoleArea.getFont().getSize()));
        gui.consoleArea.setEditable(false);
        gui.consoleArea.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        
        gui.chunks.setFont(new Font("Courier New",Font.PLAIN,gui.chunks.getFont().getSize()));
        gui.chunks.setEditable(false);
        gui.chunks.setLineWrap(true);
        gui.chunks.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        
        gui.headers.setFont(new Font("Courier New",Font.PLAIN,gui.headers.getFont().getSize()));
        gui.headers.setEditable(false);
        gui.headers.setLineWrap(true);
        gui.headers.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        
        gui.binaryArea.setFont(new Font("Courier New",Font.PLAIN,gui.binaryArea.getFont().getSize()));
        gui.binaryArea.setEditable(false);
        gui.binaryArea.setLineWrap(true);
        gui.binaryArea.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
	}
}