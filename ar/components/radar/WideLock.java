package ar.components.radar;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

import ar.components.Component;

/**
 * Simple Wide Lock implementation for our robot
 * Turn the radar PI/4 radians while we don't have a target
 * Only turn it PI/8 radians if we have a lock
 */
public class WideLock extends Component{

  private static final double MAX_RADAR_TURN = Math.PI / 4;

  private AdvancedRobot robot;
  private long lastScanTime;
  ScannedRobotEvent target;

  public WideLock(AdvancedRobot robot) {
      this.robot = robot;
  }

//-- Core ----------------------------------------------------------------------------------------------------

  /**
   * Called by parent Robot every tick
   */
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

  /**
   * Called by parent Robot at the start of a round
   */
  public void init() {
    this.target = null;
  }

//-- Event Handling ------------------------------------------------------------------------------------------

  /**
   * Update target and record scan time
   *
   * @param e Scanned robot
   */
  public void onScannedRobot(ScannedRobotEvent e) {
    target = e;
    lastScanTime = robot.getTime();
  }

}