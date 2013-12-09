package computer;


public class BluetoothMonitor {
	
	private int newReceivedData[] = new int[3];
	private boolean isNewReceived = false;
	
	private Sendobject newSendData;
	private boolean isNewSend = false;
	
	public BluetoothMonitor(){
		
	}
	
	public synchronized void newReceivedData(int[] values){
		newReceivedData[0] = values[0];
		newReceivedData[1] = values[1];
		newReceivedData[2] = values[2];
		
		isNewReceived = true;
		notifyAll();
	}
	
	public synchronized int[] getReceivedData(){
		while(!isNewReceived){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		isNewReceived = false;
		int[] temp = newReceivedData;
		notifyAll();
		return temp;
		
	}
	
	public synchronized void newSendData(Sendobject ds){
		newSendData = ds.clone();
		isNewSend = true;
		notifyAll();
	}
	
	public synchronized Sendobject getSendData(){
		while(!isNewSend){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		isNewSend = false;
		Sendobject temp = newSendData;
		notifyAll();
		return temp;
		
	}
	
	
	
	

}
