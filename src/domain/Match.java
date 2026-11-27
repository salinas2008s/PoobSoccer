package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * A match between two players. Player 1 attacks to the right and player 2 to the left.
 */
public class Match implements Serializable {

    private Player player1;
    private Player player2;
    private Stadium stadium;
    private boolean isMultiplayer;
    private Ball ball;
    private Goalpost leftGoal;          //Lo defiende el jugador 1
    private Goalpost rightGoal;         //Lo defiende el jugador 2
    private Score score;
    private Timer timer;                //Solo en partido por tiempo
    private int goalsToWin;             //Solo en partido por goles
    private String ballStyle;           //Imagen del balon que se escogio, por ejemplo Ball1
    private String goalSize;            //"SMALL", "MEDIUM" o "LARGE"
    private double startX1;             //Donde vuelven los jugadores despues de un gol
    private double startX2;
    private double ballStartHeight;
    private Player shooter;             //Jugador que hizo el tiro de poder que va volando
    private int shotDirection;
    private ArrayList<Ball> fakeBalls;  //Balones falsos de la Ilusion
    private int fakeFrames;
    private double kickDamage = 10;       //Dano de una patada que le pega al rival
    private double kickReach = 60;        //Que tan lejos del cuerpo llega el pie

    /** Creates a match with the ball falling from the center. */
    public Match(Player player1, Player player2, Stadium stadium, boolean isMultiplayer){
        this.player1 = player1;
        this.player2 = player2;
        this.stadium = stadium;
        this.isMultiplayer = isMultiplayer;
        this.ballStartHeight = 600;
        this.ball = new Ball(stadium.getWidth() / 2, ballStartHeight);
        //Por defecto los arcos son medianos
        this.leftGoal = new Goalpost('L', stadium.getWidth(), 75, 320);
        this.rightGoal = new Goalpost('R', stadium.getWidth(), 75, 320);
        this.ballStyle = "Ball1";
        this.goalSize = "MEDIUM";
        this.score = new Score();
        this.startX1 = player1.getPosX();
        this.startX2 = player2.getPosX();
        this.fakeBalls = new ArrayList<Ball>();
        //El jugador 1 mira a la derecha y el jugador 2 a la izquierda
        player1.setFacing(1);
        player2.setFacing(-1);
        player1.watch(this);
        player2.watch(this);
    }

    /** Advances the match one frame. */
    public void update(){
        //Si ya termino no se mueve nada
        if (isFinished()) {
            return;
        }
        movePlayer(player1);
        movePlayer(player2);
        separatePlayers();
        checkKickHit(player1, player2, 1);
        checkKickHit(player2, player1, -1);
        checkPowerHit();
        //1 es hacia la derecha y -1 hacia la izquierda
        checkPlayer(player1, 1);
        checkPlayer(player2, -1);
        ball.move(stadium.getGravity(), stadium.getWidth(), stadium.getHeight());
        leftGoal.bounceOnTop(ball);
        rightGoal.bounceOnTop(ball);
        checkGoal();
        moveFakeBalls();
        if (timer != null) {
            timer.tick();
        }
    }

    /** Moves a player and keeps it inside the field. */
    private void movePlayer(Player player){
        player.decide();
        player.update(stadium.getGravity());
        player.keepInside(0, stadium.getWidth());
    }

    /** Pushes the players apart if their bodies are on the same place. */
    private void separatePlayers(){
        double minDistance = (player1.getWidth() + player2.getWidth()) / 2;
        //Solo chocan si estan a la misma altura, si uno salta por encima del otro no
        boolean sameHeight = Math.abs(player1.getPosY() - player2.getPosY()) < player1.getHeight();
        Player left = player1;
        Player right = player2;
        if (player2.getPosX() < player1.getPosX()) {
            left = player2;
            right = player1;
        }
        double overlap = minDistance - (right.getPosX() - left.getPosX());
        if (!sameHeight || overlap <= 0) {
            return;
        }
        //Cada uno se mueve la mitad hacia su lado
        left.shiftX(-overlap / 2);
        right.shiftX(overlap / 2);
        left.keepInside(0, stadium.getWidth());
        right.keepInside(0, stadium.getWidth());
        //Si uno estaba pegado a la pared, el otro se mueve lo que falta
        overlap = minDistance - (right.getPosX() - left.getPosX());
        if (overlap > 0) {
            if (left.getPosX() - left.getWidth() / 2 <= 0) {
                right.shiftX(overlap);
            } else {
                left.shiftX(-overlap);
            }
        }
    }

    /** Checks if the kick of a player hits the rival. */
    private void checkKickHit(Player attacker, Player rival, int direction){
        //Cada patada solo hace dano una vez
        if (!attacker.isKicking() || attacker.hasKickHit()) {
            return;
        }
        //El rival tiene que estar adelante, al alcance del pie y a una altura parecida
        double distance = (rival.getPosX() - attacker.getPosX()) * direction;
        boolean inFront = distance > 0 && distance < (attacker.getWidth() + rival.getWidth()) / 2 + kickReach;
        boolean sameHeight = Math.abs(rival.getPosY() - attacker.getPosY()) < attacker.getHeight() / 2;
        if (inFront && sameHeight) {
            rival.receiveDamage(kickDamage * attacker.getKickMultiplier());
            attacker.markKickHit();
        }
    }

    /** Checks if the player touches or kicks the ball. */
    private void checkPlayer(Player player, int direction){
        //Mientras vuela un tiro de poder nadie lo puede desviar, solo termina cuando le pega al rival o choca con algo
        if (shooter != null && ball.isStraight()) {
            return;
        }
        boolean touching = ball.touches(player);
        boolean kicking = player.isKicking() && ball.isNearFoot(player, direction);
        //Con aura cualquier toque es tiro de poder, si no es patada normal o rebote
        if (player.hasAura() && (touching || kicking)) {
            firePowerShot(player, direction);
        } else if (kicking) {
            ball.kick(direction, player.getKickMultiplier());
        } else if (touching) {
            ball.bounceOnPlayer(player);
        }
    }

    /** Shoots the power shot of the player. */
    private void firePowerShot(Player player, int direction){
        //Se pone el balon adelante del jugador para que no lo vuelva a tocar
        ball.moveTo(player.getPosX() + direction * (player.getWidth() / 2 + ball.getRadius()), ball.getPosY());
        player.getCharacter().getPower().shoot(ball, direction);
        player.loseAura();
        shooter = player;
        shotDirection = direction;
    }

    /** Checks if the power shot hits the rival. */
    private void checkPowerHit(){
        if (shooter == null) {
            return;
        }
        Player rival = player2;
        if (shooter == player2) {
            rival = player1;
        }
        if (ball.isStraight() && ball.touches(rival)) {
            //Le pega al rival: dano, efecto y balones falsos si el poder los tiene
            Power power = shooter.getCharacter().getPower();
            rival.receiveDamage(power.getDamage() * shooter.getKickMultiplier());
            rival.receivePower(power, shotDirection);
            createFakeBalls(power.getFakeBalls(), power.getDuration());
            shooter = null;
        } else if (!ball.isStraight()) {
            //El balon choco con otra cosa antes, el tiro se acaba sin efecto
            shooter = null;
        }
    }

    /** Checks if there was a goal. */
    private void checkGoal(){
        //El gol es para el que no defiende ese arco, asi tambien funcionan los autogoles
        if (leftGoal.hasBall(ball)) {
            score.addGoalPlayer2();
            //Al que recibe el gol le sube un cuarto la barra de poder
            player1.receiveGoal();
            restartPositions();
        } else if (rightGoal.hasBall(ball)) {
            score.addGoalPlayer1();
            player2.receiveGoal();
            restartPositions();
        }
    }

    /** Puts the players and the ball back to the start. */
    private void restartPositions(){
        player1.restart(startX1);
        player2.restart(startX2);
        player1.setFacing(1);
        player2.setFacing(-1);
        ball.moveTo(stadium.getWidth() / 2, ballStartHeight);
        ball.push(0, 0);
        shooter = null;
        fakeBalls.clear();
    }

    /** Creates the fake balls of the Illusion. */
    private void createFakeBalls(int amount, int frames){
        //Salen desde el balon real, cada uno hacia un lado al azar y siempre hacia arriba
        Random random = new Random();
        for (int i = 0; i < amount; i++) {
            Ball fake = new Ball(ball.getPosX(), ball.getPosY());
            fake.push(random.nextDouble() * 30 - 15, random.nextDouble() * 15 + 5);
            fakeBalls.add(fake);
        }
        fakeFrames = frames;
    }

    /** Moves the fake balls until their time ends. */
    private void moveFakeBalls(){
        for (Ball fake : fakeBalls) {
            fake.move(stadium.getGravity(), stadium.getWidth(), stadium.getHeight());
        }
        fakeFrames--;
        //Cuando se acaba el tiempo desaparecen
        if (fakeFrames <= 0) {
            fakeBalls.clear();
        }
    }

    /** Makes it a match by time. */
    public void startTimer(int minutes){
        timer = new Timer(minutes);
    }

    /** Changes the size of the goals: "SMALL", "MEDIUM" or "LARGE". */
    public void setGoalSize(String size){
        goalSize = size;
        //Mediano es el tamano normal
        double width = 75;
        double height = 320;
        if ("SMALL".equals(size)) {
            height = 240;
        }
        if ("LARGE".equals(size)) {
            width = 100;
            height = 420;
        }
        //Se crean los arcos otra vez con el nuevo tamano
        leftGoal = new Goalpost('L', stadium.getWidth(), width, height);
        rightGoal = new Goalpost('R', stadium.getWidth(), width, height);
    }

    /** Makes it a match by goals. */
    public void setGoalsToWin(int goals){
        goalsToWin = goals;
    }

    /** Tells if the match is over. */
    public boolean isFinished(){
        int goals1 = score.getGoalsPlayer1();
        int goals2 = score.getGoalsPlayer2();
        //Por goles: alguien llego a los goles
        boolean goalsReached = goalsToWin > 0 && (goals1 >= goalsToWin || goals2 >= goalsToWin);
        //Por tiempo: se acabo el tiempo y no estan empatados
        boolean timeOver = timer != null && !timer.isInProgress() && getWinner() != null;
        return goalsReached || timeOver;
    }

    /** Tells if the match is in sudden death. */
    public boolean isSuddenDeath(){
        //Se acabo el tiempo y estan empatados, el siguiente gol gana
        return timer != null && !timer.isInProgress() && getWinner() == null;
    }

    /** Returns the player with more goals, or null if they are tied. */
    public Player getWinner(){
        if (score.getGoalsPlayer1() > score.getGoalsPlayer2()) {
            return player1;
        }
        if (score.getGoalsPlayer2() > score.getGoalsPlayer1()) {
            return player2;
        }
        return null;
    }

    /** Sends a pressed key to the players. */
    public void pressKey(int key){
        //Cada jugador revisa si la tecla es suya
        player1.pressKey(key);
        player2.pressKey(key);
    }

    /** Sends a released key to the players. */
    public void releaseKey(int key){
        player1.releaseKey(key);
        player2.releaseKey(key);
    }

    /** Changes the image of the ball. */
    public void setBallStyle(String ballStyle){
        this.ballStyle = ballStyle;
    }

    /** Returns the image of the ball. */
    public String getBallStyle(){
        return ballStyle;
    }

    /** Returns the size of the goals. */
    public String getGoalSize(){
        return goalSize;
    }

    /** Returns the goals needed to win, or 0 in a match by time. */
    public int getGoalsToWin(){
        return goalsToWin;
    }

    /** Tells if it is a match by time. */
    public boolean hasTimer(){
        return timer != null;
    }

    /** Returns the clock, or null in a match by goals. */
    public Timer getTimer(){
        return timer;
    }

    /** Returns player 1. */
    public Player getPlayer1(){
        return player1;
    }

    /** Returns player 2. */
    public Player getPlayer2(){
        return player2;
    }

    /** Returns the real ball. */
    public Ball getBall(){
        return ball;
    }

    /** Returns the fake balls. */
    public ArrayList<Ball> getFakeBalls(){
        return fakeBalls;
    }

    /** Returns the height where the ball starts. */
    public double getBallStartHeight(){
        return ballStartHeight;
    }

    /** Returns the score. */
    public Score getScore(){
        return score;
    }

    /** Returns the left goal. */
    public Goalpost getLeftGoal(){
        return leftGoal;
    }

    /** Returns the right goal. */
    public Goalpost getRightGoal(){
        return rightGoal;
    }

    /** Returns the stadium. */
    public Stadium getStadium(){
        return stadium;
    }

    /** Tells if the match is player vs player. */
    public boolean isMultiplayer(){
        return isMultiplayer;
    }
}
