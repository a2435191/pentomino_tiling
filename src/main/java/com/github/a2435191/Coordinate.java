package com.github.a2435191;

public record Coordinate(int x, int y) {
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
