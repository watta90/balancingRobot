

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
	
	private double angle = 0;
	private double gyroValue;
	private double x_acc_rate = 0;
	
	private double oldU = 0;
	private double oldSpeed = 0;
	
	private int x_acc_offset=0;
	private double x_acc_scale= (90/200);//90/220
	private int gyro_offset=0; //Not needed
	private double gyro_scale= 1;
	
	double T= 0.001;
	double z = 0;
	int graphTime;
	long t;
	long dt;
	
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
	
	public void run(){
		calibrate();
		t = System.currentTimeMillis();  
		dt = System.currentTimeMillis();
		while(!interrupted()){
			gyroValue = gyroSensor.getAngularVelocity(); 
			x_acc_rate = accelerometer.getXAccel();
			double z_acc_rate = accelerometer.getZAccel();
			double theta = Math.atan2(x_acc_rate, z_acc_rate)*180/Math.PI;
			
			double x_accel= (x_acc_rate-x_acc_offset)*x_acc_scale; //output is angle in degrees.
			double gyro=(gyroValue-gyro_offset)*gyro_scale; //output is angularvel. in degrees.
			dt = System.currentTimeMillis() - dt;
			angle = 0.98*(angle + gyro*0.01) + (0.02*theta);
			dt = System.currentTimeMillis();
			int sign = 1;
			if(oldU<0){
				sign = -1;
			}
			double speed = 0.98*oldSpeed + 0.02*m1.getPower();
			double newRef = 0;
			/*if(speed>pid.getParameters().Ti){
				//newRef = ((float)speed)*((float)pid.getParameters().K);
				newRef = (float) (sign*pid.getParameters().K);
			}*/
			
			
			//double[] sfValues = sf.calc(angle, newRef);
			double[] pidValues = pid.calculateOutput(angle, refGen.getRef(), gyro);
			double u = pidValues[0];
			
			
			if(u>100){
				u=100;
			} else if(u<-100){
				u=-100;
			}
			if(pidValues[1]>100){
				pidValues[1]=100;
			} else if(pidValues[1]<-100){
				pidValues[1]=-100;
			}
			if(pidValues[2]>100){
				pidValues[2]=100;
			} else if(pidValues[2]<-100){
				pidValues[2]=-100;
			}
			
			
			/*float []res = pd.calculateOutput();
			float u=res[0];
			float angle=res[1];
			float gyroRate=res[2];*/

			controllMotor(u);
			//controllMotor((float)pid.getParameters().N);
			//float[] params = pd.getParams();
			
			//printOnNXT(angle, gyro, u);
			
			pid.updateState(u);
			
			if(graphTime>20){
				//blMon.sendData((int)angle, (int)refGen.getRef(),  (int)u);
				blMon.sendData((int)pidValues[1], (int)pidValues[2], (int)u);
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
			e.printStackTrace();
		}
		t = System.currentTimeMillis();
	}


	private void  printOnNXT(double tempAngle, double x_acc_rate, double tempAG) {
		LCD.clear();
		LCD.drawString(""+tempAngle, 0, 0);
		LCD.drawString(""+x_acc_rate, 0, 3);
		LCD.drawString(""+tempAG, 0, 6);
		
	}


	private void controllMotor(double u) {
		int power = (int)Math.abs(u);
		int offset = 3; //(int)(pid.getParameters()).Tr;
		power = offset + power;
		/*if((oldU<0 && u>0) || oldU>0 && u<0){
			power += (int)pid.getParameters().Beta;
		}*/
		
		m1.setPower(power);
		m2.setPower(power);
		if(u<0){
			m1.forward();
			m2.forward();			
		} else {
			m1.backward();
			m2.backward();
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
