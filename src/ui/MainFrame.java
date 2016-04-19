package ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.text.DefaultCaret;
import javax.swing.tree.DefaultMutableTreeNode;
import tree.Tree;

public class MainFrame extends Frame {

	private static final long serialVersionUID = -122067383988169509L;

	private JProgressBar progress;
	private JTextArea logger;
	private JScrollPane scroll;
	private JTree tree;
	private JScrollPane scrolltree;
	private JSplitPane split;
	private JButton start;
	private JPanel rightpanel;
	private JButton openButton;
	private JPanel infospanel;
	private JLabel CDSCount;
	private JLabel CDSFailed;
	private JLabel dinucleotideCount;
	private JLabel trinucleotideCount;
	private JLabel pathLabel;
	private TreeCheckBoxSelectionModel model;

	public MainFrame(Tree t) {
		this.setLayout(new BorderLayout());
		
		// Dialog pour le choix d'un répertoire
		new FileChooserDialog(this);
		
		logger = new JTextArea();
		DefaultCaret c = (DefaultCaret) logger.getCaret();
		c.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		scroll = new JScrollPane(logger);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		start = new JButton("Start");

		progress = new JProgressBar();
		progress.setDoubleBuffered(true);
		progress.setStringPainted(true);
		progress.setMaximum(100);
		progress.setVisible(false);

		openButton = new JButton("Ouvrir le fichier");
		openButton.setVisible(false);

		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(new InfoNode("All", new ArrayList<String>()));
		MainFrame.createJTree(t, root, new ArrayList<String>());
		tree = new JTree(root);
		model = new TreeCheckBoxSelectionModel(tree.getModel());
		tree.setCellRenderer(new TreeCheckBoxRenderer(model));
		tree.addMouseListener(new TreeCheckBoxMouseListener(tree, model, this));
		//tree.addTreeSelectionListener(new TreeInfosListener(tree, infosFile, openButton));

		scrolltree = new JScrollPane(tree);
		scrolltree.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		pathLabel = new JLabel("Unknown");
		CDSCount = new JLabel("Unknown");
		CDSFailed = new JLabel("Unknown");
		dinucleotideCount = new JLabel("Unknown");
		trinucleotideCount = new JLabel("Unknown");
		
		JPanel gInfosPanel = new JPanel(new BorderLayout());
		
		infospanel = new JPanel(new GridLayout(5,2));
		infospanel.add(new JLabel("Path : "));
		infospanel.add(pathLabel);
		infospanel.add(new JLabel("CDS Count : "));
		infospanel.add(CDSCount);
		infospanel.add(new JLabel("CDS Failed : "));
		infospanel.add(CDSFailed);
		infospanel.add(new JLabel("Dinucleotide Count : "));
		infospanel.add(dinucleotideCount);
		infospanel.add(new JLabel("Trinucleotide Count : "));
		infospanel.add(trinucleotideCount);
		
		gInfosPanel.add(infospanel, BorderLayout.NORTH);
		gInfosPanel.add(openButton, BorderLayout.PAGE_END);
		
		rightpanel = new JPanel(new BorderLayout());
		rightpanel.add(gInfosPanel, BorderLayout.NORTH);
		rightpanel.add(scroll, BorderLayout.CENTER);
		rightpanel.add(start, BorderLayout.PAGE_END);

		split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrolltree, rightpanel);
		this.add(split);

		setTitle("BioInfo : Main");
		setSize(800, 600);
		setVisible(true);

		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				start.setVisible(false);
				rightpanel.remove(start);
				rightpanel.add(progress, BorderLayout.PAGE_END);
				progress.setVisible(true);
				
				UIManager.launchProcess(MainFrame.this.model.getSelectedNodes());
			}
		});

	}

	public void log(String msg) {
		this.logger.insert(msg + "\n", this.logger.getText().length());
	}

	public void setProgress(double n) {
		String str = Double.toString(n);
		str = str.substring(0, (str.length() >= 7 ? 7 : str.length()));
		this.progress.setString(str + "%");
		this.progress.setValue((int) n);
	}
	
	public void setInfos(InfoNode infos){
		if (infos == null) {
			this.pathLabel.setText("Unknown");
			this.CDSCount.setText("Unknown");
			this.CDSFailed.setText("Unknown");
			this.dinucleotideCount.setText("Uknown");
			this.trinucleotideCount.setText("Unknown");
			openButton.setVisible(false);
        	return;
        }
        else {
        	String xls = FindExtension.check(infos.getRealPath(), ".xls");
        	String bdd = FindExtension.check(infos.getRealPath(), ".bdd");
        	
        	if (xls != "") {
        		openButton.addActionListener(new OpenFileListener(xls));
        		openButton.setVisible(true);
        	} else {
        		openButton.setVisible(false);
        	}
        	
        	if(bdd != ""){
        		
        	} else {
    			this.pathLabel.setText("Unknown");
    			this.CDSCount.setText("Unknown");
    			this.CDSFailed.setText("Unknown");
    			this.dinucleotideCount.setText("Uknown");
    			this.trinucleotideCount.setText("Unknown");
        	}
        }
	}

	public static void createJTree(Tree t, DefaultMutableTreeNode treeNode,
			ArrayList<String> path) {
		Object[] nodes = t.nodes();

		for (Object o : nodes) {
			String node = (String) o;
			ArrayList<String> new_path = new ArrayList<String>(path);
			new_path.add(node);

			if (t.isLeaf(node)) {
				treeNode.add(new DefaultMutableTreeNode(new InfoNode(node,
						new_path)));
			} else {
				DefaultMutableTreeNode customNode = new DefaultMutableTreeNode(
						new InfoNode(node, new_path));
				treeNode.add(customNode);
				MainFrame.createJTree((Tree) t.get(node), customNode, new_path);
			}
		}
	}
}
