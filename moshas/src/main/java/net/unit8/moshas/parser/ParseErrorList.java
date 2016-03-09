package net.unit8.moshas.parser;

import java.util.ArrayList;

/**
 *
 * @author kawasimaF
 */
class ParseErrorList extends ArrayList<ParseError>{
    private static final int INITIAL_CAPACITY = 16;
    private final int maxSize;
    
    ParseErrorList(int initialCapacity, int maxSize) {
        super(initialCapacity);
        this.maxSize = maxSize;
    }
    
    boolean canAddError() {
        return size() < maxSize;
    }

    int getMaxSize() {
        return maxSize;
    }

    static ParseErrorList noTracking() {
        return new ParseErrorList(0, 0);
    }
    
    static ParseErrorList tracking(int maxSize) {
        return new ParseErrorList(INITIAL_CAPACITY, maxSize);
    }
}