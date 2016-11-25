package tree;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Organism implements Serializable {
	private static final long serialVersionUID = -2867789287775171672L;
	private String kingdom;
	private String group;
	private String subgroup;
	private String name;
	private String bioproject;
	private String taxonomy;
	private String accession;
	private Map<String, String> replicons;
	
	public Organism(String kingdom, String group, String subgroup, String name, String bioproject){
		this.kingdom = kingdom;
		this.group = group;
		this.subgroup = subgroup;
		this.name = name;
		this.bioproject = bioproject;
		this.replicons = new HashMap<String, String>();
	}
	
	public Organism(){
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
	
	private void readObject(ObjectInputStream inputstream) throws IOException, ClassNotFoundException 
	{
		kingdom = (String) inputstream.readObject();
		group = (String) inputstream.readObject();
		subgroup = (String) inputstream.readObject();
		name = (String) inputstream.readObject();
		bioproject = (String) inputstream.readObject();
		accession = (String) inputstream.readObject();
		taxonomy = (String) inputstream.readObject();
		replicons = (HashMap<String,String>)inputstream.readObject();
	}

	private void writeObject(ObjectOutputStream outputstream) throws IOException
	{
		outputstream.writeObject(kingdom);
		outputstream.writeObject(group);
		outputstream.writeObject(subgroup);
		outputstream.writeObject(name);
		outputstream.writeObject(bioproject);
		outputstream.writeObject(accession);
		outputstream.writeObject(taxonomy);
		outputstream.writeObject(replicons);
	}

	public String getKingdom() {
		return kingdom;
	}

	public void setKingdom(String kingdom) {
		this.kingdom = kingdom;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getSubgroup() {
		return subgroup;
	}

	public void setSubgroup(String subgroup) {
		this.subgroup = subgroup;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBioproject() {
		return bioproject;
	}

	public void setBioproject(String bioproject) {
		this.bioproject = bioproject;
	}

	public String getTaxonomy() {
		return taxonomy;
	}

	public void setTaxonomy(String taxonomy) {
		this.taxonomy = taxonomy;
	}

	public String getAccession() {
		return accession;
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}

	public Map<String, String> getReplicons() {
		return replicons;
	}

	public void setReplicons(Map<String, String> replicons) {
		this.replicons = replicons;
	}
}
