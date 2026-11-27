package presentation;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.sound.sampled.*;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;
import domain.PoobSoccer;
import domain.PoobSoccerException;

/**
 * Window of the game. It shows one screen at a time and keeps what the players chose.
 */
public class PoobSoccerGUI extends JFrame {

    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final PoobSoccer game = PoobSoccer.getInstance();
    private HomeView homeView;
    private Clip music;
    private JMenuItem openItem;
    private JMenuItem saveItem;

    //Lo que se escoge en las pantallas antes del partido
    private String character1;
    private String character2;
    private String ball;
    private String stadium;
    private String goalSize;
    private String profile;     //Perfil de la maquina, o null si es jugador contra jugador
    private String matchType;   //"BY_TIME" o "BY_GOALS"
    private int matchValue;     //Minutos o goles

    /** Creates the window of the game. */
    public PoobSoccerGUI(){
        //La ventana ocupa toda la pantalla y no se puede cambiar de tamano
        setSize(screenSize.width, screenSize.height);
        setLocationRelativeTo(null);
        setResizable(false);

        //Al iniciar se cargan los puntos y las mejoras que se guardaron la ultima vez
        loadProgress();
        prepareMenu();
        homeView = new HomeView(this);
        showScreen(homeView);
        prepareMusic();

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                exitWindows();
            }
        });
    }

    /** Creates the menu bar with the options to open and save a match. */
    private void prepareMenu(){
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        openItem = new JMenuItem("Open match...");
        saveItem = new JMenuItem("Save match...");
        gameMenu.add(openItem);
        gameMenu.add(saveItem);
        menuBar.add(gameMenu);
        setJMenuBar(menuBar);

        openItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                openMatchFromFile();
            }
        });
        saveItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                saveMatchInFile();
            }
        });
    }

    /** Shows a screen in the window instead of the one that is there. */
    public void showScreen(JPanel screen){
        //Fuera de un partido solo se puede abrir, y dentro de un partido solo guardar
        openItem.setEnabled(true);
        saveItem.setEnabled(false);
        getContentPane().removeAll();
        getContentPane().add(screen, BorderLayout.CENTER);
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    /** Shows the start screen. */
    public void showHome(){
        showScreen(homeView);
    }

    /** Shows the screen to choose the modality. */
    public void showModes(){
        showScreen(new ModesView(this));
    }

    /** Shows the screen to choose the profile of the machine. */
    public void showProfile(Screen previous){
        showScreen(new ProfileView(this, previous));
    }

    /** Shows the screen to choose the characters. */
    public void showSelection(Screen previous){
        showScreen(new SelectionView(this, previous));
    }

    /** Shows the screen to choose the ball, the stadium and the goals. */
    public void showBallStadium(Screen previous){
        showScreen(new BallStadiumView(this, previous));
    }

    /** Shows the screen to choose the game mode. */
    public void showGameMode(Screen previous){
        showScreen(new GameModeView(this, previous));
    }

    /** Creates a match with what was chosen in the screens and starts it. */
    public void startMatch(){
        //Teclas en orden: izquierda, derecha, saltar, patear y poder
        int[] keys1 = {KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_Q};
        int[] keys2 = {KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_CONTROL};
        if (!createNewMatch(keys1, keys2)) {
            return;
        }
        //Partido por tiempo o por goles segun lo que se escogio
        if ("BY_TIME".equals(matchType)) {
            game.startTimer(matchValue);
        } else {
            game.setGoalsToWin(matchValue);
        }
        game.setGoalSize(goalSize);
        game.setBallStyle(ball);
        showMatch(false);
    }

    /** Creates the match against another player or against the machine, depending on the mode. Returns false if it could not. */
    private boolean createNewMatch(int[] keys1, int[] keys2){
        if (profile == null) {
            game.createMatch(character1, character2, stadium, keys1, keys2);
            return true;
        }
        try {
            game.createMachineMatch(character1, character2, stadium, keys1, profile);
            return true;
        } catch (PoobSoccerException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return false;
        }
    }

    /** Shows the match of the game and starts it. If it was just opened it starts paused. */
    public void showMatch(boolean paused){
        MatchView view = new MatchView(this);
        showScreen(view);
        openItem.setEnabled(false);
        saveItem.setEnabled(true);
        view.start(paused);
    }

    /** Asks for a file and saves the match that is being played in it. */
    private void saveMatchInFile(){
        JFileChooser chooser = createFileChooser();
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        //Si el nombre no termina en .poob se le agrega
        if (!file.getName().endsWith(".poob")) {
            file = new File(file.getPath() + ".poob");
        }
        try {
            game.saveMatch(file);
            JOptionPane.showMessageDialog(this, "Match saved.");
        } catch (PoobSoccerException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    /** Asks for a file and opens the saved match in it. It starts paused. */
    private void openMatchFromFile(){
        JFileChooser chooser = createFileChooser();
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            game.openMatch(chooser.getSelectedFile());
            showMatch(true);
        } catch (PoobSoccerException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    /** Creates the file chooser for the saved matches. */
    private JFileChooser createFileChooser(){
        JFileChooser chooser = new JFileChooser(new File("."));
        chooser.setFileFilter(new FileNameExtensionFilter("POOBSoccer saved match (*.poob)", "poob"));
        return chooser;
    }

    /** Loads the points and upgrades of the players. */
    private void loadProgress(){
        try {
            game.loadProgress();
        } catch (PoobSoccerException e) {
            //Si el archivo esta danado el juego empieza desde cero y avisa
            JOptionPane.showMessageDialog(this, "The saved progress could not be loaded. The game starts from zero.");
        }
    }

    /** Saves the points and upgrades of the players. */
    public void saveProgress(){
        try {
            game.saveProgress();
        } catch (PoobSoccerException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    /** Shows the winner of a finished match on the podium. */
    public void showWinner(){
        showScreen(new WinnerView(this));
    }

    /** Shows the shop. */
    public void showShop(){
        showScreen(new ShopView(this));
    }

    /** Saves the profile of the machine, or null if the match is against another player. */
    public void setProfile(String profile){
        this.profile = profile;
    }

    /** Saves the characters of both teams. */
    public void setCharacters(String character1, String character2){
        this.character1 = character1;
        this.character2 = character2;
    }

    /** Saves the ball, the stadium and the size of the goals. */
    public void setBallStadium(String ball, String stadium, String goalSize){
        this.ball = ball;
        this.stadium = stadium;
        this.goalSize = goalSize;
    }

    /** Saves the game mode: "BY_TIME" with minutes or "BY_GOALS" with goals. */
    public void setMatchMode(String matchType, int matchValue){
        this.matchType = matchType;
        this.matchValue = matchValue;
    }

    /** Returns the game of the domain, the only door to it. */
    public PoobSoccer getGame(){
        return game;
    }

    /** Loads the music and plays it. */
    private void prepareMusic(){
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(new File("lib/music.wav"));
            music = AudioSystem.getClip();
            music.open(audio);
            music.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            //El error completo va al log y al usuario se le muestra un mensaje sencillo
            game.recordError(e);
            JOptionPane.showMessageDialog(this, "Sorry, we couldn't load the music :( The game will go on without music.");
        }
    }

    /** Stops the music if it is playing, and plays it if it is stopped. */
    public void toggleMusic(){
        if (music == null) {
            return;
        }
        if (music.isRunning()) {
            music.stop();
        } else {
            music.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    /** Asks before closing the game. */
    private void exitWindows(){
        int answer = JOptionPane.showConfirmDialog(this, "Do you want to close the game?", "Confirm Exit", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    /** Starts the game. */
    public static void main(String[] args) {
        PoobSoccerGUI game = new PoobSoccerGUI();
        game.setVisible(true);
    }
}
