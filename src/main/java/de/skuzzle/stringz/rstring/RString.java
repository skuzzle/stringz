package de.skuzzle.stringz.rstring;

import java.util.HashMap;
import java.util.Map;

public class RString {

    private final static Map<String, RString> INTERN_CACHE = new HashMap<>();

    public final static RString EMPTY = new RString("").intern();

    public static RString intern(String s) {
        synchronized (INTERN_CACHE) {
            RString result = INTERN_CACHE.get(s);
            if (result == null) {
                result = new RString(s);
                INTERN_CACHE.put(s, result);
            }
            return result;
        }
    }


    public final String s;
    public final int length;

    public RString(String s) {
        if (s == null) {
            throw new IllegalArgumentException("s is null");
        }
        this.s = s;
        this.length = s.length();
    }

    public RString intern() {
        return intern(this.s.intern());
    }

    public String s(Object...args) {
        return String.format(this.s, args);
    }

    @Override
    public String toString() {
        return this.s;
    }

    @Override
    public int hashCode() {
        return this.s.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null) {
            return true;
        }

        if (obj instanceof String) {
            return this.s.equals(obj);
        } else if (obj instanceof RString) {
            return this.s.equals(((RString) obj).s);
        }
        return false;
    }
}