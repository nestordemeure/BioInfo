package ui;

public class UIConsole {

	public void log(String str){
		System.out.println(str);
	}
	
	public void setProgress(double progress){
		System.out.print("Progress : |");
		for(int i = 0; i < 100; i++){
			if(i <= progress){
				System.out.print("#");
			} else {
				System.out.print(" ");
			}
		}
		System.out.println("| "+progress+"%");
	}

}
