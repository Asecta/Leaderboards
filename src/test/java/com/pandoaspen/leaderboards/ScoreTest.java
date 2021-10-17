package com.pandoaspen.leaderboards;

import lombok.Value;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ScoreTest {

    @Value
    static class Score implements Comparable<Score> {
        private double val;

        @Override
        public int compareTo(Score o) {
            return Double.compare(o.val, val);
        }

        public String toString() {
            return String.format("%.0f", val);
        }
    }

    public static void main(String[] args) throws Exception {
        int genCount = 1000000;
        Score[] randomNumbers = IntStream.range(0, genCount).mapToObj(x -> new Score(Math.random() * genCount * 10)).toArray(Score[]::new);
        int limit = 3;

        long start = System.nanoTime();
        sortV1(randomNumbers, limit);
        System.out.println(String.format("V1 Took %.2fms", (System.nanoTime() - start) / 1e+6d));

        start = System.nanoTime();
        sortV2(randomNumbers, limit);
        System.out.println(String.format("V2 Took %.2fms", (System.nanoTime() - start) / 1e+6d));

        Score[] copy = randomNumbers.clone();

        start = System.nanoTime();
        Arrays.sort(copy);
        System.out.println(String.format("Standard Took %.2fms", (System.nanoTime() - start) / 1e+6d));

        copy = randomNumbers.clone();
        start = System.nanoTime();
        Arrays.parallelSort(copy);
        System.out.println(String.format("Parrallel %.2fms", (System.nanoTime() - start) / 1e+6d));
    }

    public static List<Score> sortV1(Score[] randomNumbers, int limit) {


        List<Score> result = new ArrayList<Score>() {
            public boolean add(Score mt) {
                int index = Collections.binarySearch(this, mt);
                if (index < 0) index = ~index;
                super.add(index, mt);
                return true;
            }
        };

        double minAccept = 0;
        boolean accepting = false;
        int count = 0;

        for (Score score : randomNumbers) {
            if (!accepting) {
                result.add(score);
                if (++count == limit) {
                    minAccept = result.get(limit - 1).val;
                    accepting = true;
                }
                continue;
            }

            if (score.val > minAccept) {
                result.add(score);
                result.remove(limit);
                minAccept = result.get(limit - 1).val;
            }
        }


        return result;
    }


    public static Score[] sortV2(Score[] randomNumbers, int limit) {
        Score[] result = new Score[limit];
        for (int i = 0; i < limit; i++) {
            result[i] = new Score(0);
        }

        double minAccept = 0;
        boolean accepting = false;
        int count = 0;


        for (Score score : randomNumbers) {
            if (!accepting) {
                int index = Arrays.binarySearch(result, score);
                if (index < 0) index = ~index;
                System.arraycopy(result, index, result, index + 1, limit - index - 1);
                result[index] = score;

                if (++count == limit) {
                    minAccept = result[limit - 1].val;
                    accepting = true;
                }
                continue;
            }

            if (score.val > minAccept) {
                int index = Arrays.binarySearch(result, score);
                if (index < 0) index = ~index;
                System.arraycopy(result, index, result, index + 1, limit - index - 1);
                result[index] = score;
                minAccept = result[limit - 1].val;
            }
        }


        return result;
    }


    //    @Test
    public void test01Standard() {

        int genCount = 100000;
        List<Score> randomNumbers = IntStream.range(0, genCount).parallel().mapToObj(x -> new Score(Math.random() * genCount * 10)).collect(Collectors.toList());
        int limit = 5;

        List<Score> result = new ArrayList<Score>() {
            public boolean add(Score mt) {
                int index = Collections.binarySearch(this, mt);
                if (index < 0) index = ~index;
                super.add(index, mt);
                return true;
            }
        };

        double minAccept = 0;
        boolean accepting = false;
        int count = 0;

        for (Score score : randomNumbers) {
            if (!accepting) {
                result.add(score);
                if (++count == limit) {
                    minAccept = result.get(limit - 1).val;
                    accepting = true;
                }
                continue;
            }

            if (score.val > minAccept) {
                result.add(score);
                result.remove(limit);
                minAccept = result.get(limit - 1).val;
            }
        }

        System.out.println("Result: " + result);
    }

    //    @Test
    public void test01V1() {
        //        int genCount = 100000;
        //        List<Score> randomNumbers = IntStream.range(0, genCount).parallel().mapToObj(x -> new Score(Math.random() * genCount * 10)).collect(Collectors.toList());
        //        int limit = 5;
        //
        //        List<Score> result = new ArrayList<Score>() {
        //            public boolean add(Score mt) {
        //                int index = Collections.binarySearch(this, mt);
        //                if (index < 0) index = ~index;
        //                super.add(index, mt);
        //                return true;
        //            }
        //        };
        //
        //        double minAccept = 0;
        //        boolean accepting = false;
        //        int count = 0;
        //
        //        for (Score score : randomNumbers) {
        //            if (!accepting) {
        //                result.add(score);
        //                if (++count == limit) {
        //                    minAccept = result.get(limit - 1).val;
        //                    accepting = true;
        //                }
        //                continue;
        //            }
        //
        //            if (score.val > minAccept) {
        //                result.add(score);
        //                result.remove(limit);
        //                minAccept = result.get(limit - 1).val;
        //            }
        //        }
        //
        //        System.out.println(result);
    }


}
