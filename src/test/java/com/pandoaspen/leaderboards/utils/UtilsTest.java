package com.pandoaspen.leaderboards.utils;

import com.mysql.jdbc.TimeUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class UtilsTest {

    @Test
    public void test0ParseTime() {
        Map<String, Long> timeStringMap = new HashMap<>();
        timeStringMap.put("1w", 604800000L);
        timeStringMap.put("1d", 86400000L);
        timeStringMap.put("1h", 3600000L);
        timeStringMap.put("1m", 60000L);
        timeStringMap.put("1s", 1000L);

        long sum = timeStringMap.values().stream().mapToLong(v -> v * 2).sum();
        timeStringMap.put("2w 2d 2h 2m 2s", sum);

        timeStringMap.put("2h", 3600000L * 2);
        timeStringMap.put("1d 5h", 86400000L + 3600000L * 5);
        timeStringMap.put("2h", 3600000L * 2);

        for (Map.Entry<String, Long> entry : timeStringMap.entrySet()) {
            String timeString = entry.getKey();
            long expected = entry.getValue();
            long result = Duration.parseDuration(timeString);
            System.out.println(timeString + ": " + result + " == " + expected);
            Assert.assertEquals(expected, result);
        }

    }
}
