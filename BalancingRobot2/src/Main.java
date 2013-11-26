
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
		 PD sf = new PD();
		 BluetoothMonitor blMon = new BluetoothMonitor();
		 BluetoothSender blSender = new BluetoothSender(dataOut, blMon);
		 BluetoothReceiver blReceiver = new BluetoothReceiver(dataIn, sf, refGen);
		 
		
		
		 
		 Regulator regul = new Regulator(blMon, sf, refGen);
		 ShutDown threadForShutDown = new ShutDown();
		 blSender.start();
		 blReceiver.start();
		 regul.start();
		 threadForShutDown.start();

	}

}
