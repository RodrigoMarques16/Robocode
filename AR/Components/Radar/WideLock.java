package AR.Components.Radar;

import robocode.ScannedRobotEvent;
import robocode.util.Utils;

import AR.Beck;
import AR.Components.Component;

/*
  Becks's Radar
  Simple Wide Lock implementation.
  We turn PI/4 radians if we don't have a target,
  PI/8 if we do.

 */

public class WideLock extends Component{

  private static final double MAX_RADAR_TURN = Math.PI / 4;

  private Beck robot;
  private long lastScanTime;
  ScannedRobotEvent target;

  public WideLock(Beck robot) {
      this.robot = robot;
  }

//-- core ----------------------------------------------------------------------------------------------------

  public void execute() {
    double radarTurn, radarDirection;

    if(robot.getTime() == lastScanTime && target != null) {
       // Target locked
      double absoluteBearing = robot.getHeadingRadians() + target.getBearingRadians();

      radarTurn = Utils.normalRelativeAngle( absoluteBearing - robot.getRadarHeadingRadians() );
      radarDirection = Math.signum(radarTurn);

      radarTurn += radarDirection * (MAX_RADAR_TURN/2);
    }
    else{
      // Lost the target
      radarTurn = MAX_RADAR_TURN;
    }

    robot.setTurnRadarRightRadians(radarTurn);
  }

  public void init() {
    this.target = null;
    //this.direction = 1;
  }

//-- events --------------------------------------------------------------------------------------------------

  public void onScannedRobot(ScannedRobotEvent e) {
    target = e;
    lastScanTime = robot.getTime();
  }

}