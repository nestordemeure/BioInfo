package ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class TreeCheckBoxMouseListener extends MouseAdapter{
	
	private JTree tree;
	private TreeCheckBoxSelectionModel model;
	
	public TreeCheckBoxMouseListener(JTree tree, TreeModel model){
		this.tree = tree;
		this.model = new TreeCheckBoxSelectionModel(model);
	}
	
	@Override
	public void mouseClicked(MouseEvent me){
		TreePath tp = tree.getPathForLocation(me.getX(), me.getY());
		if(tp == null){
			return;
		}
		
		this.model.toggleSelect(tp);
		
		tree.treeDidChange();
	}

}
