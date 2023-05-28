package com.github.a2435191;

import com.github.a2435191.solvers.ISolver;
import com.github.a2435191.solvers.dancing_links.DancingLinksPentominoPuzzleSolver;
import com.github.a2435191.solvers.dancing_links.Root;

import java.util.List;


public class Main {
    public static ISolver SOLVER = new DancingLinksPentominoPuzzleSolver();

    /**
     * Grid used in my specific problem.
     * @return Boolean matrix
     */
    public static boolean[][] getDefaultGrid() {
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

    /**
     * Get an arbitrary, empty rectangular grid.
     * @param height Height of the rectangle.
     * @param width Width of the rectangle.
     * @return A rectangular grid with height {@code height} and width {@code width}, filled with {@code false}.
     */
    public static boolean[][] getRectangularGrid(int height, int width) {
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

    /**
     * Run an extremely simple (4x3) test.
     */
    public static void extremelySimpleTest() {
        boolean[][] grid = getRectangularGrid(4, 3);
        grid[3][0] = true;
        grid[3][2] = true;
        Solution answer = SOLVER.solve(
                new Pentomino[]{Pentomino.YELLOW_ORANGE, Pentomino.LIGHT_BLUE},
                grid
        );
        System.out.println(answer);
    }

    /**
     * Run a small (5x5) test.
     */
    public static void smallTest() {
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

    /**
     * Run a medium (6x7) test.
     */
    public static void mediumTest() {
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


    /**
     * Test the Dancing Links implementation. Taken directly from Knuth's paper.
     */
    public static void testDancingLinks() {
        final var root = Root.createDefault(
            new boolean[][]{
                {false, false, true,  false, true,  true,  false},
                {true,  false, false, true,  false, false, true },
                {false, true,  true,  false, false, true,  false},
                {true,  false, false, true,  false, false, false},
                {false, true,  false, false, false, false, true },
                {false, false, false, true,  true,  false, true }
            },
            "A", "B", "C", "D", "E", "F", "G"
        );
        System.out.println(root.search());
    }


    /**
     * Test the Dancing Links implementation on a 2x2 Latin square. Taken directly from
     * <a href="https://garethrees.org/2007/06/10/zendoku-generation/#section-4">here</a>.
     */
    public static void testDancingLinksLatinSquare() {
        boolean[][] table = new boolean[8][];

        int idx = 0;
        for (int row = 0; row <= 1; row++) {
            for (int col = 0; col <= 1; col++) {
                for (int num = 0; num <= 1; num++) {
                    boolean[] arr = new boolean[12];
                    arr[2 * col + row] = true;
                    arr[4 + 2 * num + row] = true;
                    arr[8 + 2 * num + col] = true;
                    table[idx++] = arr;
                }
            }
        }

        final var root = Root.createDefault(
                table,
            "column 1 & row 1",
                "column 1 & row 2",
                "column 2 & row 1",
                "column 2 & row 2",
                "1 in row 1",
                "1 in row 2",
                "2 in row 1",
                "2 in row 2",
                "1 in col 1",
                "1 in col 2",
                "2 in col 1",
                "2 in col 2"
        );
        System.out.println(root.search());
    }

    public static void main(String[] args) {
        List<Solution> solutions = SOLVER.solveForMultiple(Pentomino.values(), getDefaultGrid(), -1);
        for (Solution sol: solutions) {
            System.out.println(sol);
            System.out.println("-".repeat(40));
        }
    }
}
