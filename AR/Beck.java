package AR;

import robocode.*;

import java.awt.Color;

/*
    Beck - A robot by Rodrigo Marques e Afonso Brandão
    
    1v1 bot
      - Symbollic Pattern Matching gun
      - Random Movement
    
    Based on implementations from Diamond by voidious,
    and Black Widow by robar.
    
    We have emulated Diamond's modular structure for each
    of the robots components, which worked really well and
    made it very easy to integrate each other's work into a
    final robot.
    
    For the gun, a Symbollic Pattern Matcher looked very interesting
    and simple enough for the scope of the project.
    
*/


public class Beck extends AdvancedRobot {

	private static PatternGun gun;
	private static Radar radar;
	private static RandomMovement movement;
  
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
      movement.execute();
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
    if (movement == null) {
			movement = new RandomMovement(this);
		}
    
    radar.init();
    gun.init();
    movement.init();
	
  }
	
//-- events ---------------------------------------------------------------------------------------------------

	public void onScannedRobot(ScannedRobotEvent e) {
		gun.onScannedRobot(e);
    radar.onScannedRobot(e);
    movement.onScannedRobot(e);
	}

  public void onBulletHit(BulletHitEvent e) {
    gun.onBulletHit(e);
  }

}