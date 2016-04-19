package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import configuration.Configuration;

public class FileChooserDialog extends JDialog {
	private JFileChooser fileChooser;
	private JDialog dialog;
	private MainFrame mainFrame;
	private JLabel title;
	
	public FileChooserDialog(MainFrame mainFrame) {
		super();
		this.mainFrame = mainFrame;
		
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		dialog = this;
		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				JFileChooser theFileChooser = (JFileChooser) actionEvent.getSource();
                String command = actionEvent.getActionCommand();
                if (command.equals(JFileChooser.APPROVE_SELECTION)) {
                    File selectedFile = theFileChooser.getSelectedFile();
                    String path = selectedFile.getAbsolutePath();
                    
                    File f = new File(path);
                    if(f.exists() && f.isDirectory()) {
                    	if(! path.endsWith(Configuration.FOLDER_SEPARATOR)){
                    		path = path + Configuration.FOLDER_SEPARATOR;
                    	}
                    	Configuration.BASE_FOLDER = path;
                    	dialog.dispose();
                    }
                    else {
                    	InfoMessage.infoBox("Vous devez choisir un répertoire valide", "Erreur");
                    }
                    
                } else if (command.equals(JFileChooser.CANCEL_SELECTION)) {
                	InfoMessage.infoBox("Vous devez choisir un répertoire", "Erreur");
                }				
			}
        };
		
        fileChooser.addActionListener(actionListener);
		this.setModal(true);
		this.setLayout(new BorderLayout());
		
		//title = new JLabel("Veuillez choisir un répertoire pour stocker les résultats");
		
		this.add(fileChooser);
		
		this.setSize(500,500);
		this.setLocationRelativeTo(this.mainFrame);
		this.setVisible(true);
	}
}
