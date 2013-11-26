package computer;


import lejos.pc.comm.*;
import java.io.*;

public class Main {
	private static NXTComm nxtComm;
	private static NXTInfo[] nxtInfo = null;
	private static InputStream is;
	private static OutputStream os;
	private static DataInputStream dataIn;
	private static DataOutputStream dataOut;

	public static void main(String[] args) {
		BluetoothReceiver blReceiver;
		BluetoothSender blSender;
		BluetoothMonitor blMon = new BluetoothMonitor();
		Opcom opcom = new Opcom();
		GUI gui = new GUI("Balancing Robot", blMon);
		gui.initializeGUI();
		opcom.initializeGUI();
		Controller controller = new Controller(opcom, blMon);
		opcom.start();
		controller.start();
		try {
			connect();
		} catch (NXTCommException e1) {
			System.err.println(e1.getMessage());
		}
		blReceiver = new BluetoothReceiver(dataIn, blMon);
		blSender = new BluetoothSender(dataOut, blMon);
		blReceiver.start();
		blSender.start();
	}

	private static void connect() throws NXTCommException {
		nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);

		try {
			nxtInfo = nxtComm.search("NXT3");
		} catch (NXTCommException e) {
			System.out.println("Exception in search");
		}

		if (nxtInfo == null || nxtInfo.length == 0) {
			System.out.println("No NXT Found");
			//System.exit(1);
		} else {

		try {
			nxtComm.open(nxtInfo[0]);
		} catch (NXTCommException e) {
			System.out.println("Exception in open");
		}

		is = nxtComm.getInputStream();
		os = nxtComm.getOutputStream();
		dataIn = new DataInputStream(is);
		dataOut = new DataOutputStream(os);
		
		}

	}
}
