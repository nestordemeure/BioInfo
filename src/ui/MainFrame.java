package ui;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.text.DefaultCaret;
import javax.swing.tree.DefaultMutableTreeNode;

import org.w3c.dom.Node;

import configuration.Configuration;
import excel.ExcelManager;
import tree.Organism;
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
	private JCheckBox download;
	private TreeCheckBoxSelectionModel model;
	private Long startTime;
	private JPanel infosProcess;

	public MainFrame(Tree t) {
		this.setLayout(new BorderLayout());

		// Dialog pour le choix d'un r√©pertoire
		new FileChooserDialog(this);

		logger = new JTextArea();
		DefaultCaret c = (DefaultCaret) logger.getCaret();
		c.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		scroll = new JScrollPane(logger);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		start = new JButton("Commencer");

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
		// tree.addTreeSelectionListener(new TreeInfosListener(tree, infosFile,
		// openButton));

		scrolltree = new JScrollPane(tree);
		scrolltree.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		pathLabel = new JLabel("Inconnu");
		CDSCount = new JLabel("Inconnu");
		CDSFailed = new JLabel("Inconnu");
		dinucleotideCount = new JLabel("Inconnu");
		trinucleotideCount = new JLabel("Inconnu");

		JPanel gInfosPanel = new JPanel(new BorderLayout());

		infospanel = new JPanel(new GridLayout(5, 2));
		infospanel.add(new JLabel("Chemin : "));
		infospanel.add(pathLabel);
		infospanel.add(new JLabel("Nombre de CDS : "));
		infospanel.add(CDSCount);
		infospanel.add(new JLabel("CDS non trait√© : "));
		infospanel.add(CDSFailed);
		infospanel.add(new JLabel("Nombre de dinucleotide : "));
		infospanel.add(dinucleotideCount);
		infospanel.add(new JLabel("Nombre de trinucleotide : "));
		infospanel.add(trinucleotideCount);

		gInfosPanel.add(infospanel, BorderLayout.NORTH);
		gInfosPanel.add(openButton, BorderLayout.PAGE_END);
		
		infosProcess = new JPanel(new BorderLayout());
		download = new JCheckBox("TÈlÈcharger les fichiers");
		
		infosProcess.add(download, BorderLayout.NORTH);
		infosProcess.add(start, BorderLayout.PAGE_END);

		rightpanel = new JPanel(new BorderLayout());
		rightpanel.add(gInfosPanel, BorderLayout.NORTH);
		rightpanel.add(scroll, BorderLayout.CENTER);
		rightpanel.add(infosProcess, BorderLayout.PAGE_END);

		split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrolltree, rightpanel);
		this.add(split);

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int i = JOptionPane.showConfirmDialog(null, "Etes vous sur ?");
				if (i == 0)
					System.exit(0);
			}
		});

		setTitle("BioInfo : Main");
		setSize(800, 600);
		setVisible(true);

		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(download.isSelected()){
					Configuration.STORE_DATA = true;
				}
				else {
					Configuration.STORE_DATA = false;
				}
				infosProcess.setVisible(false);
				rightpanel.remove(infosProcess);
				rightpanel.add(progress, BorderLayout.PAGE_END);
				progress.setVisible(true);
				startTime = System.currentTimeMillis();

				UIManager.launchProcess(MainFrame.this.model.getSelectedNodes());
			}
		});

	}

	public void log(String msg) {
		this.logger.insert(msg + "\n", this.logger.getText().length());
	}

	public void setProgress(double n, int cur, int max) {
		Double elapsedTime = (double) ((System.currentTimeMillis() - startTime) / 1000); // en secondes
		Double remainingTime = (elapsedTime / cur) * (max - cur); 
		String remainingText = "";

		if (remainingTime >= 3600 * 24) {
			remainingText += ((int) (remainingTime / (3600 * 24))) + "j ";
			remainingTime = remainingTime % (3600 * 24);
		}
		if (remainingTime >= 3600) {
			remainingText += ((int) (remainingTime / 3600)) + "h ";
			remainingTime = remainingTime % 3600;
		}
		if (remainingTime >= 60) {
			remainingText += ((int) (remainingTime / 60)) + "min ";
			remainingTime = remainingTime % 60;
		}
		if (remainingTime > 0) {
			remainingText += remainingTime.longValue() + "s ";
		}

		String str = Double.toString(n);
		str = str.substring(0, (str.length() >= 7 ? 7 : str.length()));
		this.progress.setString(str + "% (" + cur + " / " + max + ") - Temps restant : " + remainingText);
		this.progress.setValue((int) n);
	}

	public void setDone() {
		progress.setVisible(false);
		rightpanel.remove(progress);
		infosProcess.setVisible(true);
		rightpanel.add(infosProcess, BorderLayout.PAGE_END);
		this.pack();
	}

	public void setInfos(InfoNode infos) {
		if (infos == null) {
			this.pathLabel.setText("Inconnu");
			this.CDSCount.setText("Inconnu");
			this.CDSFailed.setText("Inconnu");
			this.dinucleotideCount.setText("Inconnu");
			this.trinucleotideCount.setText("Inconnu");
			openButton.setVisible(false);
			return;
		} else {
			String xls = FindExtension.check(infos.getRealPath(), ".xls");
			String bdd = FindExtension.check(infos.getRealPath(), ".bdd");
			if (xls != "") {
				openButton.addActionListener(new OpenFileListener(xls));
				openButton.setVisible(true);
			} else {
				openButton.setVisible(false);
			}

			if (bdd != "") {
				try {
					HashMap<String, Long> res = ExcelManager.getInfo(bdd.substring(0, bdd.length() - 4));
					this.pathLabel.setText(infos.getTreePath());
					this.CDSCount.setText(res.get("nb_cds").toString());
					this.CDSFailed.setText(res.get("cds_non_traites").toString());
					this.dinucleotideCount.setText(res.get("nb_dinucleotides").toString());
					this.trinucleotideCount.setText(res.get("nb_trinucleotides").toString());

				} catch (IOException e) {
					this.pathLabel.setText("Inconnu");
					this.CDSCount.setText("Inconnu");
					this.CDSFailed.setText("Inconnu");
					this.dinucleotideCount.setText("Inconnu");
					this.trinucleotideCount.setText("Inconnu");
				}
			} else {
				this.pathLabel.setText("Inconnu");
				this.CDSCount.setText("Inconnu");
				this.CDSFailed.setText("Inconnu");
				this.dinucleotideCount.setText("Inconnu");
				this.trinucleotideCount.setText("Inconnu");
			}
		}
	}

	public static void createJTree(Tree t, DefaultMutableTreeNode treeNode, ArrayList<String> path) {
		Object[] nodes = t.nodes();

		for (Object o : nodes) {

			String node = (String) o;
			ArrayList<String> new_path = new ArrayList<String>(path);
			new_path.add(node);

			if (t.isLeaf(node)) {
				treeNode.add(new DefaultMutableTreeNode(new InfoNode(node, new_path,
						t.get(node).getClass().equals(Organism.class) ? (Organism) t.get(node) : null)));
			} else {
				DefaultMutableTreeNode customNode = new DefaultMutableTreeNode(new InfoNode(node, new_path));
				treeNode.add(customNode);
				MainFrame.createJTree((Tree) t.get(node), customNode, new_path);
			}
		}
	}
}
