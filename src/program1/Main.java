package program1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Scanner scanner = new Scanner(System.in);


        // Введення кількості рядків
        int m = 0;
        while (true) {
            System.out.print("Введіть кількість рядків (m): ");
            try {
                m = scanner.nextInt();
                if (m <= 0) {
                    System.out.println("Число має бути додатним і не нульовим.");
                } else {
                    break;
                }
            } catch (Exception e) {
                System.out.println("Помилка: введено некоректне значення. Будь ласка, введіть ціле число.");
                scanner.next();
            }
        }
        // Введення кількості стовпців
        int n = 0;
        while (true) {
            System.out.print("Введіть кількість стовпців (n): ");
            try {
                n = scanner.nextInt();
                if (n <= 0) {
                    System.out.println("Число має бути додатним і не нульовим.");
                } else {
                    break;
                }
            } catch (Exception e) {
                System.out.println("Помилка: введено некоректне значення. Будь ласка, введіть ціле число.");
                scanner.next();
            }
        }

        // Створення масиву та його випадкове заповнення
        int[][] array = new int[m][n];
        Random rand = new Random();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                array[i][j] = rand.nextInt(m+n+1);
            }
        }

        System.out.println("Згенерований масив:");
        for (int i = 0; i < m; i++) {
            System.out.println(Arrays.toString(array[i]));
        }

        // Work Stealing
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        SearchStealingTask workStealingTask = new SearchStealingTask(array, 0, m, 0, n);
        long startTime = System.nanoTime();
        Integer stealingResult = forkJoinPool.submit(workStealingTask).join();
        long endTime = System.nanoTime();
        System.out.println("\nРезультат пошуку через Work Stealing: " + stealingResult);
        System.out.printf("Час виконання для Work Stealing: %.8f секунд\n", (endTime - startTime) / 1_000_000_000.0);

        // Work Dealing
        ExecutorService es = Executors.newFixedThreadPool(2);
        ArrayList<SearchDealingTask> dealingTasks = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            dealingTasks.add(new SearchDealingTask(array, i, 0, n));
        }

        ArrayList<Future<Integer>> futuresList = new ArrayList<>();
        startTime = System.nanoTime();
        for (SearchDealingTask dealingTask : dealingTasks) {
            futuresList.add(es.submit(dealingTask));
        }
        Integer dealingResult = null;
        for (Future<Integer> integerFuture : futuresList) {
            dealingResult = integerFuture.get();
            if(dealingResult != null) break;
        }
        endTime = System.nanoTime();
        es.shutdown();
        System.out.println("Результат пошуку через Work Dealing: " + dealingResult);
        System.out.printf("Час виконання для Work Dealing: %.8f секунд\n", (endTime - startTime) / 1_000_000_000.0);

    }
}

