package ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class TreeCheckBoxMouseListener extends MouseAdapter{
	
	private JTree tree;
	private TreeCheckBoxSelectionModel model;
	private MainFrame mf;
	
	private static int HITBOX_START = 30;
	private static int HITBOX_SIZE  = 20;
	
	public TreeCheckBoxMouseListener(JTree tree, TreeCheckBoxSelectionModel model, MainFrame m){
		this.tree = tree;
		this.model = model;
		this.mf = m;
	}
	
	@Override
	public void mouseClicked(MouseEvent me){
		TreePath tp = tree.getPathForLocation(me.getX(), me.getY());
		if(tp == null){
			return;
		}
		
		if(me.getX() < HITBOX_START + (tp.getPathCount() - 1 ) * HITBOX_SIZE){
			this.model.toggleSelect(tp);
			tree.treeDidChange();
		} else {
			this.loadInfo((DefaultMutableTreeNode)tp.getLastPathComponent());
		}
	}
	
	private void loadInfo(DefaultMutableTreeNode node){
		this.mf.setInfos((InfoNode) node.getUserObject());
	}

}
