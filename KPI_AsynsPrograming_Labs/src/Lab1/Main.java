package Lab1;/*-----------------------------------------------
Лабораторна робота ЛР1 Варіант 17
Потоки в мові Java
1.20 F1 = MIN(A+B)*(B+C)*(MA*MD)
2.10 F2 = MA*(MG*MZ)+TRANS(ML)
3.27 F3 = SORT(R*(MO*MP)+S)
Рибачок Михайло Володимирович
Група ІО-34
-----------------------------------------------*/
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введіть розмірність N: ");
        int n = scanner.nextInt();

        long startTime = System.currentTimeMillis();

        System.out.println("Lab1 started.");

        Thread t1 = new Thread(new T1(n));
        Thread t2 = new Thread(new T2(n));
        Thread t3 = new Thread(new T3(n));

        t1.setPriority(10);
        t2.setPriority(3);
        t3.setPriority(1);

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Lab1 finished.");
        System.out.println("Загальний час виконання: " + totalTime + " мс");
    }

}
