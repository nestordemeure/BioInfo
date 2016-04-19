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
	private JTextArea infosFile;
	private JButton openButton;
	
	private static int HITBOX_START = 30;
	private static int HITBOX_SIZE  = 20;
	
	public TreeCheckBoxMouseListener(JTree tree, TreeCheckBoxSelectionModel model, JTextArea infosFile, JButton openButton){
		this.tree = tree;
		this.model = model;
		this.infosFile = infosFile;
		this.openButton = openButton;
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
		if (node == null) {
        	infosFile.setText("Pas d'information disponible");
        	openButton.setVisible(false);
        	return;
        }
        else {
        	InfoNode nodeInfo = (InfoNode) node.getUserObject();
        	String file = FindExtension.check(nodeInfo.getRealPath(), ".xls");
        	if (file != "") {
        		openButton.addActionListener(new OpenFileListener(file));
        		openButton.setVisible(true);
        	} else {
        		infosFile.setText("Pas d'information disponible");
        		openButton.setVisible(false);
        	}
        }
	}

}
