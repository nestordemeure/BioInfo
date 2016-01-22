import manager.AccessManager;
import ui.UIManager;


public class Main {
	public static void main(String[] args) {
		testAccessManager();
	}
	
	public static void testAccessManager(){
		(new Thread() {
			public void run() {
				AccessManager.accessFile("test.txt");
				UIManager.log("TH1 : Lock");

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				AccessManager.doneWithFile("test.txt");
				UIManager.log("TH1 : Unlock");
			}
		}).start();
		(new Thread() {
			public void run() {
				AccessManager.accessFile("test2.txt");
				UIManager.log("TH2 : Lock");

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				AccessManager.doneWithFile("test2.txt");
				UIManager.log("TH2 : Unlock");

			}
		}).start();
	}

}
