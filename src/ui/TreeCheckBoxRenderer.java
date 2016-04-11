package ui;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

public class TreeCheckBoxRenderer implements TreeCellRenderer{
	private JLabel mainLabel = new JLabel("");
	private TristateCheckBox checkBox = new TristateCheckBox();
	private JPanel renderer = new JPanel();
	private DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();
	
	private TreeCheckBoxSelectionModel model;

	public TreeCheckBoxRenderer(TreeCheckBoxSelectionModel model) {
		this.model = model;
		checkBox.setOpaque(false);
		renderer.add(checkBox);
		renderer.add(mainLabel);
		renderer.setOpaque(false);
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
		boolean expanded, boolean leaf, int row, boolean hasFocus) {
		
		Component returnValue = null;
		
		if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object userObject = node.getUserObject();
			TreePath path = new TreePath(node.getPath());
			
			if(userObject instanceof InfoNode){
				InfoNode n = (InfoNode) userObject;
				mainLabel.setText(n.toString()); 
				if(this.model.isPathSelected(path)){
					this.checkBox.setState(TristateCheckBox.SELECTED);
				} else if(this.model.isPartiallySelected(path)){
					this.checkBox.setState(TristateCheckBox.DONT_CARE);
				} else {
					this.checkBox.setState(TristateCheckBox.NOT_SELECTED);
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
