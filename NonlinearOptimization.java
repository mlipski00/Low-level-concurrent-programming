import java.text.DecimalFormat;
import java.util.*;

public class NonlinearOptimization {

    final static int DIMENSION_SIZE = 5;
    final static double EPSILON = 0.0001;
    static Set<int[]> randomPoints;
    static List<int[]> nPoints;
    static List<int[]> simplex;
    static int[] minimalPoint;
    static int[] simplexCenter;
    static int[] xnApexOfSimplex;
    static int[] pointBouncedByCenterOfSimplex;

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        runAlgorithm();
        long stop = System.currentTimeMillis();
        System.out.println("Czas wykonania algorytmu w sekundach: " + ((double) stop - (double) start) / 1000);
    }

    static void runAlgorithm() {
        boolean loop = false;
        boolean activateStepTwo = true;
        randomPoints = getRandomPoints();
        while (!loop) {
            if (activateStepTwo) {
                Map<Double, int[]> minimalPointMap = getMinimalPoint(randomPoints);
                final Map.Entry<Double, int[]> entry = minimalPointMap.entrySet().iterator().next();
                minimalPoint = minimalPointMap.get(entry.getKey());
                System.out.println("Aktualny punkt minimalny to: " + Arrays.toString(minimalPoint));
                DecimalFormat decimalFormat = new DecimalFormat();
                decimalFormat.setMaximumFractionDigits(15);
                System.out.println("Wynik algorytmu CRS: " + decimalFormat.format(controlledRandomSearch(minimalPoint)));
            }
            loop = controlledRandomSearch(minimalPoint) < EPSILON;
            nPoints = getNPoints(randomPoints);
            simplex = createSimplex(nPoints, minimalPoint);
            simplexCenter = getSimplexCenter(simplex);
            xnApexOfSimplex = simplex.get(DIMENSION_SIZE);
            pointBouncedByCenterOfSimplex = bounceSimplex(simplexCenter, xnApexOfSimplex);
            if (checkLimits(pointBouncedByCenterOfSimplex)) {
                double xr = controlledRandomSearch(pointBouncedByCenterOfSimplex);
                double xh = controlledRandomSearch(minimalPoint);
                /**
                 * Krok 6: Sprawdź czy jest to punkt lepszy od najgorszego, tzn. spełnia f(xr) < f(xh).
                 * Jeśli tak to w zbiorze P, w miejsce punktu xh wstaw xr i Krok 2. Jeśli nie to Krok 3
                 */
                if (xr < xh) {
                    randomPoints.remove(minimalPoint);
                    randomPoints.add(pointBouncedByCenterOfSimplex);
                    activateStepTwo = true;
                } else {
                    activateStepTwo = false;
                }
            }
        }
    }

    /**
     * Algorytm CRS.
     */
    static double controlledRandomSearch(int[] point) {
        double sumOfXsqure = 0;
        double multiplicationOfCosXdividedByIterator;

        for (int i = 0; i < point.length; i++) {
            /**
             * Obliczenie sumy kwadratów dla każdej ze składowych punktu.
             */
            if (point[i] != 0) {
                sumOfXsqure = +point[i] * point[i];
            }
        }
        /**
         * Mnożenie cos(x/i) z każdej ze składowych punktu.
         */
        multiplicationOfCosXdividedByIterator = Math.cos(point[0]);
        for (int i = 1; i < point.length; i++) {
            multiplicationOfCosXdividedByIterator *= (Math.cos(point[i] / i + 1));
        }
        return 1 / 40 * sumOfXsqure + 1 - multiplicationOfCosXdividedByIterator;
    }

    /**
     * Krok 1: Wylosować zbiór P punktów o liczności N > 10(n + 1), gdzie n — wymiar zadania.
     */
    static Set<int[]> getRandomPoints() {
        Set<int[]> generatedRandomPoints = new HashSet<>();
        Random random = new Random();
        /**
         * Ograniczam maksymalną liczbę wylosowanych punktów do 500 + (10*(n+1))
         */
        int setOfPointsSize = random.nextInt(501) + 10 * (DIMENSION_SIZE + 1) + 1;

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
     * Krok 2: wyliczanie mimalnej i maksymalnej wartości wśród każdego z wylosowanych punktów.
     */

    /**
     * Wyliczenie minimum.
     *
     * @param setOfRandomPoints lista unikalnych wylosowanych punktów.
     * @return jedno elementowa mapa, której kluczem jest najmenijszy wynik funkcji CRS a wartością punkt z którego otrzymano wynik.
     */
    static Map<Double, int[]> getMinimalPoint(Set<int[]> setOfRandomPoints) {
        double minimalPoint = Double.MAX_VALUE;
        double result;
        int[] minimalPointValues = new int[DIMENSION_SIZE];
        for (int[] valueX : setOfRandomPoints) {
            result = controlledRandomSearch(valueX);
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
     *
     * @param setOfRandomPoints lista unikalnych wylosowanych punktów.
     * @return jedno elementowa mapa, której kluczem jest największy wynik funkcji CRS a wartością punkt z którego otrzymano wynik.
     */
    static Map<Double, int[]> getMaximalPoint(Set<int[]> setOfRandomPoints) {
        double maximalPoint = 0;
        double result;
        int[] maximalPointValues = new int[DIMENSION_SIZE];
        for (int[] valueX : setOfRandomPoints) {
            result = controlledRandomSearch(valueX);
            if (result > maximalPoint) {
                maximalPoint = result;
                maximalPointValues = valueX;
            }
        }
        Map<Double, int[]> csrResultAndPointValues = new HashMap<>();
        csrResultAndPointValues.put(maximalPoint, maximalPointValues);
        return csrResultAndPointValues;
    }

    /**
     * Krok 3: Wylosować ze zbioru P n punktów i utworzyć n + 1 wymiarowy sympleks. Wyznaczyć środek sympleksu. Podział na 3 metody:
     */


    /**
     * Losowanie n punktów.
     */
    static List<int[]> getNPoints(Set<int[]> randomPoints) {
        Random random = new Random();
        List<int[]> listOfRandomPoints = new ArrayList<>(randomPoints);
        List<int[]> setOfNPoints = new ArrayList<>();
        for (int i = 0; i < DIMENSION_SIZE; i++) {
            setOfNPoints.add(listOfRandomPoints.get(random.nextInt(randomPoints.size())));
        }
        return setOfNPoints;
    }

    /**
     * Stworzenie n+1 wymiarowego symplexu.
     */
    static List<int[]> createSimplex(List<int[]> listOfNPoints, int[] bestPoint) {
        List<int[]> simplex = new ArrayList<>(Arrays.asList(bestPoint));
        simplex.addAll(listOfNPoints);
        return simplex;
    }

    /**
     * Wyznaczenie środka symplexu.
     * Uwaga: przyjąłem, że składowe każdego punktu zaokrąglam do liczby całkowitej.
     */
    static int[] getSimplexCenter(List<int[]> simplex) {
        int[] tempPoint;
        /**
         * Java jest językiem z przekazywaniem zmiennych przez wartość. Ale dotyczy to typów prostych.
         * Dla obiektów przekazywaną wartością jest referencja do obiektu.
         * Trzeba użyć clone() przy pobieraniu wartości od minimalPoint. Inaczej zmienna tempSimplexCenter byłaby referencją na tą samą wartość co minimalPoint.
         * Wtedy gdy tempSimplexCenter zmieni wartość, to minimalPoint też wskazywała by na zmienioną wartość przez tempSimplexCenter. Taka sytuacja byłaby błędem.
         */
        int[] tempSimplexCenter = minimalPoint.clone();
        for (int i = 1; i < DIMENSION_SIZE - 1; i++) {
            tempPoint = simplex.get(i);
            for (int j = 0; j < DIMENSION_SIZE; j++) {
                tempSimplexCenter[j] = tempSimplexCenter[j] + tempPoint[j];
            }
        }
        double[] simplexCenterDouble = new double[DIMENSION_SIZE];
        int[] simplexCenter = new int[DIMENSION_SIZE];
        for (int i = 0; i < DIMENSION_SIZE; i++) {
            simplexCenterDouble[i] = (1 / (double) DIMENSION_SIZE) * (double) tempSimplexCenter[i];
            simplexCenter[i] = (int) simplexCenterDouble[i];
        }
        return simplexCenter;
    }

    /**
     * Krok 4: Operacja odbicia: odbij punkt xn względem środka sympleksu.
     */
    static int[] bounceSimplex(int[] simplexCenter, int[] xnApexOfSimplex) {
        int[] pointBouncedByCenterOfSimplex = new int[DIMENSION_SIZE];
        for (int i = 0; i < DIMENSION_SIZE; i++) {
            pointBouncedByCenterOfSimplex[i] = 2 * simplexCenter[i] - xnApexOfSimplex[i];
        }
        return pointBouncedByCenterOfSimplex;
    }

    /**
     * Krok 5: Sprawdź czy xr spełnia ograniczenia. Jeśli tak to Krok 6. Jeśli nie Krok 3.
     */
    static boolean checkLimits(int[] pointBouncedByCenterOfSimplex) {
        for (int i = 0; i < DIMENSION_SIZE; i++) {
            if (pointBouncedByCenterOfSimplex[i] < -40 && pointBouncedByCenterOfSimplex[i] > 40) {
                return false;
            }
        }
        return true;
    }
}

