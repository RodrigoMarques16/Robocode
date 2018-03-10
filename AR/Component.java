package AR;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

/*
  Components are the different controllers for each of the tank's part;
  
  Functions
  
      execute()
        Will be called every tick.
        
      init()
        Called at the begining of each round.
    
  Events
    
      onScannedRobot(ScannedRobotEvent e)
        The tank's onScannedRobot event will be replicated to the component

*/


public abstract class Component {
  
  @SuppressWarnings("unused")
  private AdvancedRobot robot;
  
  public abstract void execute();
  public abstract void init();
  
  public abstract void onScannedRobot(ScannedRobotEvent e);
  
}