package AR;

import robocode.ScannedRobotEvent;
import robocode.BulletHitEvent;
import robocode.Rules;
import robocode.util.Utils;

import AR.ARUtils;

/*
	Beck's Symbolic Pattern Maching Gun, based on Black Widow's. 
  Bullet power calculation based on Diamond's.
  
  Every round we calculate the power we should use when firing
  and adjust our aim accordingly. If the tank is ready to fire,
  then we fire.
  
  When we scan the target, we take its lateral velocity and cast it
  as a character to store it at the head of a StringBuffer. 
  By storing it this way we can later use Java's own string 
  pattern matching functions to find sequences of movements later.
  
  When we want to aim at the target, we iterate through the history
  of logged movements and try to find the longest repeating pattern. 
  The process works basically like this: 
      (abcdefghiabcde) | abcdefg(hiabcde)
      (abcdefghiabcd)e | abcdefg(hiabcde)
                      ...
      (abcde)fghiabcde | abcdefg(hiabcde) MATCH!
	
  Now we have the index so we iterate back as many positions as it
  would take for the bullet to hit the target.
  
  Then we fire.
  
 */
 
public class PatternGun {
	
  private static final int MATCH_LENGTH = 30;
	private static final double DEFAULT_BULLET_POWER = 1.95D;
  private static final double STRONG_BULLET_POWER = 2.95D;
  private static final int CLOSE_QUARTERS = 150;
  private static final int LONG_RANGE = 325;
	
  private Beck robot;
	private ScannedRobotEvent target;
  private StringBuffer history;
  
  private int	shots;
  private int hits;
  private double accuracy;
	
	PatternGun(Beck robot) {
		this.robot = robot;
    this.history = new StringBuffer("000000000000000000000000000000000000000000000000000000000000");
    this.shots = 0;
    this.hits = 0;
    this.accuracy = 0;
	}
	
//-- core ------------------------------------------------------------------------------------------------------

  public void execute() {
    if (target != null) {
      double bulletPower = calculateBulletPower();
      if (robot.getGunHeat() == 0 && robot.getGunTurnRemaining() == 0 && robot.getEnergy() > bulletPower) {
        fire(bulletPower);
      }
      aim(bulletPower);
    }
  }
  
  public void initRound(){
    this.target = null;
  }

//-- events ---------------------------------------------------------------------------------------------------
	
  public void onScannedRobot(ScannedRobotEvent e) {
    setTarget(e);
    
    double absoluteBearing = target.getBearingRadians() + robot.getHeadingRadians();
    double lateralVelocity = Math.sin(target.getHeadingRadians() - absoluteBearing) * target.getVelocity();
    
    history.insert(0, (char) Math.round(lateralVelocity));
  }
	
  public void onBulletHit(BulletHitEvent e) {
      hits++;
  }
  
//-- functions ------------------------------------------------------------------------------------------------
  
    private void fire(double bulletPower) {
    System.out.println("Bullet power: " + bulletPower);
    robot.setFire(bulletPower);
    shots++;
    if (hits != 0) {
      accuracy = (double) hits / shots;
    }
    System.out.println("acc: " + accuracy + " = " + hits + " / " + shots );
  }
  
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

    
    // Convert the information back to numbers and calculate the angular velocity
    // Adding the velocities to the current bearing will help us predict the next angle of the enemy
    do {
      absoluteBearing += Math.asin( (byte) history.charAt(index) / dist);
      index--;
    }while(--BFTime >= 0);

    
    robot.setTurnGunRightRadians(Utils.normalRelativeAngle( absoluteBearing - robot.getGunHeadingRadians() ));
  }
  
	private double calculateBulletPower() {
    double bulletPower = DEFAULT_BULLET_POWER;
		if (target != null) {

      double myEnergy = robot.getEnergy();
      
      if (target.getDistance() < CLOSE_QUARTERS || accuracy > 0.33 ) {
        bulletPower = STRONG_BULLET_POWER;
      }
      
      else if (target.getDistance() > LONG_RANGE) {
        double powerDownPoint = ARUtils.clamp(35, 63 + (int)((target.getEnergy() - myEnergy) * 4), 63);
        if (myEnergy < powerDownPoint) {
          bulletPower = Math.min(bulletPower, ARUtils.cube(myEnergy / powerDownPoint) * DEFAULT_BULLET_POWER);
        }
      }
      
      bulletPower = Math.min(bulletPower, target.getEnergy() / 4);
      bulletPower = Math.max(Rules.MIN_BULLET_POWER, bulletPower);
		}

    return bulletPower;
	}
  
  void setTarget(ScannedRobotEvent target) {
    this.target = target;
	}

}