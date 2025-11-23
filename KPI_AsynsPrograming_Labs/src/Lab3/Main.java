/*-----------------------------------------------
Лабораторна робота ЛР3 Варіант 21
Мова Java. Монітори
R = max(Z) * (B * MV) + e * X* (MM * MC)
Рибачок Михайло Володимирович
Група ІО-34
22.11.2025 -----------------------------------------------*/

package Lab3;

import Lab3.Treads.T1;
import Lab3.Treads.T2;
import Lab3.Treads.T3;
import Lab3.Treads.T4;

public class Main {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        // Ініціалізація спільного монітора
        Monitor monitor = new Monitor();

        // Створення потоків
        // Тепер це працює, оскільки T1-T4 є статичними класами всередині Treads
        Thread T1_thread = new Thread(new T1(monitor));
        Thread T2_thread = new Thread(new T2(monitor));
        Thread T3_thread = new Thread(new T3(monitor));
        Thread T4_thread = new Thread(new T4(monitor));

        // Запуск потоків
        T1_thread.start();
        T2_thread.start();
        T3_thread.start();
        T4_thread.start();

        // Очікування завершення всіх потоків
        try {
            T1_thread.join();
            T2_thread.join();
            T3_thread.join();
            T4_thread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Main thread interrupted: " + e.getMessage());
        }

        System.out.println("Lab2: All threads completed!");
        long endTime = System.currentTimeMillis();
        long durationInMillis = endTime - startTime;
        System.out.println("Total execution time: " + durationInMillis + " ms");
    }
}