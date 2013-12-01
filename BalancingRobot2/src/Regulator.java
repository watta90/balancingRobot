

import lejos.nxt.*;
import lejos.nxt.addon.GyroSensor;
import lejos.nxt.addon.AccelHTSensor;

public class Regulator extends Thread{
	private BluetoothMonitor blMon;
	
	private PD pd;
	private Statefeedback sf;
	private ReferenceGenerator refGen;
	
	public final static int h = 50;
	
	float T= (float) 0.001;
	float z = 0;
	float oldY = 0;
	int graphTime;
	long t;
	
	public Regulator(BluetoothMonitor blMon, PD pd, ReferenceGenerator refGen, Statefeedback sf){
		this.blMon = blMon;
		this.pd = pd;
		this.refGen = refGen;
		this.sf = sf;
		this.setPriority(Thread.MAX_PRIORITY);
		graphTime = 0;
	}
	
	
//	private int lowPass(int y){
//		try {
//			float s = 2/h * ((y*y)-(oldY*oldY))/(y + oldY);
//			return (int) (y*(1/((s*T) + 1)));
//		} catch(Exception e){
//			return 0;
//		}
//	}
	
	public void run(){
		t = System.currentTimeMillis();
		while(!interrupted()){
			
			float []res = sf.calc();//pd.calculateOutput(tempAngle, y);
			float u=res[0];
			float angle=res[1];
			float gyroRate=res[2];

			controllMotor(u);
			//float[] params = pd.getParams();
			printOnNXT(angle, gyroRate, u);
			
			if(graphTime>4){
				blMon.sendData((int)angle, (int)refGen.getRef(), (int)u);
				graphTime = 0;
			}
			graphTime++;
			
			
			if(System.currentTimeMillis()-refGen.getLastUpdate()>1000){
				refGen.setRef(ReferenceGenerator.StableAngel);
			}
			
			if(pd.getStateParams()==PD.STATE_NEW || sf.getStateParams()==Statefeedback.STATE_NEW){
				reset();
			} else {
				oldY = gyroRate;
				t = t + h;
				long duration = t - System.currentTimeMillis();
				while (duration > 0) {
					try {
						sleep(duration);
						duration = t - System.currentTimeMillis();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}
			
			//behöver vi spara undan nytt t?
		}
	}

	private void reset() {
		Sound.beep();
		controllMotor(0);
		//angle = 0;
		//gyroRate = 0;
		//x_acc_rate = 0;
		sf.reset();
		
		pd.setStateParams(PD.STATE_OLD);
		sf.setStateParams(Statefeedback.STATE_OLD);
		try {
			sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t = System.currentTimeMillis();
	}


	private void  printOnNXT(float tempAngle, float x_acc_rate, float tempAG) {
		LCD.clear();
		LCD.drawInt((int)tempAngle, 0, 0);
		LCD.drawInt((int)x_acc_rate, 0, 3);
		
		LCD.drawInt((int)tempAG, 0, 6);
		
	}


	private void controllMotor(float u) {
		if(u<0){
			u = u*-1;
			Motor.A.setSpeed(u);
			Motor.C.setSpeed(u);
			Motor.A.forward();
			Motor.C.forward();	
		} else {
			Motor.A.setSpeed(u);
			Motor.C.setSpeed(u);
			Motor.A.backward();
			Motor.C.backward();
					
		}
		
	}

}
