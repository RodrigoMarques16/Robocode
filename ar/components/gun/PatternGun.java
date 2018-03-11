package ar.components.gun;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.BulletHitEvent;
import robocode.Rules;
import robocode.util.Utils;

import ar.components.Component;
import ar.ARUtils;

/*
 * Beck's Symbolic Pattern Maching Gun, based on Black Widow's. 
 * Bullet power calculation based on Diamond's.
 *
 * Every round we calculate the power we should use when firing
 * and adjust our aim accordingly. If the tank is ready to fire,
 * then we fire.
 *
 * When we scan the target, we take its lateral velocity and cast it
 * as a character to store it at the head of a StringBuffer. 
 * By storing it this way we can later use Java's own string 
 * pattern matching functions to find sequences of movements later.
 *
 * When we want to aim at the target, we iterate through the history
 * of logged movements and try to find the longest repeating pattern. 
 * The process works basically like this: 
 *     (abcdefghiabcde) | abcdefg(hiabcde)
 *     (abcdefghiabcd)e | abcdefg(hiabcde)
 *                     ...
 *     (abcde)fghiabcde | abcdefg(hiabcde) MATCH!
 *
 * Now we have the index so we iterate back as many positions as it
 * would take for the bullet to hit the target to get the next firing angle.
 */
 
/**
 * Symbolic Pattern Matching Gun
 * Use enemy's history of movements to predict firing angle
 * @author Rodrigo Marques
 */
public class PatternGun extends Component{

  private static final int            MATCH_LENGTH = 30;
  private static final double DEFAULT_BULLET_POWER = 1.95D;
  private static final double  STRONG_BULLET_POWER = 2.95D;
  private static final double    WEAK_BULLET_POWER = 1.00D;
  private static final int          CLOSE_QUARTERS = 150;
  private static final int              LONG_RANGE = 325;
  private static final int     ACCURACY_THRESHOLD = 0.33;

  private AdvancedRobot robot;
  private ScannedRobotEvent target;
  private StringBuffer history; 

  private int shots;
  private int hits;
  private double accuracy;

  /**
   * Constructor
   * @param robot Parent robot of this Component
   */
  public PatternGun(AdvancedRobot robot) {
    this.robot = robot;
    this.history = new StringBuffer("00000000000000000000000000000000000000000");

    this.shots = 0;
    this.hits = 0;
    this.accuracy = 0;
  }

//-- Core ---------------------------------------------------------------------

  /**
   * Called by parent Robot every tick
   */
  @Override
  public void execute() {
    if (target != null) {
      double bulletPower = calculateBulletPower();
      if (canfire(bulletPower)) {

        fire(bulletPower);
      }
      aim(bulletPower);
    }
  }
  
  /**
   * Initialize the gun
   */
  @Override
  public void init(){
    this.target = null;
  }

//-- Private Methods ----------------------------------------------------------

  /**
   * Custom firing function with logging
   *
   * @param bulletPower Power used when firing
   */
  private void fire(double bulletPower) {
    System.out.println("Bullet power: " + bulletPower);
    robot.setFire(bulletPower);
    shots++;
    System.out.println("acc: " + accuracy + " = " + hits + " / " + shots );
  }

  /**
   * Predict the enemy's position and turn the gun
   *
   * @param bulletPower Power used when firing
   */
  private void aim(double bulletPower) {
    int index;
    int matchLength = MATCH_LENGTH;
    double dist = target.getDistance();
    
    double bVelocity = ARUtils.bulletVelocity(bulletPower);
    int BFTime = ARUtils.bulletFlightTime(dist, bVelocity);

    double absoluteBearing = target.getBearingRadians() + robot.getHeadingRadians();

    // Reduce match length until we can get a match, or none is found
    // Start at BFTime so we don't later try to iterate to negative indexes
    do{
        index = history.indexOf(history.substring(0, matchLength), BFTime);
        matchLength--;
    }while(index < 0);

    // Convert the information back to numbers
    // and calculate the angular velocity
    // Adding the velocities to the current bearing will help us predict the
    // next angle of the enemy
    do {
      absoluteBearing += Math.asin( (byte) history.charAt(index) / dist);
      index--;
    }while(--BFTime >= 0);
    
    robot.setTurnGunRightRadians(
    Utils.normalRelativeAngle( 
    absoluteBearing - robot.getGunHeadingRadians() ));
  }
  
  /**
   * Calculate the power of the next bullet based on distance
   * and our accuracy
   *
   * @return Power to fire with
   */
  private double calculateBulletPower() {
    double bulletPower = DEFAULT_BULLET_POWER;
    if (target != null) {

      if (target.getDistance() < CLOSE_QUARTERS || accuracy > ACCURACY_THRESHOLD ) {
        // We're probably gonna hit
        bulletPower = STRONG_BULLET_POWER;
      }
      else if (target.getDistance() > LONG_RANGE) {
        // Save some energy and fire faster bullets
        bulletPower = WEAK_BULLET_POWER;
      }

      // Don't waste energy on a weak target
      bulletPower = Math.min(bulletPower, target.getEnergy() / 4);

      // Make sure we don't underpower the bullet
      bulletPower = Math.max(Rules.MIN_BULLET_POWER, bulletPower);
    }

    return bulletPower;
  }
  
  /**
   * Set the target to a scanned robot
   */
  private void setTarget(ScannedRobotEvent target) {
    this.target = target;
  }
 
 /**
   * Determine if the tank can fire
   * @return True if the tank can fire, false if not.
   */
  private bool canFire(double bulletPower) {
    return robot.getGunHeat() == 0 
           && robot.getGunTurnRemaining() == 0
           && robot.getEnergy() > bulletPower;
  }

//-- Event Handling -----------------------------------------------------------

  /**
   * Targets the scanned robot and stores its information
   *
   * @param e Scanned robot
   */
  public void onScannedRobot(ScannedRobotEvent e) {
    setTarget(e);
    
    double targetBearing   = target.getBearingRadians();
    double targetHeading   = target.getHeadingRadians();
    double myHeading       = robot.getHeadingRadians();
    double targetVelocity  = target.getVelocity();
   
    double absoluteBearing = targetBearing + myHeading;

    double lateralVelocity = Math.sin(targetHeading - absoluteBearing) 
                             * targetVelocity;

    history.insert(0, (char) Math.round(lateralVelocity));
  }
  
  /**
   * Updates accuracy statistics when a bullet hits
   *
   * @param e Not used
   */
  public void onBulletHit(BulletHitEvent e) {
    hits++;
    accuracy = (double) hits / shots;
  }
 
}
