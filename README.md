# program2 - Паралельний Пошук через Work Stealing та Work Dealing

## Опис

Ця програма демонструє два різних підходи до паралельної обробки задач на пошук елемента в двовимірному масиві:

1. **Work Stealing** - Динамічний розподіл задач між потоками.
2. **Work Dealing** - Фіксований розподіл задач між потоками.

# Алгоритм роботи

## 1. Створення випадкового масиву:
Програма генерує двовимірний масив розміру `m x n` із випадковими цілими числами в діапазоні від `0` до `m + n`.

```java
// Створення масиву та його випадкове заповнення
int[][] array = new int[m][n];
Random rand = new Random();
for (int i = 0; i < m; i++) {
    for (int j = 0; j < n; j++) {
        array[i][j] = rand.nextInt(m + n + 1);  // Заповнення масиву випадковими числами
    }
}
```

## 2. Work Stealing (Динамічний розподіл задач):

Масив розбивається на рядки, і кожен рядок обробляється в окремому потоці. Якщо один потік завершив свою роботу, він може "вкрасти" роботу у іншого потоку, щоб продовжити обробку.

### Опис:
- Масив розбивається на рядки.
- Для кожного рядка створюється окреме завдання, яке виконуватиметься потоком.
- Якщо потік завершує обробку свого рядка, він може "вкрасти" завдання у іншого потоку, який ще не завершив.

### Код:
```java
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
```

## 3. Work Dealing (Фіксований розподіл задач):

Масив також розбивається на рядки, але кожен потік обробляє строго один рядок, без можливості передавати задачі між потоками.

### Опис:
- Масив розбивається на рядки.
- Кожен потік обробляє один рядок, і не має можливості "вкрасти" роботу у інших потоків.
- Потоки працюють незалежно один від одного.

### Код:
```java
ExecutorService es = Executors.newFixedThreadPool(2);
ArrayList<SearchDealingTask> dealingTasks = new ArrayList<>();
for (int i = 0; i < m; i++) {
    // Створення завдання для кожного рядка
    dealingTasks.add(new SearchDealingTask(array, i, 0, n));  
}
```

### Алгоритм:
1. Створюється пул потоків з фіксованим розміром за допомогою `Executors.newFixedThreadPool(2)`.
2. Масив розбивається на рядки, і для кожного рядка створюється окреме завдання типу `SearchDealingTask`.
3. Кожен потік обробляє строго один рядок масиву.
4. Потоки працюють незалежно один від одного і не можуть передавати роботу між собою.
5. Завдання виконуються паралельно, кожен потік обробляє свій рядок масиву.
6. Результат виконання збирається після завершення всіх потоків.


## Основні відмінності між Work Stealing і Work Dealing

#### Поділ роботи:
- **Work Stealing** використовує динамічний підхід: потоки можуть "вкрасти" завдання від інших, якщо ті завершили свою роботу.
- **Work Dealing** використовує фіксований підхід: кожен потік працює тільки з певною частиною масиву (в даному випадку — рядком), і не може змінювати свою частину роботи.

#### Ефективність:
- **Work Stealing** може бути більш ефективним, оскільки дозволяє потоку, який завершив роботу, зайнятися іншими завданнями, таким чином використовуючи всі потоки більш ефективно.
- **Work Dealing** може бути менш ефективним, оскільки потоки можуть залишатися без роботи, якщо їх частина завдання обробляється швидше за інші частини.

#### Використання ресурсів:
- **Work Stealing** може призвести до меншого часу виконання, оскільки потоки можуть обробляти різні частини масиву без фіксованого поділу роботи.
- **Work Dealing** може бути менш ефективним, особливо якщо деякі потоки не використовують свій час на повну.

## Основні класи програми:

#### `Main`:
- Головний клас, який керує введенням користувача, створенням масиву та запуском пошукових задач для обох підходів.

#### `SearchDealingTask`:
- Клас, що представляє задачу для пошуку елемента в конкретному рядку масиву (для Work Dealing).

#### `SearchStealingTask`:
- Клас, що представляє задачу для пошуку елемента з динамічним розподілом задач (для Work Stealing).


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