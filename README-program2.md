# program2 - Паралельний Пошук файлів зображень у папці через Work Dealing

## Завдання

Необхідно створити програму, яка проходить по файлах певної директорії, знаходить серед них зображення і відкриває останнє зображення після завершення пошуку. Програма повинна використовувати асинхронний підхід.

### Кроки виконання:
1. Користувач обирає директорію, в якій програма повинна знайти файли.
2. Програма перевіряє всі файли в директорії на наявність зображень за допомогою списку допустимих розширень.
3. Файли, які є зображеннями, обробляються паралельно за допомогою Executor Service (Thread Pool).
4. Програма підраховує кількість знайдених зображень.
5. Останнє знайдене зображення відкривається за допомогою стандартної програми для перегляду зображень на комп'ютері.

## Пояснення реалізації

### 1. Вибір директорії
Для того, щоб користувач міг вибрати директорію, використовуємо клас `JFileChooser`, який відкриває діалогове вікно для вибору директорії. За допомогою методу `showOpenDialog()` відображаємо вікно для вибору, і після цього отримуємо шлях до вибраної директорії.

### 2. Пошук зображень
Програма створює список всіх файлів у вибраній директорії та перевіряє кожен файл, чи є він зображенням. Для цього використовується метод `isImageFile`, який перевіряє, чи має файл допустиме розширення.

### 3. Паралельна обробка файлів
Для паралельної обробки файлів використовуємо `ExecutorService` з пулом потоків, створеним за допомогою `Executors.newFixedThreadPool(4)`. Це дозволяє обробляти файли одночасно в кількох потоках, що збільшує швидкість обробки, особливо при великій кількості файлів. Кожен файл передається на обробку в пул потоків через метод `submit()`.

### 4. Виведення часу виконання
Для вимірювання часу виконання частини коду, яка займається обробкою файлів, використовуємо `System.nanoTime()`. Різниця між початковим і кінцевим часом дає нам час виконання в наносекундах, який ми переводимо в секунди для зручності виведення.

### 5. Виведення результатів
Після того, як всі файли оброблені, виводимо кількість знайдених зображень. Якщо зображення знайдені, програма відкриває останнє зображення за допомогою методу `Desktop.getDesktop().open()`, що викликає стандартну програму для перегляду зображень на операційній системі.

## Пояснення використаних технологій

1. **ExecutorService**: Вибрано для обробки файлів в декількох потоках. Це дозволяє програмі працювати швидше при обробці великої кількості файлів.
2. **Thread Pool**: Використано для обмеження кількості одночасно працюючих потоків.
3. **Fork/Join Framework**: Не був використаний безпосередньо, оскільки для цього завдання достатньо тай легше було використовувати ExecutorService для паралельного виконання задач.

## Пояснення вибору підходу

### Чому ExecutorService та Thread Pool?

ExecutorService був обраний, оскільки це **простий** і **ефективний** спосіб асинхронної обробки задач. Для правильного результату досить легко уявити алгоритм а ніж з рекурсивними реалізаціями у ForkThreadPool. Хоча й потрібно зауважити що можливо на більших _дистанціях_ та **розмірах** рекурсив міг бути й швидшим.