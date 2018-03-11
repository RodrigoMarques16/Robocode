package ar.Components.Movement;

import java.awt.geom.*;     //Point2D.Double e RoundRectangle2D.Double
import robocode.*;          //API do robocode
import robocode.util.Utils; //normalRelativeAngle
import ar.Beck;
import ar.Components.Component;

public class RandomMovement extends Component {
  static final double MAX_VELOCITY = 8;
  static final double WALL_MARGIN  = 25;

  RoundRectangle2D.Double field;

  Beck bot;
  Point2D robotLocation;
  Point2D enemyLocation;
  double enemyDistance;
  double enemyAbsoluteBearing;
  double movementLateralAngle = 0.2;
  /**
  * Construtor da Class
  * @param A class principal do robot
  */
  public RandomMovement(Beck bot) {
    this.bot = bot;
  }
  /**
  * Método para para initializar a localização do enimigo e o rectangulo para o cálculo de pontos-destino
  */
  public void init() {
    enemyLocation = null;
    //Vamos projetar um rectangulo que representa o campo do robocode. Vamos dar uma margem de 25 pxx para evitar as paredes
    //precisamos disto para escolher os pontos para os quais no mover
    field = new  RoundRectangle2D.Double(25, 25, bot.getBattleFieldWidth() - 50, bot.getBattleFieldHeight() - 50, 75, 75);
  }
  /**
  *  Comportamento do moviemento quando o robot recebe o evento scannedrobot. Atualiza a posição do inimigo
  *  @param o evento scannedRobot
  */
  public void onScannedRobot(ScannedRobotEvent e) {

    robotLocation = new Point2D.Double(bot.getX(), bot.getY());
 
    enemyAbsoluteBearing = bot.getHeadingRadians() + e.getBearingRadians();
    enemyDistance = e.getDistance();

    double xDisplacement = Math.sin(enemyAbsoluteBearing) * enemyDistance;
    double yDisplacement = Math.cos(enemyAbsoluteBearing) * enemyDistance;
    enemyLocation = new Point2D.Double(bot.getX() + xDisplacement, bot.getY() + yDisplacement);
  
    
  }
  /**
  * Aqui implementamos o behavior do moviemento a cada tick(). Esta função vai ser chamada no run
  */
  public void execute() {
    if (enemyLocation == null) {
      return;
    }
    considerChangingDirection();
    Point2D robotDestination = null;
    double tries = 0;

    do {
      double movementAngle = absoluteBearing(enemyLocation, robotLocation);
      double movementLength = enemyDistance * (1.1 - tries / 100.0);
      double xDisplacement = Math.sin(movementAngle) * movementLength;
      double yDisplacement = Math.cos(movementAngle) * movementLength;

      robotDestination = new Point2D.Double(enemyLocation.getX() + xDisplacement, enemyLocation.getY() + yDisplacement);

      tries++;
    }while (tries < 100 && !field.contains(robotDestination));

    goTo(robotDestination);
    //bot.setTurnRadarRightRadians(Utils.normalRelativeAngle(enemyAbsoluteBearing - bot.getRadarHeadingRadians()) * 2);
  }
  /**
  * Método para introduzir aleatoridade no movimento através de uma chance de inverter a direção
  */
  private void considerChangingDirection() {
    double chanceToFlatten = 0.025;
    if (Math.random() < chanceToFlatten) {
      movementLateralAngle *= -1;
    }
  }
  /**
  * Método para dirigir o robot a determinadas coordenadas
  * @param a posição de destino
  */
  private void goTo(Point2D destination) {
    double angle = Utils.normalRelativeAngle(absoluteBearing(robotLocation, destination) - bot.getHeadingRadians());
    double turnAngle = Math.atan(Math.tan(angle));
    bot.setTurnRightRadians(turnAngle); //Transformar o angulo num valor entre -pi/2 e +pi/2
    bot.setAhead(robotLocation.distance(destination) * (angle == turnAngle ? 1 : -1));

    //Se tivermos de virar muito, limitamos a velocidade a zero
    bot.setMaxVelocity(Math.abs(bot.getTurnRemaining()) > 33 ? 0 : 25);
  }
  /**
  * Ângulo absoluto entre dois pontos
  * @param os dois pontos
  * @return o ângulo entre eles
  */
  static double absoluteBearing(Point2D source, Point2D target) {
      return Math.atan2(target.getX() - source.getX(), target.getY() - source.getY());
  }
}