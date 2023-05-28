package com.github.a2435191;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Wrapper record for puzzle solutions.
 * @param data A {@link Map} from each pentomino used in the solution to its affine transformation.
 */
public record Solution(Map<Pentomino, TransformationAndCoordinate> data) {
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
