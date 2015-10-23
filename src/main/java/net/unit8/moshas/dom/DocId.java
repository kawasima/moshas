package net.unit8.moshas.dom;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author kawasima
 */
public class DocId {
    private static final AtomicInteger nextId = new AtomicInteger(1);

    private static final ThreadLocal<Integer> docId = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return nextId.getAndIncrement();
        }
    };
    
    
    public static int get() {
        return docId.get();
    }
}
