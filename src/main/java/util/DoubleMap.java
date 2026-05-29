package util;

import java.util.HashMap;
import java.util.Map;

/**
 * Hilfsklasse zum bidirectional mappen von Objekten
 */
public class DoubleMap<A, B> {
    private final Map<A, B> mapA;
    private final Map<B, A> mapB;

    public DoubleMap() {
        mapA = new HashMap<>();
        mapB = new HashMap<>();
    }

    public void put(final A a, final B b) {
        mapA.put(a, b);
        mapB.put(b, a);
    }

    public B getB(final A a) {
        return mapA.get(a);
    }

    public A getA(final B b) {
        return mapB.get(b);
    }

    public void removeByA(final A a) {
        final B b = mapA.get(a);
        mapA.remove(a);
        mapB.remove(b);
    }

    public void removeByB(final B b) {
        final A a = mapB.get(b);
        mapB.remove(b);
        mapA.remove(a);
    }

    public void clear() {
        mapA.clear();
        mapB.clear();
    }
}
