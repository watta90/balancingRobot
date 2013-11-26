
public class Statefeedback {		
		float l1;
		float l2;
		float x1;//angle
		float x2;//angular velocity
		float a11,a12,a21,a22;
		float b1,b2;
		float c1,c2;
		float u;
	
	
	public Statefeedback( ){
		this.l1=(float) 1.0018;
		this.l2= (float) -0.0061;
		this.x1=0;
		this.x2=0;  //h=0.05
		this.a11=0;
		this.a12=1;
		this.a21=(float) 98.21;
		this.a22=0;
		b1=0;
		b2=(float) 98.21;
		c1=1;
		c2=0;
		u=0;
	}
	public float calc(){
		u=-(l1*x1+l2*x2);
		updateState();
		return u;
	}
	public void updateState(){
		x1=x2;
		x2=a21*x1+b2*u;
	}
}
