package program1;

import java.util.concurrent.Callable;

public class SearchDealingTask implements Callable<Integer> {
    int[][] array;
    int rowIndex, colStart, colEnd;

    public SearchDealingTask(int[][] array, int rowIndex, int colStart, int colEnd) {
        this.array = array;
        this.rowIndex = rowIndex;
        this.colStart = colStart;
        this.colEnd = colEnd;
    }

    @Override
    public Integer call() throws Exception {
        Integer result;
        for (int j = colStart; j < colEnd; j++) {
            if (array[rowIndex][j] == rowIndex + j) {
                result = new Integer(array[rowIndex][j]);
                return result;
            }
        }
        return null;
    }
}
