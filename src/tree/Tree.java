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
	
	public String[] nodes(){
		return (String[])this.nodes.keySet().toArray();
	}

}
