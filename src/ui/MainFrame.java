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
import javax.swing.text.DefaultCaret;
import javax.swing.tree.DefaultMutableTreeNode;

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
		infos.append("info1 \n info2");
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
		MainFrame.createJTree(t, root);	
		tree = new JTree(root);
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
	
   public static void createJTree(Tree t, DefaultMutableTreeNode treeNode){
	   Object[] nodes = t.nodes();
	   
	   for (Object o : nodes) {
			String node = (String) o;
			
			if (t.isLeaf(node)) {
				treeNode.add(new DefaultMutableTreeNode(node));
			}
			else {
				DefaultMutableTreeNode customNode = new DefaultMutableTreeNode(node);
				treeNode.add(customNode);
				MainFrame.createJTree((Tree) t.get(node), customNode);
			}
		}
   }
}
