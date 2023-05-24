package com.github.a2435191;

import com.github.a2435191.solvers.PentominoPuzzleSolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record Solution(Map<Pentomino, PentominoPuzzleSolver.TransformationAndCoordinate> data) {
    public @Override String toString() {
        if (this.data == null) {
            return "null";
        } else {
            List<String> sb = new ArrayList<>();
            for (var entry : this.data.entrySet()) {
                sb.add(entry.getKey().name() + ": " + entry.getValue().toString());
            }
            return String.join("\n", sb);
        }
    }
}
