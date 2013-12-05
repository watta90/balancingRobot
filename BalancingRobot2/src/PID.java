// PID class to be written by you
public class PID {
	public static int STATE_NEW = 1;
	public static int STATE_OLD = 0;
	private int stateParams = STATE_OLD;

	// Current PID parameters
	private PIDParameters p;

	private double I; // Integrator state
	private double D; // Derivator state
	private double P;

	private double v; // Desired control signal
	private double e; // Current control error

	private double y_k;
	private double yk;

	// Constructor
	public PID() {
		PIDParameters p = new PIDParameters();
		p.Beta = 1.0;
		p.H = 0.01;
		p.integratorOn = true;
		p.K = 4;
		p.Ti = 0;
		p.Tr = 0;
		p.Td = 0.2;
		p.N = 10;
		setParameters(p);

		this.I = 0.0;
		this.D = 0.0;
		this.P = 0.0;
		this.v = 0.0;
		this.e = 0.0;
		this.yk = 0.0;
		this.y_k = 0.0;

	}

	public synchronized double[] calculateOutput(double y, double yref, float gyro) {
		yk = y;
		D = ((p.Td) / (p.Td + p.N * p.H)) * D
				- ((p.K * p.Td * p.N) / (p.Td + p.N * p.H)) * (yk - y_k);
		e = yref - y;
		P = p.K * (1 * yref - y);
		v = P + I + D;
		return new double[]{v, P, D};
	}

	// Updates the controller state.
	// Should use tracking-based anti-windup
	public synchronized void updateState(double u) {
		if (p.integratorOn && p.Ti!=0) {
			I = I + (p.K * p.H / p.Ti) * e + (p.H / p.Tr) * (u - v);
		} else {
			I = 0.0;
		}
		y_k = yk;
	}

	public synchronized void setYlast(double y) {
		this.y_k = y;
		// System.out.println(y_last);
	}

	// Returns the sampling interval expressed as a long.
	// Explicit type casting needed.
	public synchronized long getHMillis() {
		return (long) (p.H * 1000.0);
	}

	// Sets the PIDParameters.
	// Called from PIDGUI.
	// Must clone newParameters.
	public synchronized void setParameters(PIDParameters newParameters) {
		p = (PIDParameters) newParameters.clone();
		if (!p.integratorOn) {
			I = 0.0;
		}
		stateParams = STATE_NEW;
		notifyAll();
	}

	public synchronized void reset() {
		this.I = 0.0;
	}

	public synchronized PIDParameters getParameters() {
		return this.p;
	}

	public synchronized int getStateParams() {
		return stateParams;
	}

	public synchronized void setStateParams(int state) {
		stateParams = state;
		notifyAll();
	}

}
