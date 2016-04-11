package ui;

import java.util.ArrayList;

public class InfoNode {
	private String name;
	private ArrayList<String> path = new ArrayList<String>();
	private String rootPath = "";
	private String separator = "/";
	private boolean selected;
	
	public InfoNode(String n, ArrayList<String> p) {
		name = n;
		path = p;
		selected = true;
	}
	
	public String getPath() {
		String res = rootPath;
		for (String element : path) {
			res += separator+element;
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
