import java.util.ArrayList;
import manager.ThreadManager;
import tree.*;

public class Main {
	public static void main(String[] args) throws Exception {
		
		Tree plop = new Tree();
		plop=TreeManager.constree();
		
		ThreadManager.start(plop, new ArrayList<String>());
		
//		Tree<Tree> tree = new Tree<Tree>();
//		
//		if(! tree.contains("eukaryota")){
//			tree.add("eukaryota", new Tree<Tree>());
//		}
//		
//		Tree cur = tree.get("eukaryota");
		
	}

}
