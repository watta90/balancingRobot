public class Statefeedback {
	public static int STATE_NEW = 1;
	public static int STATE_OLD = 0;
	private int stateParams = STATE_OLD;
	double l1;
	double l2;
	double x1;// angle
	double x01;
	double x2;// angular velocity
	double x02;
	double a11, a12, a21, a22;
	double b1, b2;
	double c1, c2;
	double u;
	double k= 0.12;

	public Statefeedback() {
		this.l1 = 9500;//362.1856; //102; //102.7913; // 5.0319;102
		this.l2 = 2;//3.1528; //20;// 2.3452; //0.4837;
		this.x1 = 0;
		this.x01 = 0;
		this.x2 = 0; // h=0.05
		this.x02 = 0;
		this.a11 = 0;
		this.a12 = 1;
		this.a21 = 49.1;
		this.a22 = 0;
		b1 = 0;
		b2 = 49.1;//98.21;
		c1 = 1;
		c2 = 0;
		u = 0;
	}

	public synchronized double [] calc(double angle, double newRef) {
		//x1 = vinkel;//(float) ((vinkel/180)*Math.PI);
		//x2 = vinkelhastighet;//(float) ((vinkelhastighet/180)*Math.PI);
		angle = angle* 0.0175;
		x2 = (x1-angle)/0.01;
		x1 = angle;
		x01 = newRef;
		double tempx1 = l1 * (x1-x01)*k;
		double tempx2 =  l2 * x2 * k;
		//u = -(l1 * (x1-x01) + l2 * x2)*k;
		u = -(tempx1 + tempx2);
		
		double []res={u,tempx1,tempx2};
		return res;
	}

	public synchronized void updateState() {//not used
		x1 = x2;
		x2 = a21 * x1 + b2 * u;
	}
	public synchronized void updateState(double angle,double vel){
		this.x1=angle;
		this.x2=vel;
	}
	public synchronized void setParameter(double l1, double l2, double k) {
		this.l1 = l1;
		this.l2 = l2;
		this.k = k;
		stateParams = STATE_NEW;
		notifyAll();
	}
//	public float getAngle(){
//		return x1;
//	}
//	public float getAngularVelocity(){
//		return x2;
//	}
//	
	public synchronized int getStateParams(){
		return stateParams;
	}
	
	public synchronized void setStateParams(int state){
		stateParams = state;
		notifyAll();
	}

	public synchronized void reset() {
		x1=0;
		x2=0;
		
	}
}