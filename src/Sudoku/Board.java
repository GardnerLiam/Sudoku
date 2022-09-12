package Sudoku;

import Sudoku.Modules.Restriction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class Board {
    Cell[] cells;
    int[] completed;

    private Random random;
    Restriction[] restrictions;

    private HashMap<Integer, Integer> validator;

    private ArrayList<Integer> values;

    int removed = 0;

    public Board(Restriction... restrictions) {
        this.random = new Random();
        this.random.setSeed(1L);
        cells = new Cell[81];
        for (int col = 0; col < 9; col++) {
            for (int row = 0; row < 9; row++) {
                cells[row * 9 + col] = new Cell(row, col);
            }
        }
        this.validator = new HashMap<>();
        values = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            values.add(i + 1);
        }
        this.restrictions = restrictions;
        for (Restriction r : this.restrictions) {
            r.setBoard(this.cells);
        }
    }

    public boolean compareCompleted(Board board) {
        for (int i = 0; i < 81; i++) {
            if (this.cells[i].getValue() != board.completed[i]) {
                return false;
            }
        }
        return true;
    }

    public void fill(Board board) {
        for (int i = 0; i < 81; i++) {
            if (board.cells[i].hasValue()) {
                setCell(cells[i], board.cells[i].getValue());
            }
        }
    }

    public void remove() {
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < 81; i++) {
            indices.add(i);
        }
        Collections.shuffle(indices);
        int count = 81;
        for (int i : indices) {
            int v = cells[i].getValue();
            cells[i].revert();
            for (int n = 0; n < 10; n++) {
                Board board = new Board(this.restrictions);
                board.fill(this);
                board.collapse();
                if (!board.compareCompleted(this)) {
                    cells[i].setValue(v);
                    count--;
                    break;
                }
            }
        }
        System.out.println("Removed: " + count +", Clues: " + (81-count));
    }

    public void fillRandom() {
        for (Cell c : cells) {
            c.setValue(random.nextInt(8) + 1);
        }
    }

    public Cell getCell(int row, int col) {
        return cells[row + col * 9];
    }

    //indexed from 0 to 8
    public Cell[] getRow(int row) {
        Cell[] rowArray = new Cell[9];
        int c = 0;
        for (int i = 9 * row; i < 9 * (row + 1); i++) {
            rowArray[c] = cells[i];
            c++;
        }
        return rowArray;
    }

    public Cell[] getCol(int col) {
        Cell[] colArray = new Cell[9];
        int c = 0;
        for (int i = col; i < col + 81; i += 9) {
            colArray[c] = cells[i];
            c++;
        }
        return colArray;
    }

    public Cell[] getBox(int box) {
        int row = 3 * (box / 3);
        int col = 3 * (box % 3);
        int c = 0;

        Cell[] boxArray = new Cell[9];
        for (int i = col; i < col + 3; i++) {
            for (int j = row; j < row + 3; j++) {
                boxArray[c] = cells[i + 9 * j];
                c++;
            }
        }
        return boxArray;
    }

    public Cell[] getCells() {
        return this.cells;
    }

    public boolean collapse() {
        Cell current = getLeastEntropy();
        if (current == null) {
            return true;
        }
        for (int i : current.getAvailable()) {
            if (setCell(current, i)) {
                if (collapse()) return true;
            }
            unsetCell(current, i);
        }
        return false;
    }

    public Cell getEmptyCell() {
        for (Cell c : cells) {
            if (!c.hasValue()) {
                return c;
            }
        }
        return null;
    }

    public Cell getLeastEntropy() {
        int minEnt = getMinEntropy();
        for (Cell c : cells) {
            if (c.hasValue()) continue;
            if (c.numAvailable == minEnt) {
                return c;
            }
        }
        return null;
    }

    private int getMinEntropy() {
        int ent = 9;
        for (Cell c : cells) {
            if (c.hasValue()) continue;
            if (c.numAvailable < ent) {
                ent = c.numAvailable;
            }
        }
        return ent;
    }

    public boolean setCell(Cell cell, int value) {
        cell.setValue(value);
        for (Restriction r : restrictions) {
            if (!r.restrict(cell)) {
                return false;
            }
        }
        return true;
    }

    public void unsetCell(Cell cell) {
        for (Restriction r : restrictions) {
            r.unrestrict(cell);
        }
    }

    public void unsetCell(Cell cell, int value) {
        for (Restriction r : restrictions) {
            r.unrestrict(cell, value);
        }
    }


    public boolean sudokuRequirements() {
        for (int i = 0; i < 9; i++) {
            Cell[] curRow = this.getRow(i);
            Cell[] curCol = this.getCol(i);
            Cell[] curBox = this.getBox(i);
            if (!validateArray(curRow) || !validateArray(curCol) || !validateArray(curBox)) {
                return false;
            }

        }
        return true;
    }

    public boolean canPlace(Cell cell, int value) {
        int row = cell.row;
        int col = cell.col;
        for (Cell c : getRow(row)) {
            if (c == cell) continue;
            if (c.hasValue() && c.getValue() == value) {
                return false;
            }
        }
        for (Cell c : getCol(col)) {
            if (c == cell) continue;
            if (c.hasValue() && c.getValue() == value) {
                return false;
            }
        }
        for (Cell c : getBox(3 * (row / 3) + (col / 3))) {
            if (c == cell) continue;
            if (c.hasValue() && c.getValue() == value) {
                return false;
            }
        }
        if (row + 2 < 9) {
            if (cells[col + 9 * (row + 1)].hasValue() && cells[col + 9 * (row + 2)].hasValue()) {
                if ((value + cells[col + 9 * (row + 1)].getValue() + cells[col + 9 * (row + 2)].getValue()) % 3 != 0) {
                    return false;
                }
            }
        }

        if (row - 1 >= 0 && row + 1 < 9) {
            if (cells[col + 9 * (row - 1)].hasValue() && cells[col + 9 * (row + 1)].hasValue()) {
                if ((cells[col + 9 * (row - 1)].getValue() + value + cells[col + 9 * (row + 1)].getValue()) % 3 != 0) {
                    return false;
                }
            }
        }

        if (row - 2 >= 0) {
            if (cells[col + 9 * (row - 2)].hasValue() && cells[col + 9 * (row - 1)].hasValue()) {
                if ((cells[col + 9 * (row - 2)].getValue() + cells[col + 9 * (row - 1)].getValue() + value) % 3 != 0) {
                    return false;
                }
            }
        }

        if (col + 2 < 9) {
            if (cells[(col + 1) + 9 * row].hasValue() && cells[(col + 2) + 9 * row].hasValue()) {
                if ((value + cells[col + 1 + 9 * row].getValue() + cells[col + 2 + 9 * row].getValue()) % 3 != 0) {
                    return false;
                }
            }
        }

        if (col - 1 >= 0 && col + 1 < 9) {
            if (cells[col - 1 + 9 * row].hasValue() && cells[col + 1 + 9 * row].hasValue()) {
                if ((cells[col - 1 + 9 * row].getValue() + value + cells[col + 1 + 9 * row].getValue()) % 3 != 0) {
                    return false;
                }
            }
        }

        if (col - 2 >= 0) {
            if (cells[(col - 2) + 9 * row].hasValue() && cells[(col - 1) + 9 * row].hasValue()) {
                if ((cells[col - 2 + 9 * row].getValue() + cells[col - 1 + 9 * row].getValue() + value) % 3 != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean validateArray(Cell[] c) {
        validator.clear();
        for (int i = 0; i < 9; i++) {
            validator.put(i + 1, 1);
        }
        for (Cell cell : c) {
            int x = cell.getValue();
            if (validator.get(x) == null || validator.get(x) == 0) {
                return false;
            }
            int k = validator.get(x);
            validator.put(x, k - 1);
        }
        return true;
    }

    public void assignCompleted() {
        this.completed = new int[81];
        for (int i = 0; i < 81; i++) {
            this.completed[i] = this.cells[i].getValue();
        }
    }

    public void printBoard() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (this.cells[i + 9 * j].hasValue())
                    System.out.print(this.cells[i + 9 * j].getValue());
                else
                    System.out.print(".");
                System.out.print(" ");
                if (j % 3 == 2)
                    System.out.print(" ");
            }
            System.out.println();
            if (i % 3 == 2)
                System.out.println();
        }
    }

    public void printCompleted() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(this.completed[i + 9 * j]);
                System.out.print(" ");
                if (j % 3 == 2) {
                    System.out.print(" ");
                }
            }
            System.out.println();
            if (i % 3 == 2) {
                System.out.println();
            }
        }
    }
}
