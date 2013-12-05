

import lejos.nxt.*;
import lejos.nxt.addon.GyroSensor;
import lejos.nxt.addon.AccelHTSensor;

public class Regulator extends Thread{
	private BluetoothMonitor blMon;
	
	private GyroSensor gyroSensor;
	private AccelHTSensor accelerometer;
	private NXTMotor m1;
	private NXTMotor m2;
	
	private PD pd;
	private Statefeedback sf;
	private PID pid;
	private ReferenceGenerator refGen;
	
	public final static int h = 10;
	
	private float angle = 0;
	private float gyroValue;
	private float x_acc_rate = 0;
	
	private double oldU = 0;
	private double oldSpeed = 0;
	
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
		m1 = new NXTMotor(MotorPort.A);
		m2 = new NXTMotor(MotorPort.C);
		
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
			int sign = 1;
			if(oldU<0){
				sign = -1;
			}
			double speed = 0.98*oldSpeed + 0.2*m1.getPower();
			float newRef = 0;
			/*if(speed>pid.getParameters().Ti){
				//newRef = ((float)speed)*((float)pid.getParameters().K);
				newRef = (float) (sign*pid.getParameters().K);
			}*/
			
			
			float[] sfValues = sf.calc(angle, newRef);
			//double[] pidValues = pid.calculateOutput(angle, refGen.getRef(), gyro);
			double u = (double)sfValues[0];
			
			
			if(u>100){
				u=100;
			} else if(u<-100){
				u=-100;
			}
			if(sfValues[1]>100){
				sfValues[1]=100;
			} else if(sfValues[1]<-100){
				sfValues[1]=-100;
			}
			if(sfValues[2]>100){
				sfValues[2]=100;
			} else if(sfValues[2]<-100){
				sfValues[2]=-100;
			}
			
			/*float []res = pd.calculateOutput();
			float u=res[0];
			float angle=res[1];
			float gyroRate=res[2];*/

			controllMotor((float)u);
			//controllMotor((float)pid.getParameters().N);
			//float[] params = pd.getParams();
			
			printOnNXT(angle, gyro, m1.getPower());
			
			pid.updateState(u);
			
			if(graphTime>20){
				//blMon.sendData((int)angle, (int)refGen.getRef(),  (int)u);
				blMon.sendData((int)sfValues[1], (int)sfValues[2], (int)u);
				graphTime = 0;
			}
			graphTime++;
			
			
			if(System.currentTimeMillis()-refGen.getLastUpdate()>1000){
				refGen.setRef(ReferenceGenerator.StableAngel);
			}
			
			oldU = u;
			oldSpeed = speed;
			
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
		PIDParameters p = pid.getParameters();
		printOnNXT((float)p.K, (float)p.Ti, p.Td);
		
		try {
			sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t = System.currentTimeMillis();
	}


	private void  printOnNXT(float tempAngle, float x_acc_rate, double tempAG) {
		LCD.clear();
		LCD.drawString(""+tempAngle, 0, 0);
		LCD.drawString(""+x_acc_rate, 0, 3);
		
		//LCD.drawInt((int)tempAG, 0, 6);
		LCD.drawString(""+tempAG, 0, 6);
		
	}


	private void controllMotor(float u) {
		int power = (int)Math.abs(u);
		int offset = 3; //(int)(pid.getParameters()).Tr;
		power = offset + power;
		if((oldU<0 && u>0) || oldU>0 && u<0){
			power += (int)pid.getParameters().Beta;
		}
		
		m1.setPower(power);
		m2.setPower(power);
		if(u<0){
			//m1.setPower(power);
			//m2.setPower(0);
			//m2.flt();
			m1.forward();
			m2.forward();
			//m2.backward();
			/*Motor.A.setSpeed(u);
			Motor.C.setSpeed(u);
			Motor.A.forward();
			Motor.C.forward();*/
			
		} else {
			//m1.forward();
			m1.backward();
			m2.backward();
			//m1.setPower(0);
			//m2.flt();
			//m1.setPower(power);
			
					
					
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
