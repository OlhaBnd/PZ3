package program2;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    public static final String[] imageExtensions = {
            ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".tiff",
            ".webp", ".heif", ".heic", ".svg", ".raw", ".ico"};

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        JFileChooser folderChooser = new JFileChooser();
        folderChooser.setDialogTitle("Виберіть директорію");
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // Відображаємо діалогове вікно для вибору директорії
        int folderChooseResult = folderChooser.showOpenDialog(null);
        if (folderChooseResult != JFileChooser.APPROVE_OPTION) {
            System.out.println("Не вибрано директорію!");
            return;
        }
        // Отримуємо вибрану директорію
        File directory = folderChooser.getSelectedFile();
        if (!directory.isDirectory()) {
            System.out.println("Вказаний шлях не є директорією!");
            return;
        }
        System.out.println("Вибрана директорія: " + directory);

        // Всі *файли* найдені в папці в папці
        ArrayList<File> filesInDir = new ArrayList<>();
        File[] allInDir = directory.listFiles();
        if (allInDir.length == 0){
            System.out.println("Папка пуста");
            return;
        } else {
            // System.out.print("Знайдені файли: ");
            for (File file : allInDir) {
                if(!file.isDirectory()) {
                    // System.out.print('\"'+file.getName()+"\"; ");
                    filesInDir.add(file);
                }
            }
        }
        if (filesInDir.size() == 0){
            System.out.println("У папці немає файлів лише папки.");
            return;
        }

        ExecutorService es = Executors.newFixedThreadPool(4);
        ArrayList<Future<File>> futures = new ArrayList<>();

        // Починаємо вимірювати час
        long startTime = System.nanoTime();

        // Проходимо по всіх файлах директорії і подаємо кожен файл на обробку
        for (File file : filesInDir) {
            futures.add(es.submit(() -> {
                if (isImageFile(file)) {
                    return file;
                }
                return null;
            }));

        }
        ArrayList<File> imageFiles = new ArrayList<>();
        for (Future<File> future : futures) {
            File result = future.get();
            if (result != null) {
                imageFiles.add(result);
            }
        }
        es.shutdown();
        // Закінчуємо вимірювати час
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        System.out.printf("Час виконання: %.6f секунд\n", duration / 1_000_000_000.0);

        System.out.println("Кількість знайдених зображень: " + imageFiles.size());

        if (!imageFiles.isEmpty()) {
            File lastImage = imageFiles.get(imageFiles.size() - 1);
            System.out.println("Відкриваю останній файл: " + lastImage.getName());
            try {
                openImage(lastImage);
            } catch (IOException e) {
                System.err.println("Не вдалося відкрити зображення.");
                throw new RuntimeException(e);

            }

        }
    }

    private static void openImage(File image) throws IOException {
        // Використовую стандартну програму для цього формату у Windows для відкриття файлу
        Desktop.getDesktop().open(image);
        System.out.println("Зображення успішно відкрито: " + image.getName());
    }

    private static boolean isImageFile(File file) {
        String fileName = file.getName();
        String fileExt = fileName.substring(fileName.lastIndexOf('.'));
        for (String ext : imageExtensions) {
            if (fileExt.equals(ext)){
//                System.out.println(fileName);
                return true;
            }
        }
        return false;
    }
}