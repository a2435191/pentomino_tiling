package com.github.a2435191.solvers;

import com.github.a2435191.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The basic, naive implementation.
 */
public final class PentominoPuzzleSolver implements ISolver {
    /**
     * Determine the first (left-right, then up-down) empty square of a grid.
     * @param p Pentomino to use to compute the offset (see {@link Pentomino#startingOffsets}).
     * @param rotation Rotation to use to compute the offset.
     * @param grid Boolean matrix representing the puzzle base.
     *
     * @return A coordinate in which the upper left corner of the transformed pentomino {@code rotation}
     * can be placed. Does not guarantee that the pentomino can actually fit (see {@link #canFit}), only
     * that the upper-left coordinate and the first filled-in coordinate of {@code rotation} do.
     */
    public static Coordinate determineFirstEmptySquare(Pentomino p, boolean[][] rotation, boolean[][] grid) {
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

    /**
     * Determine if a pentomino can fit on the board.
     * @param pentomino Rotated pentomino shape to check.
     * @param offset Offset to apply to the pentomino, so that it can be compared against different places on the board.
     * @param grid The board, represented as a boolean matrix.
     * @return {@code false} if the pentomino exceeds the bounds of the {@code grid} or if one of its
     * filled squares overlaps an already-filled board tile, {@code true} otherwise.
     */
    public static boolean canFit(boolean[][] pentomino, Coordinate offset, boolean[][] grid) {
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

    /**
     * Update the game board with a pentomino. Does no checks to see if the pentomino's position is valid.
     * @param pentomino Rotated pentomino shape to update the board with.
     * @param offset Offset to apply to the pentomino, so that it can be added to different places on the board.
     * @param grid The board, represented as a boolean matrix.
     * @return An updated boolean matrix.
     */
    public static boolean[][] updateGrid(boolean[][] pentomino, Coordinate offset, boolean[][] grid) {
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
        //int attempts = 0;

        final Queue<State> q = new LinkedList<>();
        q.add(new State(new HashMap<>(), grid));

        while (!q.isEmpty()) {
            //attempts++;
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


    record State(Map<Pentomino, TransformationAndCoordinate> map, boolean[][] grid) {
    }
}
