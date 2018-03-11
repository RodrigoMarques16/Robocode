package AR;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Method;

import robocode.*;

import AR.Components.Component;
import AR.Components.Gun.PatternGun;
import AR.Components.Movement.RandomMovement;
import AR.Components.Radar.WideLock;

/**
 * Beck - A robot by Afonso Brandão e Rodrigo Marques
 *  
 *  up201504440 / up201605427
 *   
 *  1v1 bot
 *    - Symbollic Pattern Matching gun
 *    - Random Movement
 *
 *  Based on implementations from Diamond by voidious,
 *  and Black Widow by robar.
 *
 *  We have emulated Diamond's modular structure for each
 *  of the robots components, which worked really well and
 *  made it very easy to integrate each other's work into a
 *  final robot.
 *
 *  For the gun, a Symbollic Pattern Matcher looked very interesting
 *  and simple enough for the scope of the project.
 * 
 * @author Afonso Brandão e Rodrigo Marques
 * @version 2.0 09/03/2018  
 */
public class Beck extends AdvancedRobot {

  private static List<Component> components = new ArrayList<Component>();
  private static PatternGun gun;
  private static WideLock radar;
  private static RandomMovement movement;
  
//-- Run ------------------------------------------------------------------------------------------------------

  /**
   * Override of AdvancedRobot's run()
   * Initialize the robot's components and execute them every tick
   */
  @Override
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

//-- Private Methods ------------------------------------------------------------------------------------------

  /**
   * Paint the robot
   */
  private void initColors(){
    Color blue = new Color(29,70,152);
    Color yellow = new Color(251,209,0); 
    setColors(blue, yellow, blue);
  }
  
  /**
   * Create each component if it is not yet created
   * and initialize them
   */
  private void initComponents(){
    if (radar == null) {
      radar = new WideLock(this);
      components.add(radar);
    }
    if (gun == null) {
      gun = new PatternGun(this);
      components.add(gun);
    }
    if (movement == null) {
      movement = new RandomMovement(this);
      components.add(movement);
    }
    
    for (Component cmp : components) {
      System.out.println(cmp.getClass().getName() + " loaded");
      cmp.init();
    }

  }
  
//-- Events ---------------------------------------------------------------------------------------------------

  /**
   * Replicate events to components
   *
   * @param name Name of the method to call when the event fires
   * @param e Event to pass on
   */
  private void replicate(String name, Event e) {
    // should have used lookup tables instead of try/catch
    for (Component cmp : components) {
      try {
        Method m = cmp.getClass().getMethod(name, e.getClass());
        m.invoke(cmp, e);
        //System.out.println(name + " replicated to " + cmp.getClass().getName());
      }catch (NoSuchMethodException ex) {
        /* component didn't implement this method */
      }catch (Exception ex) {
        /* any other */
        System.out.println(name + " didn't reach " +cmp.getClass().getName());
      }
    }
  }

  /**
   * Replicate onScannedRobot
   *
   * @param e ScannedRobotEvent
   */
  @Override
  public void onScannedRobot(ScannedRobotEvent e) {
    replicate("onScannedRobot", e);
  }
  
  /**
   * Replicate onBulletHit
   *
   * @param e BulletHitEvent
   */
  @Override
  public void onBulletHit(BulletHitEvent e) {
    replicate("onBulletHit", e);
  }

}