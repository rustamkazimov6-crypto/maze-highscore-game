package com.mycompany.assingnment3;

import java.util.*;

public class MazeGenerator {
    public static class Result {
        public CellType[][] grid;
        public Pos start;
        public Set<Pos> coins;
    }

    public static Result generate(Difficulty d, long seed) {
        Random r = new Random(seed);
        int w = d.w | 1;
        int h = d.h | 1;

        CellType[][] grid = new CellType[h][w];
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                grid[y][x] = CellType.WALL;

        Pos start = new Pos(1, 1);
        carve(grid, start, r);

        breakWallsForLoops(grid, r, d.loopPercent);

        Set<Pos> coins = new HashSet<>();
        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {
                if (grid[y][x] == CellType.FLOOR && !(x == start.x && y == start.y)) {
                    coins.add(new Pos(x, y));
                }
            }
        }

        Result res = new Result();
        res.grid = grid;
        res.start = start;
        res.coins = coins;
        return res;
    }

    private static void carve(CellType[][] g, Pos s, Random r) {
        int[][] dirs = {{2, 0}, {-2, 0}, {0, 2}, {0, -2}};
        Deque<Pos> stack = new ArrayDeque<>();
        stack.push(s);
        g[s.y][s.x] = CellType.FLOOR;

        while (!stack.isEmpty()) {
            Pos c = stack.peek();

            List<int[]> order = new ArrayList<>();
            order.add(dirs[0]);
            order.add(dirs[1]);
            order.add(dirs[2]);
            order.add(dirs[3]);
            Collections.shuffle(order, r);

            boolean moved = false;

            for (int[] d : order) {
                int nx = c.x + d[0];
                int ny = c.y + d[1];

                if (ny <= 0 || nx <= 0 || ny >= g.length - 1 || nx >= g[0].length - 1) continue;
                if (g[ny][nx] != CellType.WALL) continue;

                int wx = c.x + d[0] / 2;
                int wy = c.y + d[1] / 2;

                g[wy][wx] = CellType.FLOOR;
                g[ny][nx] = CellType.FLOOR;

                stack.push(new Pos(nx, ny));
                moved = true;
                break;
            }

            if (!moved) stack.pop();
        }
    }

    private static void breakWallsForLoops(CellType[][] g, Random r, double percent) {
        int h = g.length;
        int w = g[0].length;

        List<Pos> candidates = new ArrayList<>();
        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {
                if (g[y][x] == CellType.WALL && isGoodWallToBreak(g, x, y)) {
                    candidates.add(new Pos(x, y));
                }
            }
        }

        Collections.shuffle(candidates, r);
        int toBreak = (int) Math.max(1, Math.round(candidates.size() * percent));

        for (int i = 0; i < toBreak && i < candidates.size(); i++) {
            Pos p = candidates.get(i);
            g[p.y][p.x] = CellType.FLOOR;
        }
    }

    private static boolean isGoodWallToBreak(CellType[][] g, int x, int y) {
        boolean up = g[y - 1][x] == CellType.FLOOR;
        boolean down = g[y + 1][x] == CellType.FLOOR;
        boolean left = g[y][x - 1] == CellType.FLOOR;
        boolean right = g[y][x + 1] == CellType.FLOOR;

        if (up && down && !left && !right) return true;
        if (left && right && !up && !down) return true;
        return false;
    }
}
