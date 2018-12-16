package com.company;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class NonlinearOptimization {

    final static int DIMENSION_SIZE = 5;

    public static void main(String[] args) {
        System.out.println(ControlledRandomSearch(new int[]{0, 0, 0, 0, 0}));
        System.out.println(ControlledRandomSearch(new int[]{2, 0, 0, 0, 0}));
        Set<int[]> randomPoints = getRandomPoints();
        for (int[] number : randomPoints) {
            System.out.print("\n");
            for (int valueOfPoint : number) {
                System.out.print(valueOfPoint + " | ");
            }
        }
        System.out.print("\n");
        System.out.println(getMinimalPoint(randomPoints));
        System.out.println(getMaximalPoint(randomPoints));
    }

    static double ControlledRandomSearch(int[] point) {
        double sumOfXsqure = 0;
        double multiplicationOfCosXdividedByIterator = 0;
        for (int i = 0; i < point.length; i++) {
            // obliczenie sumy kwadratów wartości X tyle ile wymiarów
            sumOfXsqure = +Math.sqrt(point[i]);

        }
        //obliczenie mnożenia cos(x/i) tyle ile wymiarów
        multiplicationOfCosXdividedByIterator = Math.cos(point[0]);
        for (int i = 1; i < point.length; i++) {
            multiplicationOfCosXdividedByIterator *= Math.cos(point[i]);
        }
        return 1 / 40 * sumOfXsqure + 1 - multiplicationOfCosXdividedByIterator;
    }

    //krok 1
    static Set<int[]> getRandomPoints() {
        Set<int[]> generatedRandomPoints = new HashSet<>();
        Random random = new Random();
        // ograniczam maksymalną liczbę wylosowanych punktów do 100 + (10*(n+))
        // dodatkowo dodaje 1 żeby liczba wylosowanych punków była większa od (10*(n+))
        int setOfPointsSize = random.nextInt(101) + 10 * (DIMENSION_SIZE + 1) + 1;

        while (generatedRandomPoints.size() != setOfPointsSize) {
            int[] point = new int[DIMENSION_SIZE];
            for (int i = 0; i < DIMENSION_SIZE; i++) {
                point[i] = random.nextInt();
            }
            generatedRandomPoints.add(point);
        }
        return generatedRandomPoints;
    }

    //krok 2
    static double getMinimalPoint(Set<int[]> setOfRandomPoints) {
        double minimalPoint = Double.MAX_VALUE;
        double result;
        for (int[] valueX : setOfRandomPoints) {
            result = ControlledRandomSearch(valueX);
            if (result < minimalPoint) {
                minimalPoint = result;
            }
        }
        return minimalPoint;
    }

    static double getMaximalPoint(Set<int[]> setOfRandomPoints) {
        double maximalPoint = 0;
        double result;
        for (int[] valueX : setOfRandomPoints) {
            result = ControlledRandomSearch(valueX);
            if (result > maximalPoint) {
                maximalPoint = result;
            }
        }
        return maximalPoint;
    }
}
