package Sudoku;

import java.util.ArrayList;
import java.util.Random;

public class Cell {
    /*
      a cell has all superpositions as a one-hot int array.
      1 --> index+1 is available
      0 --> index+1 is not available
     */

    int row, col;

    int value;

    int[] available;
    int numAvailable;

    private Random random;
    ArrayList<Integer> availableIndices;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.random = new Random();
        value = -1;
        available = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1};
        numAvailable = 9;
        availableIndices = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            availableIndices.add(i);
        }
    }

    public int[] getAvailable() {
        int[] n = getOrderedAvailable();
        shuffleArray(n);
        return n;
    }

    public int[] getOrderedAvailable() {
        int[] n = new int[numAvailable];
        int c = 0;
        for (int i = 0; i < 9; i++) {
            if (available[i] == 1){
                n[c] = i+1;
                c++;
            }
        }
        return n;
    }

    private void shuffleArray(int[] arr) {
        for (int i = arr.length-1; i > 0; i--) {
            int index = random.nextInt(i+1);
            int a = arr[index];
            arr[index] = arr[i];
            arr[i] = a;
        }
    }

    public void setValue(int v) {
        this.value = v;
    }

    public boolean hasValue() {
        return this.value != -1;
    }

    public boolean isAvailable(int num) {
        return available[num - 1] == 1;
    }

    public int getValue() {
        assert (value != -1);
        return value;
    }

    public int getRandomAvailable() {
        assert (numAvailable > 0);
        return getAvailable()[0];
    }

    public void addAvailable(int num) {
        if (available[num - 1] == 0) {
            available[num - 1] = 1;
            availableIndices.add(num);
            numAvailable++;
        }
    }

    public void removeAvailable(int num) {
        if (available[num - 1] == 1) {
            available[num - 1] = 0;
            availableIndices.remove(Integer.valueOf(num));
            numAvailable--;
        }
    }

    public boolean isBroken() {
        return numAvailable == 0;
    }

    public void revert() {
        this.value = -1;
    }

    @Override
    public String toString() {
        String s = "{"+row+", "+col+": [";
        if (value != -1) {
            s += getValue();
        } else if (numAvailable == 0) {
            s += "BROKEN";
        } else {
            for (int i : getOrderedAvailable()) {
                s+=i+", ";
            }
            s = s.substring(0, s.length()-2);

        }
        s+="]}";
        return s;
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }
}
