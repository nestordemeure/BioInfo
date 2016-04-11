package ui;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class TreeCheckBoxSelectionModel extends DefaultTreeSelectionModel{
	
	private static final long serialVersionUID = 1290070328333170448L;
	private TreeModel model;
	
	public TreeCheckBoxSelectionModel(TreeModel model){
		this.model = model;
		
		this.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
	}
	
	public boolean isPathSelected(TreePath path){
		InfoNode node = getInfoNode(path);
		return (node == null) ? false : node.isSelected();
	}
	
	public void selectPath(TreePath path){
		InfoNode node = this.getInfoNode(path);
		if(node != null){
			node.setSelected(true);
		}
		
		Object curNode = path.getLastPathComponent();
		int childCount = this.model.getChildCount(curNode);
		
		for(int i = 0; i < childCount; i++){
			Object child = this.model.getChild(curNode, i);
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) child;
			TreePath temp = new TreePath(treeNode.getPath());
			this.selectPath(temp);
		}
	}
	
	
	private void deselectChilds(TreePath path){
		InfoNode node = this.getInfoNode(path);
		if(node != null){
			node.setSelected(false);
		}
		
		Object curNode = path.getLastPathComponent();
		int childCount = this.model.getChildCount(curNode);
		
		for(int i = 0; i < childCount; i++){
			Object child = this.model.getChild(curNode, i);
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) child;
			TreePath temp = new TreePath(treeNode.getPath());
			this.deselectChilds(temp);
		}
	}
	
	public void deselectPath(TreePath path){
		this.deselectChilds(path);
		TreePath temp = path.getParentPath();
		while(temp != null){
			InfoNode node = this.getInfoNode(temp);
			if(node != null){
				node.setSelected(false);
			}
			temp = temp.getParentPath();
		}
	}
	
	public void toggleSelect(TreePath path){
		if(this.isPathSelected(path)){
			this.deselectPath(path);
		} else {
			this.selectPath(path);
		}
	}
	
	public boolean isPartiallySelected(TreePath path){
		InfoNode node = getInfoNode(path);
		if(node != null && node.isSelected()){
			return false;
		}
		
		Object curNode = path.getLastPathComponent();
		int childCount = this.model.getChildCount(curNode);
		
		for(int i = 0; i < childCount; i++){
			Object child = this.model.getChild(curNode, i);
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) child;
			TreePath temp = new TreePath(treeNode.getPath());
			if(this.isPathSelected(temp) || this.isPartiallySelected(temp)){
				return true;
			}
		}
		return false;
	}
	
	private InfoNode getInfoNode(TreePath path){
		DefaultMutableTreeNode mutable = (DefaultMutableTreeNode) path.getLastPathComponent();
		if(mutable != null){
			Object userObject = mutable.getUserObject();
			if(userObject != null && userObject instanceof InfoNode){
				InfoNode node = (InfoNode) userObject;
				return node;
			}
		}
		return null;
	}
}
