package Lab1;

import java.util.Scanner;

public class T2 implements Runnable {
    private final int n;

    public T2(int n) {
        this.n = n;
    }

    @Override
    public void run() {
        System.out.println("[F2] Lab1.T2 is started");

        int[][] MA, MG, MZ, ML;
        boolean manual = (n < 4);

        if (manual) {
            Scanner scanner = new Scanner(System.in);
            MA = Data.inputMatrix(scanner, "MA", n);
            MG = Data.inputMatrix(scanner, "MG", n);
            MZ = Data.inputMatrix(scanner, "MZ", n);
            ML = Data.inputMatrix(scanner, "ML", n);
        } else {
            MA = Data.fillMatrixOnes(n);
            MG = Data.fillMatrixOnes(n);
            MZ = Data.fillMatrixOnes(n);
            ML = Data.fillMatrixOnes(n);
        }

        int[][] MGZ = Data.multiply(MG, MZ);
        int[][] MAMGZ = Data.multiply(MA, MGZ);
        int[][] trML = Data.transpose(ML);

        int[][] MK = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                MK[i][j] = MAMGZ[i][j] + trML[i][j];

        Data.printMatrix("Lab1.T2 result (F2=2.10):", MK);
        System.out.println("[F2] Lab1.T2 is finished");
    }
}
