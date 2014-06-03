package de.skuzzle.stringz.extstring;

import java.util.HashMap;
import java.util.Map;

public class ExtString {
    
    private final static Map<String, ExtString> INTERN_CACHE = new HashMap<>();
    
    public final static ExtString EMPTY = new ExtString("").intern();
        
    
    public final String s;
    public final int length;
    
    public ExtString(String s) {
        if (s == null) {
            throw new IllegalArgumentException("s is null");
        }
        this.s = s;
        this.length = s.length();
    }
    
    public ExtString intern() {
        synchronized (INTERN_CACHE) {
            ExtString canonical = INTERN_CACHE.get(this.s);
            if (canonical == null) {
                canonical = new ExtString(this.s.intern());
                INTERN_CACHE.put(canonical.s, canonical);
            }
            return canonical;
        }
    }
    
    public String s(Object...args) {
        return String.format(this.s, args);
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
        } else if (obj instanceof ExtString) {
            return this.s.equals(((ExtString) obj).s);
        }
        return false;
    }
}