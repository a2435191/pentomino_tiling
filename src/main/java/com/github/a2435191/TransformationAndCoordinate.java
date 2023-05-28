package com.github.a2435191;

/**
 * Tuple-like utility record containing a {@link Transformation} and {@link Coordinate}.
 * @param transform {@link Transformation} instance, applied first.
 * @param coord {@link Coordinate} instance, the translation by which is applied second.
 */
public record TransformationAndCoordinate(Transformation transform, Coordinate coord) {
}