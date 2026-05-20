package com.mycompany.assingnment3;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HighscorePanel extends JPanel {
    private Runnable onBack;

    private final JComboBox<String> filter = new JComboBox<>(new String[]{"ALL", "EASY", "MEDIUM", "HARD"});
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Rank", "Name", "Diff", "Score", "Time", "Steps", "Coins", "Seed", "Date"}, 0
    ) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    private final JTable table = new JTable(model);

    public HighscorePanel() {
        setLayout(new BorderLayout());

        JButton refresh = new JButton("Refresh");
        JButton back = new JButton("Back");

        refresh.addActionListener(e -> reload());
        back.addActionListener(e -> { if (onBack != null) onBack.run(); });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Filter:"));
        top.add(filter);
        top.add(refresh);
        top.add(back);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void setOnBack(Runnable onBack) { this.onBack = onBack; }

    public void reload() {
        model.setRowCount(0);
        String f = (String) filter.getSelectedItem();

        List<Highscore> rows;
        try {
            rows = "ALL".equals(f) ? Database.top10All() : Database.top10ByDifficulty(f);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not load highscores:\n" + ex.getMessage(),
                    "DB error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int rank = 1;
        for (Highscore s : rows) {
            model.addRow(new Object[]{
                    rank++,
                    s.playerName,
                    s.difficulty,
                    s.score,
                    formatTime(s.elapsedSeconds),
                    s.steps,
                    s.coinsCollected + "/" + s.coinsTotal,
                    s.seed,
                    new java.util.Date(s.createdAtMs).toString()
            });
        }
    }

    private static String formatTime(double seconds) {
        int s = (int) Math.floor(seconds);
        int m = s / 60;
        int r = s % 60;
        return String.format("%02d:%02d", m, r);
    }
}
