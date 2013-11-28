

import lejos.nxt.*;
import lejos.nxt.addon.GyroSensor;
import lejos.nxt.addon.AccelHTSensor;

public class Regulator extends Thread{
	BluetoothMonitor blMon;
	GyroSensor gyroSensor;
	AccelHTSensor accelerometer;
	PD pd;
	Statefeedback sf;
	ReferenceGenerator refGen;
	public final static int h = 50;
	public static int gyroOffset = 597;
	float T= (float) 0.001;
	float z = 0;
	float oldY = 0;
	float angle = 0;
	//int gyroValue;
	float gyroValue;
	float gyroRate = 0;
	float x_acc_rate = 0;
	int sampTime;
	long t;
	
	public Regulator(BluetoothMonitor blMon, PD pd, ReferenceGenerator refGen, Statefeedback sf){
		this.blMon = blMon;
		this.pd = pd;
		this.refGen = refGen;
		this.sf = sf;
		gyroSensor = new GyroSensor(SensorPort.S1);
		accelerometer = new AccelHTSensor(SensorPort.S2);
		this.setPriority(Thread.MAX_PRIORITY);
		sampTime = 0;
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
		calibrate();
		t = System.currentTimeMillis();
		while(!interrupted()){
			//gyroValue = gyroSensor.readValue();
			gyroValue = gyroSensor.getAngularVelocity();
			int x_acc = accelerometer.getXAccel();

			x_acc_rate = (((float)x_acc) * ((float)0.491));
			//y = lowPass(y);
			gyroRate = gyroValue;//(float) ((gyroValue - gyroOffset) * 1);
			
			//angle += gyroRate * ((float)h/1000);
			//float tempAG = angle + gyroValue *  ((float)h/1000);
			angle = (float) ((0.9074)*(angle + (gyroRate *  (((float)h)/1000))) + (0.0926)*(x_acc_rate));
			//float tempAngle = (float) (angle*0.0077) ;
			float u = sf.calc(angle, gyroRate);//pd.calculateOutput(tempAngle, y);

			if(u>720){
				u = 720;
			} else if(u<-720){
				u = -720; 
			}
			
			//float[] params = pd.getParams();
			printOnNXT(angle, gyroRate, angle);
			
			controllMotor(u);
			
			if(sampTime>4){
				blMon.sendData((int)angle, (int)refGen.getRef(), (int)u);
				sampTime = 0;
			}
			sampTime++;
			
			
			if(System.currentTimeMillis()-refGen.getLastUpdate()>1000){
				refGen.setRef(ReferenceGenerator.StableAngel);
			}
			
			if(pd.getStateParams()==PD.STATE_NEW || sf.getStateParams()==Statefeedback.STATE_NEW){
				reset();
			} else {
				oldY = gyroRate;
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


	private void calibrate() {
		/*
		int sum = 0;
		int s = 200;
		for(int i=0; i<s; i++){
			sum += gyroSensor.readValue();
		}
		gyroOffset = sum/s;
		*/
		gyroSensor.recalibrateOffset();
		
	}


	private void reset() {
		Sound.beep();
		controllMotor(0);
		angle = 0;
		gyroRate = 0;
		x_acc_rate = 0;
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
