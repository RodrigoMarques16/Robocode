package AR.Components;

import robocode.AdvancedRobot;

/**
 * Components are the different controllers for each part of the tank
 */
public abstract class Component {

  /**
   * Parent robot of this component
   */
  @SuppressWarnings("unused")
  private AdvancedRobot robot;
  
  /**
   * Called by parent Robot every tick
   */
  public abstract void execute();
  
  /**
   * Called by parent Robot at the start of a round
   */
  public abstract void init();

}