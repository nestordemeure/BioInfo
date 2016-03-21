package tree;

import java.util.HashMap;

public class Tree<T> {
	
	private HashMap<String, T> nodes;
	
	public Tree(){
		this.nodes = new HashMap<String, T>();
	}
	
	public boolean contains(String node){
		return this.nodes.containsKey(node);
	}
	
	public boolean add(String node, T obj){
		if(! this.contains(node)){
			this.nodes.put(node, obj);
			return true;
		} else {
			return false;
		}
	}
	
	public T get(String node){
		return this.nodes.get(node);
	}
	
	public Object[] nodes(){
		return this.nodes.keySet().toArray();
	}
	
	public void printTree_aux(int level){
		if (this != null){
			Object[] nodess=this.nodes();
			for (Object node : nodess){
				for (int i=0; i < level; i++){
					System.out.print("-");
				}
				System.out.println(node);
					
				if (this.get((String)node) !=null){
					((Tree<?>) this.get((String)node)).printTree_aux(level+1);
				}
			}
		}
	}
	
	public void printTree(){
		printTree_aux(0);
	}
	
	public boolean isLeaf(String node) {
		return ! (this.nodes.get(node) instanceof Tree);
	}

}
