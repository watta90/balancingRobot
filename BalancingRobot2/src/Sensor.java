
import lejos.nxt.*;
import lejos.nxt.addon.GyroSensor;
import lejos.nxt.addon.AccelHTSensor;

public class Sensor extends Thread{
	private GyroSensor gyroSensor;
	private AccelHTSensor accelerometer;
	private Statefeedback sf;
	public final static int h = 10; //period T=0.01s
	
	private float angle = 0;
	private float gyroValue;
	private float x_acc_rate = 0;
	
	private int x_acc_offset=0;
	private float x_acc_scale=(float) 90/200;//90/220
	private int gyro_offset=0; //Not needed
	private float gyro_scale=(float) 1;
	
	long t;
	
	public Sensor(BluetoothMonitor blMon, PD pd, ReferenceGenerator refGen, Statefeedback sf){
		this.sf = sf;
		gyroSensor = new GyroSensor(SensorPort.S1);
		accelerometer = new AccelHTSensor(SensorPort.S2);
		this.setPriority(Thread.MAX_PRIORITY);
	}
	
	public void run(){//tau=0.49
		calibrate();
		t = System.currentTimeMillis();
		while(!interrupted()){
			gyroValue = gyroSensor.getAngularVelocity(); 
			x_acc_rate = accelerometer.getXAccel();
			
			float x_accel=(float) (x_acc_rate-x_acc_offset)*x_acc_scale; //output is angle in radians.
			float gyro=(float)(gyroValue-gyro_offset)*gyro_scale; //output is angularvel. in radians.
			angle = (float) ((float) (0.98)*(angle+gyro*0.01)+(0.02)*(x_accel));
			
			sf.updateState(angle, gyro); 
			
			t = t + h;
			long duration = t - System.currentTimeMillis();
			while (duration > 0) {
				try {
					sleep(duration);
					duration = t - System.currentTimeMillis();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//t = System.currentTimeMillis();// Ska vi ha den här
			}
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
