package ar.components.movement;

import java.awt.geom.*;

import robocode.*;
import robocode.AdvancedRobot;
import robocode.util.Utils;

import ar.components.Component;

/**
 * RandomMovement
 * @author Afonso Brandão
 */
public class RandomMovement extends Component {
  static final double MAX_VELOCITY      = 8;
  static final double WALL_MARGIN       = 25;
  static final double CHANCE_TO_FLATTEN = 0.05;
  static final double INITIAL_MOV_ANGLE = 0.2;
  static final int NUMBER_OF_TRIES      = 100;

  RoundRectangle2D.Double field;

  AdvancedRobot bot;
  Point2D robotLocation;
  Point2D enemyLocation;
  double enemyDistance;
  double enemyAbsoluteBearing;
  double movementLateralAngle;

  /**
   * Construtor da Class
   * @param A class principal do robot
   */
  public RandomMovement(AdvancedRobot bot) {
    this.bot = bot;
  }

  /**
   * Método para para initializar a localização do enimigo e o rectangulo 
   * para o cálculo de pontos-destino
   */
  public void init() {
    enemyLocation = null;
    //Vamos projetar um rectangulo que representa o campo do robocode.
    //Vamos dar uma margem de 25 pxx para evitar as paredes
    //precisamos disto para escolher os pontos para os quais no mover
    field = new RoundRectangle2D.Double( 
      WALL_MARGIN, 
      WALL_MARGIN, 
      bot.getBattleFieldWidth() - 2 * WALL_MARGIN, 
      bot.getBattleFieldHeight() - 2 * WALL_MARGIN,
      100 - WALL_MARGIN, 100 - WALL_MARGIN
    );
    
    movementLateralAngle = INITIAL_MOV_ANGLE;
  }

  /**
   *  Comportamento do movimento quando o robot recebe o evento scannedrobot.
   *  Atualiza a posição do inimigo
   *  @param o evento scannedRobot
   */
  public void onScannedRobot(ScannedRobotEvent e) {
    
    robotLocation        = new Point2D.Double(bot.getX(), bot.getY());

    enemyAbsoluteBearing = bot.getHeadingRadians() + e.getBearingRadians();
    enemyDistance        = e.getDistance();

    double xDisplacement = Math.sin(enemyAbsoluteBearing) * enemyDistance;
    double yDisplacement = Math.cos(enemyAbsoluteBearing) * enemyDistance;
    
    enemyLocation        = new Point2D.Double(bot.getX() + xDisplacement, 
                                              bot.getY() + yDisplacement);

  }

  /**
  * Aqui implementamos o behavior do moviemento a cada tick(). 
  * Esta função vai ser chamada no run
  */
  public void execute() {

    if (enemyLocation == null) {
      return;
    }
    
    considerChangingDirection();

    Point2D robotDestination = null;
    double tries             = 0;

    do {
      
      double movementAngle  = absoluteBearing(enemyLocation, robotLocation) 
                              + movementLateralAngle;

      double movementLength = enemyDistance * (1.1 - tries / 100.0);
      double xDisplacement  = Math.sin(movementAngle) * movementLength;
      double yDisplacement  = Math.cos(movementAngle) * movementLength;

      robotDestination = new Point2D.Double(
        enemyLocation.getX() + xDisplacement, 
        enemyLocation.getY() + yDisplacement
      );

      tries++;

    }while (tries < NUMBER_OF_TRIES && !field.contains(robotDestination));

    goTo(robotDestination);
  }

  /**
   * Método para introduzir aleatoridade no movimento 
   * através de uma chance de inverter a direção
   */
  private void considerChangingDirection() {
    
    if (Math.random() < CHANCE_TO_FLATTEN) {
      movementLateralAngle *= -1;
    }
  }

  /**
   * Método para dirigir o robot a determinadas coordenadas
   * @param a posição de destino
   */
  private void goTo(Point2D destination) {
    double absBearing = absoluteBearing(robotLocation, destination);
    double myHeading = bot.getHeadingRadians();
    double angle = Utils.normalRelativeAngle(absBearing - myHeading);

    double turnAngle = Math.atan(Math.tan(angle));

    //Transformar o angulo num valor entre -pi/2 e +pi/2
    bot.setTurnRightRadians(turnAngle); 

    bot.setAhead(robotLocation.distance(destination) 
              * (angle == turnAngle ? 1 : -1));

    //Se tivermos de virar muito, limitamos a velocidade a zero
    bot.setMaxVelocity(Math.abs(bot.getTurnRemaining()) > 33 ? 0 : 25);
  }

  /**
   * Ângulo absoluto entre dois pontos
   * @param os dois pontos
   * @return o ângulo entre eles
   */
  static double absoluteBearing(Point2D source, Point2D target) {
      return Math.atan2(target.getX() - source.getX(), 
                        target.getY() - source.getY());
  }
}
