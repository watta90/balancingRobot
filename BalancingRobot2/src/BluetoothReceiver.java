import java.io.DataInputStream;
import java.io.IOException;

import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;

public class BluetoothReceiver extends Thread {
	DataInputStream dataIn;
	PD pd;
	PID pid;
	Statefeedback sf;
	ReferenceGenerator refGen;
	public static int CMD_SET_PARAMETERS = 1;
	public static int CMD_SET_DIRECTION = 2;

	public BluetoothReceiver(DataInputStream dataIn, PD pd,
			ReferenceGenerator refGen, Statefeedback sf, PID pid) {
		this.dataIn = dataIn;
		this.pd = pd;
		this.sf = sf;
		this.pid = pid;
		this.refGen = refGen;
	}

	public void run() {
		while (!Thread.interrupted()) {
			try {
				int cmd = dataIn.readInt();
				LCD.drawInt(cmd, 2, 1);
				switch (cmd) {
				case 1:
					PIDParameters par = new PIDParameters();
					par.Beta = dataIn.readFloat();
					par.H = dataIn.readFloat();
					par.K = dataIn.readFloat();
					par.N = dataIn.readFloat();
					par.Td = dataIn.readFloat();
					par.Ti = dataIn.readFloat();
					par.Tr = dataIn.readFloat();
					pid.setParameters(par);
					
					/*float[] values = new float[2];
					values[0] = dataIn.readFloat();
					values[1] = dataIn.readFloat();*/
					//pd.setParameters(values);
					pid.setParameters(par);
					//sf.setParameter((float)par.K, (float)par.Ti, (float)par.Td);
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
