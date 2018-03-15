/*
 * Copyright (C) 2018 geekbrains homework lesson5
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Домашнее задание 5
 *
 * @author DSerov
 * @version dated 14 March, 2018
 */
public class EntryPoint {
    static final int SIZE = 10000000;
    static float[] arr = new float[SIZE];
    static int cpuCount = 1;

    public static void main(String[] args) throws Exception {
        // сколько процессоров в системе
        cpuCount = Runtime.getRuntime().availableProcessors();
        if (SIZE % cpuCount != 0)
            throw new Exception("Ваш массив не делится нацело на колво процессовров в системе - " + cpuCount);

        System.out.println("Метод 1 - без многопоточности");
        // инициализируем единицами
        onesFill();

        // замеры по расчету методом 1, без мультипоточности
        long startTime = System.nanoTime();
        method1();
        long estimatedTime = System.nanoTime() - startTime;
        checkResult();
        printFormattedNanoSeconds(estimatedTime);


        System.out.println("Метод 2 - с многопоточностью");
        // инициализируем единицами
        onesFill();

        // замеры по расчету методом 2, с мультипоточностью.
        startTime = System.nanoTime();
        method2();
        estimatedTime = System.nanoTime() - startTime;
        checkResult();
        printFormattedNanoSeconds(estimatedTime);
    }

    /**
     * обнуление масиива
     */
    private static void onesFill() {
        for (int i = 0; i < arr.length; i++)
            arr[i] = 1f;
    }

    /**
     * пересчет значений в ячейках
     */
    private static void method1() {
        for (int i = 0; i < arr.length; i++)
            arr[i] = (float) (arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) *
                    Math.cos(0.4f + i / 2));
    }

    /**
     * пересчет значений в ячейках с мультипоточностью
     */
    private static void method2() throws Exception {
        int halfSize = SIZE / cpuCount;

        // создадим n массивов
        float[][] splices = new float[cpuCount][];

        // разобъем массив на n частей
        for (int i = 0; i < splices.length; i++) {
            splices[i] = new float[halfSize];
            System.arraycopy(arr, i * halfSize, splices[i], 0, halfSize);
        }

        // подготовим и запустим нужное количество потоков для обработки
        Thread threads[] = new Thread[cpuCount];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new CalculateArray(splices[i], i * halfSize));
            // и дождемся, пока они отработают
            System.out.println("Стартуем поток #" + i);
            threads[i].start();
        }

        // Подождем потоки....
        for (int i = 0; i < threads.length; i++)
            threads[i].join();

        // склеим массив воедино
        for (int i = 0; i < splices.length; i++) {
            splices[i] = new float[halfSize];
            System.arraycopy(splices[i], 0, arr, i * halfSize, halfSize);
        }
    }

    /**
     * красиво отформатируем наносекунды в секунды
     *
     * @param nanoseconds
     */
    private static void printFormattedNanoSeconds(long nanoseconds) {
        long nanos = 0;
        long micros = 0;
        long milis = 0;
        long seconds = 0;

        nanos = nanoseconds % 1000;
        nanoseconds /= 1000;
        milis = nanoseconds % 1000;
        nanoseconds /= 1000;
        micros = nanoseconds % 1000;
        nanoseconds /= 1000;
        seconds = nanoseconds;

        System.out.println(String.format("%3ds %3dms %3dmcs %3dns", seconds, micros, milis, nanos));
    }

    /**
     * проверка, что все члены исходного массива пересчитаны
     */
    private static void checkResult() {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == 1f) {
                System.err.println("Ошибка: массив не полностью пересчитан!");
                return;
            }
        }
        System.out.println("Весь массив пересчитан успешно!");
    }

}

/**
 * Класс для пересчета массива в потоке
 */
class CalculateArray implements Runnable {
    private float[] array;
    private int shift; // смещение ячейки в оригинальном массиве

    CalculateArray(float[] array, int shift) {
        this.array = array;
        this.shift = shift;
    }

    public void run() {
        for (int i = 0; i < array.length; i++)
            array[i] = (float) (array[i] *
                    Math.sin(0.2f + (i + shift) / 5) *
                    Math.cos(0.2f + (i + shift) / 5) *
                    Math.cos(0.4f + (i + shift) / 2));
    }
}
