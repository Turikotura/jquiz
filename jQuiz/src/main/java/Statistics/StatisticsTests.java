package Statistics;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;

public class StatisticsTests extends TestCase {
    public void testAverage() {
        assertEquals(2.0, Statistics.getAverage(new ArrayList<>(Arrays.asList(1, 2, 2, 3))), 0.001);
        assertEquals(0.0, Statistics.getAverage(new ArrayList<>()), 0.001);
        assertEquals(5.0, Statistics.getAverage(new ArrayList<>(Arrays.asList(5))), 0.001);
        assertEquals(3.0, Statistics.getAverage(new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5))), 0.001);
        assertEquals(-3.0, Statistics.getAverage(new ArrayList<>(Arrays.asList(-1, -2, -3, -4, -5))), 0.001);
        assertEquals(0.0, Statistics.getAverage(new ArrayList<>(Arrays.asList(-1, -2, 3, 4, -3, -4, 3))), 0.001);
    }

    public void testVariance() {
        assertEquals(0.5, Statistics.getVariance(new ArrayList<>(Arrays.asList(1, 2, 2, 3))), 0.001);
        assertEquals(0.0, Statistics.getVariance(new ArrayList<>()), 0.001);
        assertEquals(0.0, Statistics.getVariance(new ArrayList<>(Arrays.asList(5))), 0.001);
        assertEquals(2.0, Statistics.getVariance(new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5))), 0.001);
        assertEquals(2.0, Statistics.getVariance(new ArrayList<>(Arrays.asList(-1, -2, -3, -4, -5))), 0.001);
        assertEquals(64.0 / 7.0, Statistics.getVariance(new ArrayList<>(Arrays.asList(-1, -2, 3, 4, -3, -4, 3))), 0.001);
    }
}
