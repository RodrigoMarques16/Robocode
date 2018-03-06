package LA;

import java.awt.Color;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;


/*
  HOW THE FUCK THE GUN WORKS:
  Take the enemy's movement in given momement (heading, bearing and velocity
  cast that shit as a char
  save the char
  this sequence of characters (movements) can be used to guess the next movement
  if a movement is already mapped then it's probable the one before it in history will hapen next
  you use this information to calculate the firing angle
  if it's not mapped just fire at current location
 */

// heading = direction facing
// bearing = Returns the bearing to the robot you scanned, relative to your robot's headin

public class PatternMatch.java extends AdvancedRobot {

    static StringBuffer history = new StringBuffer("00000000000000000000000000000");
    static final double BULLET_VELOCITY = 12.5D;
    static final int MATCH_DEPTH = 30;
    
    void updateHistory(ScanedRobotEvent e) { 

	double dist = e.getDistance();
	double absBearing = e.getBearingRadians() + getHeadingRadians();
    
	// Convert the enemy's current movement into an identifiable character 
	
	char hash = (char) (int) (Math.sin(e.getHeadingRadians() - absBearing) * e.getVelocity());

	// Insert the behaviour at the top of history
	history.insert(0, hash);
    }

    double getAngle(ScannedRobotEvent e) {

	double dist = e.getDistance();
	double absBearing = e.getBearingRadians() + getHeadingRadians();
    
	updateHistory(e);

	int index, matchlength = MATCH_DEPTH;

	/*
	  Try to find the longest chain of movements that lead to this one
	  This will obviously be the most accurate chain
	 */
	do{
	    String s = history.toString(); // necessary??
	    index = indexOf(history.substring(0, matchlength), 1);
	    matchlength--;
	}while(index < 0);


	// This is as many movements as you can go up in history before you're overestimating the enemy location
	// abcd(e)fg     e = current
	// a(b)cdefg     b = probable bearing considering bullet travel time
	matchlength = index - (int) (dist / BULLET_VELOCITY);

	/*
	  Go up the history as far as you can before overestimating
	  Try to estimate the new bearing from previous angles.
	 */
	do {
	    absBearing += Math.asin( (byte) history.charAt(index) / dist);
	    index--;
	}while( index >= Math.max(0, matchLength()));
	
	return Utils.normalRelativeAngle(absBearing - getGunHeadingRadians());
    }
}
