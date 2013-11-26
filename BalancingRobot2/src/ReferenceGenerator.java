
public class ReferenceGenerator {
	public static final int StableAngel = 0;
	float ref;
	long lastUpdate;
	
	public ReferenceGenerator(){
		ref = 0;
		lastUpdate = System.currentTimeMillis();
	}
	
	public synchronized float getRef(){
		return ref;
	}
	
	public synchronized void setRef(float ref){
		this.ref = ref;
		if(this.ref>30){
			this.ref=30;
		} else if(this.ref<-30){
			this.ref=-30;
		}
		lastUpdate = System.currentTimeMillis();
		notifyAll();
	}

	public long getLastUpdate() {
		return lastUpdate;
	}
}
