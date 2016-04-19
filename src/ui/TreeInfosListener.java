package ui;

import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

public class TreeInfosListener implements TreeSelectionListener {
	private JTree tree;
	private JTextArea infosFile;
	private JButton openButton;

	public TreeInfosListener(JTree tree, JTextArea infosFile, JButton openButton) {
		this.tree = tree;
		this.infosFile = infosFile;
		this.openButton = openButton;
	}
	
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (node == null) {
        	infosFile.setText("Pas d'information disponible");
        	openButton.setVisible(false);
        	return;
        }
        else {
        	InfoNode nodeInfo = (InfoNode) node.getUserObject();
        	String file = FindExtension.check(nodeInfo.getRealPath(), ".xls");
        	if (file != "") {
        		openButton.addActionListener(new OpenFileListener(file));
        		openButton.setVisible(true);
        	} else {
        		infosFile.setText("Pas d'information disponible");
        		openButton.setVisible(false);
        	}
        }
		
	}

}
