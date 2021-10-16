package com.pandoaspen.leaderboards;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SorterTest {

    @Test
    public void test() {

        List<Integer> list1 = new ArrayList<Integer>() {
            public boolean add(Integer mt) {
                super.add(mt);
                Collections.sort(this);
                return true;
            }
        };

        List<Integer> list2 = new ArrayList<Integer>() {
            public boolean add(Integer mt) {
                int index = Collections.binarySearch(this, mt);
                if (index < 0) index = ~index;
                super.add(index, mt);
                return true;
            }
        };

        int no = 10000;

        long start = System.currentTimeMillis();
        for (int i = 0; i < no; i++) {
            list1.add(i);
        }

        long end1 = System.currentTimeMillis() - start;

        System.out.printf("List 1 took %.3f Seconds\n", end1/ 1000d);

        start = System.currentTimeMillis();
        for (int i = 0; i < no; i++) {
            list2.add(i);
        }

        long end2 = System.currentTimeMillis() - start;
        System.out.printf("List 2 took %.3f Seconds\n", end2 / 1000d);

        System.out.printf("List 2 is %.3f%% faster", ((double) end1) / ((double) end2) * 1000);

        int[] arr1 = list1.stream().mapToInt(i -> i).toArray();
        int[] arr2 = list2.stream().mapToInt(i -> i).toArray();


        Assert.assertArrayEquals(arr1, arr2);
    }

}
