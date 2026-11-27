package presentation;

import java.awt.*;
import javax.swing.*;

/**
 * Scoreboard of the match: the image Marcador.png with the names, the score and the game mode.
 */
public class ScoreBoard extends JLabel {

    private double factor;     //Ancho real de la pantalla / 1920
    private JLabel name1;
    private JLabel score;
    private JLabel name2;
    private JLabel mode;
    private String modeText;

    /** Creates the scoreboard scaled to the screen. */
    public ScoreBoard(double factor){
        this.factor = factor;
        //La imagen mide 1024 x 169, en una pantalla de 1920 se muestra de 900 x 149
        Image image = new ImageIcon("lib/Marcador.png").getImage().getScaledInstance(scale(900), scale(149), Image.SCALE_SMOOTH);
        setIcon(new ImageIcon(image));
        setSize(scale(900), scale(149));

        //Los numeros son el centro de cada recuadro de la imagen
        name1 = createText(205, 49, 260, 30, Color.WHITE);
        score = createText(486, 49, 200, 48, new Color(20, 30, 70));
        name2 = createText(734, 49, 260, 30, Color.WHITE);
        mode = createText(486, 120, 600, 28, Color.WHITE);
        modeText = "";
    }

    /** Creates a text centered on a point of the image. */
    private JLabel createText(int centerX, int centerY, int width, int fontSize, Color color){
        int height = fontSize + 10;
        JLabel text = new JLabel("", SwingConstants.CENTER);
        text.setFont(new Font("Arial", Font.BOLD, scale(fontSize)));
        text.setForeground(color);
        text.setSize(scale(width), scale(height));
        //Se resta la mitad del ancho y del alto para que quede centrado
        text.setLocation(scale(centerX - width / 2), scale(centerY - height / 2));
        add(text);
        return text;
    }

    /** Shows the names and the goals. */
    public void setScore(String player1, int goals1, String player2, int goals2){
        name1.setText(player1.toUpperCase());
        score.setText(goals1 + " - " + goals2);
        name2.setText(player2.toUpperCase());
    }

    /** Shows the game mode. */
    public void setMode(String text){
        modeText = text;
        mode.setText(text);
    }

    /** Shows the clock next to the game mode. */
    public void setTime(String time, int remainingSeconds){
        mode.setText(modeText + "   " + time);
        //En los ultimos 10 segundos se pone rojo
        if (remainingSeconds <= 10) {
            mode.setForeground(Color.RED);
        } else {
            mode.setForeground(Color.WHITE);
        }
    }

    /** Scales a size made for a 1920 px wide screen. */
    private int scale(int value){
        return (int) Math.round(value * factor);
    }
}
