package ui;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

public class TreeCheckBoxRenderer implements TreeCellRenderer{
	JLabel mainLabel = new JLabel("");
	JCheckBox checkBox = new JCheckBox();
	
	JPanel renderer = new JPanel();

	DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

	public TreeCheckBoxRenderer() {
		checkBox.setOpaque(false);
		renderer.add(checkBox);
		renderer.add(mainLabel);
		renderer.setOpaque(false);
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
		boolean expanded, boolean leaf, int row, boolean hasFocus) {
		
		Component returnValue = null;
		
		if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
			
			Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
			
			if(userObject instanceof InfoNode){
				InfoNode n = (InfoNode) userObject;
				mainLabel.setText(n.toString()); 
				if(n.isSelected()){
					this.checkBox.setSelected(true);
				} else {
					this.checkBox.setSelected(false);
				}
			} else {
				mainLabel.setText("/");
			}
			renderer.setEnabled(tree.isEnabled());
			returnValue = renderer;
		}
		if (returnValue == null) {
			returnValue = defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded,
					leaf, row, hasFocus);
		}
		return returnValue;
	}
}
