package ui;

import java.util.ArrayList;

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
		if(node != null && node.isSelected()){
			return true;
		}
		
		Object curNode = path.getLastPathComponent();
		int childCount = this.model.getChildCount(curNode);
		
		for(int i = 0; i < childCount; i++){
			Object child = this.model.getChild(curNode, i);
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) child;
			TreePath temp = new TreePath(treeNode.getPath());
			InfoNode n = this.getInfoNode(temp);
			if(! n.isSelected()){
				return false;
			}
		}
		return childCount > 0;
		
	}
	
	public void selectChilds(TreePath path){
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
			this.selectChilds(temp);
		}
	}
	
	public void updateParents(TreePath path){
		if(path.getParentPath() == null){
			return;
		}
		
		path = path.getParentPath();
		
		Object curNode = path.getLastPathComponent();
		int childCount = this.model.getChildCount(curNode);
		
		boolean update = true;
		
		for(int i = 0; i < childCount; i++){
			Object child = this.model.getChild(curNode, i);
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) child;
			TreePath temp = new TreePath(treeNode.getPath());
			if(! this.isPathSelected(temp)){
				update = false;
				break;
			}
		}
		if(update){
			InfoNode node = this.getInfoNode(path);
			if(node != null){
				node.setSelected(true);
			}
			this.updateParents(path);
		}
	}
	
	public void selectPath(TreePath path){
		this.selectChilds(path);
		this.updateParents(path);
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
	
	public ArrayList<InfoNode> getSelectedSubNodes(Object root){
		ArrayList<InfoNode> nodes = new ArrayList<>();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) root;
		Object userObject = node.getUserObject();
		
		boolean curSelected = false;
		
		if(userObject != null && userObject instanceof InfoNode){
			InfoNode n = (InfoNode) userObject;
			if(n.isSelected()){
				nodes.add(n);
				curSelected = true;
			}
		}
		
		if(!curSelected){
			int childCount = node.getChildCount();
			for(int i = 0; i < childCount ; i++){
				Object n = this.model.getChild(root, i);
				ArrayList<InfoNode> temp = this.getSelectedSubNodes(n);
				nodes.addAll(temp);
			}
		}
		
		return nodes;
	}
	
	public ArrayList<InfoNode> getSelectedNodes()
	{
		return this.getSelectedSubNodes(this.model.getRoot());
	}
	
	
	private InfoNode getInfoNode(TreePath path){
		DefaultMutableTreeNode mutable = (DefaultMutableTreeNode) path.getLastPathComponent();
		if(mutable != null){
			Object userObject = mutable.getUserObject();
			if(userObject != null && userObject instanceof InfoNode){
				return (InfoNode) userObject;
			}
		}
		return null;
	}
}
