package AR;

import robocode.*;
import robocode.util.Utils;
import java.awt.geom.*;

public class RandomMovement {
  static final double MAX_VELOCITY = 8;
  static final double WALL_MARGIN = 25;
	
  RoundRectangle2D.Double field;
	
	AdvancedRobot bot;
  Point2D robotLocation;
  Point2D enemyLocation;
  double enemyDistance;
  double enemyAbsoluteBearing;
  double movementLateralAngle = 0.2;

	public RandomMovement(AdvancedRobot bot) {
		this.bot = bot;
	}
	
  public void initRound() {
		enemyLocation = null;
		//Vamos projetar um rectangulo que representa o campo do robocode. Vamos dar uma margem de 25 pxx para evitar as paredes
		//precisamos disto para escolher os pontos para os quais no mover
		field = new  RoundRectangle2D.Double(25, 25, bot.getBattleFieldWidth() - 50, bot.getBattleFieldHeight() - 50, 75, 75);
	}

  public void update(ScannedRobotEvent e) {
    robotLocation = new Point2D.Double(bot.getX(), bot.getY());
      
    enemyAbsoluteBearing = bot.getHeadingRadians() + e.getBearingRadians();
    enemyDistance = e.getDistance();
    enemyLocation = vectorToLocation(enemyAbsoluteBearing, enemyDistance, robotLocation);
 
    /*
    double enemyMovementAngle = enemyAbsoluteBearing + movementLateralAngle;
    double xDisplacement = Math.sin(movementAngle) * movementLength;
    double yDisplacement = Math.cos(movementAngle) * movementLength;
       
    enemyLocation = new Point2D.Double(enemyLocation.getX() + xDisplacement, enemyLocation.getY() + yDisplacement);
    */
  }

  public void execute() {
  	//Esta código executa a todos os ticks
		if (enemyLocation == null) {
			//Ainda não houve um scanned event
			//bot.setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
			return;
		}
		considerChangingDirection();
		Point2D robotDestination = null;
		double tries = 0;

		do {
      double movementAngle = absoluteBearing(enemyLocation, robotLocation) + movementLateralAngle;
			double movementLength = enemyDistance * (1.1 - tries / 100.0);
      double xDisplacement = Math.sin(movementAngle) * movementLength;
			double yDisplacement = Math.cos(movementAngle) * movementLength;
			 
      robotDestination = new Point2D.Double(enemyLocation.getX() + xDisplacement, enemyLocation.getY() + yDisplacement);
				
		  tries++;
		}while (tries < 100 && !field.contains(robotDestination));

		goTo(robotDestination);
		//bot.setTurnRadarRightRadians(Utils.normalRelativeAngle(enemyAbsoluteBearing - bot.getRadarHeadingRadians()) * 2);
    }

  void considerChangingDirection() {
		//Atiramos um dados para alisar o movimento. Podemos alterar este valor como quisermos. Quanto mais
		//liso, mais nos conseguimos mexer, mas mais previsível o movimento se torna
		double chanceToFlatten = 0.05;
		if (Math.random() < chanceToFlatten) {
		    movementLateralAngle *= -1;
		}
  }

    //Método para dirigir o robot a determinadas coordenadas
  void goTo(Point2D destination) {
    double angle = Utils.normalRelativeAngle(absoluteBearing(robotLocation, destination) - bot.getHeadingRadians());
		double turnAngle = Math.atan(Math.tan(angle));
    bot.setTurnRightRadians(turnAngle); //Transformar o angulo num valor entre -pi/2 e +pi/2
    bot.setAhead(robotLocation.distance(destination) * (angle == turnAngle ? 1 : -1));

		//Se tivermos de virar muito, limitamos a velocidade a zero
		bot.setMaxVelocity(Math.abs(bot.getTurnRemaining()) > 33 ? 0 : 25);
    }

    static Point2D vectorToLocation(double angle, double length, Point2D sourceLocation) {
      return vectorToLocation(angle, length, sourceLocation, new Point2D.Double());
    }

    static Point2D vectorToLocation(double angle, double length, Point2D sourceLocation, Point2D targetLocation) {
      targetLocation.setLocation(sourceLocation.getX() + Math.sin(angle) * length,
      sourceLocation.getY() + Math.cos(angle) * length);
      return targetLocation;
    }

    //função utilitária para nos dar o angulo absoluto entre dois pontos
    static double absoluteBearing(Point2D source, Point2D target) {
      return Math.atan2(target.getX() - source.getX(), target.getY() - source.getY());
    }
}