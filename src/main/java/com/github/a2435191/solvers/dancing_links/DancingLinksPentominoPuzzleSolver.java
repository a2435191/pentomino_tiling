package com.github.a2435191.solvers.dancing_links;

import com.github.a2435191.*;
import com.github.a2435191.solvers.ISolver;
import com.github.a2435191.solvers.PentominoPuzzleSolver;

import java.util.*;

/**
 * The implementation using Knuth's dancing links algorithm.
 */
public class DancingLinksPentominoPuzzleSolver implements ISolver {
    private static List<Coordinate> allEmptyCoordinates(boolean[][] grid) {
        List<Coordinate> out = new ArrayList<>();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (!grid[i][j]) {
                    out.add(new Coordinate(j, i));
                }
            }
        }
        return out;
    }

    private static String[] createColumns(Pentomino[] pieces, List<Coordinate> emptyCoordinates) {
        String[] columns = new String[pieces.length + emptyCoordinates.size()];
        for (int i = 0; i < pieces.length; i++) {
            columns[i] = "Piece: " + pieces[i].name();
        }

        int columnsIdx = 0;
        for (Coordinate c : emptyCoordinates) {
            columns[pieces.length + columnsIdx++] = "Position: " + c;
        }

        return columns;
    }

    @Override
    public List<Solution> solveForMultiple(Pentomino[] pieces, boolean[][] grid, int limit) {
        final int positionsToPlace = Arrays.stream(grid)
                .mapToInt(arr -> {
                    int count = 0;
                    for (boolean b : arr) {
                        if (!b) count++;
                    }
                    return count;
                })
                .sum();

        // columns:
        // one each for if each Pentomino is on the board
        // one each for each empty grid square being covered
        final int width = pieces.length + positionsToPlace;
        List<Coordinate> allEmptyCoordinates = allEmptyCoordinates(grid);
        String[] columns = createColumns(pieces, allEmptyCoordinates);

        List<Map.Entry<Pentomino, TransformationAndCoordinate>> associatedRowData = new ArrayList<>();

        List<boolean[]> constraints = new ArrayList<>();
        for (int i = 0; i < pieces.length; i++) {
            Pentomino p = pieces[i];
            for (var equivalentTransform : p.equivalentTransforms.entrySet()) {
                boolean[][] rotated = equivalentTransform.getKey();
                Transformation transform = equivalentTransform.getValue()[0];

                for (int y = 0; y < grid.length; y++) {
                    for (int x = 0; x < grid[0].length; x++) {
                        Coordinate coord = new Coordinate(x, y);
                        if (PentominoPuzzleSolver.canFit(rotated, coord, grid)) {
                            boolean[] row = new boolean[width];
                            row[i] = true;

                            boolean[][] newGrid = PentominoPuzzleSolver.updateGrid(rotated, coord, grid);
                            for (int j = 0; j < allEmptyCoordinates.size(); j++) {
                                Coordinate testCoordinate = allEmptyCoordinates.get(j);
                                if (newGrid[testCoordinate.y()][testCoordinate.x()]) {
                                    row[pieces.length + j] = true;
                                }
                            }
                            constraints.add(row);
                            associatedRowData.add(Map.entry(p, new TransformationAndCoordinate(transform, coord)));
                        }
                    }
                }
            }
        }

        Root<Map.Entry<Pentomino, TransformationAndCoordinate>> root =
                new Root<>(constraints.toArray(new boolean[0][]), columns, associatedRowData);
        var solution = root.search();

        return solution.stream().map(set -> {
                    Map<Pentomino, TransformationAndCoordinate> map = new HashMap<>();
                    for (var entry : set) {
                        map.put(entry.getKey(), entry.getValue());
                    }
                    return new Solution(map);
                })
                .toList();
    }
}
