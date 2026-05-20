package com.mycompany.assingnment3;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Game {
    private final Difficulty difficulty;
    private final long seed;

    private final CellType[][] grid;
    private final Pos start;
    private final Set<Pos> coins;

    private GameState state = GameState.RUNNING;
    private double elapsedSeconds;

    private Pos player;
    private int steps;
    private int coinsCollected;
    private boolean winHandled;

    public Game(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.seed = new Random().nextLong();

        MazeGenerator.Result r = MazeGenerator.generate(difficulty, seed);
        this.grid = r.grid; 
        this.start = r.start;
        this.coins = new HashSet<>(r.coins);

        this.player = start;
    }

    public void tick(double dt) {
        if (state == GameState.RUNNING) elapsedSeconds += dt;
    }

    public void tryMove(int dx, int dy) {
        if (state != GameState.RUNNING) return;

        int nx = player.x + dx;
        int ny = player.y + dy;

        if (nx < 0 || ny < 0 || ny >= grid.length || nx >= grid[0].length) return;
        if (grid[ny][nx] == CellType.WALL) return;

        player = new Pos(nx, ny);
        steps++;

        if (coins.remove(player)) coinsCollected++;

        if (coins.isEmpty() && player.equals(start)) state = GameState.WON;
    }

    public int getScore() {
        int base = coinsCollected * 10;
        int penalty = (int) (elapsedSeconds * 2) + steps;
        double mult = switch (difficulty) {
            case EASY -> 1.0;
            case MEDIUM -> 1.2;
            case HARD -> 1.5;
        };
        return Math.max(0, (int) ((base - penalty) * mult));
    }

    public boolean isWall(int x, int y) { return grid[y][x] == CellType.WALL; }
    public int getGridW() { return grid[0].length; }
    public int getGridH() { return grid.length; }
    public int getStartX() { return start.x; }
    public int getStartY() { return start.y; }
    public Set<Pos> getCoins() { return coins; }
    public int getCoinsTotal() { return coinsCollected + coins.size(); }
    public int getCoinsCollected() { return coinsCollected; }
    public int getSteps() { return steps; }
    public double getElapsedSeconds() { return elapsedSeconds; }
    public GameState getState() { return state; }
    public Difficulty getDifficulty() { return difficulty; }
    public long getSeed() { return seed; }
    public boolean isWinHandled() { return winHandled; }
    public void setWinHandled(boolean v) { winHandled = v; }
    public int getPlayerX() { return player.x; }
    public int getPlayerY() { return player.y; }
}
