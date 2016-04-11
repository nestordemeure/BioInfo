package ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.DefaultCaret;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import manager.ParserManager;
import manager.ThreadManager;
import tree.Tree;
import tree.TreeManager;

public class MainFrame extends Frame {
	
	private static final long serialVersionUID = -122067383988169509L;
	
	private JProgressBar progress;
	private JTextArea logger;
	private JScrollPane scroll;
	private JTree tree;
	private JScrollPane scrolltree;
	private JTextArea infos;
	private JSplitPane split;
	private JPanel rightpanel;
	
	public MainFrame(Tree t){
		this.setLayout(new BorderLayout());
		logger = new JTextArea();
		DefaultCaret c = (DefaultCaret) logger.getCaret();
		c.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		scroll = new JScrollPane(logger);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		progress = new JProgressBar();
		progress.setDoubleBuffered(true);
		progress.setStringPainted(true);
		progress.setMaximum(100);
		
		infos = new JTextArea();
		infos.setEditable(false);
		infos.append("Pas d'information disponible");
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
		MainFrame.createJTree(t, root, new ArrayList<String>());	
		tree = new JTree(root);
		tree.setCellRenderer(new TreeCheckBoxRenderer());
		tree.addMouseListener(new TreeCheckBoxMouseListener(tree, tree.getModel()));
//		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		//CheckTreeManager checkTreeManager = new CheckTreeManager(tree);
		
		//tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
//		tree.addTreeSelectionListener(new TreeSelectionListener() {
//		    public void valueChanged(TreeSelectionEvent e) {
//		        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
//		                           tree.getLastSelectedPathComponent();
// 
//		        if (node == null) {
//		        	infos.setText("Pas d'information disponible");
//		        	return;
//		        }
//		        else {
//		        	InfoNode nodeInfo = (InfoNode) node.getUserObject();
//		        	// TODO : Check si le fichier existe déjà et affiche ses informations
//		        	/* File f = new File(nodeInfo.getPath());
//		        	   if(f.exists() && !f.isDirectory()) { 
//		        	     infos.setText(nodeInfo.getPath());
//		        	   }
//		        	   else {
//		        	   	 infos.setText("Pas d'information disponible");
//		        	   }*/
//		        	infos.setText(nodeInfo.getPath());
//		        }
//		    }
//		});

		scrolltree = new JScrollPane(tree);
		scrolltree.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		JPanel rightpanel = new JPanel(new BorderLayout());
		rightpanel.add(infos, BorderLayout.NORTH);
		rightpanel.add(scroll, BorderLayout.CENTER);
		rightpanel.add(progress, BorderLayout.PAGE_END);
		
		split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrolltree, rightpanel);
		this.add(split);
		
		setTitle("BioInfo : Main");
		setSize(800,600);
		setVisible(true);
	}
	
	public void log(String msg){
		this.logger.insert(msg + "\n",this.logger.getText().length());
	}
	
	public void setProgress(int n){
		this.progress.setValue(n);
	}
	
   public static void createJTree(Tree t, DefaultMutableTreeNode treeNode, ArrayList<String> path){
	   Object[] nodes = t.nodes();
	   
	   for (Object o : nodes) {
			String node = (String) o;
			ArrayList<String> new_path = new ArrayList<String>(path);
			new_path.add(node);
			
			if (t.isLeaf(node)) {
				treeNode.add(new DefaultMutableTreeNode(new InfoNode(node,new_path)));
			}
			else {
				DefaultMutableTreeNode customNode = new DefaultMutableTreeNode(new InfoNode(node,new_path));
				treeNode.add(customNode);
				MainFrame.createJTree((Tree) t.get(node), customNode, new_path);
			}
		}
   }
}
