package computer;


import java.io.DataInputStream;
import java.io.IOException;
import java.util.Random;

public class BluetoothReceiver extends Thread {
	DataInputStream dataIn;
	BluetoothMonitor blMon;
	Random rand = new Random();

	public BluetoothReceiver(DataInputStream dataIn, BluetoothMonitor blMon) {
		this.dataIn = dataIn;
		this.blMon = blMon;
	}

	public void run() {
		int val = 0;
		while (!Thread.interrupted()) {
			try {
				int[] values = new int[3];
				values[0] = dataIn.readInt();
				values[1] = dataIn.readInt();
				values[2] = dataIn.readInt();
				//val = dataIn.readInt();
				blMon.newReceivedData(values);
				//System.out.println(values[0] + ", " + values[1] + ", " + values[2]);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
