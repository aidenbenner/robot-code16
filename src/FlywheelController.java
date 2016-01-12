import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.SpeedController;





public class FlywheelController implements Runnable {
	
	final int TICKS_PER_WHEEL_ROTATION = 360;
	final int THREAD_TIME = 20;
	final double kP = 0.1;
	final double kI = 0;
	final double kD = 0;
	final double intLim = 5000;
	PidController pid;
	
	private Counter enc;
	private SpeedController s1;
	private double setPoint;
	private double predPow;
	private double error;
	//assuming encoder ticks once per degree and is mounted at flywheel 
	//(probably will not be the case)
	
	
	public FlywheelController(Counter encoder, SpeedController speedcntrl){
		enc = encoder;
		s1 = speedcntrl;
		pid = new PidController(kP, kI, kD, intLim, false);
	}
	
	//returns the rpm of the flywheel
	
	public double getSpeed(){
		//I've never used this
		//if we're using
		//get rate of encoder ticks (ticks/sec)
		double rate = this.enc.getRate();
		//convert to rotations / s 
		double speed = rate / TICKS_PER_WHEEL_ROTATION;
		//convert to rpm 
		speed = speed * 60;	
		return speed;
	}
	
	
	private void setflyPow(double pow){
		//we don't want to run the flywheel backwards things will explode
		if(pow < 0){
			pow = 0; 
		}
		s1.set(pow);
	}
	
	private void setFlySpeed(double rpm, double pred){
		this.setPoint = rpm;
		this.predPow = pred;
	}

	@Override
	public void run() {
		this.error = this.setPoint - this.getSpeed();
		double output = this.predPow + pid.calculate(this.error);
		try {
			Thread.sleep(THREAD_TIME);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}