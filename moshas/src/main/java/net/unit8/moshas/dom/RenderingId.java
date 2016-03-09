package net.unit8.moshas.dom;

import java.util.ArrayDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author kawasima
 */
public class RenderingId {
    private static final AtomicInteger nextId = new AtomicInteger(1);

    private static final ThreadLocal<ArrayDeque<Integer>> renderingId = new ThreadLocal<ArrayDeque<Integer>>() {
        @Override
        protected ArrayDeque<Integer> initialValue() {
            return new ArrayDeque<>(8);
        }
    };
    
    public static int push() {
        Integer id = nextId.incrementAndGet();
        renderingId.get().addFirst(id);
        return id;
    }
    
    public static int get() {
        ArrayDeque<Integer> deque = renderingId.get();
        return deque.isEmpty() ? 0 : deque.getFirst();
    }
    
    public static int pop() {
        return renderingId.get().removeFirst();
    }
}
