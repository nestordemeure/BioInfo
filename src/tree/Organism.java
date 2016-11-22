package tree;

import java.util.HashMap;
import java.util.Map;

public class Organism {
	private String kingdom;
	private String group;
	private String subgroup;
	private String name;
	private String bioproject;
	private Map<String, String> replicons;
	
	public Organism(String kingdom, String group, String subgroup, String name, String bioproject){
		this.kingdom = kingdom;
		this.group = group;
		this.subgroup = subgroup;
		this.name = name;
		this.bioproject = bioproject;
		this.replicons = new HashMap<String, String>();
	}
	
	public boolean addReplicon(String name, String id){
		if(this.replicons.containsKey(name)){
			return false;
		} else {
			this.replicons.put(name, id);
			return true;
		}
	}
	
	public void updateTree(Tree mainT){
		Tree kingdomT;
		Tree groupT;
		Tree subgroupT;
		if(mainT.contains(this.kingdom)){
			kingdomT = (Tree)mainT.get(this.kingdom);
		} else {
			kingdomT = new Tree<Tree>();
			mainT.add(this.kingdom, kingdomT);
		}
		
		if(kingdomT.contains(this.group)){
			groupT = (Tree)kingdomT.get(this.group);
		} else {
			groupT = new Tree<Tree>();
			kingdomT.add(this.group, groupT);
		}
		
		if(groupT.contains(this.subgroup)){
			subgroupT = (Tree)groupT.get(this.subgroup);
		} else {
			subgroupT = new Tree<Organism>();
			groupT.add(this.subgroup, subgroupT);
		}
		
		subgroupT.add(this.name, this);
	}

	@Override
	public String toString(){
		String str = this.kingdom+"/"+this.group+"/"+this.subgroup+"/"+this.name+"("+this.bioproject+")";
		str += "\nReplicons :";
		for(String name : this.replicons.keySet()){
			str += "\n - "+name+" - "+this.replicons.get(name);
		}
		return str;
	}
}