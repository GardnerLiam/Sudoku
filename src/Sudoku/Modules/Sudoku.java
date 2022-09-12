package Sudoku.Modules;

import Sudoku.Cell;

public class Sudoku extends Restriction{
    /*
      A module imposes some restriction onto the board
     */

    @Override
    public Cell[] getAffected(Cell cell) {
        Cell[] affected = new Cell[20];
        int count = 0;
        int boxRow = 3*(cell.getRow()/3);
        int boxCol = 3*(cell.getCol()/3);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int curCol = boxCol+i;
                int curRow = boxRow+j;
                if (curCol == cell.getCol() && curRow == cell.getRow()) {
                    continue;
                }
                affected[count] = board[curCol+curRow*9];
                count++;
            }
        }
        for (int i = 9*cell.getRow(); i < 9*(cell.getRow()+1); i++) {
            if (!(boxCol <= board[i].getCol() && board[i].getCol() < 3*((cell.getCol()+3)/3))) {
                affected[count] = board[i];
                count++;
            }
        }
        for (int i = cell.getCol(); i < cell.getCol()+81; i+=9) {
            if (!(boxRow <= board[i].getRow() && board[i].getRow() < 3*((cell.getRow()+3)/3))) {
                affected[count] = board[i];
                count++;
            }
        }
        return affected;
    }
}
