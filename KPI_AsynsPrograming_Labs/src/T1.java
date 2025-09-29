import java.util.Arrays;
import java.util.Scanner;

public class T1 implements Runnable {
    private final int n;

    public T1(int n) {
        this.n = n;
    }

    @Override
    public void run() {
        System.out.println("[F1] T1 is started");
        int[] A, B, C;
        int[][] MA, MD;

        boolean manual = (n < 4);
        if (manual) {
            Scanner scanner = new Scanner(System.in);
            A = Data.inputVector(scanner, "A", n);
            B = Data.inputVector(scanner, "B", n);
            C = Data.inputVector(scanner, "C", n);
            MA = Data.inputMatrix(scanner, "MA", n);
            MD = Data.inputMatrix(scanner, "MD", n);
        } else {
            A = Data.fillVectorOnes(n);
            B = Data.fillVectorOnes(n);
            C = Data.fillVectorOnes(n);
            MA = Data.fillMatrixOnes(n);
            MD = Data.fillMatrixOnes(n);
        }

        int[] AB = Data.addVec(A, B);
        int minAB = Data.minVec(AB);
        int[] BC = Data.addVec(B, C);
        int[][] MAMD = Data.multiply(MA, MD);

        int[] D = new int[n];
        for (int i = 0; i < n; i++) {
            int rowSum = Arrays.stream(MAMD[i]).sum();
            D[i] = minAB * BC[i] * rowSum;
        }

        Data.printVector("T1 result (F1): ", D);
        System.out.println("[F1] T1 finished");
    }
}
