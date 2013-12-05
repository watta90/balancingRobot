public class Statefeedback {
	public static int STATE_NEW = 1;
	public static int STATE_OLD = 0;
	private int stateParams = STATE_OLD;
	float l1;
	float l2;
	float x1;// angle
	float x01;
	float x2;// angular velocity
	float x02;
	float a11, a12, a21, a22;
	float b1, b2;
	float c1, c2;
	float u;
	float k=(float)0.12;

	public Statefeedback() {
		this.l1 = (float)  9500;//362.1856; //102; //102.7913; // 5.0319;102
		this.l2 = (float) 2;//3.1528; //20;// 2.3452; //0.4837;
		this.x1 = 0;
		this.x01 = 0;
		this.x2 = 0; // h=0.05
		this.x02 = 0;
		this.a11 = 0;
		this.a12 = 1;
		this.a21 = (float) 49.1;
		this.a22 = 0;
		b1 = 0;
		b2 = (float) 49.1;//98.21;
		c1 = 1;
		c2 = 0;
		u = 0;
	}

	public synchronized float [] calc(float angle, float ref) {
		//x1 = vinkel;//(float) ((vinkel/180)*Math.PI);
		//x2 = vinkelhastighet;//(float) ((vinkelhastighet/180)*Math.PI);
		float tempX1 = (float) (x1*0.0175);
		float tempX2 = (float) (x2*0.0175);
		angle = angle*((float)0.0175);
		x2 = (x1-angle)/((float)0.01);
		x1 = angle;
		x01 = ref;
		float tempx1 = l1 * (x1-x01)*k;
		float tempx2 =  l2 * x2 * k;
		//u = -(l1 * (x1-x01) + l2 * x2)*k;
		u = -(tempx1 + tempx2);
		if(x1>30){
			u = -720;
		} else if(x1<-30){
			u = 720;
		}
		if(u>720){
			u = 720;
		} else if(u<-720){
			u = -720; 
		}
		float []res={u,tempx1,tempx2};
		return res;
	}

	public synchronized void updateState() {//not used
		x1 = x2;
		x2 = a21 * x1 + b2 * u;
	}
	public synchronized void updateState(float angle,float vel){
		this.x1=angle;
		this.x2=vel;
	}
	public synchronized void setParameter(float value, float l2, float k) {
		this.l1 = value;
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