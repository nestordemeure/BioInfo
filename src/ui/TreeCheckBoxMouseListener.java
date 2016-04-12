package ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class TreeCheckBoxMouseListener extends MouseAdapter{
	
	private JTree tree;
	private TreeCheckBoxSelectionModel model;
	
	public TreeCheckBoxMouseListener(JTree tree, TreeCheckBoxSelectionModel model){
		this.tree = tree;
		this.model = model;
	}
	
	@Override
	public void mouseClicked(MouseEvent me){
		TreePath tp = tree.getPathForLocation(me.getX(), me.getY());
		if(tp == null){
			return;
		}
		
		this.model.getSelectedNodes();
		
		this.model.toggleSelect(tp);
		
		tree.treeDidChange();
		
		ArrayList<InfoNode> n = this.model.getSelectedNodes();
		
		for(InfoNode node : n){
			System.out.println(node.getPath());
		}
	}

}
