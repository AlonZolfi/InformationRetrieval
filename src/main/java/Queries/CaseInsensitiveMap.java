package Queries;

import java.util.HashMap;

/**
 * extends hash map to get insensitive keys (for lower and upper case string)
 */
public class CaseInsensitiveMap extends HashMap<String, String>  {

    /**
     * put entries as lower case to ignore case
     * @param key key of entry
     * @param value value of entry
     * @return df
     */
    @Override
    public String put(String key, String value) {
        return super.put(key.toLowerCase(), value);
    }

    /**
     * return the String of the value of the given key
     * @param key key in the map
     * @return value
     */
    public String get(String key) {
        return super.get(key.toLowerCase());
    }
}