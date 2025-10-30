package Lab1;

import java.util.Scanner;

public class T3 implements Runnable {
    private final int n;

    public T3(int n) {
        this.n = n;
    }

    @Override
    public void run() {
        System.out.println("[F3] Lab1.T3 is started");

        int[] R, S;
        int[][] MO, MP;
        boolean manual = (n < 4);

        if (manual) {
            Scanner scanner = new Scanner(System.in);
            R = Data.inputVector(scanner, "R", n);
            S = Data.inputVector(scanner, "S", n);
            MO = Data.inputMatrix(scanner, "MO", n);
            MP = Data.inputMatrix(scanner, "MP", n);
        } else {
            R = Data.fillVectorOnes(n);
            S = Data.fillVectorOnes(n);
            MO = Data.fillMatrixOnes(n);
            MP = Data.fillMatrixOnes(n);
        }

        int[][] MOMP = Data.multiply(MO, MP);
        int[] Rmult = Data.multVecMat(R, MOMP);
        int[] sum = Data.addVec(Rmult, S);
        int[] sorted = Data.sortVec(sum);

        Data.printVector("Lab1.T3 result (F3=3.27): ", sorted);
        System.out.println("[F3] Lab1.T3 is finished");
    }
}
