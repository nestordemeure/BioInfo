package ui;

public class UIManager {
	
	private static UIConsole console;
	
	private static void check(){
		if(UIManager.console == null){
			UIManager.console = new UIConsole(); 
		}
	}
	
	public static void log(String message){
		UIManager.check();
		UIManager.console.log(message);
	}
	
	public static void setProgress(double progress){
		UIManager.check();
		UIManager.console.setProgress(progress);
		
	}

}
