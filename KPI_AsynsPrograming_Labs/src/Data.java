import java.util.Arrays;
import java.util.Scanner;

public class Data {
    private static final Object inputLock = new Object();

    // ====== Ввід даних ======

    // ручний ввід вектора
    public static int[] inputVector(Scanner scanner, String name, int n) {
        synchronized (inputLock) {
            int[] v = new int[n];
            System.out.println("Введіть вектор " + name + ":");
            for (int i = 0; i < n; i++) {
                System.out.print(name + "[" + i + "] = ");
                v[i] = scanner.nextInt();
            }
            return v;
        }
    }

    // ручний ввід матриці
    public static int[][] inputMatrix(Scanner scanner, String name, int n) {
        synchronized (inputLock) {
            int[][] m = new int[n][n];
            System.out.println("Введіть матрицю " + name + ":");
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    System.out.print(name + "[" + i + "][" + j + "] = ");
                    m[i][j] = scanner.nextInt();
                }
            }
            return m;
        }
    }

    // автогенерація
    public static int[] fillVectorOnes(int n) {
        int[] v = new int[n];
        Arrays.fill(v, 1);
        return v;
    }

    public static int[][] fillMatrixOnes(int n) {
        int[][] m = new int[n][n];
        for (int i = 0; i < n; i++) Arrays.fill(m[i], 1);
        return m;
    }

    // ====== Операції над векторами та матрицями ======

    public static int[] addVec(int[] A, int[] B) {
        int n = A.length;
        int[] R = new int[n];
        for (int i = 0; i < n; i++) R[i] = A[i] + B[i];
        return R;
    }

    public static int minVec(int[] A) {
        return Arrays.stream(A).min().orElse(0);
    }

    public static int[][] multiply(int[][] A, int[][] B) {
        int n = A.length;
        int[][] R = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                for (int k = 0; k < n; k++)
                    R[i][j] += A[i][k] * B[k][j];
        return R;
    }

    public static int[][] transpose(int[][] A) {
        int n = A.length;
        int[][] R = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                R[j][i] = A[i][j];
        return R;
    }

    public static int[] multVecMat(int[] V, int[][] M) {
        int n = V.length;
        int[] R = new int[n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                R[i] += V[j] * M[j][i];
        return R;
    }

    public static int[] sortVec(int[] V) {
        int[] R = V.clone();
        Arrays.sort(R);
        return R;
    }

    // ====== Вивід ======

    public static void printVector(String msg, int[] V) {
        System.out.println(msg + Arrays.toString(V));
    }

    public static void printMatrix(String msg, int[][] M) {
        System.out.println(msg);
        for (int[] row : M) {
            System.out.println(Arrays.toString(row));
        }
    }
}
