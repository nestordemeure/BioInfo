import java.util.ArrayList;

import configuration.Configuration;

import excel.ExcelManager;
import manager.ThreadManager;
import tree.*;
import ui.UIManager;

public class Main {

	public static void main(String[] args){
		UIManager.startPreloading();
		Tree plop = new Tree();
		plop=TreeManager.constree();
		UIManager.setMaxProgress(plop.size());
		UIManager.startMainProcess();
		ThreadManager.start(plop, new ArrayList<String>());
		UIManager.log("Creating excel files...");
		ExcelManager.fusionExcels(Configuration.BASE_FOLDER);

	}

}
