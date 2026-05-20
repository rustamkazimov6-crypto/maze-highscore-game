package com.mycompany.assingnment3;

import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel {
    private StartListener onStart;
    private Runnable onScores;
    private Runnable onExit;

    public MenuPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.gridx = 0;

        JLabel title = new JLabel("Coin Return Maze");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));

        JButton easy = new JButton("Start - EASY");
        JButton med = new JButton("Start - MEDIUM");
        JButton hard = new JButton("Start - HARD");
        JButton scores = new JButton("Highscores (Top 10)");
        JButton exit = new JButton("Exit");

        easy.addActionListener(e -> { if (onStart != null) onStart.start(Difficulty.EASY); });
        med.addActionListener(e -> { if (onStart != null) onStart.start(Difficulty.MEDIUM); });
        hard.addActionListener(e -> { if (onStart != null) onStart.start(Difficulty.HARD); });
        scores.addActionListener(e -> { if (onScores != null) onScores.run(); });
        exit.addActionListener(e -> { if (onExit != null) onExit.run(); });

        c.gridy = 0; add(title, c);
        c.gridy++; add(easy, c);
        c.gridy++; add(med, c);
        c.gridy++; add(hard, c);
        c.gridy++; add(scores, c);
        c.gridy++; add(exit, c);
    }

    public void setOnStart(StartListener onStart) { this.onStart = onStart; }
    public void setOnScores(Runnable onScores) { this.onScores = onScores; }
    public void setOnExit(Runnable onExit) { this.onExit = onExit; }

    public interface StartListener {
        void start(Difficulty diff);
    }
}
