package ui;

public class UIGraphics {
	private PreLoaderFrame plFrame;
	private MainFrame mFrame;
	
	public UIGraphics(){
		this.plFrame = null;
		this.mFrame = null;
	}
	
	public void startPreloader(){
		this.plFrame = new PreLoaderFrame();
	}
	
	public void startMainProcess(){
		if(this.plFrame != null){
			this.plFrame.setVisible(false);
			this.plFrame.dispose();
			this.plFrame = null;
		}
		this.mFrame = new MainFrame();
	}
	
	public void log(String str){
		if(this.plFrame != null){
			this.plFrame.log(str);
		} else if(this.mFrame != null){
			this.mFrame.log(str);
		}
	}
	
	public void setProgress(double n){
		if(this.mFrame != null){
			this.mFrame.setProgress((int)n);
		}
	}
}
