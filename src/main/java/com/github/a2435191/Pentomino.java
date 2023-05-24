package com.github.a2435191;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public enum Pentomino {
    PINK("-----"),
    DARK_GREEN("---\n00-\n00-"),
    YELLOW_ORANGE("--\n0-\n--"),
    LIME("0---\n--00"),
    LIGHT_BLUE("0-0\n---\n0-0"),
    LIGHT_PURPLE("--0\n0--\n00-"),
    YELLOW("0-0\n0-0\n---"),
    TEAL("00-\n---\n-00"),
    RED("--0\n---"),
    DARK_BLUE("----\n0-00"),
    ORANGE("00-\n---\n0-0"),
    DARK_PURPLE("----\n-000");

    private static final String REPR_DELIMITER = "\n";
    private static final char REPR_TRUE = '-';
    private static final char REPR_FALSE = '0';

    // true— filled, false— empty
    public final boolean[][] shape;
    public final int width;
    public final int height;
    public final int area;

    public final Map<boolean[][], Transformation[]> equivalentTransforms;
    public final Map<boolean[][], Coordinate> startingOffsets;

    Pentomino(String shape) {
        String[] split = shape.split(REPR_DELIMITER);
        this.height = split.length;
        this.width = split[0].length();

        AtomicInteger area = new AtomicInteger(); // for use in lambda expression
        this.shape = Arrays.stream(split)
                .map(row -> {
                    if (row.length() != this.width) {
                        throw new IllegalArgumentException("non-uniform length across rows");
                    }
                    boolean[] out = new boolean[row.length()];
                    for (int i = 0; i < row.length(); i++) {
                        switch (row.charAt(i)) {
                            case REPR_TRUE -> {
                                out[i] = true;
                                area.getAndIncrement();
                            }
                            case REPR_FALSE -> out[i] = false;
                            default -> throw new IllegalArgumentException(
                                    "shape array has illegal character: " + row.charAt(i));
                        }
                    }
                    return out;
                })
                .toList()
                .toArray(new boolean[0][]);
        this.area = area.get();

        Map<Boolean2DArrayWrapper, List<Transformation>> tmp = new HashMap<>();
        // compute all actions on this pentomino ahead of time
        for (Transformation t : Transformation.TRANSFORMS) {
            Boolean2DArrayWrapper result = new Boolean2DArrayWrapper(t.apply(this.shape));
            if (!tmp.containsKey(result)) {
                tmp.put(result, new ArrayList<>());
            }
            tmp.get(result).add(t);
        }
        this.equivalentTransforms = tmp.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        entry -> entry.getKey().array(),
                        entry -> entry.getValue().toArray(new Transformation[0]))
                );
        this.startingOffsets = this.equivalentTransforms.keySet().stream()
                .collect(Collectors.toMap(arr -> arr, arr -> {
                    for (int i = 0; i < arr.length; i++) {
                        for (int j = 0; j < arr[0].length; j++) {
                            if (arr[i][j]) {
                                return new Coordinate(j, i);
                            }
                        }
                    }
                    throw new RuntimeException("could not find filled-in coordinate");
                }));

    }

    @Override
    public String toString() {
        // return toString(this.shape);
        return this.name();
    }


//    public static String toString(boolean[][] shape) {
//        int height = shape.length;
//        String[] rows = new String[height];
//        for (int i = 0; i < height; i++) {
//            StringBuilder sb = new StringBuilder();
//            for (boolean b : shape[i]) {
//                sb.append(b ? REPR_TRUE : REPR_FALSE);
//            }
//            rows[i] = sb.toString();
//        }
//        return String.join(REPR_DELIMITER, rows);
//    }

    // for comparison
    record Boolean2DArrayWrapper(boolean[][] array) {
        @Override
        public boolean equals(Object other) {
            return other instanceof Boolean2DArrayWrapper
                    && Arrays.deepEquals(this.array, ((Boolean2DArrayWrapper) other).array());
        }

        @Override
        public int hashCode() {
            return Arrays.deepHashCode(this.array);
        }
    }

}
