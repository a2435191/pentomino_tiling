package com.github.a2435191;

/**
 * Wrapper record for a 2D coordinate.
 * @param x x-coordinate.
 * @param y y-coordinate.
 */
public record Coordinate(int x, int y) {
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
