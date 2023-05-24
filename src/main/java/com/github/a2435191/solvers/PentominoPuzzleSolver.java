package com.github.a2435191.solvers;

import com.github.a2435191.Coordinate;
import com.github.a2435191.Pentomino;
import com.github.a2435191.Solution;
import com.github.a2435191.Transformation;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public final class PentominoPuzzleSolver implements ISolver {
    private static Coordinate determineFirstEmptySquare(Pentomino p, boolean[][] rotation, boolean[][] grid) {
        // get the next candidate square to check
        Coordinate firstEmptySquare = null;
        out:
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (!grid[i][j]) {
                    firstEmptySquare = new Coordinate(j, i);
                    break out;
                }
            }
        }
        if (firstEmptySquare == null) {
            throw new RuntimeException("could not find an empty square");
        }


        // can't just use first empty square directly, since some pieces (like light blue)
        // aren't filled in at the top left
        // so must use pre-computed offsets
        Coordinate offset = p.startingOffsets.get(rotation);
        return new Coordinate(firstEmptySquare.x() - offset.x(), firstEmptySquare.y() - offset.y());
    }

    private static boolean canFit(boolean[][] pentomino, Coordinate offset, boolean[][] grid) {
        final int height = pentomino.length;
        final int width = pentomino[0].length;
        try {

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (grid[i + offset.y()][j + offset.x()] && pentomino[i][j]) {
                        return false;
                    }
                }
            }
            return true;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    private static boolean[][] updateGrid(boolean[][] pentomino, Coordinate offset, boolean[][] grid) {
        final int height = pentomino.length;
        final int width = pentomino[0].length;
        boolean[][] out = Transformation.IDENTITY.apply(grid); // copy

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (pentomino[i][j]) {
                    out[i + offset.y()][j + offset.x()] = true;
                }
            }
        }

        return out;
    }

    @Override
    public List<Solution> solveForMultiple(Pentomino[] pieces, boolean[][] grid, int limit) {
        List<Solution> out = new ArrayList<>();
        // Simple BFS
        final Set<Pentomino> piecesSet = Arrays.stream(pieces).collect(Collectors.toSet());
        int attempts = 0;

        final Queue<State> q = new LinkedList<>();
        q.add(new State(new HashMap<>(), grid));

        while (!q.isEmpty()) {
            attempts++;
            State state = q.remove();

            Set<Pentomino> piecesToVisit = new HashSet<>(piecesSet);
            piecesToVisit.removeAll(state.map.keySet()); // remove already visited

            for (Pentomino p : piecesToVisit) {
                for (var entry : p.equivalentTransforms.entrySet()) {
                    boolean[][] rotation = entry.getKey();
                    Transformation transform = entry.getValue()[0];
                    Coordinate coordinateToCheck = determineFirstEmptySquare(p, rotation, state.grid());

                    if (canFit(rotation, coordinateToCheck, state.grid())) {
                        Map<Pentomino, TransformationAndCoordinate> newMap = new HashMap<>(state.map());
                        newMap.put(p, new TransformationAndCoordinate(transform, coordinateToCheck));

                        if (newMap.keySet().size() == pieces.length) {
                            //System.out.println(attempts);
                            out.add(new Solution(newMap));
                            if (out.size() == limit) {
                                return out;
                            }
                        } else {
                            boolean[][] newGrid = updateGrid(rotation, coordinateToCheck, state.grid());
                            q.add(new State(newMap, newGrid));
                        }
                    }
                }
            }
        }
        return out;
    }

    @Override
    public @Nullable Solution solve(Pentomino[] pieces, boolean[][] grid) {
        List<Solution> list = solveForMultiple(pieces, grid, 1);
        return list.size() == 0 ? null : list.get(0);
    }

    private record State(Map<Pentomino, TransformationAndCoordinate> map, boolean[][] grid) {
    }
}
