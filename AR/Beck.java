package AR;

import java.awt.Color;
import robocode.*;

import AR.Components.Gun.PatternGun;
import AR.Components.Movement.RandomMovement;
import AR.Components.Radar.WideLock;

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
  private static WideLock radar;
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
      radar = new WideLock(this);
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