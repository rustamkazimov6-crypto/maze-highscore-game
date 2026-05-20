package com.mycompany.assingnment3;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Database.init();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        null,
                        "Database error (game will still start):\n" + ex.getMessage(),
                        "Database warning",
                        JOptionPane.WARNING_MESSAGE
                );
            }

            JFrame frame = new JFrame("Coin Return Maze");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(980, 720);
            frame.setLocationRelativeTo(null);

            CardLayout layout = new CardLayout();
            JPanel root = new JPanel(layout);

            MenuPanel menu = new MenuPanel();
            GamePanel game = new GamePanel();
            HighscorePanel scores = new HighscorePanel();

            menu.setOnStart(diff -> {
                game.startNewGame(diff, () -> layout.show(root, "menu"), () -> {
                    scores.reload();
                    layout.show(root, "scores");
                });
                layout.show(root, "game");
                game.requestFocusInWindow();
            });

            menu.setOnScores(() -> {
                scores.reload();
                layout.show(root, "scores");
            });

            menu.setOnExit(() -> System.exit(0));
            scores.setOnBack(() -> layout.show(root, "menu"));

            root.add(menu, "menu");
            root.add(game, "game");
            root.add(scores, "scores");

            frame.setContentPane(root);
            layout.show(root, "menu");
            frame.setVisible(true);
        });
    }
}
