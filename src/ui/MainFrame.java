package ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
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
	private JTextArea infosFile;
	private JSplitPane split;
	private JButton start;
	private JPanel rightpanel;
	private JButton openButton;
	private JPanel infospanel;

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
		
		infosFile = new JTextArea();
		infosFile.setEditable(false);
		infosFile.append("Pas d'information disponible");

		DefaultMutableTreeNode root = new DefaultMutableTreeNode(new InfoNode("All", new ArrayList<String>()));
		MainFrame.createJTree(t, root, new ArrayList<String>());
		tree = new JTree(root);
		TreeCheckBoxSelectionModel model = new TreeCheckBoxSelectionModel(tree.getModel());
		tree.setCellRenderer(new TreeCheckBoxRenderer(model));
		//tree.addMouseListener(new TreeCheckBoxMouseListener(tree, model, infosFile, openButton));
		//tree.addTreeSelectionListener(new TreeInfosListener(tree, infosFile, openButton));

		scrolltree = new JScrollPane(tree);
		scrolltree.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		infospanel = new JPanel(new BorderLayout());
		infospanel.add(infosFile, BorderLayout.NORTH);
		infospanel.add(openButton, BorderLayout.SOUTH);
		
		rightpanel = new JPanel(new BorderLayout());
		rightpanel.add(infospanel, BorderLayout.NORTH);
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
				UIManager.launchProcess();
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
