package com.github.a2435191.solvers;

import com.github.a2435191.Pentomino;
import com.github.a2435191.Solution;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Interface for all pentomino puzzle solvers.
 */
public interface ISolver {
    /**
     * Compute multiple solutions.
     *
     * @param pieces Pentominos (unique up to rotation) to use in solving the puzzle.
     * @param grid   Boolean 2D array representing the puzzle space,
     *               where {@code true} represents a filled tile and {@code false} an unfilled tile.
     * @param limit  The maximum number of results to return. Calls with a negative {@code limit} value should return
     *               all results.
     * @return A list of {@link Solution} instances, each representing a particular set of transformations of pentominos.
     * All pentominos from {@code pieces} are used.
     */
    List<Solution> solveForMultiple(Pentomino[] pieces, boolean[][] grid, int limit);

    /**
     * Compute a single solution.
     *
     * @param pieces Pentominos to use in solving the puzzle.
     *               Rotations are automatically computed.
     * @param grid   Boolean 2D array representing the puzzle space,
     *               where {@code true} represents a filled tile and {@code false} an unfilled tile.
     * @return If no solution exists, {@code null}. Otherwise, a {@link Solution} instance representing a particular
     * set of transformations of pentominos. All pentominos from {@code pieces} are used.
     */
    default @Nullable Solution solve(Pentomino[] pieces, boolean[][] grid) {
        List<Solution> list = solveForMultiple(pieces, grid, 1);
        return list.size() == 0 ? null : list.get(0);
    }


}
