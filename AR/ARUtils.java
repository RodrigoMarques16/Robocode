package AR;

public class ARUtils {

  public static int clamp(int min, int value, int max) {
      return Math.max(min, Math.min(value, max));
  }
  
  public static double cube(double value) {
    return value * value * value;
  }
  
  public static double bulletVelocity(double power) {
    return 20.0D - 3.0D * power;
  }
	
	public static int bulletFlightTime(double dist, double velocity){
		return (int) (dist / velocity);
	}
   
}
