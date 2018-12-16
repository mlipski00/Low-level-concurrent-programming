package com.company;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class NonlinearOptimization {

    final static int DIMENSION_SIZE = 5;

    public static void main(String[] args) {
        System.out.println(ControlledRandomSearch(0));
        System.out.println(ControlledRandomSearch(2));
        Set<Double> randomPoints = getRandomPoints(DIMENSION_SIZE);
        for (double number : randomPoints) {
            System.out.println(number);
        }
        System.out.println(getMinimalPoint(randomPoints));
        System.out.println(getMaximalPoint(randomPoints));
    }

    static double ControlledRandomSearch(double point) {

            double sumOfXsqure = 0;

            // obliczenie sumy kwadratów wartości X tyle ile wymiarów
            for (int j = 1; j <= DIMENSION_SIZE; j++) {
                sumOfXsqure = +Math.sqrt(point);
            }

            //obliczenie mnożenia cos(x/i) tyle ile wymiarów
            double multiplicationOfCosXdividedByIterator = Math.cos(point);
            for (int j = 2; j <= DIMENSION_SIZE; j++) {
                multiplicationOfCosXdividedByIterator *= Math.cos(point);
            }

        return 1 / 40 * sumOfXsqure + 1 - multiplicationOfCosXdividedByIterator;
    }

    //krok 1
    static Set<Double> getRandomPoints(int dimension) {
        Set<Double> generatedRandomPoints = new HashSet<>();
        Random random = new Random();
        // ograniczam maksymalną liczbę wylosowanych punktów do 100 + (10*(n+))
        // dodatkowo dodaje 1 żeby liczba wylosowanych punków była większa od (10*(n+))
        int setOfPointsSize = random.nextInt(101) + 10 * (dimension + 1) + 1;

        while (generatedRandomPoints.size() != setOfPointsSize) {
            //przyjąłem, że losuję liczby od 0 do 100 (nextDouble() losuje liczby zmiennoprzecinkowe w przedziale 0-1)
            generatedRandomPoints.add(random.nextDouble()*100);
        }
        return generatedRandomPoints;
    }

    //krok 2
    static double getMinimalPoint(Set<Double> setOfRandomPoints)  {
        double minimalPoint = Double.MAX_VALUE;
        double result;
        for (double valueX : setOfRandomPoints) {
            result = ControlledRandomSearch(valueX);
            if (result < minimalPoint) {
                minimalPoint = result;
            }
        }
        return minimalPoint;
    }

    static double getMaximalPoint(Set<Double> setOfRandomPoints) {
        double maximalPoint = 0;
        double result;
        for (double valueX : setOfRandomPoints) {
            result = ControlledRandomSearch(valueX);
            if (result > maximalPoint) {
                maximalPoint = result;
            }
        }
        return maximalPoint;
    }
}
