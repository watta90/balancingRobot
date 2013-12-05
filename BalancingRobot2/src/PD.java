
public class PD {
	   
	public static int STATE_NEW = 1;
	public static int STATE_OLD = 0;
	private double kP = 16;
	private double kD =  1.8;
	private double kI = 0.5;
	private double angle = 0;
	private double angleVelocity = 0;
	private double prevError = 0;
	private int stateParams = STATE_OLD;
	
	
	public PD(){
		
	}
	
	public synchronized void setParameters(double[] params){
		kP = params[0];
		kD = params[1];
		stateParams = STATE_NEW;
		notifyAll();
	}
	
	public synchronized double[] calculateOutput(){
		double error = 0-angleVelocity;
		double u = (kP*(angle-0) + (angle-0)*kI +  (kD*(error-prevError)));
		//prevError = error;
		/*if(angle>5){
			u = 720;
		} else if(angle<-5){
			u = -720;
		}*/
		return new double[]{u, angle, angleVelocity};
		
	}
	
	public synchronized int getStateParams(){
		return stateParams;
	}
	
	public synchronized void setStateParams(int state){
		stateParams = state;
		notifyAll();
	}

	public synchronized double[] getParams() {
		return new double[]{kP, kD};
	}

	public synchronized void updateState(double angle, double f) {
		this.angle = angle;
		this.angleVelocity = f;
		notifyAll();
		
	}

}
