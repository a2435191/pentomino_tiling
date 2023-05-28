package com.github.a2435191.solvers.dancing_links;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The "root object," taken almost directly from
 * <a href="https://arxiv.org/pdf/cs/0011047.pdf">Knuth's original paper</a>.
 *
 * @param <T> The type of the additional data associated with each row.
 *            Unused here, since this class is in the header row.
 */
public final class Root<T> extends Column<T> {

    private final ArrayList<Data<T>> dataList = new ArrayList<>();
    private final List<Set<T>> solutions = new ArrayList<>();
    /**
     * If {@code true}, choose columns by the lowest {@link Column#getSize()}.
     * Otherwise, just choose the first column to the right of the root.
     */
    public boolean useShortestColumnAsHeuristic = true;

    public Root(Column<T> left, Column<T> right) {
        super(null, left, right, null, null);
    }


    /**
     * Construct a new instance.
     *
     * @param choices               Boolean matrix representing binary choices, where each row is a
     *                              choice satisfying some constraint (column).
     * @param columnNames           Names to use for the columns. Helpful for debugging. It must be true that
     *                              {@code choices[0].length == columnNames.length}.
     * @param associatedRowDataList Each element is associated with its corresponding row.
     *                              Therefore, {@code choices.length == associatedRowDataList.size()} must hold.
     */
    public Root(boolean[][] choices, String[] columnNames, List<T> associatedRowDataList) {
        this(null, null);

        List<Column<T>> headers = new ArrayList<>();
        Column<T> header = this;
        for (String name : columnNames) {
            Column<T> nextHeader = new Column<>(name, header, null, null, null);
            header.right = nextHeader;

            header = nextHeader;

            headers.add(header);
        }

        header.right = this;
        this.left = header;

        List<Data<T>> lastDataAtEachIndex = new ArrayList<>(headers);
        for (int i = 0; i < choices.length; i++) {
            boolean[] row = choices[i];
            T rowData = associatedRowDataList.get(i);

            final Data<T> dummy = new Data<>(null, null, null, null, null, null);
            Data<T> prevInRow = dummy;
            for (int j = 0; j < row.length; j++) {
                if (row[j]) {
                    Data<T> data = new Data<>(prevInRow, null, lastDataAtEachIndex.get(j), null, headers.get(j), rowData);
                    prevInRow.right = data;
                    lastDataAtEachIndex.get(j).down = data;


                    lastDataAtEachIndex.set(j, data);
                    prevInRow = data;
                }
            }

            // create wraparound links
            Data<T> firstInRow = dummy.right;
            prevInRow.right = firstInRow;
            firstInRow.left = prevInRow;
        }

        for (int i = 0; i < headers.size(); i++) {
            Column<T> c = headers.get(i);
            Data<T> d = lastDataAtEachIndex.get(i);
            c.up = d;
            d.down = c;
            c.initializeSize();
        }
    }

    /**
     * Create a default instance, where the {@code associatedRowDataList} is just the integer index.
     *
     * @param choices     Boolean matrix representing binary choices, where each row is a
     *                    choice satisfying some constraint (column).
     * @param columnNames Names to use for the columns. Helpful for debugging. It must be true that
     *                    {@code choices[0].length == columnNames.length}.
     * @return A default instance of {@code Root<Integer>}.
     */
    public static Root<Integer> createDefault(boolean[][] choices, String... columnNames) {
        List<Integer> index = new ArrayList<>();
        for (int i = 0; i < choices.length; i++) {
            index.add(i);
        }
        return new Root<>(choices, columnNames, index);
    }

    private void search(int k) {
        //System.out.println(k);
        if (this.right == this) {
            this.solutions.add(this.getCurrentSolution());
            return;
        }

        Column<T> column = this.chooseColumn();
        column.cover();

        for (Data<T> verticalNeighbor = column.down;
             verticalNeighbor != column;
             verticalNeighbor = verticalNeighbor.down) {
            if (k < this.dataList.size()) {
                this.dataList.set(k, verticalNeighbor);
            } else if (k == this.dataList.size()) {
                this.dataList.add(verticalNeighbor);
            } else {
                throw new RuntimeException("index too large");
            }

            for (Data<T> horizontalNeighbor = verticalNeighbor.right;
                 horizontalNeighbor != verticalNeighbor;
                 horizontalNeighbor = horizontalNeighbor.right) {
                horizontalNeighbor.column.cover();
            }

            this.search(k + 1);

            verticalNeighbor = this.dataList.get(k);
            column = verticalNeighbor.column;

            for (Data<T> horizontalNeighbor = verticalNeighbor.left;
                 horizontalNeighbor != verticalNeighbor;
                 horizontalNeighbor = horizontalNeighbor.left) {
                horizontalNeighbor.column.uncover();
            }
        }

        column.uncover();
    }

    /**
     * Complete a search of the data.
     *
     * @return A list, where each element (set) is the {@link Data#getAssociatedRowData()}
     * from each row in a particular solution.
     */
    public List<Set<T>> search() {
        // return column labels
        this.search(0);
        return this.solutions;
    }

    private Column<T> chooseColumn() {
        if (this.useShortestColumnAsHeuristic) {
            // choose column with fewest 1s
            int minSize = Integer.MAX_VALUE;
            Column<T> out = null;

            for (Column<T> c = (Column<T>) this.right; c != this; c = (Column<T>) c.right) {
                if (c.getSize() <= minSize) {
                    out = c;
                    minSize = c.getSize();
                }
            }

            return Objects.requireNonNull(out);
        } else {
            return (Column<T>) this.right;
        }
    }

    @Override
    public String toString() {
        return "Root object";
    }

    private Set<T> getCurrentSolution() {
        return this.dataList.stream()
                .map(Data::getAssociatedRowData)
                .collect(Collectors.toSet());
    }
}