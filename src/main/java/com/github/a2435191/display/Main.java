package com.github.a2435191.display;

import com.github.a2435191.Pentomino;
import com.github.a2435191.Solution;
import com.github.a2435191.solvers.dancing_links.DancingLinksPentominoPuzzleSolver;

import javax.swing.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        boolean[][] grid = com.github.a2435191.Main.getDefaultGrid();
        List<Solution> solutions = new DancingLinksPentominoPuzzleSolver().solveForMultiple(
                Pentomino.values(),
                grid,
                -1
        );
        SwingUtilities.invokeLater(() ->
                new PentominoDisplay(solutions, grid[0].length, grid.length).setVisible(true)
        );
    }
}
