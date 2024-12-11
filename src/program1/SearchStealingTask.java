package program1;

import java.util.ArrayList;
import java.util.concurrent.RecursiveTask;

class SearchStealingTask extends RecursiveTask<Integer> {
    int[][] array;
    int rowStart, rowEnd, colStart, colEnd;

    public SearchStealingTask(int[][] array, int rowStart, int rowEnd, int colStart, int colEnd) {
        this.array = array;
        this.rowStart = rowStart;
        this.rowEnd = rowEnd;
        this.colStart = colStart;
        this.colEnd = colEnd;
    }

    @Override
    protected Integer compute() {
        if ((rowEnd - rowStart) <= 1){
            Integer result;
            for (int j = colStart; j < colEnd; j++) {
                if (array[rowStart][j] == rowStart + j) {
                    result = new Integer(array[rowStart][j]);
                    return result;
                }
            }
            return null;
        } else {
            ArrayList<SearchStealingTask> tasks = new ArrayList<>();

            for (int i = rowStart; i < rowEnd; i++) {
                SearchStealingTask newRecursiveTask = new SearchStealingTask(array, i, i+1, 0, colEnd);
                tasks.add(newRecursiveTask);
                newRecursiveTask.fork();
            }
            for (SearchStealingTask task : tasks) {
                Integer result = task.join();
                if(result != null) return result;
            }
            return null;
        }
    }
}
