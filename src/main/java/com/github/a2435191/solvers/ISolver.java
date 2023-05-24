package com.github.a2435191.solvers;

import com.github.a2435191.Coordinate;
import com.github.a2435191.Pentomino;
import com.github.a2435191.Solution;
import com.github.a2435191.Transformation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ISolver {
    // TODO: speed up with dancing links
    List<Solution> solveForMultiple(Pentomino[] pieces, boolean[][] grid, int limit);

    @Nullable Solution solve(Pentomino[] pieces, boolean[][] grid);

    record TransformationAndCoordinate(Transformation transform, Coordinate coord) {
    }
}
