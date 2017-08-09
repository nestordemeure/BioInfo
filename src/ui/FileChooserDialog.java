package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import configuration.Configuration;
import main.Main;

public class FileChooserDialog extends JDialog {
	private JDialog dialog;

	public FileChooserDialog(MainFrame mainFrame) {
		super();

		String preferencePath = this.getPreferencePath();
		JFileChooser fileChooser;
		if (preferencePath != null) {
			fileChooser = new JFileChooser(preferencePath);
		}
		else {
			fileChooser = new JFileChooser();
		}
		
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
                    	FileChooserDialog.setPreferencePath(path);
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
		this.setLocationRelativeTo(mainFrame);
		this.setVisible(true);
	}
	
	public static String getJarPath() {
		String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		try {
			String decodedPath = URLDecoder.decode(path, "UTF-8");
			return decodedPath;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String getPreferencePath() {		
		try {
			String decodedPath = FileChooserDialog.getJarPath();
			String str = null;
			
			File f = new File(decodedPath+"chosenPath.txt");
			if(f.exists() && !f.isDirectory()) { 
				BufferedReader in = null;
				try {
					in = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF8"));
				} catch (FileNotFoundException e2) {
					e2.printStackTrace();
				}

				try {
					str = in.readLine();
					System.out.println(str);	
				} catch (IOException e1) {
					e1.printStackTrace();
					return null;
				}

                try {
					in.close();
					return str;
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
			return str;

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void setPreferencePath(String newPath) {
		String decodedPath = FileChooserDialog.getJarPath();
		try {
			FileOutputStream output = new FileOutputStream(decodedPath+"chosenPath.txt", false);
			byte[] b= newPath.getBytes();
            try {
				output.write(b);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
            try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
