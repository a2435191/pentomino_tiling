package com.github.a2435191;

import java.util.Arrays;


/**
 * Assume the axis conventions, as follows:
 * <p>
 * x-axis: -left, +right,
 * <p>
 * y-axis: -down, +up,
 * <p>
 * z-axis: -in,   +out.
 * <p>
 * <br>

 * Then the dihedral group D8 generated from the following matrices represents all the
 transformations that we can apply to pentominoes, assuming the pentominoes lie in the x-y plane:
 * <p>
 * r = { <p>
 * {0, -1, 0}, <p>
 * {1, 0, 0}, <p>
 * {0, 0, 1} <p>
 * } (rotate 90 degrees CW about z-axis); <p></p><br>

 * s = { <p>
 * {-1, 0, 0}, <p>
 * {0, 1, 0}, <p>
 * {0, 0, -1} <p>
 * } (rotate 180 degrees about y-axis / reflect vertically); <p><br>

 * and t = { <p>
 * {1, 0, 0}, <p>
 * {0, -1, 0}, <p>
 * {0, 0, -1} <p>
 * } (rotate 180 degrees about x-axis / reflect horizontally). <p><br>

 * This group has eight elements: 1, r, r^2, r^3, s, t, rs, rt.

 * This interface and its subclasses provide access to these transformations.
 **/
@FunctionalInterface
public interface Transformation {


    /**
     * The identity transformation. Just a (deep) copy operation.
     */
    Transformation IDENTITY = new Identity();

    /**
     * Counter-clockwise rotation by 90 degrees.
     */
    Transformation R = new R();

    /**
     * Rotation by 180 degrees.
     */
    Transformation R2 = new R2();

    /**
     * Clockwise rotation by 90 degrees.
     */
    Transformation R3 = new R3();



    /**
     * Reflection about the y-axis.
     */
    Transformation S = new S();

    /**
     * Reflection about the x-axis.
     */
    Transformation T = new T();

    /**
     * Reflection about the y-axis, then counter-clockwise rotation by 90 degrees.
     */
    Transformation RS = new RS();

    /**
     * Reflection about the x-axis, then counter-clockwise rotation by 90 degrees.
     */
    Transformation RT = new RT();

    /**
     * All transform instances in the class.
     */
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
            case 1 -> {
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

    /**
     * Apply the transformation.
     * @param shape Boolean matrix representing some shape.
     * @return A new matrix representing the transformed shape.
     */
    boolean[][] apply(boolean[][] shape);

    /**
     * Helpful for pretty-printing.
     */
    class SimpleStringRepresentation {
        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }

    /**
     * The identity transformation. Just a (deep) copy operation.
     */
    final class Identity extends SimpleStringRepresentation implements Transformation {
        @Override
        public boolean[][] apply(boolean[][] shape) {
            return deepCopy(shape);
        }
    }

    /**
     * Counter-clockwise rotation by 90 degrees.
     */
    final class R extends SimpleStringRepresentation implements Transformation {
        @Override
        public boolean[][] apply(boolean[][] shape) {
            return rotate(shape, 1);
        }
    }

    /**
     * Rotation by 180 degrees.
     */
    final class R2 extends SimpleStringRepresentation implements Transformation {
        @Override
        public boolean[][] apply(boolean[][] shape) {
            return rotate(shape, 2);
        }
    }

    /**
     * Clockwise rotation by 90 degrees.
     */
    final class R3 extends SimpleStringRepresentation implements Transformation {
        @Override
        public boolean[][] apply(boolean[][] shape) {
            return rotate(shape, 3);
        }
    }

    /**
     * Reflection about the y-axis.
     */
    final class S extends SimpleStringRepresentation implements Transformation {
        @Override
        public boolean[][] apply(boolean[][] shape) {
            return reflectAboutVertical(shape);
        }
    }

    /**
     * Reflection about the x-axis.
     */
    final class T extends SimpleStringRepresentation implements Transformation {
        @Override
        public boolean[][] apply(boolean[][] shape) {
            return reflectAboutHorizontal(shape);
        }
    }

    /**
     * Reflection about the y-axis, then counter-clockwise rotation by 90 degrees.
     */
    final class RS extends SimpleStringRepresentation implements Transformation {
        @Override
        public boolean[][] apply(boolean[][] shape) {
            return rotate(reflectAboutVertical(shape), 1);
        }
    }

    /**
     * Reflection about the x-axis, then counter-clockwise rotation by 90 degrees.
     */
    final class RT extends SimpleStringRepresentation implements Transformation {
        @Override
        public boolean[][] apply(boolean[][] shape) {
            return rotate(reflectAboutHorizontal(shape), 1);
        }
    }


}
