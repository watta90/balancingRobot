
import java.io.*;

import lejos.nxt.*;
import lejos.nxt.comm.*;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		 NXTConnection connection = null;
		 connection = Bluetooth.waitForConnection();
		 LCD.drawChar('Y', 0,0);
		 
		 DataOutputStream dataOut = connection.openDataOutputStream();
		 DataInputStream dataIn = connection.openDataInputStream();
		 
		 ReferenceGenerator refGen = new ReferenceGenerator();
		 PD pd = new PD();
		 Statefeedback sf = new Statefeedback();
		 PID pid = new PID();
		 BluetoothMonitor blMon = new BluetoothMonitor();
		 BluetoothSender blSender = new BluetoothSender(dataOut, blMon);
		 BluetoothReceiver blReceiver = new BluetoothReceiver(dataIn, pd, refGen, sf, pid);
		 
		
		
		 Sensor sensor = new Sensor(blMon, pd, refGen, sf);
		 Regulator regul = new Regulator(blMon, pd, refGen, sf, pid);
		 ShutDown threadForShutDown = new ShutDown();
		 blSender.start();
		 blReceiver.start();
		 sensor.start();
		 regul.start();
		 threadForShutDown.start();

	}

}
