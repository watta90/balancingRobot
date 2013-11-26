

import lejos.nxt.*;
import lejos.nxt.addon.GyroSensor;
import lejos.nxt.addon.AccelHTSensor;

public class Regulator extends Thread{
	BluetoothMonitor blMon;
	GyroSensor gyroSensor;
	AccelHTSensor accelerometer;
	PD sf;
	ReferenceGenerator refGen;
	public final static int h = 50;
	public final static int gyroOffset = 597;
	float T= (float) 0.001;
	float z = 0;
	float oldY = 0;
	float angle = 0;
	float gyroRate = 0;
	float x_acc_rate = 0;
	long t;
	
	public Regulator(BluetoothMonitor blMon, PD sf, ReferenceGenerator refGen){
		this.blMon = blMon;
		this.sf = sf;
		this.refGen = refGen;
		gyroSensor = new GyroSensor(SensorPort.S1);
		accelerometer = new AccelHTSensor(SensorPort.S2);
		this.setPriority(Thread.MAX_PRIORITY);
	}
	
	
	private int lowPass(int y){
		try {
			float s = 2/h * ((y*y)-(oldY*oldY))/(y + oldY);
			return (int) (y*(1/((s*T) + 1)));
		} catch(Exception e){
			return 0;
		}
	}
	
	public void run(){
		t = System.currentTimeMillis();
		while(!interrupted()){
			int y = gyroSensor.readValue();
			int x_acc = accelerometer.getXAccel();
			x_acc_rate = (float) ((float)(x_acc - 0) * (180/Math.PI));
			y = lowPass(y);
			gyroRate = (float) ((y - gyroOffset) * 1);
			
			//angle += gyroRate * ((float)h/1000);
			float tempAG = angle + gyroRate *  ((float)h/1000);
			angle = (float) ((0.9375)*(angle + gyroRate *  ((float)h/1000)) + (0.0625)*(x_acc_rate));
			float tempAngle = (float) (angle*0.0077) ;
			float u = sf.calculateOutput(tempAngle, y);

			if(u>720){
				u = 720;
			} else if(u<-720){
				u = -720; 
			}
			
			float[] params = sf.getParams();
			printOnNXT(tempAngle, refGen.getRef(), u);
			
			controllMotor(u);
			
			blMon.sendData((int)tempAngle, (int)refGen.getRef(), (int)u);
			
			if(System.currentTimeMillis()-refGen.getLastUpdate()>1000){
				refGen.setRef(ReferenceGenerator.StableAngel);
			}
			
			if(sf.getStateParams()==PD.STATE_NEW){
				reset();
			} else {
				oldY = y;
				t = t + h;
				long duration = t - System.currentTimeMillis();
				if (duration > 0) {
					try {
						sleep(duration);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}
		}
	}


	private void reset() {
		Sound.beep();
		controllMotor(0);
		angle = 0;
		gyroRate = 0;
		x_acc_rate = 0;
		sf.setStateParams(PD.STATE_OLD);
		try {
			sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t = System.currentTimeMillis();
	}


	private void printOnNXT(float tempAngle, float x_acc_rate, float tempAG) {
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
			Motor.A.backward();
			Motor.C.backward();
		} else {
			Motor.A.setSpeed(u);
			Motor.C.setSpeed(u);
			Motor.A.forward();
			Motor.C.forward();
		}
		
	}

}
