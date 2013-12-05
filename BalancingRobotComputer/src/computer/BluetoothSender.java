package computer;


import java.io.DataOutputStream;
import java.io.IOException;





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
				Sendobject so = blMon.getSendData();
				if(so.getCMD()==Sendobject.CMD_SET_PARAMETERS){
					double[] values = (double[]) so.getData();
					dataOut.writeInt(Sendobject.CMD_SET_PARAMETERS);
					dataOut.writeDouble(values[0]);
					dataOut.writeDouble(values[1]);
					dataOut.writeDouble(values[2]);
					dataOut.writeDouble(values[3]);
					dataOut.writeDouble(values[4]);
					dataOut.writeDouble(values[5]);
					dataOut.writeDouble(values[6]);
					System.out.println("Send data to NXT: " + so.getCMD() + ", " + values[0] + ", " + values[1]);
				} else if(so.getCMD()==Sendobject.CMD_SET_DIRECTION){
					int direction = (int) so.getData();
					dataOut.writeInt(Sendobject.CMD_SET_DIRECTION);
					dataOut.writeInt(direction);
					System.out.println("Send data to NXT: " + so.getCMD() + ", " + direction);
				}
				dataOut.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
