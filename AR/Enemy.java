package AR;

import robocode.ScannedRobotEvent;

public class Enemy {
	public double dist;
	public double energy;
	public double bearing;
	public double velocity;
	public double heading;
  public double absoluteBearing;

  Enemy(ScannedRobotEvent e) {
    this.dist = e.getDistance();
    this.energy = e.getEnergy();
    this.bearing = e.getBearing();
    this.heading = e.getHeading();
    this.velocity = e.getVelocity();
  }
  
  public void setAbsoluteBearing(double absoluteBearing) {
    this.absoluteBearing = absoluteBearing;
  }
  
}
