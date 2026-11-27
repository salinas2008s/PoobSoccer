package presentation;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import domain.PoobSoccer;
import domain.PoobSoccerException;

/**
 * Shop with the points and the upgrades of each player.
 */
public class ShopView extends Screen {

    private final String[] statNames = {"Speed", "Jump", "Kick", "Survival"};   //Mejoras que se compran en la tienda
    private final PoobSoccer game = PoobSoccer.getInstance();

    /** Creates the shop. */
    public ShopView(PoobSoccerGUI window){
        super(window);
        createBackground("lib/PoobUpgrates.png");

        JButton backButton = createBackButton();
        background.add(backButton);
        backButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                window.showHome();
            }
        });

        //Un panel por jugador, el jugador 1 a la izquierda y el jugador 2 a la derecha
        background.add(createStatsPanel(1, "PLAYER 1", 110));
        background.add(createStatsPanel(2, "PLAYER 2", 1010));
    }

    /** Creates the panel of a player with its name, its points and its four upgrades. */
    private JPanel createStatsPanel(int player, String title, int x){
        //Panel negro con transparencia para que se lea encima del fondo
        JPanel panel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                g.setColor(new Color(0, 0, 0, 170));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setOpaque(false);
        panel.setLayout(null);
        panel.setBounds(scale(x), scale(140), scale(800), scale(840));

        //Nombre del jugador
        JLabel name = new JLabel(title);
        name.setFont(new Font("Arial", Font.BOLD, scale(56)));
        name.setForeground(Color.WHITE);
        name.setSize(scale(400), scale(100));
        panel.add(name);

        //Puntos: el jugador 1 tiene la moneda a la izquierda y el jugador 2 a la derecha
        String pointsImage = "lib/Puntos.png";
        int pointsX = 460;
        int textX = 125;
        int nameX = 40;
        if (player == 2) {
            pointsImage = "lib/Puntos2.png";
            pointsX = 40;
            textX = 10;
            nameX = 400;
            name.setHorizontalAlignment(SwingConstants.RIGHT);
        }
        name.setLocation(scale(nameX), scale(25));

        //El texto de los puntos va primero para que quede encima de la imagen
        JLabel points = new JLabel("" + game.getPoints(player), SwingConstants.CENTER);
        points.setFont(new Font("Arial", Font.BOLD, scale(44)));
        points.setForeground(Color.WHITE);
        points.setBounds(scale(pointsX + textX), scale(45), scale(165), scale(90));
        panel.add(points);
        panel.add(createImageLabel(pointsImage, pointsX, 20, 300, 149));

        //Una fila por mejora: la barra, el boton de mejorar y el precio
        for (int i = 0; i < statNames.length; i++) {
            addStatRow(panel, player, statNames[i], 200 + i * 155);
        }
        return panel;
    }

    /** Adds the bar, the upgrade button and the price of a stat in the given row. */
    private void addStatRow(JPanel panel, int player, String stat, int rowY){
        //La barra tiene una imagen por nivel, por ejemplo Kick1.png hasta Kick10.png
        String bar = "lib/" + stat + game.getLevel(player, stat) + ".png";
        ImageIcon icon = new ImageIcon(bar);
        int width = 480;
        int height = width * icon.getIconHeight() / icon.getIconWidth();
        //Las barras no miden lo mismo de alto, entonces se centran en la fila
        panel.add(createImageLabel(bar, 30, rowY + 65 - height / 2, width, height));

        JButton upgrade = createUpgradeButton(player, stat);
        upgrade.setLocation(scale(530), scale(rowY + 10));
        panel.add(upgrade);

        //Precio del siguiente nivel, o MAX si ya esta en el ultimo
        String priceText = game.getPrice(player, stat) + " pts";
        if (game.getLevel(player, stat) >= game.getMaxLevel()) {
            priceText = "MAX";
        }
        JLabel price = new JLabel(priceText);
        price.setFont(new Font("Arial", Font.BOLD, scale(30)));
        price.setForeground(Color.YELLOW);
        price.setBounds(scale(650), scale(rowY + 40), scale(150), scale(50));
        panel.add(price);
    }

    /** Creates the button that buys one level of a stat. */
    private JButton createUpgradeButton(int player, String stat){
        //Las imagenes se llaman Upgrade + mejora, por ejemplo UpgradeKick.png
        JButton button = createImageButton("lib/Upgrade" + stat + ".png", 110, 110);
        button.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try {
                    game.upgrade(player, stat);
                    //El progreso se guarda al comprar para no perder los puntos gastados
                    window.saveProgress();
                    //Se vuelve a crear la tienda para mostrar la barra y los puntos nuevos
                    window.showShop();
                } catch (PoobSoccerException ex) {
                    showMessage(ex.getMessage());
                }
            }
        });
        return button;
    }
}
