package com.github.a2435191;

import java.util.Arrays;

public interface Transformation {
    /*
    Assume the axis conventions, as follows:
    x-axis: -left, +right,
    y-axis: -down, +up,
    z-axis: -in,   +out.
    
    Then there is a group generated from the following matrices that represents all the
    transformations that we can apply to pentominoes, assuming the pentominoes lie in the x-y plane:
    r = {
        {0, -1, 0},
        {1, 0, 0},
        {0, 0, 1}
    } (rotate 90 degrees CW about z-axis);

    s = {
        {-1, 0, 0},
        {0, 1, 0},
        {0, 0, -1}
    } (rotate 180 degrees about y-axis / reflect vertically);

    and t = {
        {1, 0, 0},
        {0, -1, 0},
        {0, 0, -1}
    } (rotate 180 degrees about x-axis / reflect horizontally).

    Additionally, the relations r^4 = s^2 = t^2 = 1, rs = sr^3, rt = tr^3 = sr, and st = ts = r^2 hold. Computation shows that
    this group has eight elements: 1, r, r^2, r^3, s, t, rs, rt.
     */

    Transformation IDENTITY = new Identity();
    Transformation R = new R();
    Transformation R2 = new R2();
    Transformation R3 = new R3();
    Transformation S = new S();
    Transformation T = new T();
    Transformation RS = new RS();
    Transformation RT = new RT();

    Transformation[] TRANSFORMS = {
            IDENTITY,
            R,
            R2,
            R3,
            S,
            T,
            RS,
            RT
    };

    // s
    private static boolean[][] reflectAboutVertical(boolean[][] shape) {
        final int height = shape.length;
        final int width = shape[0].length;

        boolean[][] out = new boolean[height][];

        for (int i = 0; i < height; i++) {
            boolean[] tmpRow = new boolean[width];
            for (int j = 0; j < width; j++) {
                tmpRow[j] = shape[i][width - j - 1];
            }
            out[i] = tmpRow;
        }
        return out;
    }

    // t
    private static boolean[][] reflectAboutHorizontal(boolean[][] shape) {
        final int height = shape.length;
        final int width = shape[0].length;
        boolean[][] out = new boolean[height][];

        for (int i = 0; i < height; i++) {
            out[i] = Arrays.copyOf(shape[height - i - 1], width);
        }

        return out;
    }

    private static boolean[][] deepCopy(boolean[][] shape) {
        final int height = shape.length;
        final int width = shape[0].length;
        boolean[][] out = new boolean[height][];

        for (int i = 0; i < height; i++) {
            out[i] = Arrays.copyOf(shape[i], width);
        }

        return out;
    }

    // r
    private static boolean[][] rotate(boolean[][] shape, int rotations) {
        return switch (rotations) {
            case 0 -> deepCopy(shape);
            case 1 -> { // only use case 1, but other three are still here just in case
                int oldHeight = shape.length;
                int oldWidth = shape[0].length;
                boolean[][] out = new boolean[oldWidth][];

                for (int i = 0; i < oldWidth; i++) {
                    out[i] = new boolean[oldHeight];
                }

                for (int i = 0; i < oldHeight; i++) {
                    for (int j = 0; j < oldWidth; j++) {
                        out[oldWidth - j - 1][i] = shape[i][j];
                    }
                }
                yield out;
            }
            case 2 -> {
                int height = shape.length;
                int width = shape[0].length;
                boolean[][] out = new boolean[height][];

                for (int i = 0; i < height; i++) {
                    boolean[] tmpRow = new boolean[width];
                    for (int j = 0; j < width; j++) {
                        tmpRow[j] = shape[i][width - j - 1];
                    }
                    out[height - i - 1] = tmpRow;
                }

                yield out;
            }
            case 3 -> {
                int oldHeight = shape.length;
                int oldWidth = shape[0].length;
                boolean[][] out = new boolean[oldWidth][];

                for (int i = 0; i < oldWidth; i++) {
                    out[i] = new boolean[oldHeight];
                }

                for (int i = 0; i < oldHeight; i++) {
                    for (int j = 0; j < oldWidth; j++) {
                        out[j][oldHeight - i - 1] = shape[i][j];
                    }
                }
                yield out;
            }
            default -> throw new IllegalArgumentException("rotations must be 0, 1, 2, or 3");
        };
    }

    boolean[][] apply(boolean[][] shape);

    class SimpleStringRepresentation {
        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }

    final class Identity extends SimpleStringRepresentation implements Transformation {
        @Override
        public boolean[][] apply(boolean[][] shape) {
            return deepCopy(shape);
        }
    }

    final class R extends SimpleStringRepresentation implements Transformation {
        @Override
        public boolean[][] apply(boolean[][] shape) {
            return rotate(shape, 1);
        }
    }

    final class R2 extends SimpleStringRepresentation implements Transformation {
        @Override
        public boolean[][] apply(boolean[][] shape) {
            return rotate(shape, 2);
        }
    }

    final class R3 extends SimpleStringRepresentation implements Transformation {
        @Override
        public boolean[][] apply(boolean[][] shape) {
            return rotate(shape, 3);
        }
    }

    final class S extends SimpleStringRepresentation implements Transformation {
        @Override
        public boolean[][] apply(boolean[][] shape) {
            return reflectAboutVertical(shape);
        }
    }

    final class T extends SimpleStringRepresentation implements Transformation {
        @Override
        public boolean[][] apply(boolean[][] shape) {
            return reflectAboutHorizontal(shape);
        }
    }

    final class RS extends SimpleStringRepresentation implements Transformation {
        @Override
        public boolean[][] apply(boolean[][] shape) {
            return rotate(reflectAboutVertical(shape), 1);
        }
    }

    final class RT extends SimpleStringRepresentation implements Transformation {
        @Override
        public boolean[][] apply(boolean[][] shape) {
            return rotate(reflectAboutHorizontal(shape), 1);
        }
    }


}
