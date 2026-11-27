package presentation;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import domain.PoobSoccer;

/**
 * Screen of the end of a match: the winner on the podium.
 */
public class WinnerView extends Screen {

    private final PoobSoccer game = PoobSoccer.getInstance();

    /** Creates the screen with the winner of the match that just finished. */
    public WinnerView(PoobSoccerGUI window){
        super(window);
        int width = window.getContentPane().getWidth();
        int height = window.getContentPane().getHeight();
        setLayout(null);

        //La imagen ocupa toda la ventana
        int winner = game.getWinnerNumber();
        background = new JLabel(createIcon("lib/" + backgroundName(winner) + ".png", width, height));
        background.setBounds(0, 0, width, height);
        add(background);

        //Las imagenes miden 1024 x 572, entonces estas son las medidas para la ventana
        double factorX = width / 1024.0;
        double factorY = height / 572.0;

        //El personaje esta parado sobre el podio, que queda en el centro de la imagen
        String image = "lib/" + game.getCharacterName(winner) + "1.png";
        ImageIcon character = new ImageIcon(image);
        int characterHeight = (int) (230 * factorY);
        int characterWidth = characterHeight * character.getIconWidth() / character.getIconHeight();
        JLabel characterLabel = new JLabel(createIcon(image, characterWidth, characterHeight));
        characterLabel.setBounds((int) (510 * factorX) - characterWidth / 2, (int) (385 * factorY) - characterHeight, characterWidth, characterHeight);
        background.add(characterLabel);

        //Home vuelve a la pantalla de inicio
        JButton homeButton = createImageButton("lib/home.png", 288, 122);
        homeButton.setLocation(width - scale(288) - scale(30), height - scale(122) - scale(20));
        background.add(homeButton);
        homeButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                window.showHome();
            }
        });
    }

    /** Returns the name of the background image for the winner: 1 or 2. */
    private String backgroundName(int winner){
        if (winner == 1) {
            return "Player1Win";
        }
        //Si el partido no es de dos jugadores, el jugador 2 es la maquina
        if (game.isMultiplayer()) {
            return "Player2Win";
        }
        return "IAWin";
    }
}
