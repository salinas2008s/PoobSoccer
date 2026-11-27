package presentation;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import domain.PoobSoccer;

/**
 * Screen of the match: the field, the players, the bars, the score and the pause.
 */
public class MatchView extends Screen {

    private final PoobSoccer game = PoobSoccer.getInstance();
    private JLabel player1Label, player2Label, ballLabel;
    private JLabel matchBackground;
    private ArrayList<JLabel> fakeBallLabels;
    private ScoreBoard scoreBoard;
    private JButton resumeButton;
    private JButton homeButton;
    private JPanel pausePanel;      //Capa oscura que tapa el partido mientras esta en pausa
    private JLabel power1Label, power2Label;
    private ImageIcon[] powerIcons;
    private int powerWidth = 300, powerHeight = 80;
    private JLabel life1Label, life2Label;
    private ImageIcon[] lifeIcons;
    private int lifeWidth = 300, lifeHeight = 44;
    private JLabel ray1Label, ray2Label;
    private JLabel storm1Label, storm2Label;
    private javax.swing.Timer matchTimer;
    private KeyEventDispatcher keyDispatcher;
    private final Map<String, ImageIcon> playerIcons = new HashMap<String, ImageIcon>();   //Imagenes de los jugadores ya cargadas
    private int groundY;   //Altura en pantalla del suelo de la cancha

    /** Creates the screen of a match that is already created, new or opened from a file. */
    public MatchView(PoobSoccerGUI window){
        super(window);
        createField();
        createPausePanel();
        prepareKeys();
        prepareClock();
        drawMatch();
    }

    /** Creates the field with the score, the bars, the players, the ball and the goals. */
    private void createField(){
        int width = window.getContentPane().getWidth();
        int height = window.getContentPane().getHeight();
        //El suelo de la cancha es la parte de abajo de la ventana
        groundY = height;

        setLayout(null);
        matchBackground = new JLabel(createIcon("lib/" + game.getStadiumName() + ".png", width, height));
        matchBackground.setBounds(0, 0, width, height);
        add(matchBackground);

        //Lo que se agrega primero queda encima de lo que se agrega despues
        scoreBoard = new ScoreBoard((double) screenSize.width / 1920);
        scoreBoard.setLocation(width / 2 - scoreBoard.getWidth() / 2, scale(20));
        scoreBoard.setMode(gameModeText());
        matchBackground.add(scoreBoard);

        //Boton de pausa pequeno a la derecha del marcador
        JButton pauseButton = createImageButton("lib/pause.png", 70, 70);
        pauseButton.setLocation(width / 2 + scoreBoard.getWidth() / 2 + scale(10), scale(40));
        matchBackground.add(pauseButton);
        pauseButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                pauseMatch();
            }
        });

        int ballSize = scale((int) (game.getBallRadius() * 2));
        ballLabel = new JLabel(createIcon("lib/" + game.getBallStyle() + ".png", ballSize, ballSize));
        ballLabel.setSize(ballSize, ballSize);
        matchBackground.add(ballLabel);
        fakeBallLabels = new ArrayList<JLabel>();

        //Barras de poder: una imagen por cada nivel, de Power0 a Power5
        powerIcons = new ImageIcon[game.getMaxPower() + 1];
        for (int i = 0; i <= game.getMaxPower(); i++) {
            powerIcons[i] = createIcon("lib/Power" + i + ".png", scale(powerWidth), scale(powerHeight));
        }
        power1Label = createBarLabel(scale(30), scale(80), scale(powerWidth), scale(powerHeight));
        power2Label = createBarLabel(width - scale(30) - scale(powerWidth), scale(80), scale(powerWidth), scale(powerHeight));

        //Barras de vida: hay una imagen cada media barra, de 0Life a 5Life
        lifeIcons = new ImageIcon[11];
        for (int i = 0; i <= 10; i++) {
            lifeIcons[i] = createIcon("lib/" + lifeImageName(i) + ".png", scale(lifeWidth), scale(lifeHeight));
        }
        life1Label = createBarLabel(scale(30), scale(25), scale(lifeWidth), scale(lifeHeight));
        life2Label = createBarLabel(width - scale(30) - scale(lifeWidth), scale(25), scale(lifeWidth), scale(lifeHeight));

        //Rayos: solo se ven cuando un jugador esta electrocutado
        ray1Label = createEffectLabel("lib/Ray.png", 50, 90);
        ray2Label = createEffectLabel("lib/Ray.png", 50, 90);

        player1Label = new JLabel();
        player1Label.setSize(scale((int) game.getPlayerWidth(1)), scale((int) game.getPlayerHeight(1)));
        player2Label = new JLabel();
        player2Label.setSize(scale((int) game.getPlayerWidth(2)), scale((int) game.getPlayerHeight(2)));
        matchBackground.add(player1Label);
        matchBackground.add(player2Label);

        //Tormentas: se agregan despues de los jugadores para que el jugador quede encima
        storm1Label = createEffectLabel("lib/Storm.png", 230, 360);
        storm2Label = createEffectLabel("lib/Storm.png", 230, 360);

        //Los arcos van de ultimas para que queden detras de los jugadores y el balon
        matchBackground.add(createGoalLabel(1, "lib/goalLeft.png"));
        matchBackground.add(createGoalLabel(2, "lib/goalRight.png"));
    }

    /** Creates an empty label for a bar and adds it to the field. */
    private JLabel createBarLabel(int x, int y, int width, int height){
        JLabel label = new JLabel();
        label.setBounds(x, y, width, height);
        matchBackground.add(label);
        return label;
    }

    /** Creates a label for an effect that is hidden until it is needed, and adds it to the field. */
    private JLabel createEffectLabel(String image, int width, int height){
        JLabel label = new JLabel(createIcon(image, scale(width), scale(height)));
        label.setSize(scale(width), scale(height));
        label.setVisible(false);
        matchBackground.add(label);
        return label;
    }

    /** Creates the dark layer of the pause with the resume and home buttons. */
    private void createPausePanel(){
        int width = matchBackground.getWidth();
        int height = matchBackground.getHeight();
        //Un panel transparente que se pinta de negro con transparencia, asi el partido se ve oscuro detras
        pausePanel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                g.setColor(new Color(0, 0, 0, 160));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        pausePanel.setOpaque(false);
        pausePanel.setLayout(null);
        pausePanel.setBounds(0, 0, width, height);
        pausePanel.setVisible(false);

        //Resume arriba y home abajo, en el centro de la pantalla. Las imagenes miden 768 x 325, se muestran a la mitad
        resumeButton = createImageButton("lib/resume.png", 384, 162);
        resumeButton.setLocation(width / 2 - scale(192), height / 2 - scale(180));
        homeButton = createImageButton("lib/home.png", 384, 162);
        homeButton.setLocation(width / 2 - scale(192), height / 2 + scale(20));
        pausePanel.add(resumeButton);
        pausePanel.add(homeButton);

        resumeButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                resumeMatch();
            }
        });
        homeButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                goHome();
            }
        });

        //La pausa se agrega en la posicion 0 para que quede encima de todo
        matchBackground.add(pausePanel, 0);
    }

    /** Starts the clock of the match, or leaves it paused if it was just opened. */
    public void start(boolean paused){
        if (paused) {
            pauseMatch();
        } else {
            matchTimer.start();
        }
    }

    /** Pauses the match: the clock of the game stops and the pause layer appears. */
    private void pauseMatch(){
        matchTimer.stop();
        pausePanel.setVisible(true);
    }

    /** Goes on with the match after a pause. */
    private void resumeMatch(){
        pausePanel.setVisible(false);
        matchTimer.start();
    }

    /** Stops the clock and the keys. */
    private void stopMatch(){
        matchTimer.stop();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(keyDispatcher);
    }

    /** Stops the match and goes back to the start screen. */
    private void goHome(){
        stopMatch();
        game.endMatch();
        window.showHome();
    }

    /** Prepares the keys of the match. */
    private void prepareKeys(){
        //Se usa KeyEventDispatcher porque recibe todas las teclas, aunque este presionado Ctrl
        keyDispatcher = new KeyEventDispatcher(){
            public boolean dispatchKeyEvent(KeyEvent e){
                int key = e.getKeyCode();
                //Solo cuenta el Ctrl derecho, el izquierdo se ignora
                if (key == KeyEvent.VK_CONTROL && e.getKeyLocation() != KeyEvent.KEY_LOCATION_RIGHT) {
                    return false;
                }
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    //Esc sale del partido y vuelve al inicio, las demas teclas van al dominio
                    if (key == KeyEvent.VK_ESCAPE) {
                        goHome();
                    } else {
                        game.pressKey(key);
                    }
                } else if (e.getID() == KeyEvent.KEY_RELEASED) {
                    game.releaseKey(key);
                }
                return false;
            }
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyDispatcher);
    }

    /** Prepares the clock of the match. */
    private void prepareClock(){
        //El partido avanza 60 cuadros por segundo
        matchTimer = new javax.swing.Timer(1000 / 60, new ActionListener(){
            public void actionPerformed(ActionEvent e){
                playMatchFrame();
            }
        });
    }

    /** Plays one frame of the match. */
    private void playMatchFrame(){
        try {
            //Primero el dominio mueve todo y despues se dibuja
            game.updateMatch();
            drawMatch();
            if (game.isFinished()) {
                finishMatch();
            }
        } catch (Exception e) {
            //Si pasa un error inesperado se guarda en el log, se avisa y se vuelve al inicio sin cerrar el juego
            game.recordError(e);
            matchTimer.stop();
            showMessage("Something went wrong in the match. The game goes back to the start.");
            goHome();
        }
    }

    /** Gives the points and shows the winner on the podium. */
    private void finishMatch(){
        matchTimer.stop();
        //Los puntos se reparten una sola vez, cuando el partido termina: 100 al ganador y 25 al perdedor
        game.givePoints();
        window.saveProgress();
        stopMatch();
        //La pantalla del ganador lee el partido, por eso el partido se termina despues de mostrarla
        window.showWinner();
        game.endMatch();
    }

    /** Creates the image of a goal: 1 is the left one and 2 the right one. */
    private JLabel createGoalLabel(int goal, String image){
        int width = scale((int) game.getGoalWidth(goal));
        int height = scale((int) game.getGoalHeight(goal));
        JLabel label = new JLabel(createIcon(image, width, height));
        label.setSize(width, height);
        label.setLocation(scale((int) game.getGoalX(goal)), groundY - scale((int) game.getGoalY(goal)) - height);
        return label;
    }

    /** Draws everything of the match. */
    private void drawMatch(){
        drawPlayer(player1Label, 1);
        drawPlayer(player2Label, 2);
        drawBall(ballLabel, game.getBallX(), game.getBallY());
        drawFakeBalls();
        power1Label.setIcon(powerIcons[game.getEnergyPower(1)]);
        power2Label.setIcon(powerIcons[game.getEnergyPower(2)]);
        life1Label.setIcon(lifeIcons[halfBlocks(1)]);
        life2Label.setIcon(lifeIcons[halfBlocks(2)]);
        drawRay(ray1Label, 1, player1Label);
        drawRay(ray2Label, 2, player2Label);
        drawStorm(storm1Label, 1, player1Label);
        drawStorm(storm2Label, 2, player2Label);
        scoreBoard.setScore(game.getCharacterName(1), game.getGoals(1),
                            game.getCharacterName(2), game.getGoals(2));
        if (game.isSuddenDeath()) {
            //Muerte subita: sale en rojo porque se pasan 0 segundos
            scoreBoard.setTime("SUDDEN DEATH", 0);
        } else if (game.hasTimer()) {
            scoreBoard.setTime(game.getTimerText(), game.getRemainingTime());
        }
    }

    /** Puts a ball where the domain says, with the position of its center. */
    private void drawBall(JLabel label, double ballX, double ballY){
        //En el dominio la y crece hacia arriba y en la pantalla hacia abajo, por eso se resta de groundY
        int x = scale((int) Math.round(ballX)) - label.getWidth() / 2;
        int y = groundY - scale((int) Math.round(ballY)) - label.getHeight() / 2;
        label.setLocation(x, y);
    }

    /** Draws the fake balls of the Illusion. */
    private void drawFakeBalls(){
        int fakes = game.getFakeBallCount();
        //Si hay mas balones falsos que labels, se crean labels nuevos al lado del balon real
        while (fakeBallLabels.size() < fakes) {
            JLabel label = new JLabel(ballLabel.getIcon());
            label.setSize(ballLabel.getWidth(), ballLabel.getHeight());
            matchBackground.add(label, matchBackground.getComponentZOrder(ballLabel) + 1);
            fakeBallLabels.add(label);
        }
        //Se muestran los que hay y se esconden los que sobran
        for (int i = 0; i < fakeBallLabels.size(); i++) {
            JLabel label = fakeBallLabels.get(i);
            if (i < fakes) {
                label.setVisible(true);
                drawBall(label, game.getFakeBallX(i), game.getFakeBallY(i));
            } else {
                label.setVisible(false);
            }
        }
    }

    /** Shows the ray over an electrocuted player. */
    private void drawRay(JLabel ray, int player, JLabel playerLabel){
        //Solo se ve si el efecto que tiene el jugador es el Rayo
        boolean electrocuted = "Ray".equals(game.getEffectName(player));
        ray.setVisible(electrocuted);
        if (electrocuted) {
            ray.setLocation(playerLabel.getX() + playerLabel.getWidth() / 2 - ray.getWidth() / 2,
                            playerLabel.getY() - ray.getHeight());
        }
    }

    /** Shows the storm under a player hit by the Tornado. */
    private void drawStorm(JLabel storm, int player, JLabel playerLabel){
        //Solo se ve si el efecto que tiene el jugador es el Tornado, y sale desde el suelo
        boolean unconscious = "Tornado".equals(game.getEffectName(player));
        storm.setVisible(unconscious);
        if (unconscious) {
            storm.setLocation(playerLabel.getX() + playerLabel.getWidth() / 2 - storm.getWidth() / 2,
                              groundY - storm.getHeight());
        }
    }

    /** Returns how many half blocks of life the player has. */
    private int halfBlocks(int player){
        //Se redondea hacia arriba para que con poquita vida se vea media barra
        return (int) Math.ceil(game.getLife(player) / game.getMaxLife(player) * 10);
    }

    /** Returns the name of the life image. */
    private String lifeImageName(int halfBlocks){
        //Por ejemplo 7 medias barras es la imagen 3.5Life
        String name = "" + (halfBlocks / 2);
        if (halfBlocks % 2 == 1) {
            name = name + ".5";
        }
        return name + "Life";
    }

    /** Returns the text of the game mode. */
    private String gameModeText(){
        //Por tiempo se muestran los minutos y por goles los goles que hay que hacer
        if (game.hasTimer()) {
            int minutes = game.getTotalTime() / 60;
            if (minutes == 1) {
                return "1 MINUTE";
            }
            return minutes + " MINUTES";
        }
        return game.getGoalsToWin() + " GOALS";
    }

    /** Puts the player where the domain says, with its image. */
    private void drawPlayer(JLabel label, int player){
        //La imagen se arma con el nombre, Po si tiene aura y el numero, por ejemplo JackPo2
        String image = game.getCharacterName(player);
        if (game.hasAura(player)) {
            image = image + "Po";
        }
        //Las imagenes 1 y 2 miran a la derecha y las 3 y 4 son las volteadas, que miran a la izquierda
        if (game.getFacing(player) == -1) {
            if (game.isKicking(player)) {
                image = image + "4";
            } else {
                image = image + "3";
            }
        } else {
            if (game.isKicking(player)) {
                image = image + "2";
            } else {
                image = image + "1";
            }
        }
        label.setIcon(playerIcon(image, label.getWidth(), label.getHeight()));
        label.setLocation(scale((int) Math.round(game.getPlayerX(player))) - label.getWidth() / 2,
                          groundY - scale((int) Math.round(game.getPlayerY(player))) - label.getHeight());
    }

    /** Returns the image of a player. */
    private ImageIcon playerIcon(String name, int width, int height){
        //Cada imagen se carga una sola vez y se guarda en el mapa
        ImageIcon icon = playerIcons.get(name);
        if (icon == null) {
            icon = createIcon("lib/" + name + ".png", width, height);
            playerIcons.put(name, icon);
        }
        return icon;
    }
}
