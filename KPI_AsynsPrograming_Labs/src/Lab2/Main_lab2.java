package Lab2;

import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Main_lab2 {

    public static void main(String[] args) {
        System.out.println("Lab2 Варіант 18: Запуск потоків");
        long startTime = System.currentTimeMillis();

        // Створюємо потоки
        Thread t1 = new T1();
        Thread t2 = new T2();
        Thread t3 = new T3();
        Thread t4 = new T4();

        // Запускаємо потоки
        t1.start();
        t2.start();
        t3.start();
        t4.start();

        // Чекаємо завершення потоків
        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Головний потік перерваний: " + e.getMessage());
        }

        System.out.println("Lab2: Всі потоки завершені");
        long endTime = System.currentTimeMillis();
        long durationInMillis = endTime - startTime;
        System.out.println("Час виконання: " + durationInMillis + " мс");
        System.out.println("Результат MX[0][0] для N=4, value= " + Data.MX[0][0]);
    }

    // --- Клас для спільних даних та службових методів ---
    static class Data {
        public static final int N = 2000; // Розмір векторів/матриць (для тестування)
        public static final int P = 4; // Кількість процесорів/потоків
        public static final int H = N / P; // Розмір підвектора/підматриці

        // Спільні ресурси
        public static volatile long a = 0; // Спільний ресурс 'a'
        public static AtomicLong Atom_d = new AtomicLong(1); // Спільний ресурс 'd' (Atomic)

        // Вектори та матриці (Спільні та введені)
        public static long[] Z = new long[N];
        public static long[] B = new long[N];
        public static long[][] MM = new long[N][N];
        public static long[][] MR = new long[N][N];
        public static long[][] MC = new long[N][N];
        public static long[][] MZ = new long[N][N];
        public static long[][] MX = new long[N][N];

        // Семафори (для синхронізації введення та обчислення 'a')
        public static Semaphore S1 = new Semaphore(0, true);
        public static Semaphore S3 = new Semaphore(0, true);
        public static Semaphore S5 = new Semaphore(0, true);
        public static Semaphore S7 = new Semaphore(0, true);

        public static Semaphore S2 = new Semaphore(0, true);
        public static Semaphore S4 = new Semaphore(0, true);
        public static Semaphore S6 = new Semaphore(0, true);
        public static Semaphore S8 = new Semaphore(0, true);

        // Критична секція
        public static final Object CS_a = new Object(); // Для керування доступом до 'a'

        // Бар'єр (B1 - єдиний бар'єр для синхронізації фінального обчислення)
        public static CyclicBarrier B1 = new CyclicBarrier(P);

        // --- Службові методи ---
        public static long[] fillVector(int size, long value) {
            long[] v = new long[size];
            Arrays.fill(v, value);
            return v;
        }

        public static long[][] fillMatrix(int rows, int cols, long value) {
            long[][] m = new long[rows][cols];
            for (int i = 0; i < rows; i++) Arrays.fill(m[i], value);
            return m;
        }

        public static long[] getPartOfVector(long[] vector, int start, int end) {
            return Arrays.copyOfRange(vector, start, end);
        }

        public static long[][] getPartOfMatrixColumns(long[][] matrix, int startCol, int endCol) {
            int rows = matrix.length;
            long[][] part = new long[rows][endCol - startCol];
            for (int i = 0; i < rows; i++) {
                System.arraycopy(matrix[i], startCol, part[i], 0, endCol - startCol);
            }
            return part;
        }

        public static long scalarProduct(long[] v1, long[] v2) {
            long result = 0;
            for (int i = 0; i < v1.length; i++) {
                result += v1[i] * v2[i];
            }
            return result;
        }

        public static long[][] multiplyMatrices(long[][] matrix1, long[][] matrix2) {
            int rows1 = matrix1.length;
            int cols1 = matrix1[0].length;
            int cols2 = matrix2[0].length;

            if (cols1 != matrix2.length) {
                throw new IllegalArgumentException("Невідповідність розмірів матриць для множення");
            }

            long[][] result = new long[rows1][cols2];
            for (int i = 0; i < rows1; i++) {
                for (int j = 0; j < cols2; j++) {
                    long sum = 0;
                    for (int k = 0; k < cols1; k++) {
                        sum += matrix1[i][k] * matrix2[k][j];
                    }
                    result[i][j] = sum;
                }
            }
            return result;
        }

        public static long[][] multiplyNumMatrix(long num, long[][] matrix) {
            int rows = matrix.length;
            int cols = matrix[0].length;
            long[][] result = new long[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    result[i][j] = num * matrix[i][j];
                }
            }
            return result;
        }

        public static synchronized void joinResultMatrixColumns(long[][] part, long[][] target, int startCol, int endCol) {
            for (int i = 0; i < target.length; i++) {
                System.arraycopy(part[i], 0, target[i], startCol, endCol - startCol);
            }
        }
    }

    // --- Потік Т1 ---
    static class T1 extends Thread {
        @Override
        public void run() {
            System.out.println("[T1] Старт.");
            long a1, d1;
            long[][] MX1;
            int start = 0;
            int end = Data.H;

            try {
                // 1. Уведення Z, d
                Data.Z = Data.fillVector(Data.N, 1);
                Data.Atom_d.set(1);

                // 2. Сигнал T2, T3, T4 про введення даних (S2.1, S3.1, S4.1)
                Data.S1.release(3);

                // 3. Чекати на уведення даних у задачах T2, T3, T4 (W2.1, W3.1, W4.1)
                Data.S3.acquire(1);
                Data.S5.acquire(1);
                Data.S7.acquire(1);

                // 4. Обчислити: a1 = (B_H * Z_H)
                a1 = Data.scalarProduct(Data.getPartOfVector(Data.B, start, end),
                        Data.getPartOfVector(Data.Z, start, end));

                // 5. Обчислити: a = a + a1 (КД1)
                synchronized (Data.CS_a) {
                    Data.a += a1;
                }

                // 6. Сигнал T2, T3, T4 про завершення обчислення a (S2.2, S3.2, S4.2)
                Data.S2.release(3);

                // 7. Чекати на завершення обчислень a в T2, T3, T4 (W2.2, W3.2, W4.2)
                Data.S4.acquire(1);
                Data.S6.acquire(1);
                Data.S8.acquire(1);

                // 8. Копіювати a1 := a (КД2)
                a1 = Data.a;

                // 9. Копіювати d1 := d (КД3 - Atom_d)
                d1 = Data.Atom_d.get();

                // 10. Обчислити: MX_H = a1*(MZ*MM_H) - (MR*MC_H)*d1
                long[][] MMH = Data.getPartOfMatrixColumns(Data.MM, start, end);
                long[][] MCH = Data.getPartOfMatrixColumns(Data.MC, start, end);
                long[][] MZ_MMH = Data.multiplyMatrices(Data.MZ, MMH);
                long[][] MR_MCH = Data.multiplyMatrices(Data.MR, MCH);
                long[][] Term1 = Data.multiplyNumMatrix(a1, MZ_MMH);
                long[][] Term2 = Data.multiplyNumMatrix(d1, MR_MCH);

                MX1 = new long[Data.N][Data.H];
                for (int i = 0; i < Data.N; i++) {
                    for (int j = 0; j < Data.H; j++) {
                        MX1[i][j] = Term1[i][j] - Term2[i][j];
                    }
                }

                // Об'єднати результат у спільну матрицю MX
                Data.joinResultMatrixColumns(MX1, Data.MX, start, end);

                Data.B1.await();

            } catch (InterruptedException | BrokenBarrierException ex) {
                throw new RuntimeException(ex);
            }
            System.out.println("[T1] Завершено.\n");
        }
    }

    // --- Потік Т2 ---
    static class T2 extends Thread {
        @Override
        public void run() {
            System.out.println("[T2] Старт.");
            long a2, d2;
            long[][] MX2;
            int start = Data.H;
            int end = 2 * Data.H;

            try {
                // 1. Уведення MM
                Data.MM = Data.fillMatrix(Data.N, Data.N, 1);

                // 2. Сигнал T1, T3, T4 про введення даних (S1.1, S3.1, S4.1)
                Data.S3.release(3);

                // 3. Чекати на уведення даних у задачах T1, T3, T4 (W1.1, W3.1, W4.1)
                Data.S1.acquire(1);
                Data.S5.acquire(1);
                Data.S7.acquire(1);

                // 4. Обчислити: a2 = (B_H * Z_H)
                a2 = Data.scalarProduct(Data.getPartOfVector(Data.B, start, end),
                        Data.getPartOfVector(Data.Z, start, end));

                // 5. Обчислити: a = a + a2 (КД1)
                synchronized (Data.CS_a) {
                    Data.a += a2;
                }

                // 6. Сигнал T1, T3, T4 про завершення обчислення a (S1.2, S3.2, S4.2)
                Data.S4.release(3);

                // 7. Чекати на завершення обчислень a в T1, T3, T4 (W1.2, W3.2, W4.2)
                Data.S2.acquire(1);
                Data.S6.acquire(1);
                Data.S8.acquire(1);

                // 8. Копіювати a2 := a (КД2)
                a2 = Data.a;

                // 9. Копіювати d2 := d (КД3 - Atom_d)
                d2 = Data.Atom_d.get();

                // 10. Обчислити: MX_H = a2*(MZ*MM_H) - (MR*MC_H)*d2
                long[][] MMH = Data.getPartOfMatrixColumns(Data.MM, start, end);
                long[][] MCH = Data.getPartOfMatrixColumns(Data.MC, start, end);
                long[][] MZ_MMH = Data.multiplyMatrices(Data.MZ, MMH);
                long[][] MR_MCH = Data.multiplyMatrices(Data.MR, MCH);
                long[][] Term1 = Data.multiplyNumMatrix(a2, MZ_MMH);
                long[][] Term2 = Data.multiplyNumMatrix(d2, MR_MCH);
                MX2 = new long[Data.N][Data.H];
                for (int i = 0; i < Data.N; i++) {
                    for (int j = 0; j < Data.H; j++) {
                        MX2[i][j] = Term1[i][j] - Term2[i][j];
                    }
                }

                Data.joinResultMatrixColumns(MX2, Data.MX, start, end);

                // 11. Чекати на завершення обчислень MX_H в T1, T3, T4 (W1.3, W3.3, W4.3)
                Data.B1.await();

                // 12. Виведення результату MX (Т2 виводить результат)
                System.out.println("[T2] Результат MX[0][0]=" + Data.MX[0][0]);

            } catch (InterruptedException | BrokenBarrierException ex) {
                throw new RuntimeException(ex);
            }
            System.out.println("[T2] Завершено.\n");
        }
    }

    // --- Потік Т3 ---
    static class T3 extends Thread {
        @Override
        public void run() {
            System.out.println("[T3] Старт.");
            long a3, d3;
            long[][] MX3;
            int start = 2 * Data.H;
            int end = 3 * Data.H;

            try {
                // 1. Уведення MR, B
                Data.MR = Data.fillMatrix(Data.N, Data.N, 1);
                Data.B = Data.fillVector(Data.N, 1);

                // 2. Сигнал T1, T2, T4 про введення даних (S1.1, S2.1, S4.1)
                Data.S5.release(3);

                // 3. Чекати на уведення даних у задачах T1, T2, T4 (W1.1, W2.1, W4.1)
                Data.S1.acquire(1);
                Data.S3.acquire(1);
                Data.S7.acquire(1);

                // 4. Обчислити: a3 = (B_H * Z_H)
                a3 = Data.scalarProduct(Data.getPartOfVector(Data.B, start, end),
                        Data.getPartOfVector(Data.Z, start, end));

                // 5. Обчислити: a = a + a3 (КД1)
                synchronized (Data.CS_a) {
                    Data.a += a3;
                }

                // 6. Сигнал T1, T2, T4 про завершення обчислення a (S1.2, S2.2, S4.2)
                Data.S6.release(3);

                // 7. Чекати на завершення обчислень a в T1, T2, T4 (W1.2, W2.2, W4.2)
                Data.S2.acquire(1);
                Data.S4.acquire(1);
                Data.S8.acquire(1);

                // 8. Копіювати a3 := a (КД2)
                a3 = Data.a;

                // 9. Копіювати d3 := d (КД3 - Atom_d)
                d3 = Data.Atom_d.get();

                // 10. Обчислити: MX_H = a3*(MZ*MM_H) - (MR*MC_H)*d3
                long[][] MMH = Data.getPartOfMatrixColumns(Data.MM, start, end);
                long[][] MCH = Data.getPartOfMatrixColumns(Data.MC, start, end);
                long[][] MZ_MMH = Data.multiplyMatrices(Data.MZ, MMH);
                long[][] MR_MCH = Data.multiplyMatrices(Data.MR, MCH);
                long[][] Term1 = Data.multiplyNumMatrix(a3, MZ_MMH);
                long[][] Term2 = Data.multiplyNumMatrix(d3, MR_MCH);
                MX3 = new long[Data.N][Data.H];
                for (int i = 0; i < Data.N; i++) {
                    for (int j = 0; j < Data.H; j++) {
                        MX3[i][j] = Term1[i][j] - Term2[i][j];
                    }
                }

                Data.joinResultMatrixColumns(MX3, Data.MX, start, end);

                // 11. Синхронізація (B1.await() замість S2.3.release(1))
                Data.B1.await();

            } catch (InterruptedException | BrokenBarrierException ex) {
                throw new RuntimeException(ex);
            }
            System.out.println("[T3] Завершено.\n");
        }
    }

    // --- Потік Т4 ---
    static class T4 extends Thread {
        @Override
        public void run() {
            System.out.println("[T4] Старт.");
            long a4, d4;
            long[][] MX4;
            int start = 3 * Data.H;
            int end = 4 * Data.H;

            try {
                // 1. Уведення MC, MZ
                Data.MC = Data.fillMatrix(Data.N, Data.N, 1);
                Data.MZ = Data.fillMatrix(Data.N, Data.N, 1);

                // 2. Сигнал T1, T2, T3 про введення даних (S1.1, S2.1, S3.1)
                Data.S7.release(3);

                // 3. Чекати на уведення даних у задачах T1, T2, T3 (W1.1, W2.1, W3.1)
                Data.S1.acquire(1);
                Data.S3.acquire(1);
                Data.S5.acquire(1);

                // 4. Обчислити: a4 = (B_H * Z_H)
                a4 = Data.scalarProduct(Data.getPartOfVector(Data.B, start, end),
                        Data.getPartOfVector(Data.Z, start, end));

                // 5. Обчислити: a = a + a4 (КД1)
                synchronized (Data.CS_a) {
                    Data.a += a4;
                }

                // 6. Сигнал T1, T2, T3 про завершення обчислення a (S1.2, S2.2, S3.2)
                Data.S8.release(3);

                // 7. Чекати на завершення обчислень a в T1, T2, T3 (W1.2, W2.2, W3.2)
                Data.S2.acquire(1);
                Data.S4.acquire(1);
                Data.S6.acquire(1);

                // 8. Копіювати a4 := a (КД2)
                a4 = Data.a;

                // 9. Копіювати d4 := d (КД3 - Atom_d)
                d4 = Data.Atom_d.get();

                // 10. Обчислити: MX_H = a4*(MZ*MM_H) - (MR*MC_H)*d4
                long[][] MMH = Data.getPartOfMatrixColumns(Data.MM, start, end);
                long[][] MCH = Data.getPartOfMatrixColumns(Data.MC, start, end);
                long[][] MZ_MMH = Data.multiplyMatrices(Data.MZ, MMH);
                long[][] MR_MCH = Data.multiplyMatrices(Data.MR, MCH);
                long[][] Term1 = Data.multiplyNumMatrix(a4, MZ_MMH);
                long[][] Term2 = Data.multiplyNumMatrix(d4, MR_MCH);
                MX4 = new long[Data.N][Data.H];
                for (int i = 0; i < Data.N; i++) {
                    for (int j = 0; j < Data.H; j++) {
                        MX4[i][j] = Term1[i][j] - Term2[i][j];
                    }
                }

                Data.joinResultMatrixColumns(MX4, Data.MX, start, end);

                // 11. Синхронізація (B1.await() замість S2.3.release(1))
                Data.B1.await();

            } catch (InterruptedException | BrokenBarrierException ex) {
                throw new RuntimeException(ex);
            }
            System.out.println("[T4] Завершено.\n");
        }
    }
}