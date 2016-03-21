import java.util.ArrayList;
import manager.ThreadManager;
import tree.*;

public class Main {
	public static void main(String[] args) throws Exception {
		
		Tree plop = new Tree();
		plop=TreeManager.constree();
		
		ThreadManager.start(plop, new ArrayList<String>());
		
	}

}
