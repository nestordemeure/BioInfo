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
		int i = 1;
		for (String element : path) {
			if (i++ != path.size()) {
				res += separator;
		    }
			res += element;
			i++;
		}
		return res;
	}
	
	public String getTreePath() {
		String res = "";
		int i = 1;
		for (String element : path) {
			if (i++ != path.size() && i != 1) {
				res += separator;
		    }
			res += element;
			i++;
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
}
