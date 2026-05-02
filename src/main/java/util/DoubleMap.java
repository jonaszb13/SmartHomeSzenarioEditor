package util;

import java.util.HashMap;

/**
 * Hilfsklasse zum bidirectional mappen von Objekten
 */
public class DoubleMap <A, B> {
    private final HashMap<A, B> mapA;
    private final HashMap<B, A> mapB;

    public DoubleMap() {
        mapA = new HashMap<>();
        mapB = new HashMap<>();
    }

    public void put(A a, B b) {
        mapA.put(a, b);
        mapB.put(b, a);
    }

    public B getB(A a) {
        return mapA.get(a);
    }

    public A getA(B b) {
        return mapB.get(b);
    }

    public void removeByA(A a) {
        B b = mapA.get(a);
        mapA.remove(a);
        mapB.remove(b);
    }

    public void removeByB(B b) {
        A a = mapB.get(b);
        mapB.remove(b);
        mapA.remove(a);
    }
}
