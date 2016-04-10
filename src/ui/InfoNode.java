package ui;

import java.util.ArrayList;

public class InfoNode {
	private String name;
	private ArrayList<String> path = new ArrayList<String>();
	private String rootPath = "";
	private String separator = "/";
	
	public InfoNode(String n, ArrayList<String> p) {
		name = n;
		path = p;
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
}
