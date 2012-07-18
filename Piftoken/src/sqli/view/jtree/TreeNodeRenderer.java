package sqli.view.jtree;

import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class TreeNodeRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 6713145837575127059L;

	private DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object nodeRenderer,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		
		Component returnValue = null;
		
		if ((nodeRenderer != null) && (nodeRenderer instanceof DefaultMutableTreeNode)) {
			DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) nodeRenderer;
			if(currentNode != null){
				Object userObject = currentNode.getUserObject();
				if(userObject instanceof TreeNodeModel<?>){
					TreeCellCustom c = new TreeCellCustom(tree,currentNode);
					TreeNodeModel<?> dataModel = (TreeNodeModel<?>) userObject;
	
					if(dataModel.isColumn()){
						c.checkBox.setText(dataModel+"");
						c.checkBox.setSelected(dataModel.isSelected);
						c.checkBox.setVisible(true);
						
					}else if(dataModel.isTable() || dataModel.isDatabase()){					
						c.label.setText(dataModel+"");
						c.label.setVisible(true);
						c.icon.setVisible(true);
						
						if(leaf){
				      		c.icon.setIcon(this.getLeafIcon());
				      	}else if(expanded){
				      		c.icon.setIcon(this.getOpenIcon());
				      	}else{
				      		c.icon.setIcon(this.getClosedIcon());
				      	}
				      	
						if (selected) {
							c.label.setForeground(this.getTextSelectionColor());
							c.label.setBackground(this.getBackgroundSelectionColor());
							c.label.setBorder(BorderFactory.createLineBorder(  
	                                UIManager.getColor("Tree.selectionBorderColor")));  
						} else {
							c.label.setForeground(this.getTextNonSelectionColor());
							c.label.setBackground(this.getBackgroundNonSelectionColor());
							c.label.setBorder(BorderFactory.createLineBorder(  
	                                UIManager.getColor("Tree.textBackground")));  
						}
		
						if(dataModel.hasChildSelected){
							c.button.setVisible(true);
						}
						
						if(dataModel.hasProgress){
							int dataCount = dataModel.dataObject.getCount();
							c.progressBar.setMaximum(dataCount);
							c.progressBar.setValue(dataModel.childUpgradeCount);
							c.progressBar.setVisible(true);
						}else if(dataModel.hasIndeterminatedProgress){
							c.loader.setVisible(true);
						}
						
					}
					returnValue = c;
				}
			}
		}
		if (returnValue == null) {
			returnValue = defaultRenderer.getTreeCellRendererComponent(tree, nodeRenderer, selected, expanded,
					leaf, row, hasFocus);
		}
		return returnValue;
	}
}