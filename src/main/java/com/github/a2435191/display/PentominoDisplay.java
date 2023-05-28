package com.github.a2435191.display;

import com.github.a2435191.Main;
import com.github.a2435191.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Quick display for the solutions.
 */
public final class PentominoDisplay extends JFrame {
    private final List<Solution> solutions;
    private final JButton buttonPrev = new JButton("<");
    private final JButton buttonNext = new JButton(">");
    private final JPanel imagePanel = new JPanel();
    private final int cellsWidth;
    private final int cellsHeight;
    private int index = 0;

    public PentominoDisplay(List<Solution> solutions, int cellsWidth, int cellsHeight) {
        this.cellsWidth = cellsWidth;
        this.cellsHeight = cellsHeight;
        this.solutions = new ArrayList<>(solutions);

        this.initializeDisplay();
        this.updateButtonsEnabled();
        this.showPentominoes();
    }

    private void initializeDisplay() {
        this.updateTitle();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));


        buttonPrev.setPreferredSize(new Dimension(20, 20));


        buttonNext.setPreferredSize(new Dimension(20, 20));

        buttonPanel.add(buttonPrev);
        buttonPanel.add(buttonNext);

        buttonPrev.addActionListener(this::onPrevPressed);
        buttonNext.addActionListener(this::onNextPressed);

        this.add(buttonPanel, BorderLayout.NORTH);


        imagePanel.setLayout(new GridLayout(this.cellsHeight, this.cellsWidth, 1, 1));
        imagePanel.setPreferredSize(new Dimension(600, 600));
        this.add(imagePanel, BorderLayout.CENTER);

        this.pack();
    }

    private static Color nameToColor(String name) {
        return switch (name) {
            case "PINK" -> Color.PINK;
            case "DARK_GREEN" -> new Color(25, 105, 43);
            case "YELLOW_ORANGE" -> new Color(255, 188, 0);
            case "LIME" -> Color.GREEN;
            case "LIGHT_BLUE" -> Color.BLUE;
            case "LIGHT_PURPLE" -> new Color(217, 79, 245);
            case "YELLOW" -> Color.YELLOW;
            case "TEAL" -> Color.CYAN;
            case "RED" -> Color.RED;
            case "DARK_BLUE" -> new Color(21, 42, 148);
            case "ORANGE" -> Color.ORANGE;
            case "DARK_PURPLE" -> new Color(128, 12, 151);
            case "GRAY" -> Color.GRAY;
            case "WHITE" -> Color.WHITE;
            default -> throw new RuntimeException("invalid color name: " + name);
        };
    }

    private void showPentominoes() {
        imagePanel.removeAll();

        boolean[][] grid = Main.getDefaultGrid(); // starting grid
        String[][] colorNameGrid = new String[this.cellsHeight][];

        for (int i = 0; i < this.cellsHeight; i++) {
            colorNameGrid[i] = new String[this.cellsWidth];
            for (int j = 0; j < this.cellsWidth; j++) {
                colorNameGrid[i][j] = grid[i][j] ? "GRAY" : "WHITE";
            }
        }

        Solution solution = this.solutions.get(this.index);
        for (var entry : solution.data().entrySet()) {
            Pentomino p = entry.getKey();
            Transformation transform = entry.getValue().transform();
            Coordinate coord = entry.getValue().coord();

            boolean[][] rotated = transform.apply(p.shape);
            for (int y = 0; y < rotated.length; y++) {
                for (int x = 0; x < rotated[0].length; x++) {
                    if (rotated[y][x]) {
                        colorNameGrid[y + coord.y()][x + coord.x()] = p.name();
                    }
                }
            }
        }

        for (String[] row : colorNameGrid) {
            for (String colorName : row) {
                JLabel label = new JLabel();
                label.setToolTipText(colorName);
                label.setOpaque(true);

                label.setBackground(nameToColor(colorName));
                this.imagePanel.add(label);
            }
        }
        imagePanel.revalidate();
        imagePanel.repaint();
    }

    private void onPrevPressed(ActionEvent e) {
        if (this.index > 0) {
            this.index--;
            updateTitle();
            showPentominoes();
        }
        updateButtonsEnabled();

    }

    private void onNextPressed(ActionEvent e) {
        if (this.index < this.solutions.size() - 1) {
            this.index++;
            updateTitle();
            showPentominoes();
        }
        updateButtonsEnabled();
    }

    private void updateTitle() {
        this.setTitle("PentominoDisplay [" + this.index + "]");
    }

    private void updateButtonsEnabled() {
        this.buttonPrev.setEnabled(this.index != 0);
        this.buttonNext.setEnabled(this.index != this.solutions.size() - 1);
    }
}
