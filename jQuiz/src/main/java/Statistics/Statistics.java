package Statistics;

import java.util.ArrayList;

public class Statistics {
    public static double getAverage(ArrayList<Integer> arr) {
        if(arr.isEmpty()) return 0;
        double sum = 0;
        for(Integer i : arr) sum += i;
        return sum / arr.size();
    }

    public static double getVariance(ArrayList<Integer> arr) {
        double av = getAverage(arr);
        ArrayList<Integer> squares = new ArrayList();
        for(Integer i : arr) squares.add(i * i);
        return getAverage(squares) - (av * av);
    }
}
