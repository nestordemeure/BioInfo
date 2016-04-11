package ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class TreeCheckBoxMouseListener extends MouseAdapter{
	
	private JTree tree;
	
	public TreeCheckBoxMouseListener(JTree tree){
		this.tree = tree;
	}
	
	@Override
	public void mouseClicked(MouseEvent me){
		TreePath tp = tree.getPathForLocation(me.getX(), me.getY());
		if(tp == null){
			return;
		}
		
		DefaultMutableTreeNode mutable = (DefaultMutableTreeNode) tp.getLastPathComponent();
		if(mutable != null){
			Object userObject = mutable.getUserObject();
			if(userObject != null && userObject instanceof InfoNode){
				InfoNode node = (InfoNode) userObject;
				if(node.isSelected()){
					node.setSelected(false);
				} else {
					node.setSelected(true);
				}
				tree.treeDidChange();
			}
		}
	}

}
