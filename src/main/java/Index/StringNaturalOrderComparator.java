package Index;

import java.util.Comparator;

public class StringNaturalOrderComparator implements Comparator<String> {

    /**
     * compares between 2 strings
     * @param o1 String 1
     * @param o2 String 2
     * @return returns compare to result
     */
    @Override
    public int compare(String o1, String o2) {
        int res = String.CASE_INSENSITIVE_ORDER.compare(o1, o2);
        if (res == 0) {
            res = o1.compareTo(o2);
        }
        return res;
    }
}
