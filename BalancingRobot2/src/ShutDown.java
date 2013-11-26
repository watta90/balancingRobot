import lejos.nxt.Button;


public class ShutDown extends Thread{
	public ShutDown(){
		
	}
	
	public void run(){
		Button.waitForAnyPress();
		System.exit(0);
	}
}
