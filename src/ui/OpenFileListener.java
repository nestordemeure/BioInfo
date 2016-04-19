package ui;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class OpenFileListener implements ActionListener {
    private String path;

    public OpenFileListener(String path) {
        this.path = path;
    }

    public void actionPerformed(ActionEvent e) {
		Desktop dt = Desktop.getDesktop();
		try {
			dt.open(new File(path));
		} catch (IOException e1) {
			System.out.println("Impossible d'ouvrir le fichier");
		}
    }
}