package AR.Components;

import robocode.ScannedRobotEvent;

import AR.Beck;

/*
  Components are the different controllers for each of the tank's part;

  Objects:

      Beck robot;
        The paren't robot of this component


  Functions:
  
      execute()
        Will be called every tick.
        
      init()
        Called at the begining of each round.


  Events:
    
      onScannedRobot(ScannedRobotEvent e)
        The tank's onScannedRobot event will be replicated to the component

*/


public abstract class Component {

  @SuppressWarnings("unused")
  private Beck robot;

  public abstract void execute();
  public abstract void init();

  public abstract void onScannedRobot(ScannedRobotEvent e);

}