import java.io.DataInputStream;
import java.io.IOException;

import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;

public class BluetoothReceiver extends Thread {
	DataInputStream dataIn;
	PD sf;
	ReferenceGenerator refGen;
	public static int CMD_SET_PARAMETERS = 1;
	public static int CMD_SET_DIRECTION = 2;

	public BluetoothReceiver(DataInputStream dataIn, PD sf,
			ReferenceGenerator refGen) {
		this.dataIn = dataIn;
		this.sf = sf;
		this.refGen = refGen;
	}

	public void run() {
		while (!Thread.interrupted()) {
			try {
				int cmd = dataIn.readInt();
				LCD.drawInt(cmd, 2, 1);
				switch (cmd) {
				case 1:
					float[] values = new float[2];
					values[0] = dataIn.readFloat();
					values[1] = dataIn.readFloat();
					sf.setParameters(values);
					break;
				case 2:
					int direction = dataIn.readInt();
					if(direction==1){
						refGen.setRef(refGen.getRef()+5);
					} else if(direction==-1){
						refGen.setRef(refGen.getRef()-5);
					}
					break;
				}

				/*
				 * if(direction==1){ refGen.setRef(refGen.getRef()-5); //Glöm
				 * inte att lägga en spärr på refGen! } else if(direction==2){
				 * refGen.setRef(refGen.getRef()+5); } else {
				 * refGen.setRef(ReferenceGenerator.StableAngel); }
				 */

				

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
