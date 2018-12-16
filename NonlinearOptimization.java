package com.company;

import java.util.*;

public class NonlinearOptimization {

    final static int DIMENSION_SIZE = 5;
    final static double EPSILON = 0.001;

    public static void main(String[] args) {
        System.out.println(ControlledRandomSearch(new int[]{0, 0, 0, 0, 0}));
        System.out.println(ControlledRandomSearch(new int[]{2, 0, 0, 0, 0}));
        System.out.println("\nLosowe N punktów");
        Set<int[]> randomPoints = getRandomPoints();
        for (int[] point : randomPoints) {
            System.out.print("\n");
            for (int valueOfPoint : point) {
                System.out.print(valueOfPoint + " | ");
            }
        }
        System.out.println("\nLosowe 5 punktów");
        Set<int[]> randomNPoints = getNPoints(randomPoints);
        for (int[] point : randomNPoints) {
            System.out.print("\n");
            for (int valueOfPoint : point) {
                System.out.print(valueOfPoint + " | ");
            }
        }
        System.out.println("\n minimum i maksimum");
        System.out.println(getMaximalPoint(randomPoints));
        double minimalPoint = getMinimalPoint(randomPoints);
        System.out.println(minimalPoint);
        System.out.println("\n Centrum symplexu");
        double simplex = createSimplexAndGetCenter(randomNPoints, minimalPoint);

            System.out.print(simplex + "\n | ");
    }

    static double ControlledRandomSearch(int[] point) {
        double sumOfXsqure = 0;
        double multiplicationOfCosXdividedByIterator = 0;

        for (int i = 0; i < point.length; i++) {
            // obliczenie sumy kwadratów wartości X dla każdej z wartości punku
            if (point[i] != 0) {
                sumOfXsqure = +point[i] * point[i];
            }
        }
        //obliczenie mnożenia cos(x/i) dla każdej z wartości punku
        multiplicationOfCosXdividedByIterator = Math.cos(point[0]);
        for (int i = 1; i < point.length; i++) {
            multiplicationOfCosXdividedByIterator *= Math.cos(point[i]);
        }
        return 1 / 40 * sumOfXsqure + 1 - multiplicationOfCosXdividedByIterator;
    }

    //krok 1 Wylosować zbiór P punktów o liczności N > 10(n + 1), gdzie n — wymiar zadania.
    static Set<int[]> getRandomPoints() {
        Set<int[]> generatedRandomPoints = new HashSet<>();
        Random random = new Random();
        // ograniczam maksymalną liczbę wylosowanych punktów do 100 + (10*(n+))
        int setOfPointsSize = random.nextInt(101) + 10 * (DIMENSION_SIZE + 1) + 1;

        while (generatedRandomPoints.size() != setOfPointsSize) {
            int[] point = new int[DIMENSION_SIZE];
            for (int i = 0; i < DIMENSION_SIZE; i++) {
                point[i] = random.nextInt(80) - 40;
            }
            generatedRandomPoints.add(point);
        }
        return generatedRandomPoints;
    }

    //krok 2 wyliczanie mimalnej i maksymalnej wartości wśród każdego z wylosowanych punktów
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

    //krok 3 Wylosować ze zbioru P n punktów i utworzyć n + 1 wymiarowy sympleks. Wyznaczyć środek sympleksu:
    static Set<int[]> getNPoints(Set<int[]> randomPoints) {
        Random random = new Random();
        List<int[]> listOfRandomPoints = new ArrayList<>(randomPoints);
        int[] randomNPoints = new int[DIMENSION_SIZE];
        Set<int[]> setOfNPoints = new HashSet<>();
        for (int i = 0; i <= DIMENSION_SIZE; i++) {
            setOfNPoints.add(listOfRandomPoints.get(random.nextInt(randomPoints.size())));
        }
        return setOfNPoints;
    }

    static double createSimplexAndGetCenter(Set<int[]> setOfNPoints, double bestPoint) {
        List<int[]> listOfNPoints = new ArrayList<>(setOfNPoints);
        double[] simplex = new double[DIMENSION_SIZE + 1];
        simplex[0] = bestPoint;
        for (int i = 1; i <= DIMENSION_SIZE; i++) {
            int[] point = listOfNPoints.get(i - 1);
            double crsResult = ControlledRandomSearch(point);
            simplex[i] = crsResult;
        }
        double sumOfSimplexApexes = simplex[0];
        for (int i = 1; i < DIMENSION_SIZE; i++) {
            sumOfSimplexApexes =+ simplex[i];
        }
        double v = (1.0 / (double) DIMENSION_SIZE) * sumOfSimplexApexes;
        return v;
    }
}
