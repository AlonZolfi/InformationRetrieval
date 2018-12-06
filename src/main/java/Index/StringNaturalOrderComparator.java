package Index;

import java.util.Comparator;
import java.util.LinkedList;

public class StringNaturalOrderComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        int res = String.CASE_INSENSITIVE_ORDER.compare(o1, o2);
        if (res == 0) {
            res = o1.compareTo(o2);
        }
        return res;
    }
}
