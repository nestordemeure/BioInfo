import ui.UIManager;


public class Main {
	public static void main(String[] args) {
		UIManager.log("Hello World");
		UIManager.setProgress(0.);
		UIManager.setProgress(50.2);
		UIManager.setProgress(100);
		UIManager.log("DONE !");
	}

}
