package AR;

import robocode.ScannedRobotEvent;
import robocode.util.Utils;

/*
	__'s Radar
  Simple Width Lock implementation
	
 */
 
public class Radar {
	
  private static final double MAX_RADAR_TRACKING_AMOUNT = Math.PI / 8;
  
  private Beck robot;
  
  Radar(Beck robot) {
      this.robot = robot;
  }
  
  //-- execute --------------------------------------------------------------------------------------------------

  public void execute() {
    
    if ( robot.getRadarTurnRemaining() == 0.0 )
      robot.setTurnRadarRightRadians( Double.POSITIVE_INFINITY );
    
  }
    
  public void onScannedRobot(ScannedRobotEvent e) {
    
    double absoluteBearing = robot.getHeadingRadians() + e.getBearingRadians();
      
    double radarTurn = Utils.normalRelativeAngle( absoluteBearing - robot.getRadarHeadingRadians() );
    double radarDirection = Math.signum(radarTurn);

    radarTurn += radarDirection * MAX_RADAR_TRACKING_AMOUNT;
    
    robot.setTurnRadarRightRadians(radarTurn);
 
  }

}