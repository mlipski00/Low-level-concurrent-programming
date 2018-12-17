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
        Map<Double, int[]> minimalPoint = getMinimalPoint(randomPoints);
        System.out.println(minimalPoint);
        System.out.println("\n Wierzchołki wygenerowanego symplexu");
        Map.Entry<Double, int[]> entry = minimalPoint.entrySet().iterator().next();
        List<int[]> simplex = createSimplex(randomNPoints, minimalPoint.get(entry.getKey()));

        System.out.print(Arrays.toString(simplex.toArray()) + "\n | ");
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

    /**
     * Krok 2 wyliczanie mimalnej i maksymalnej wartości wśród każdego z wylosowanych punktów
     */

    /**
     * Wyliczenie minimum.
     * @param setOfRandomPoints lista unikalnych wylosowanych punktów.
     * @return jednoelementowa mapa, której kluczem jest najmenijszy wynik funkcji CRS a wartością punkt z którego otrzymano wynik.
     */
    static Map<Double, int[]> getMinimalPoint(Set<int[]> setOfRandomPoints) {
        double minimalPoint = Double.MAX_VALUE;
        double result;
        int[] minimalPointValues = new int[DIMENSION_SIZE];
        for (int[] valueX : setOfRandomPoints) {
            result = ControlledRandomSearch(valueX);
            if (result < minimalPoint) {
                minimalPoint = result;
                minimalPointValues = valueX;
            }
        }
        Map<Double, int[]> csrResultAndPointValues = new HashMap<>();
        csrResultAndPointValues.put(minimalPoint, minimalPointValues);
        return csrResultAndPointValues;
    }

    /**
     * Wyliczenie maximum.
     * @param setOfRandomPoints lista unikalnych wylosowanych punktów.
     * @return jednoelementowa mapa, której kluczem jest największy wynik funkcji CRS a wartością punkt z którego otrzymano wynik.
     */
    static Map<Double, int[]> getMaximalPoint(Set<int[]> setOfRandomPoints) {
        double maximalPoint = 0;
        double result;
        int[] maximalPointValues = new int[DIMENSION_SIZE];
        for (int[] valueX : setOfRandomPoints) {
            result = ControlledRandomSearch(valueX);
            if (result > maximalPoint) {
                maximalPoint = result;
                maximalPointValues = valueX;
            }
        }
        Map<Double, int[]> csrResultAndPointValues = new HashMap<>();
        csrResultAndPointValues.put(maximalPoint, maximalPointValues);
        return csrResultAndPointValues;
    }

    //krok 3 Wylosować ze zbioru P n punktów i utworzyć n + 1 wymiarowy sympleks. Wyznaczyć środek sympleksu:
    static Set<int[]> getNPoints(Set<int[]> randomPoints) {
        Random random = new Random();
        List<int[]> listOfRandomPoints = new ArrayList<>(randomPoints);
        int[] randomNPoints = new int[DIMENSION_SIZE];
        Set<int[]> setOfNPoints = new HashSet<>();
        for (int i = 0; i < DIMENSION_SIZE; i++) {
            setOfNPoints.add(listOfRandomPoints.get(random.nextInt(randomPoints.size())));
        }
        return setOfNPoints;
    }

    static List<int[]> createSimplex(Set<int[]> setOfNPoints, int[] bestPoint) {
        List<int[]> simplex = new ArrayList<>(Arrays.asList(bestPoint));
        simplex.addAll(setOfNPoints);
        return simplex;
    }

    static double getSimplexCenter(double[] simplex) {
        double sumOfSimplexApexes = simplex[0];
        for (int i = 1; i < DIMENSION_SIZE; i++) {
            sumOfSimplexApexes = +simplex[i];
        }
        return (1.0 / (double) DIMENSION_SIZE) * sumOfSimplexApexes;
    }

    // Krok 4 Operacja odbicia: odbij punkt xn względem środka sympleksu
    static double bounceSimplex(double simplexCenter, double[] simplex) {
        return 2 * simplexCenter - simplex[DIMENSION_SIZE + 1];
    }

    // Krok 5  Sprawdź czy xr pełnia ograniczenia. Jeśli tak to Krok 6. Jeśli nie Krok 3.
    static boolean checkLimits(double bouncedSiplex) {
        return bouncedSiplex > -40 && bouncedSiplex < 40 || false;
    }

    //Krok 6 Sprawdź czy jest to punkt lepszy od najgorszego, tzn. spełnia f(xr) < f(xh).
    // Jeśli takto w zbiorze P, w miejsce punktu xh wstaw xr i Krok 2. Jeśli nie to Krok 3

}
