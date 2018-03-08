package LA;

import robocode.ScannedRobotEvent;
import robocode.util.Utils; // normalRelativeAngle
import LA.Enemy;

// heading = direction facing
// bearing = Returns the bearing to the robot you scanned, relative to your robot's headin

public class PatternMatch {

    static StringBuffer history = new StringBuffer("000000000000000000000000000000");
    static final double BULLET_VELOCITY = 12.5D;
    static final int MATCH_LENGTH = 30;

    void updateHistory(Enemy e, double absBearing) { 
	    
	// Convert the enemy's current movement into an identifiable character 
	char hash = (char) (int) (Math.sin(e.heading - absBearing) * e.velocity);
	
	// Insert the behaviour at the top of history
	history.insert(0, hash);
    }	

    public double getAngle(Enemy e, double absoluteBearing, double gunHeading) {
	double dist = e.dist;
	
	int index;
	int matchLength = MATCH_LENGTH;
		
	/*
	  Try to find the longest chain of movements that lead to this one
	  This will obviously be the most accurate chain
	*/
		
	do{
	    index = history.indexOf(history.substring(0, matchLength), 1);
	    matchLength--;
	}while(index < 0);
	
	// This is as many movements as you can go up in history before you're overestimating the enemy location
	// abcd(e)fg     e = current
	// a(b)cdefg     b = probable bearing considering bullet travel time
	matchLength = index - (int) (dist / BULLET_VELOCITY);
	
	/*
	  Go up the history as far as you can before overestimating
	  Try to estimate the new bearing from previous angles.
	*/
	do {
	    absoluteBearing += Math.asin( (byte) history.charAt(index) / dist);
	    index--;
	}while( index >= Math.max(0, matchLength));
		
	return absoluteBearing;
    }
}
