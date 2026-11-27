package domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Main class of the game. There is only one, and the presentation talks to the domain only through it.
 */
public class PoobSoccer {

    private static PoobSoccer instance;

    private String statusMatch;   //"NO_MATCH" o "PLAYING"
    private Match actualMatch;
    private Stats stats1;         //Puntos y mejoras del jugador 1, se guardan entre partidos
    private Stats stats2;         //Puntos y mejoras del jugador 2
    private String progressFile;  //Archivo donde se guardan los puntos y las mejoras de los dos jugadores

    /** Creates the game without a match. It is private because there is only one game. */
    private PoobSoccer(){
        statusMatch = "NO_MATCH";
        actualMatch = null;
        stats1 = new Stats();
        stats2 = new Stats();
        progressFile = "progress.dat";
    }

    /** Returns the only game, and creates it the first time. */
    public static PoobSoccer getInstance(){
        if (instance == null) {
            instance = new PoobSoccer();
        }
        return instance;
    }

    /** Starts again from zero: no match, no points and no upgrades. */
    public void restart(){
        statusMatch = "NO_MATCH";
        actualMatch = null;
        stats1 = new Stats();
        stats2 = new Stats();
        progressFile = "progress.dat";
    }

    //Crear el partido
    /** Creates a player vs player match. Keys: left, right, jump, kick, power. */
    public Match createMatch(String character1, String character2, String stadiumName, int[] keys1, int[] keys2){
        Stadium stadium = Stadium.create(stadiumName);
        double width = stadium.getWidth();

        //El jugador 1 empieza en un cuarto de la cancha y el jugador 2 en tres cuartos
        HumanPlayer player1 = new HumanPlayer(character1, createCharacter(character1), width / 4, 1,
                                              keys1[0], keys1[1], keys1[2], keys1[3], keys1[4]);

        HumanPlayer player2 = new HumanPlayer(character2, createCharacter(character2), width * 3 / 4, 2,
                                              keys2[0], keys2[1], keys2[2], keys2[3], keys2[4]);

        //Cada jugador juega con las mejoras que compro en la tienda
        player1.applyStats(stats1);
        player2.applyStats(stats2);
        actualMatch = new Match(player1, player2, stadium, true);
        statusMatch = "PLAYING";
        return actualMatch;
    }

    /** Creates a match of a player against the machine. Player 2 is the machine with the given profile. */
    public Match createMachineMatch(String character1, String character2, String stadiumName, int[] keys1, String profile) throws PoobSoccerException {
        Stadium stadium = Stadium.create(stadiumName);
        double width = stadium.getWidth();

        HumanPlayer player1 = new HumanPlayer(character1, createCharacter(character1), width / 4, 1,
                                              keys1[0], keys1[1], keys1[2], keys1[3], keys1[4]);
        player1.applyStats(stats1);
        //La maquina no tiene mejoras, siempre juega con las estadisticas base
        AIPlayer machine = createMachine(profile, character2, width * 3 / 4);

        actualMatch = new Match(player1, machine, stadium, false);
        statusMatch = "PLAYING";
        return actualMatch;
    }

    /** Creates the machine with the way of playing that has the given profile name. */
    private AIPlayer createMachine(String profile, String name, double posX) throws PoobSoccerException {
        Character character = createCharacter(name);
        if ("Clumsy".equals(profile)) {
            return new Clumsy(name, character, posX);
        }
        if ("Offensive".equals(profile)) {
            return new Offensive(name, character, posX);
        }
        if ("Defensive".equals(profile)) {
            return new Defensive(name, character, posX);
        }
        if ("Expert".equals(profile)) {
            return new Expert(name, character, posX);
        }
        throw new PoobSoccerException(PoobSoccerException.INVALID_PROFILE);
    }

    /** Creates a character with its power. */
    private Character createCharacter(String name){
        //Cada personaje tiene su poder, Rigby todavia no tiene
        Power power = null;
        if ("Dipper".equals(name)) {
            power = new BlueAura();
        }
        if ("Gumball".equals(name)) {
            power = new Ray();
        }
        if ("Jack".equals(name)) {
            power = new Tornado();
        }
        if ("Raven".equals(name)) {
            power = new Illusion();
        }
        //Por ahora todos tienen la misma velocidad, salto y fuerza
        return new Character(name, 8, 18, 10, power);
    }

    /** Changes how the match ends: by time, with the minutes. */
    public void startTimer(int minutes){
        actualMatch.startTimer(minutes);
    }

    /** Changes how the match ends: by goals, with the goals needed to win. */
    public void setGoalsToWin(int goals){
        actualMatch.setGoalsToWin(goals);
    }

    /** Changes the size of the goals: "SMALL", "MEDIUM" or "LARGE". */
    public void setGoalSize(String size){
        actualMatch.setGoalSize(size);
    }

    /** Changes the style of the ball. */
    public void setBallStyle(String ballStyle){
        actualMatch.setBallStyle(ballStyle);
    }

    //Jugar el partido
    /** Plays one frame of the match. */
    public void updateMatch(){
        actualMatch.update();
    }

    /** A key was pressed. */
    public void pressKey(int key){
        actualMatch.pressKey(key);
    }

    /** A key was released. */
    public void releaseKey(int key){
        actualMatch.releaseKey(key);
    }

    /** Returns true if the match is over. */
    public boolean isFinished(){
        return actualMatch.isFinished();
    }

    /** Gives 100 points to the winner and 25 to the loser. */
    public void givePoints(){
        if (actualMatch == null || actualMatch.getWinner() == null) {
            return;
        }
        //Contra la maquina solo el jugador 1 gana puntos, la maquina no tiene progreso
        if (!actualMatch.isMultiplayer()) {
            if (actualMatch.getWinner() == actualMatch.getPlayer1()) {
                stats1.addPoints(100);
            } else {
                stats1.addPoints(25);
            }
            return;
        }
        //El ganador recibe 100 puntos y el que perdio 25
        if (actualMatch.getWinner() == actualMatch.getPlayer1()) {
            stats1.addPoints(100);
            stats2.addPoints(25);
        } else {
            stats1.addPoints(25);
            stats2.addPoints(100);
        }
    }

    /** Ends the match. */
    public void endMatch(){
        actualMatch = null;
        statusMatch = "NO_MATCH";
    }

    //Guardar y abrir
    /** Saves the match that is being played in a file. */
    public void saveMatch(File file) throws PoobSoccerException {
        if (actualMatch == null) {
            throw new PoobSoccerException(PoobSoccerException.NO_MATCH_TO_SAVE);
        }
        //Se guarda el partido completo: jugadores, balon, arcos, marcador y reloj
        try (FileOutputStream stream = new FileOutputStream(file);
             ObjectOutputStream out = new ObjectOutputStream(stream)) {
            out.writeObject(actualMatch);
        } catch (IOException e) {
            Log.record(e);
            throw new PoobSoccerException(PoobSoccerException.SAVE_ERROR);
        }
    }

    /** Opens a saved match and leaves it ready to go on playing. */
    public Match openMatch(File file) throws PoobSoccerException {
        try (FileInputStream stream = new FileInputStream(file);
             ObjectInputStream in = new ObjectInputStream(stream)) {
            Match match = (Match) in.readObject();
            actualMatch = match;
            statusMatch = "PLAYING";
            return match;
        } catch (ClassCastException e) {
            //Es un archivo de java pero no es un partido
            throw new PoobSoccerException(PoobSoccerException.INVALID_FILE);
        } catch (ClassNotFoundException e) {
            throw new PoobSoccerException(PoobSoccerException.INVALID_FILE);
        } catch (java.io.InvalidClassException e) {
            //El archivo se guardo con una version anterior de las clases
            throw new PoobSoccerException(PoobSoccerException.OLD_VERSION);
        } catch (java.io.EOFException e) {
            throw new PoobSoccerException(PoobSoccerException.INVALID_FILE);
        } catch (java.io.StreamCorruptedException e) {
            //Ni siquiera es un archivo guardado por el juego
            throw new PoobSoccerException(PoobSoccerException.INVALID_FILE);
        } catch (java.io.FileNotFoundException e) {
            //Si el archivo no existe no es un error del programa, no se guarda en el log
            throw new PoobSoccerException(PoobSoccerException.OPEN_ERROR);
        } catch (IOException e) {
            Log.record(e);
            throw new PoobSoccerException(PoobSoccerException.OPEN_ERROR);
        }
    }

    /** Saves the points and the upgrades of both players. */
    public void saveProgress() throws PoobSoccerException {
        try (FileOutputStream stream = new FileOutputStream(progressFile);
             ObjectOutputStream out = new ObjectOutputStream(stream)) {
            out.writeObject(stats1);
            out.writeObject(stats2);
        } catch (IOException e) {
            Log.record(e);
            throw new PoobSoccerException(PoobSoccerException.SAVE_ERROR);
        }
    }

    /** Loads the points and the upgrades of both players, if they were saved before. */
    public void loadProgress() throws PoobSoccerException {
        File file = new File(progressFile);
        //La primera vez que se juega todavia no hay archivo y se empieza desde cero
        if (!file.exists()) {
            return;
        }
        try (FileInputStream stream = new FileInputStream(file);
             ObjectInputStream in = new ObjectInputStream(stream)) {
            Stats loaded1 = (Stats) in.readObject();
            Stats loaded2 = (Stats) in.readObject();
            stats1 = loaded1;
            stats2 = loaded2;
        } catch (ClassCastException e) {
            throw new PoobSoccerException(PoobSoccerException.INVALID_FILE);
        } catch (ClassNotFoundException e) {
            throw new PoobSoccerException(PoobSoccerException.INVALID_FILE);
        } catch (java.io.InvalidClassException e) {
            //El archivo se guardo con una version anterior de las clases
            throw new PoobSoccerException(PoobSoccerException.OLD_VERSION);
        } catch (java.io.EOFException e) {
            throw new PoobSoccerException(PoobSoccerException.INVALID_FILE);
        } catch (java.io.StreamCorruptedException e) {
            throw new PoobSoccerException(PoobSoccerException.INVALID_FILE);
        } catch (IOException e) {
            Log.record(e);
            throw new PoobSoccerException(PoobSoccerException.OPEN_ERROR);
        }
    }

    /** Changes the file of the progress. */
    public void setProgressFile(String progressFile){
        this.progressFile = progressFile;
    }

    /** Saves an unexpected error in the log. */
    public void recordError(Exception e){
        Log.record(e);
    }

    //Tienda: el jugador es 1 o 2
    /** Returns the stats of a player: 1 or 2. */
    private Stats getStats(int player){
        if (player == 1) {
            return stats1;
        }
        return stats2;
    }

    /** Returns the points of a player. */
    public int getPoints(int player){
        return getStats(player).getPoints();
    }

    /** Returns the level of a stat of a player. */
    public int getLevel(int player, String stat){
        return getStats(player).getLevel(stat);
    }

    /** Returns the price of the next level of a stat of a player. */
    public int getPrice(int player, String stat){
        return getStats(player).getPrice(stat);
    }

    /** Returns the highest level of a stat. */
    public int getMaxLevel(){
        return stats1.getMaxLevel();
    }

    /** Buys one level of a stat for a player. */
    public void upgrade(int player, String stat) throws PoobSoccerException {
        getStats(player).upgrade(stat);
    }

    //Lo que se dibuja del partido: el jugador es 1 o 2, el arco es 1 (izquierda) o 2 (derecha)
    /** Returns a player of the match: 1 or 2. */
    private Player getPlayer(int number){
        if (number == 1) {
            return actualMatch.getPlayer1();
        }
        return actualMatch.getPlayer2();
    }

    /** Returns a goal of the match: 1 is the left one and 2 the right one. */
    private Goalpost getGoal(int number){
        if (number == 1) {
            return actualMatch.getLeftGoal();
        }
        return actualMatch.getRightGoal();
    }

    /** Returns the name of the stadium. */
    public String getStadiumName(){
        return actualMatch.getStadium().getName();
    }

    /** Returns the style of the ball. */
    public String getBallStyle(){
        return actualMatch.getBallStyle();
    }

    /** Returns the radius of the ball. */
    public double getBallRadius(){
        return actualMatch.getBall().getRadius();
    }

    /** Returns the x of the center of the ball. */
    public double getBallX(){
        return actualMatch.getBall().getPosX();
    }

    /** Returns the y of the center of the ball, 0 is the ground. */
    public double getBallY(){
        return actualMatch.getBall().getPosY();
    }

    /** Returns how many fake balls there are. */
    public int getFakeBallCount(){
        return actualMatch.getFakeBalls().size();
    }

    /** Returns the x of a fake ball. */
    public double getFakeBallX(int index){
        return actualMatch.getFakeBalls().get(index).getPosX();
    }

    /** Returns the y of a fake ball. */
    public double getFakeBallY(int index){
        return actualMatch.getFakeBalls().get(index).getPosY();
    }

    /** Returns the name of the character of a player. */
    public String getCharacterName(int player){
        return getPlayer(player).getCharacter().getName();
    }

    /** Returns the x of the center of a player. */
    public double getPlayerX(int player){
        return getPlayer(player).getPosX();
    }

    /** Returns the y of the feet of a player, 0 is the ground. */
    public double getPlayerY(int player){
        return getPlayer(player).getPosY();
    }

    /** Returns the width of a player. */
    public double getPlayerWidth(int player){
        return getPlayer(player).getWidth();
    }

    /** Returns the height of a player. */
    public double getPlayerHeight(int player){
        return getPlayer(player).getHeight();
    }

    /** Returns where a player looks: 1 to the right and -1 to the left. */
    public int getFacing(int player){
        return getPlayer(player).getFacing();
    }

    /** Returns true if a player is kicking. */
    public boolean isKicking(int player){
        return getPlayer(player).isKicking();
    }

    /** Returns true if a player has the aura of the power. */
    public boolean hasAura(int player){
        return getPlayer(player).hasAura();
    }

    /** Returns the levels that are full in the power bar of a player. */
    public int getEnergyPower(int player){
        return getPlayer(player).getEnergyPower();
    }

    /** Returns the levels of the power bar. */
    public int getMaxPower(){
        return actualMatch.getPlayer1().getMaxPower();
    }

    /** Returns the life of a player. */
    public double getLife(int player){
        return getPlayer(player).getLife();
    }

    /** Returns the life of a player when it is full. */
    public double getMaxLife(int player){
        return getPlayer(player).getMaxLife();
    }

    /** Returns the name of the power that affects a player, or an empty text if nothing affects it. */
    public String getEffectName(int player){
        Player affected = getPlayer(player);
        if (affected.isUnderEffect()) {
            return affected.getEffect().getName();
        }
        return "";
    }

    /** Returns the x of the left side of a goal. */
    public double getGoalX(int goal){
        return getGoal(goal).getPosX();
    }

    /** Returns the y of the bottom of a goal. */
    public double getGoalY(int goal){
        return getGoal(goal).getPosY();
    }

    /** Returns the width of a goal. */
    public double getGoalWidth(int goal){
        return getGoal(goal).getWidth();
    }

    /** Returns the height of a goal. */
    public double getGoalHeight(int goal){
        return getGoal(goal).getHeight();
    }

    /** Returns the goals that a player has scored. */
    public int getGoals(int player){
        if (player == 1) {
            return actualMatch.getScore().getGoalsPlayer1();
        }
        return actualMatch.getScore().getGoalsPlayer2();
    }

    /** Returns the goals needed to win, or 0 if the match is by time. */
    public int getGoalsToWin(){
        return actualMatch.getGoalsToWin();
    }

    /** Returns true if the match is by time. */
    public boolean hasTimer(){
        return actualMatch.hasTimer();
    }

    /** Returns true if the match is in sudden death. */
    public boolean isSuddenDeath(){
        return actualMatch.isSuddenDeath();
    }

    /** Returns the time of the match in seconds. */
    public int getTotalTime(){
        return actualMatch.getTimer().getTotalTime();
    }

    /** Returns the seconds that are left. */
    public int getRemainingTime(){
        return actualMatch.getTimer().getRemainingTime();
    }

    /** Returns the clock written as minutes and seconds. */
    public String getTimerText(){
        return actualMatch.getTimer().getText();
    }

    /** Returns the winner: 1 or 2, or 0 if there is no winner. */
    public int getWinnerNumber(){
        Player winner = actualMatch.getWinner();
        if (winner == null) {
            return 0;
        }
        if (winner == actualMatch.getPlayer1()) {
            return 1;
        }
        return 2;
    }

    /** Returns true if the match is between two people. */
    public boolean isMultiplayer(){
        return actualMatch.isMultiplayer();
    }

    //Estado del juego
    /** Returns the match that is being played. */
    public Match getActualMatch(){
        return actualMatch;
    }

    /** Returns the stats of player 1. */
    public Stats getStats1(){
        return stats1;
    }

    /** Returns the stats of player 2. */
    public Stats getStats2(){
        return stats2;
    }

    /** Returns the status of the match. */
    public String getStatusMatch(){
        return statusMatch;
    }
}
