package com.github.a2435191.solvers.dancing_links;

import java.util.List;

/**
 * The "data object," taken almost directly from
 * <a href="https://arxiv.org/pdf/cs/0011047.pdf">Knuth's original paper</a>.
 *
 * @param <T> The type of the additional data associated with each row.
 *            Unused here, since this class is in the header row.
 */
public class Column<T> extends Data<T> {
    private final String name;
    private int size = 0; // number of 1s in column

    public Column(String name, Column<T> left, Column<T> right, Data<T> up, Data<T> down) {
        super(left, right, up, down, null, null);
        this.column = this;
        this.name = name;
    }

    /**
     * Due to how {@link Root#Root(boolean[][], String[], List)} works, we need to initialize {size}
     * late.
     */
    protected final void initializeSize() {
        for (Data<T> verticalNeighbor = this.down;
             verticalNeighbor != this;
             verticalNeighbor = verticalNeighbor.down) {
            this.size++;
        }
    }

    /**
     * Return how many {@code true} values the column holds.
     *
     * @return How many (non-{@code Column}) nodes are in this column.
     */
    public final int getSize() {
        return this.size;
    }

    /**
     * Perform Knuth's cover operation: remove this column from the header row,
     * and remove any row containing any value in this column from all other columns.
     */
    public final void cover() {
        //System.out.println("covering " + this.name);
        // remove this column from the header
        this.right.left = this.left;
        this.left.right = this.right;

        // remove all rows in `this`'s list from all other column lists
        for (Data<T> verticalNeighbor = this.down;
             verticalNeighbor != this;
             verticalNeighbor = verticalNeighbor.down) {
            for (Data<T> horizontalNeighbor = verticalNeighbor.right;
                 horizontalNeighbor != verticalNeighbor;
                 horizontalNeighbor = horizontalNeighbor.right) {
                horizontalNeighbor.down.up = horizontalNeighbor.up;
                horizontalNeighbor.up.down = horizontalNeighbor.down;
                horizontalNeighbor.column.size--;
            }
        }
    }

    /**
     * Undo the cover operation in reverse order.
     */
    final void uncover() {
        //System.out.println("uncovering " + this.name);

        for (Data<T> verticalNeighbor = this.up;
             verticalNeighbor != this;
             verticalNeighbor = verticalNeighbor.up) {
            for (Data<T> horizontalNeighbor = verticalNeighbor.left;
                 horizontalNeighbor != verticalNeighbor;
                 horizontalNeighbor = horizontalNeighbor.left) {
                horizontalNeighbor.down.up = horizontalNeighbor;
                horizontalNeighbor.up.down = horizontalNeighbor;
                horizontalNeighbor.column.size++;
            }
        }

        this.right.left = this;
        this.left.right = this;
    }

    @Override
    public String toString() {
        return "Column object (" + this.name + ")";
    }

    /**
     * Get the name of this column.
     *
     * @return A string representation of this column's constraint.
     */
    public String getName() {
        return this.name;
    }
}
