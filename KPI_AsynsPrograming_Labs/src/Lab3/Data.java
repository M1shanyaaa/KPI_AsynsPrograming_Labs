package Lab3;


import java.util.Arrays;

public class Data {
    // Розміри
    public static final int N = 2000; // Розмір векторів/матриць
    public static final int P = 4;
    public static final int H = N / P;

    // Вектори та Матриці
    public static long[] V = new long[N];
    public static long[][] MC = new long[N][N];
    public static long[][] MM = new long[N][N];
    public static long[] R = new long[N];
    public static long[] B = new long[N];
    public static long[][] X = new long[N][N];
    public static long e;
    public static long[] Z = new long[N];

    // Проміжні результати
    public static long max_a = 0;

    // --- Допоміжні методи для ініціалізації ---

    public static void fillVector(long[] vector, long value) {
        Arrays.fill(vector, value);
    }

    public static void fillMatrix(long[][] matrix, long value) {
        for (long[] row : matrix) {
            Arrays.fill(row, value);
        }
    }

    // --- Методи для паралельних обчислень ---

    // Отримання частини вектора
    public static long[] getPartOfVector(long[] vector, int start, int end) {
        return Arrays.copyOfRange(vector, start, end);
    }

    // Отримання частини матриці (рядки)
    public static long[][] getPartOfMatrix(long[][] matrix, int start, int end) {
        long[][] part = new long[end - start][matrix[0].length];
        for (int i = 0; i < end - start; i++) {
            System.arraycopy(matrix[start + i], 0, part[i], 0, matrix[0].length);
        }
        return part;
    }

    // Множення матриці на вектор (M*V)
    public static long[] multiplyMatrixVector(long[][] matrix, long[] vector) {
        long[] result = new long[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            result[i] = 0;
            for (int j = 0; j < vector.length; j++) {
                result[i] += matrix[i][j] * vector[j];
            }
        }
        return result;
    }

    // Множення матриць (M1*M2)
    public static long[][] multiplyMatrices(long[][] matrix1, long[][] matrix2) {
        int rows = matrix1.length;
        int cols = matrix2[0].length;
        int common = matrix2.length;
        long[][] result = new long[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = 0;
                for (int k = 0; k < common; k++) {
                    result[i][j] += matrix1[i][k] * matrix2[k][j];
                }
            }
        }
        return result;
    }

    // Множення скаляра на вектор (s*V)
    public static long[] multiplyNumVector(long num, long[] vector) {
        long[] result = new long[vector.length];
        for (int i = 0; i < vector.length; i++) {
            result[i] = num * vector[i];
        }
        return result;
    }

    // Множення скаляра на матрицю (s*M)
    public static long[][] multiplyNumMatrix(long num, long[][] matrix) {
        long[][] result = new long[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                result[i][j] = num * matrix[i][j];
            }
        }
        return result;
    }

    // Додавання векторів (V1+V2)
    public static long[] addVectors(long[] v1, long[] v2) {
        long[] result = new long[v1.length];
        for (int i = 0; i < v1.length; i++) {
            result[i] = v1[i] + v2[i];
        }
        return result;
    }

    // Пошук максимального елемента у векторі
    public static long maxVector(long[] vector) {
        long max = Long.MIN_VALUE;
        for (long val : vector) {
            if (val > max) {
                max = val;
            }
        }
        return max;
    }

    public static long[] getVectorFromMatrixColumn(long[][] matrix, int columnIndex) {
        long[] vector = new long[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            vector[i] = matrix[i][columnIndex];
        }
        return vector;
    }
}