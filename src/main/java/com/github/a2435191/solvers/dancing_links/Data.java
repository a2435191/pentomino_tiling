package com.github.a2435191.solvers.dancing_links;


/**
 * The "data object," taken almost directly from
 * <a href="https://arxiv.org/pdf/cs/0011047.pdf">Knuth's original paper</a>.
 *
 * @param <T> The type of the additional data associated with each row.
 */
public class Data<T> {
    public Data<T> left;
    public Data<T> right;
    public Data<T> up;
    public Data<T> down;
    protected Column<T> column;
    protected T associatedRowData;

    public Data(Data<T> left, Data<T> right, Data<T> up, Data<T> down, Column<T> column, T associatedRowData) {
        this.left = left;
        this.right = right;
        this.up = up;
        this.down = down;
        this.column = column;
        this.associatedRowData = associatedRowData;
    }

    /**
     * Get the column of {@code this}.
     *
     * @return The column.
     */
    public final Column<T> getColumn() {
        return this.column;
    }

    /**
     * Arbitrary data associated with the row of {@code this}.
     *
     * @return Data for this row. In the simplest case, a string or integer identifier.
     * It depends on how the problem is encoded.
     */
    public final T getAssociatedRowData() {
        return this.associatedRowData;
    }

    @Override
    public String toString() {
        return "Data object";
    }
}
