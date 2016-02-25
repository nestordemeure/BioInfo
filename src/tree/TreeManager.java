package tree;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ui.UIManager;

import io.*;;

public class TreeManager {
	
	public static Tree constree(){
		System.out.println("Building Tree... (2-3min)");
		Tree res = new Tree();
		String baseurl = "http://www.ncbi.nlm.nih.gov/genomes/Genome2BE/genome2srv.cgi?action=GetGenomeList4Grid&filterText=%7CAll&page=";
		int page = 1;
		int pageSize = 100;
		Scanner buffer = new Scanner("");
		Boolean flag = true;
		while (flag){
			UIManager.log("Page : "+page);
			buffer = Net.getUrl(baseurl+page+"&pageSize="+pageSize);
			int debut=0;
			while(buffer.hasNext()){
				String cur = buffer.next();
				if (debut==0){
					if (buffer.hasNext()){
						cur = buffer.next();
						debut++;
					}
					else{
						flag=false;
						break;
					}
				}
				
				// regexp1
				Pattern regex1 = Pattern.compile("<td>(.*?)<\\/td>");
				Matcher m = regex1.matcher(cur);
				m.find();
				String organism = m.group(1);
				m.find();
				String kingdom = m.group(1);
				//System.out.println(kingdom);
				m.find();
				String group = m.group(1);
				//System.out.println(group);
				m.find();
				String subgroup = m.group(1);
				//System.out.println(subgroup);
				
				//Regexp2
				Pattern regex2 = Pattern.compile(">(.*?)<\\/a>");
				Matcher n = regex2.matcher(organism);
				n.find();
				organism = n.group(1);
				//System.out.println(organism);
				
				
				//Building Tree
				//Root Level
				Tree<Tree> level1 = new Tree<Tree>();
				if (!res.contains(kingdom)){
					res.add(kingdom, level1);
				}
				else {
					level1=(Tree<Tree>) res.get(kingdom);
				}
				
				//Level 1
				Tree<Tree> level2 = new Tree<Tree>();
				
				if (!level1.contains(group)){
					level1.add(group, level2);
				}
				else {
					level2=(Tree<Tree>) level1.get(group);
				}
				
				//Level 2
				Tree<String> level3 = new Tree<String>();
				
				if (!level2.contains(subgroup)){
					level2.add(subgroup, level3);
				}
				else {
					level3=(Tree<String>) level2.get(subgroup);
				}
				
				//Level 3
				level3.add(organism, null);
				
			}
			page++;
		}
		//res.printTree();
		System.out.println("Done. =D");
		return res;
	}
}
//Scanner sc = new Scanner(new URL("http://www.ncbi.nlm.nih.gov/genome/browse/").openStream(), "UTF-8").useDelimiter("\n");
//while(sc.hasNext()){
//	String cur = sc.next();
//	if(cur.startsWith("subgroup_sel_text = ")){
//		cur = cur.split("width:8.5em;\">")[1].split("</SELECT>")[0];
//		for(String str : cur.split("<OPTION")){
//			if(str.split(";\">").length > 1){
//				str = str.split(";\">")[1].split("</OPTION>")[0];
//				if(str.contains("All ") && ! str.equals("All")){
//					int level = str.length() - str.replaceAll("\\-", "").length();
//					str = str.split("All")[1].split("-")[0].trim();
//					System.out.println("GROUP ("+level/2+") : "+str);
//				} else if (! str.equals("All")){
//					System.out.println("Elem : "+ str.trim());
//				}
//			}
//		}
//	}
//}
//testAccessManager();