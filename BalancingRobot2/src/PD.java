
public class PD {
	public static int STATE_NEW = 1;
	public static int STATE_OLD = 0;
	private float kP = 50;
	private float kD =  4;
	private int stateParams = STATE_OLD;
	
	
	public PD(){
		
	}
	
	public synchronized void setParameters(float[] params){
		kP = params[0];
		kD = params[1];
		stateParams = STATE_NEW;
		notifyAll();
	}
	
	public synchronized float calculateOutput(float angle, float angleVelocity){
		float u = (kP*angle + (kD*kP*(angleVelocity - 0)));
		return u;
		
	}
	
	public synchronized int getStateParams(){
		return stateParams;
	}
	
	public synchronized void setStateParams(int state){
		stateParams = state;
		notifyAll();
	}

	public float[] getParams() {
		return new float[]{kP, kD};
	}

}
