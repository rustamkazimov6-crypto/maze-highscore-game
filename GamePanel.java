package com.mycompany.assingnment3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GamePanel extends JPanel {
    private static final int TILE = 24;

    private Game game;
    private Timer timer;

    private Runnable onMenu;
    private Runnable onScores;

    private final JButton restart = new JButton("Restart");
    private final JButton highscores = new JButton("Highscores");
    private final JButton menu = new JButton("Menu");

    private final JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));

    private static final double PREVIEW_SECONDS = 3.0;
    private double previewRemaining = 0.0;
    private boolean inPreview = false;

    public GamePanel() {
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        setLayout(new BorderLayout());

        topBar.add(restart);
        topBar.add(highscores);
        topBar.add(menu);
        add(topBar, BorderLayout.NORTH);

        restart.addActionListener(e -> {
            if (game != null) startNewGame(game.getDifficulty(), onMenu, onScores);
            requestFocusInWindow();
        });

        highscores.addActionListener(e -> {
            if (onScores != null) onScores.run();
        });

        menu.addActionListener(e -> {
            if (onMenu != null) onMenu.run();
        });

        setupKeyBindings();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    public void startNewGame(Difficulty diff, Runnable onMenu, Runnable onScores) {
        this.onMenu = onMenu;
        this.onScores = onScores;

        this.game = new Game(diff);

        inPreview = true;
        previewRemaining = PREVIEW_SECONDS;

        if (timer != null) timer.stop();
        timer = new Timer(100, e -> {
            if (game == null) return;

            double dt = 0.1;

            if (inPreview) {
                previewRemaining -= dt;
                if (previewRemaining <= 0) {
                    previewRemaining = 0;
                    inPreview = false;
                }
            } else {
                game.tick(dt);

                if (game.getState() == GameState.WON && !game.isWinHandled()) {
                    game.setWinHandled(true);
                    handleWin();
                }
            }

            repaint();
        });
        timer.start();

        requestFocusInWindow();
        repaint();
    }

    private void handleWin() {
        String name = JOptionPane.showInputDialog(this, "You won! Enter your name:", "Save score",
                JOptionPane.PLAIN_MESSAGE);
        if (name == null || name.trim().isEmpty()) name = "Player";

        Highscore hs = new Highscore();
        hs.playerName = name.trim();
        hs.difficulty = game.getDifficulty().name();
        hs.seed = game.getSeed();
        hs.gridW = game.getGridW();
        hs.gridH = game.getGridH();
        hs.coinsTotal = game.getCoinsTotal();
        hs.coinsCollected = game.getCoinsCollected();
        hs.elapsedSeconds = game.getElapsedSeconds();
        hs.steps = game.getSteps();
        hs.score = game.getScore();
        hs.createdAtMs = System.currentTimeMillis();

        try {
            Database.insert(hs);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not save to DB:\n" + ex.getMessage(),
                    "DB error",
                    JOptionPane.WARNING_MESSAGE);
        }

        JOptionPane.showMessageDialog(this,
                "Result:\nScore: " + hs.score +
                        "\nTime: " + formatTime(hs.elapsedSeconds) +
                        "\nSteps: " + hs.steps,
                "Saved",
                JOptionPane.INFORMATION_MESSAGE);

        if (onScores != null) onScores.run();
    }

    private void setupKeyBindings() {
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke('W'), "up");
        im.put(KeyStroke.getKeyStroke('w'), "up");
        im.put(KeyStroke.getKeyStroke('A'), "left");
        im.put(KeyStroke.getKeyStroke('a'), "left");
        im.put(KeyStroke.getKeyStroke('S'), "down");
        im.put(KeyStroke.getKeyStroke('s'), "down");
        im.put(KeyStroke.getKeyStroke('D'), "right");
        im.put(KeyStroke.getKeyStroke('d'), "right");

        im.put(KeyStroke.getKeyStroke("UP"), "up");
        im.put(KeyStroke.getKeyStroke("LEFT"), "left");
        im.put(KeyStroke.getKeyStroke("DOWN"), "down");
        im.put(KeyStroke.getKeyStroke("RIGHT"), "right");

        am.put("up", new AbstractAction() { public void actionPerformed(ActionEvent e) { movePlayer(0, -1); }});
        am.put("left", new AbstractAction() { public void actionPerformed(ActionEvent e) { movePlayer(-1, 0); }});
        am.put("down", new AbstractAction() { public void actionPerformed(ActionEvent e) { movePlayer(0, 1); }});
        am.put("right", new AbstractAction() { public void actionPerformed(ActionEvent e) { movePlayer(1, 0); }});
    }

    private void movePlayer(int dx, int dy) {
        if (game == null) return;
        if (inPreview) return;
        game.tryMove(dx, dy);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        if (game == null) return;

        Graphics2D g = (Graphics2D) g0.create();
        try {
            int topH = topBar.getHeight();
            int offX = 20;
            int offY = topH + 60;

            g.setColor(new Color(245, 245, 245));
            g.fillRect(0, 0, getWidth(), getHeight());

            for (int y = 0; y < game.getGridH(); y++) {
                for (int x = 0; x < game.getGridW(); x++) {
                    int px = offX + x * TILE;
                    int py = offY + y * TILE;

                    g.setColor(game.isWall(x, y) ? new Color(70, 70, 70) : Color.WHITE);
                    g.fillRect(px, py, TILE, TILE);
                    g.setColor(new Color(220, 220, 220));
                    g.drawRect(px, py, TILE, TILE);
                }
            }

            g.setColor(new Color(60, 180, 75));
            drawCentered(g, offX, offY, game.getStartX(), game.getStartY(), 0.35);

            g.setColor(new Color(255, 200, 0));
            for (Pos c : game.getCoins()) {
                int px = offX + c.x * TILE;
                int py = offY + c.y * TILE;
                int r = (int) (TILE * 0.55);
                g.fillOval(px + (TILE - r) / 2, py + (TILE - r) / 2, r, r);
            }

            g.setColor(new Color(50, 100, 255));
            drawCentered(g, offX, offY, game.getPlayerX(), game.getPlayerY(), 0.65);

            g.setFont(g.getFont().deriveFont(Font.BOLD, 14f));
            g.setColor(Color.DARK_GRAY);

            int hudY = topH + 22;

            g.drawString("Difficulty: " + game.getDifficulty().name(), 20, hudY);

            String[] rightLines = {
                    inPreview ? "Preview: " + (int) Math.ceil(previewRemaining) + "s" : "Time:  " + formatTime(game.getElapsedSeconds()),
                    "Steps: " + game.getSteps(),
                    "Score: " + game.getScore(),
                    "Coins: " + game.getCoinsCollected() + "/" + game.getCoinsTotal()
            };

            FontMetrics fm = g.getFontMetrics();
            int y = hudY;
            int rightPadding = 20;

            for (String line : rightLines) {
                int x = getWidth() - fm.stringWidth(line) - rightPadding;
                g.drawString(line, x, y);
                y += fm.getHeight();
            }

            g.setFont(g.getFont().deriveFont(Font.PLAIN, 12f));
            g.setColor(Color.DARK_GRAY);
            g.drawString("Goal: Collect ALL coins and return to the GREEN start tile.", 20, topH + 48);

            if (inPreview) {
                g.setFont(g.getFont().deriveFont(Font.BOLD, 18f));
                g.setColor(Color.RED);

                String msg = "PREVIEW MODE \u2013 Find the fastest path!";
                FontMetrics fm2 = g.getFontMetrics();

                int x = (getWidth() - fm2.stringWidth(msg)) / 2;
                int yy = topH + 40;

                g.drawString(msg, x, yy);

                g.setFont(g.getFont().deriveFont(Font.BOLD, 14f));
                String countdown = "Starting in " + (int) Math.ceil(previewRemaining) + "s";

                int cx = (getWidth() - g.getFontMetrics().stringWidth(countdown)) / 2;
                g.drawString(countdown, cx, yy + 22);
            }

        } finally {
            g.dispose();
        }
    }

    private static void drawCentered(Graphics2D g, int offX, int offY, int x, int y, double scale) {
        int px = offX + x * TILE;
        int py = offY + y * TILE;
        int w = (int) (TILE * scale);
        int h = (int) (TILE * scale);
        g.fillRoundRect(px + (TILE - w) / 2, py + (TILE - h) / 2, w, h, 8, 8);
    }

    private static String formatTime(double seconds) {
        int s = (int) Math.floor(seconds);
        int m = s / 60;
        int r = s % 60;
        return String.format("%02d:%02d", m, r);
    }
}
