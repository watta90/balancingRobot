

import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.LCD;

public class BluetoothSender extends Thread {
	DataOutputStream dataOut;
	BluetoothMonitor blMon;

	public BluetoothSender(DataOutputStream dataOut, BluetoothMonitor blMon) {
		this.dataOut = dataOut;
		this.blMon = blMon;
	}

	public void run() {
		while (!Thread.interrupted()) {
			try {
				int[] d = blMon.getData();
				//dataOut.write(b);
				dataOut.writeInt(d[0]);
				dataOut.writeInt(d[1]);
				dataOut.writeInt(d[2]);
				//LCD.drawChar(' ', 0,0);
				//LCD.drawInt(d, 0,0);
				dataOut.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
