package ui;

import java.util.ArrayList;

import configuration.Configuration;

public class InfoNode {
	private String name;
	private ArrayList<String> path = new ArrayList<String>();
	private String rootPath = Configuration.BASE_FOLDER;
	private String separator = Configuration.FOLDER_SEPARATOR;
	private boolean selected;
	
	public InfoNode(String n, ArrayList<String> p) {
		name = n;
		path = p;
		selected = true;
	}
	
	public ArrayList<String> getPath() {
		return path;
	}
	
	public String getRealPath() {
		String res = rootPath;
		for (String element : path) {
			res += separator;
			res += element;
		}
		return res;
	}
	
	public String getTreePath() {
		String res = "";
		for (String element : path) {
			res += "/";
			res += element;
		}
		return res;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public boolean isSelected(){
		return this.selected;
	}
	
	public void setSelected(boolean selected){
		this.selected = selected;
	}
	

	public boolean canBeInPath(ArrayList<String> r_path){
		if(this.path.size() == 0){
			return true;
		}
		
		int length = 0;
		if(this.path.size() < r_path.size()) {
			length = this.path.size();
		} else {
			length = r_path.size();
		}
		
		for(int i = 0; i < length; i++){
			if(! this.path.get(i).toLowerCase().equals(r_path.get(i).toLowerCase())){
				return false;
			}
		}
		
		return true;
	}
}
