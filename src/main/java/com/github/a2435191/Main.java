package com.github.a2435191;

import com.github.a2435191.solvers.ISolver;
import com.github.a2435191.solvers.PentominoPuzzleSolver;

import java.util.List;


public class Main {
    public static ISolver SOLVER = new PentominoPuzzleSolver();

    private static boolean[][] getDefaultGrid() {
        // 11 across
        // 10 down
        // true— filled, false— empty
        boolean[][] out = new boolean[10][];
        for (int i = 0; i < out.length; i++) {
            boolean[] row = new boolean[11];
            int startFill = Math.abs(i - 5);
            int stopFill = 10 - startFill;
            for (int j = 0; j < row.length; j++) {
                row[j] = j < startFill || j > stopFill;
            }
            out[i] = row;
        }
        return out;
    }

    private static boolean[][] getRectangularGrid(int height, int width) {
        boolean[][] out = new boolean[height][];
        for (int i = 0; i < height; i++) {
            boolean[] row = new boolean[width];
            for (int j = 0; j < width; j++) {
                row[j] = false;
            }
            out[i] = row;
        }
        return out;
    }

    private static void extremelySimpleTest() {
        boolean[][] grid = getRectangularGrid(4, 3);
        grid[3][0] = true;
        grid[3][2] = true;
        Solution answer = SOLVER.solve(
                new Pentomino[]{Pentomino.YELLOW_ORANGE, Pentomino.LIGHT_BLUE},
                grid
        );
        System.out.println(answer);
    }

    private static void smallTest() {
        // 5x5 grid test
        Solution answer = SOLVER.solve(
                new Pentomino[]{
                        Pentomino.PINK,
                        Pentomino.RED,
                        Pentomino.DARK_PURPLE,
                        Pentomino.DARK_GREEN,
                        Pentomino.YELLOW},
                getRectangularGrid(5, 5)
        );
        System.out.println(answer);
    }

    private static void mediumTest() {
        // larger test
        boolean[][] grid = getRectangularGrid(6, 7);
        grid[0][0] = true;
        grid[2][1] = true;

        Solution answer = SOLVER.solve(
                new Pentomino[]{
                        Pentomino.DARK_BLUE,
                        Pentomino.LIGHT_PURPLE,
                        Pentomino.DARK_PURPLE,
                        Pentomino.LIME,
                        Pentomino.YELLOW_ORANGE,
                        Pentomino.ORANGE,
                        Pentomino.PINK,
                        Pentomino.YELLOW
                },
                grid
        );
        System.out.println(answer);

    }

    public static void main(String[] args) {
        List<Solution> solution = SOLVER.solveForMultiple(Pentomino.values(), getDefaultGrid(), -1);
        System.out.println(String.join(
                "\n------------------------------------------------------------------\n",
                solution.stream()
                        .map(Solution::toString)
                        .toList()
        ));
    }
}
