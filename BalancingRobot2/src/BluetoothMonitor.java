import lejos.nxt.LCD;



public class BluetoothMonitor {
	
	private int dataToSend[] = new int[3];
	private boolean sendNow = false;
	
	public BluetoothMonitor(){
		
	}
	
	public synchronized void sendData(int data, int i, int j){
		dataToSend[0] = data;
		dataToSend[1] = i;
		dataToSend[2] = j;
		sendNow = true;
		notifyAll();
	}
	
	public synchronized int[] getData(){
		while(!sendNow){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		sendNow = false;
		int[] temp = dataToSend;
		notifyAll();
		return temp;
		
	}
	
	

}
