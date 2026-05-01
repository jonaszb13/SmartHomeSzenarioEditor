package util;

import java.util.HashMap;

/**
 * Hilfsklasse zum bidirectional mappen von Objekten
 */
public class DoubleMap <A, B> {
    private final HashMap<A, B> MapA;
    private final HashMap<B, A> MapB;

    public DoubleMap() {
        MapA = new HashMap<>();
        MapB = new HashMap<>();
    }

    public void put(A a, B b) {
        MapA.put(a, b);
        MapB.put(b, a);
    }

    public B getB(A a) {
        return MapA.get(a);
    }

    public A getA(B b) {
        return MapB.get(b);
    }
}
