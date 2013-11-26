package computer;
import se.lth.control.DoublePoint;



public class Controller extends Thread{
	private Opcom opcom;
	private BluetoothMonitor blMon;

    private boolean doIt = true;


    /** Constructor. Sets initial values of the controller parameters and initial mode. */
    public Controller(Opcom opcom, BluetoothMonitor blMon) {

		  this.opcom = opcom;
		  this.blMon = blMon;

    }

    /** Run method. Sends data periodically to Opcom. */
    public void run() {
		  final long h = 25; // period (ms)
		  long duration;
		  long t = System.currentTimeMillis();
		  DoublePoint dp;
		  PlotData pd;
		  double vel = 0, pos = 0, ctrl = 0;
		  double realTime = 0;


		  setPriority(7);

		  while (doIt) {
				try {
					int[] values = blMon.getReceivedData();
					 vel = values[0];
					 pos = values[1]; //Hämta senare från blMon
					 ctrl = values[2]; //Hämta senare från blMon
				} catch (Exception e) {
					 System.out.println(e);
				} 

				pd = new PlotData();
				pd.y = vel;
				pd.ref = pos;
				pd.x = realTime;
				opcom.putMeasurementDataPoint(pd);
	    
				dp = new DoublePoint(realTime,ctrl);
				opcom.putControlDataPoint(dp);

				realTime += ((double) h)/1000.0;

				/*t += h;
				duration = (int) (t - System.currentTimeMillis());
				if (duration > 0) {
					 try {
						  sleep(duration);
					 } catch (Exception e) {}
				}*/
		  }
    }

    /** Stops the thread. */
    private void stopThread() {
		  doIt = false;
    }

    /** Called by Opcom when the Stop button is pressed. */
    public synchronized void shutDown() {
		  stopThread();
    } 
}
