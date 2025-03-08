package net.unit8.moshas;

import java.util.function.Consumer;

/**
 * @author kawasima
 */
public class ServletMoshasEngineProvider {
    private static MoshasEngine engine = new MoshasEngine();

    public static void init(Consumer<MoshasEngine> initializer) {
        initializer.accept(engine);
    }

    public static MoshasEngine get() {
        return engine;
    }
}
