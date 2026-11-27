package presentation;

import java.awt.event.*;
import javax.swing.*;

/**
 * Start screen with the Play, Shop and sound buttons.
 */
public class HomeView extends Screen {

    /** Creates the start screen. */
    public HomeView(PoobSoccerGUI window){
        super(window);
        createBackground("lib/PoobSoccer.png");

        //Play lleva directo a escoger la modalidad
        JButton startButton = createImageButton("lib/StartButton.png", 300, 170);
        startButton.setLocation(screenSize.width / 2 - scale(150), screenSize.height * 36 / 100 - scale(100));
        background.add(startButton);
        startButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                window.showModes();
            }
        });

        //Shop esta debajo del boton de jugar
        JButton shopButton = createImageButton("lib/shop.png", 300, 95);
        shopButton.setLocation(screenSize.width / 2 - scale(150), screenSize.height * 36 / 100 + scale(80));
        background.add(shopButton);
        shopButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                window.showShop();
            }
        });

        //El boton de sonido esta arriba a la derecha
        JButton soundButton = createImageButton("lib/soundButton.png", 80, 80);
        soundButton.setLocation(screenSize.width - scale(100), scale(20));
        background.add(soundButton);
        soundButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                window.toggleMusic();
            }
        });
    }
}
