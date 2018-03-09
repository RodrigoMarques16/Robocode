package AR;

import robocode.*;
//import robocode.util.Utils;

import java.awt.Color;

/*
  It's a cold ass fashion when she stole my passion
  It's an everlasting, it's a ghetto blasting
  
  comments are legacy gun stuff from before restructuring code
*/


public class Beck extends AdvancedRobot {

	//private static Enemy target;
	private static PatternGun gun;
	private static Radar radar;
		
//-- run ------------------------------------------------------------------------------------------------------

	public void run() {
		
		initColors();
		initComponents();
		
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		
		setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
		//target = new Enemy();
		
		while(true) {
			gun.execute();
			radar.execute();
			execute();
		}
		
	}
	
//-- functions ------------------------------------------------------------------------------------------------

	private void initColors(){
		Color blue = new Color(29,70,152);
		Color yellow = new Color(251,209,0); 
		setColors(blue, yellow, blue);
	}
	
	private void initComponents(){
    if (radar == null) {
			radar = new Radar(this);
		}
		if (gun == null) {
			gun = new PatternGun(this);
		}
	}
	
//-- events ---------------------------------------------------------------------------------------------------
	public void onScannedRobot(ScannedRobotEvent e) {
		
		//target.energy = e.getEnergy();
		//target.dist = e.getDistance();
		//target.bearing = e.getBearingRadians();
		//target.velocity = e.getVelocity();
		//target.heading = e.getHeadingRadians();

		gun.onScannedRobot(e);
    radar.onScannedRobot(e);
    
		/*
		double myEnergy = getEnergy();
		
		double absoluteBearing = target.bearing + getHeadingRadians();
		double firePower = Math.min(Math.min(myEnergy/6, 1300/e.getDistance()), e.getEnergy()/3);
		
		pGun.logEnemy(target, absoluteBearing);
		
		setTurnGunRightRadians(Utils.normalRelativeAngle( pGun.getAngle(target, absoluteBearing, firePower) - getGunHeadingRadians()));
		
		if(getGunTurnRemaining() == 0 && myEnergy > 1) {
			setFire(firePower);
		}
		
		setTurnRadarLeftRadians(getRadarTurnRemainingRadians());
		*/
	}

    public void onBulletHit(BulletHitEvent e) {
      gun.onBulletHit(e);
    }

}