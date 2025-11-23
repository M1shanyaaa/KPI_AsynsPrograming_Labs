package Lab3;

import java.util.Arrays;

public class Treads {

    public static class T1 implements Runnable {
        private final Monitor monitor;

        public T1(Monitor monitor) {
            this.monitor = monitor;
        }

        @Override
        public void run() {
            System.out.println("[T1] T1 is started");
            int start = 0 * Data.H;
            int end = 1 * Data.H;

            Data.fillVector(Data.V, 1);
            Data.fillMatrix(Data.MC, 1);

            monitor.signal_in();

            monitor.wait_in();

            long[] ZH = Data.getPartOfVector(Data.Z, start, end);
            long a1 = Data.maxVector(ZH);

            monitor.put_a(a1);

            monitor.signal_out_a();

            monitor.wait_out_a();

            long a_copy = monitor.copy_a();

            long e_copy = monitor.copy_e();

            //Обчислення

            //  Обчислення: MDH = MM * MCH
            long[][] MC_H = Data.getPartOfMatrix(Data.MC, start, end);
            long[][] MD_H = Data.multiplyMatrices(Data.MM, MC_H);

            //  Обчислення: LH = e1 * X * MDH
            long[][] e_X = Data.multiplyNumMatrix(e_copy, Data.X);
            long[] MD_H_as_vector = Data.getVectorFromMatrixColumn(MD_H, 0);
            long[] L_H = Data.multiplyMatrixVector(e_X, MD_H_as_vector);

            //  Обчислення: CH = a1 * (B * MVH)
            long[] MV_H = Data.getPartOfVector(Data.V, start, end);
            long[] B_MV_H = Data.multiplyNumVector(Data.B[0], MV_H);
            long[] C_H = Data.multiplyNumVector(a_copy, B_MV_H);

            //  Обчислення: RH = CH + LH
            long[] R_H = Data.addVectors(C_H, L_H);

            System.arraycopy(R_H, 0, Data.R, start, R_H.length);

            monitor.signal_end_RH();

            System.out.println("[T1] T1 is finished");
        }
    }


    public static class T2 implements Runnable {
        private final Monitor monitor;

        public T2(Monitor monitor) {
            this.monitor = monitor;
        }

        @Override
        public void run() {
            System.out.println("[T2] T2 is started");
            int start = 1 * Data.H;
            int end = 2 * Data.H;

            Data.fillMatrix(Data.MM, 1);

            monitor.signal_in();

            monitor.wait_in();

            long[] ZH = Data.getPartOfVector(Data.Z, start, end);
            long a2 = Data.maxVector(ZH);

            monitor.put_a(a2);
            monitor.signal_out_a();

            monitor.wait_out_a();

            long a_copy = monitor.copy_a();

            long e_copy = monitor.copy_e();

            // --- Обчислення ---

            //  Обчислення: MDH = MM * MCH
            long[][] MC_H = Data.getPartOfMatrix(Data.MC, start, end);
            long[][] MD_H = Data.multiplyMatrices(Data.MM, MC_H);

            //  Обчислення: LH = e2 * X * MDH
            long[][] e_X = Data.multiplyNumMatrix(e_copy, Data.X);
            long[] MD_H_as_vector = Data.getVectorFromMatrixColumn(MD_H, 0);
            long[] L_H = Data.multiplyMatrixVector(e_X, MD_H_as_vector);

            //  Обчислення: CH = a2 * (B * MVH)
            long[] MV_H = Data.getPartOfVector(Data.V, start, end);
            long[] B_MV_H = Data.multiplyNumVector(Data.B[0], MV_H);
            long[] C_H = Data.multiplyNumVector(a_copy, B_MV_H);

            // Обчислення: RH = CH + LH
            long[] R_H = Data.addVectors(C_H, L_H);

            // Копіювання результату в спільний R
            System.arraycopy(R_H, 0, Data.R, start, R_H.length);

            //  Чекати на завершення обчислень RH в Т1, T3, T4
            monitor.wait_end_RH();

            System.out.println("Result R = " + Arrays.toString(Data.R));
            System.out.println("[T2] T2 is finished");
        }
    }

    public static class T3 implements Runnable {
        private final Monitor monitor;

        public T3(Monitor monitor) {
            this.monitor = monitor;
        }

        @Override
        public void run() {
            System.out.println("[T3] T3 is started");
            int start = 2 * Data.H;
            int end = 3 * Data.H;

            monitor.wait_in();
            long[] ZH = Data.getPartOfVector(Data.Z, start, end);
            long a3 = Data.maxVector(ZH);
            monitor.put_a(a3);


            monitor.signal_out_a();


            monitor.wait_out_a();

            long a_copy = monitor.copy_a();


            long e_copy = monitor.copy_e();

            // --- Обчислення ---

            // Обчислення: MDH = MM * MCH
            long[][] MC_H = Data.getPartOfMatrix(Data.MC, start, end);
            long[][] MD_H = Data.multiplyMatrices(Data.MM, MC_H);

            // Обчислення: LH = e3 * X * MDH
            long[][] e_X = Data.multiplyNumMatrix(e_copy, Data.X);
            long[] MD_H_as_vector = Data.getVectorFromMatrixColumn(MD_H, 0);
            long[] L_H = Data.multiplyMatrixVector(e_X, MD_H_as_vector);

            //Обчислення: CH = a3 * (B * MVH)
            long[] MV_H = Data.getPartOfVector(Data.V, start, end);
            long[] B_MV_H = Data.multiplyNumVector(Data.B[0], MV_H);
            long[] C_H = Data.multiplyNumVector(a_copy, B_MV_H);

            // Обчислення: RH = CH + LH
            long[] R_H = Data.addVectors(C_H, L_H);

            // Копіювання результату в спільний R
            System.arraycopy(R_H, 0, Data.R, start, R_H.length);

            // Сигнал Т2 про завершення обчислень R_H
            monitor.signal_end_RH();

            System.out.println("[T3] T3 is finished");
        }
    }


    public static class T4 implements Runnable {
        private final Monitor monitor;

        public T4(Monitor monitor) {
            this.monitor = monitor;
        }

        @Override
        public void run() {
            System.out.println("[T4] T4 is started");
            int start = 3 * Data.H;
            int end = 4 * Data.H;

            Data.fillVector(Data.B, 2);
            Data.fillMatrix(Data.X, 3);
            monitor.put_e(5);
            Data.fillVector(Data.Z, 10);
            Data.Z[end - 1] = 20;

            monitor.signal_in();

            monitor.wait_in();

            long[] ZH = Data.getPartOfVector(Data.Z, start, end);
            long a4 = Data.maxVector(ZH);

            monitor.put_a(a4);

            monitor.signal_out_a();

            monitor.wait_out_a();

            long a_copy = monitor.copy_a();
            long e_copy = monitor.copy_e();

            // Обчислення: MDH, LH, CH, RH
            long[][] MC_H = Data.getPartOfMatrix(Data.MC, start, end);
            long[][] MD_H = Data.multiplyMatrices(Data.MM, MC_H);

            long[][] e_X = Data.multiplyNumMatrix(e_copy, Data.X);
            long[] MD_H_as_vector = Data.getVectorFromMatrixColumn(MD_H, 0);
            long[] L_H = Data.multiplyMatrixVector(e_X, MD_H_as_vector);

            long[] MV_H = Data.getPartOfVector(Data.V, start, end);
            long[] B_MV_H = Data.multiplyNumVector(Data.B[0], MV_H);
            long[] C_H = Data.multiplyNumVector(a_copy, B_MV_H);

            long[] R_H = Data.addVectors(C_H, L_H);

            System.arraycopy(R_H, 0, Data.R, start, R_H.length);

            monitor.signal_end_RH();

            System.out.println("[T4] T4 is finished");
        }
    }
}