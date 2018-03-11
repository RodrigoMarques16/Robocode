package ar;

/*
  Utility class with some math functions
*/

public class ARUtils {

  /**
   * Function that clamps a value between a lower and upper bounds 
   *
   * @param lower Lower bound
   * @param value Value to compare
   * @param upper Upper bound
   *
   * @return Lower if value < lower,   Upper if value > upper,  value if lower <= value <= upper
   */
  public static int clamp(int lower, int value, int upper) {
      return Math.max(lower, Math.min(value, upper));
  }
  
  /**
   * Function that clamps a value between a lower and upper bounds 
   *
   * @param lower Lower bound
   * @param value Value to compare
   * @param upper Upper bound
   *
   * @return Lower if value < lower,   Upper if value > upper,  value if lower <= value <= upper
   */
  public static double clamp(double lower, double value, double upper) {
      return Math.max(lower, Math.min(value, upper));
  }

  /**
   * Function that returns the velocity of a bullet given it's power
   *
   * @param power Power of the bullet
   *
   * @return Velocity of the bullet
   */
  public static double bulletVelocity(double power) {
    return 20.0D - 3.0D * power;
  }

  /**
   * Function that returns the travel time of a bullet
   *
   * @param dist Distance to the target
   * @param velocity Speed of the bullet
   *
   * @return Time to reach the target
   */
  public static int bulletFlightTime(double dist, double velocity){
    return (int) (dist / velocity);
  }
  
}
