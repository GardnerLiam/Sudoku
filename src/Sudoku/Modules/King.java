package Sudoku.Modules;

import Sudoku.Cell;
public class King extends Restriction{
    @Override
    public Cell[] getAffected(Cell cell) {
        int size = 1;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (cell.getCol()+i < 0 || cell.getCol()+i > 9 ||
                        cell.getRow()+j < 0 || cell.getRow()+j > 9 || (i==0 && j== 0)) {
                    continue;
                }
                size++;
            }
        }
        Cell[] affected = new Cell[size];
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (cell.getCol()+i < 0 || cell.getCol()+i > 9 ||
                        cell.getRow()+j < 0 || cell.getRow()+j > 9 || (i==0 && j== 0)) {
                    continue;
                }
                affected[count] = board[(cell.getCol()+i) + 9 * (cell.getRow()+j)];
                count++;
            }
        }
        return affected;
    }
}
