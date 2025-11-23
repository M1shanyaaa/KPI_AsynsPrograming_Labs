package Lab3;


public class Monitor {
    // Спільні ресурси
    private long a = 0;   // Максимальний елемент Z
    private long e = 0;   // Скаляр e

    // Прапорці для синхронізації
    private int F1 = 0; // Лічильник введення даних (для wait_in)
    private int F2 = 0; // Лічильник завершення обчислення a (для wait_out_a)
    private int F3 = 0; // Лічильник для a (max(a, a_i))
    private int F4 = 0; // Лічильник для e (put_e)
    private int F5 = 0; // Лічильник завершення R_H (для wait_end_RH)

    // --- Методи для роботи з ресурсами ---

    // T4 вводить e
    public synchronized void put_e(long value) {
        this.e = value;
        F4++;
        if (F4 == 1) {
            notifyAll();
        }
    }

    // T1, T2, T3, T4 копіюють e
    public synchronized long copy_e() {
        while (F4 < 1) {
            try {
                wait();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
        return e;
    }

    // T1, T2, T3, T4 обчислюють max(a, a_i) і оновлюють спільне a
    public synchronized void put_a(long a_i) {
        if (a_i > this.a) {
            this.a = a_i;
        }
        F3++;
        if (F3 == Data.P) {
            notifyAll(); // Всі потоки обчислили свій a_i, спільне a готове
        }
    }

    // T1, T2, T3, T4 копіюють спільне a
    public synchronized long copy_a() {
        while (F3 < Data.P) {
            try {
                wait();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
        return a;
    }

    // --- Методи синхронізації ---

    // Сигнал про завершення введення даних (T1, T2, T4 вводять)
    public synchronized void signal_in() {
        F1++;
        if (F1 == 3) { // T1, T2, T4 вводять
            notifyAll();
        }
    }

    // Очікування завершення введення даних
    public synchronized void wait_in() {
        try {
            while (F1 < 3) {
                wait();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // Сигнал про завершення обчислення a (всі 4 потоки)
    public synchronized void signal_out_a() {
        F2++;
        if (F2 == Data.P) {
            notifyAll();
        }
    }

    // Очікування завершення обчислення a (всі потоки чекають)
    public synchronized void wait_out_a() {
        try {
            while (F2 < Data.P) {
                wait();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // Сигнал про завершення обчислень R_H (T1, T3, T4)
    public synchronized void signal_end_RH() {
        F5++;
        if (F5 == 3) { // T1, T3, T4
            notifyAll();
        }
    }

    // Очікування завершення R_H
    public synchronized void wait_end_RH() {
        try {
            while (F5 < 3) {
                wait();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
