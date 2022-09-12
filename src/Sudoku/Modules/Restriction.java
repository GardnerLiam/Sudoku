package Sudoku.Modules;

import Sudoku.Cell;

public abstract class Restriction {
    Cell[] board;

    public void setBoard(Cell[] cells) {
        this.board = cells;
    }

    public abstract Cell[] getAffected(Cell cell);

    public boolean restrict(Cell current) {
        assert(current.hasValue());
        for (Cell cell : getAffected(current)) {
            if (current != cell) {
                cell.removeAvailable(current.getValue());
                if (cell.isBroken()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void unrestrict(Cell current) {
        assert(current.hasValue());
        for (Cell cell : getAffected(current)) {
            if (current != cell) {
                cell.addAvailable(current.getValue());
            }
        }
        current.removeAvailable(current.getValue());
        current.revert();
    }

    public void unrestrict(Cell current, int value) {
        for (Cell cell : getAffected(current)) {
            if (current != cell) {
                cell.addAvailable(value);
            }
        }
        current.removeAvailable(value);
        current.revert();
    }
}
