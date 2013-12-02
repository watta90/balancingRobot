

import lejos.nxt.*;
import lejos.nxt.addon.GyroSensor;
import lejos.nxt.addon.AccelHTSensor;

public class Regulator extends Thread{
	private BluetoothMonitor blMon;
	
	private GyroSensor gyroSensor;
	private AccelHTSensor accelerometer;
	
	private PD pd;
	private Statefeedback sf;
	private PID pid;
	private ReferenceGenerator refGen;
	
	public final static int h = 10;
	
	private float angle = 0;
	private float gyroValue;
	private float x_acc_rate = 0;
	
	private int x_acc_offset=0;
	private float x_acc_scale=(float) 90/200;//90/220
	private int gyro_offset=0; //Not needed
	private float gyro_scale=(float) 1;
	
	float T= (float) 0.001;
	float z = 0;
	//float oldY = 0;
	int graphTime;
	long t;
	
	public Regulator(BluetoothMonitor blMon, PD pd, ReferenceGenerator refGen, Statefeedback sf, PID pid){
		this.blMon = blMon;
		this.pd = pd;
		this.refGen = refGen;
		this.sf = sf;
		this.pid = pid;
		gyroSensor = new GyroSensor(SensorPort.S1);
		accelerometer = new AccelHTSensor(SensorPort.S2);
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
		calibrate();
		t = System.currentTimeMillis();  
		while(!interrupted()){
			gyroValue = gyroSensor.getAngularVelocity(); 
			x_acc_rate = accelerometer.getXAccel();
			
			float x_accel=(float) (x_acc_rate-x_acc_offset)*x_acc_scale; //output is angle in radians.
			float gyro=(float)(gyroValue-gyro_offset)*gyro_scale; //output is angularvel. in radians.
			angle = (float) ((float) (0.98)*(angle+gyro*0.01)+(0.02)*(x_accel));
			
			
			float u = (float) pid.calculateOutput(angle, refGen.getRef());
			
			/*float []res = pd.calculateOutput();
			float u=res[0];
			float angle=res[1];
			float gyroRate=res[2];*/

			controllMotor(u);
			//float[] params = pd.getParams();
			printOnNXT(angle, gyro, u);
			
			pid.updateState(u);
			
			if(graphTime>20){
				blMon.sendData((int)angle, (int)refGen.getRef(), (int)u);
				graphTime = 0;
			}
			graphTime++;
			
			
			if(System.currentTimeMillis()-refGen.getLastUpdate()>1000){
				refGen.setRef(ReferenceGenerator.StableAngel);
			}
			
			if(pid.getStateParams()==PID.STATE_NEW  || pd.getStateParams()==PD.STATE_NEW || sf.getStateParams()==Statefeedback.STATE_NEW){
				reset();
			} else {
				//oldY = gyroRate;
				t = t + h;
				long duration = t - System.currentTimeMillis();
				if (duration > 0) {
					try {
						sleep(duration);
						//duration = t - System.currentTimeMillis();
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
		//angle = 0;
		//gyroRate = 0;
		//x_acc_rate = 0;
		sf.reset();
		pid.reset();
		pid.setStateParams(PID.STATE_OLD);
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
			Motor.A.backward();
			Motor.C.backward();
		} else {
			Motor.A.setSpeed(u);
			Motor.C.setSpeed(u);
			Motor.A.forward();
			Motor.C.forward();			
					
		}
		
	}
	
	private void calibrate() {
		gyroSensor.recalibrateOffset();
		int sum = 0;
		int s = 400;
		for(int i=0; i<s; i++){
			sum += accelerometer.getXAccel();
		}
		x_acc_offset=sum/s;
	}

}
