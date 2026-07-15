package quiver.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VectorTest {

    @Test
    void constructsValidVector() {
        Vector v = new Vector(42L, new float[]{1f, 2f, 3f});
        assertEquals(42L, v.id());
        assertArrayEquals(new float[]{1f, 2f, 3f}, v.data());
        assertEquals(3, v.dim());
    }

    @Test
    void nullDataThrows() {
        NullPointerException e = assertThrows(
                NullPointerException.class,
                () -> new Vector(1L, null));
        assertTrue(e.getMessage() != null && e.getMessage().contains("data"),
                "expected message to mention 'data', got: " + e.getMessage());
    }

    @Test
    void emptyDataThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Vector(1L, new float[0]));
    }

    @Test
    void nanDataThrows() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> new Vector(1L, new float[]{1f, Float.NaN, 3f}));
        assertTrue(e.getMessage().contains("1"),
                "expected message to mention offending index, got: " + e.getMessage());
    }

    @Test
    void positiveInfinityDataThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Vector(1L, new float[]{Float.POSITIVE_INFINITY}));
    }

    @Test
    void negativeInfinityDataThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Vector(1L, new float[]{Float.NEGATIVE_INFINITY}));
    }

    @Test
    void sameIdEqualEvenWithDifferentData() {
        Vector a = new Vector(7L, new float[]{1f, 2f, 3f});
        Vector b = new Vector(7L, new float[]{9f, 8f, 7f});
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void differentIdNotEqual() {
        Vector a = new Vector(1L, new float[]{1f, 2f, 3f});
        Vector b = new Vector(2L, new float[]{1f, 2f, 3f});
        assertNotEquals(a, b);
    }

    @Test
    void notEqualToOtherTypes() {
        Vector v = new Vector(1L, new float[]{1f, 2f, 3f});
        assertNotEquals(v, "not a vector");
        assertNotEquals(v, null);
    }

    @Test
    void noDefensiveCopyOnConstructionOrRead() {
        float[] arr = {1f, 2f, 3f};
        Vector v = new Vector(1L, arr);
        arr[0] = 99f;
        assertEquals(99f, v.data()[0],
                "Vector must not defensively copy data on construction or read");
    }

    @Test
    void toStringContainsIdAndDim() {
        Vector v = new Vector(42L, new float[]{1f, 2f, 3f});
        String s = v.toString();
        assertTrue(s.contains("42"), "toString should include id, got: " + s);
        assertTrue(s.contains("3"),  "toString should include dim, got: " + s);
    }
}
