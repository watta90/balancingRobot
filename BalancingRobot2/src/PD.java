
public class PD {
	   
	public static int STATE_NEW = 1;
	public static int STATE_OLD = 0;
	private float kP = 16;
	private float kD =  (float) 1.8;
	private float kI = (float) 0.5;
	private float angle = 0;
	private float angleVelocity = 0;
	private float prevError = 0;
	private int stateParams = STATE_OLD;
	
	
	public PD(){
		
	}
	
	public synchronized void setParameters(float[] params){
		kP = params[0];
		kD = params[1];
		stateParams = STATE_NEW;
		notifyAll();
	}
	
	public synchronized float[] calculateOutput(){
		float error = 0-angleVelocity;
		float u = (kP*(angle-0) + (angle-0)*kI +  (kD*(error-prevError)));
		//prevError = error;
		/*if(angle>5){
			u = 720;
		} else if(angle<-5){
			u = -720;
		}*/
		return new float[]{u, angle, angleVelocity};
		
	}
	
	public synchronized int getStateParams(){
		return stateParams;
	}
	
	public synchronized void setStateParams(int state){
		stateParams = state;
		notifyAll();
	}

	public synchronized float[] getParams() {
		return new float[]{kP, kD};
	}

	public synchronized void updateState(float angle, float f) {
		this.angle = angle;
		this.angleVelocity = f;
		notifyAll();
		
	}

}
