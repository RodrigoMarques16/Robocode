package AR;

import robocode.*;

import java.awt.Color;

/*
  It's a cold ass fashion when she stole my passion
  It's an everlasting, it's a ghetto blasting
*/


public class Beck extends AdvancedRobot {

	private static PatternGun gun;
	private static Radar radar;
		
//-- run ------------------------------------------------------------------------------------------------------

	public void run() {
		
		initColors();
		initComponents();
		
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		
		setTurnRadarRightRadians(Double.POSITIVE_INFINITY);

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
		gun.onScannedRobot(e);
    radar.onScannedRobot(e);
	}

  public void onBulletHit(BulletHitEvent e) {
    gun.onBulletHit(e);
  }

}