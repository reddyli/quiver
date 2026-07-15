package quiver.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DistanceTest {

    private static final float EPS_SMALL = 1e-6f;
    private static final float EPS_MED = 1e-5f;

    // ---------- cosine ----------

    @Test
    void cosineIdenticalVectorsIsZero() {
        float[] a = {1f, 2f, 3f};
        assertEquals(0.0f, Distance.cosine(a, a), EPS_SMALL);
    }

    @Test
    void cosineOrthogonalIsOne() {
        assertEquals(1.0f, Distance.cosine(new float[]{1f, 0f}, new float[]{0f, 1f}), EPS_SMALL);
    }

    @Test
    void cosineOppositeDirectionIsTwo() {
        assertEquals(2.0f, Distance.cosine(new float[]{1f, 0f}, new float[]{-1f, 0f}), EPS_SMALL);
    }

    @Test
    void cosineHandComputed() {
        // a=[1,2,3], b=[4,5,6]
        // dot = 4+10+18 = 32
        // |a| = sqrt(14), |b| = sqrt(77)
        // cos = 32 / sqrt(14*77) = 32 / sqrt(1078) ~= 0.97463
        // distance = 1 - 0.97463 = 0.02537
        float result = Distance.cosine(new float[]{1f, 2f, 3f}, new float[]{4f, 5f, 6f});
        assertEquals(0.025368f, result, EPS_MED);
    }

    @Test
    void cosineIsScaleInvariant() {
        // same direction, different magnitude -> distance ~ 0
        float d = Distance.cosine(new float[]{1f, 2f, 3f}, new float[]{2f, 4f, 6f});
        assertEquals(0.0f, d, EPS_MED);
    }

    @Test
    void cosineZeroLeftReturnsSentinel() {
        assertEquals(2.0f, Distance.cosine(new float[]{0f, 0f, 0f}, new float[]{1f, 2f, 3f}));
    }

    @Test
    void cosineZeroRightReturnsSentinel() {
        assertEquals(2.0f, Distance.cosine(new float[]{1f, 2f, 3f}, new float[]{0f, 0f, 0f}));
    }

    @Test
    void cosineBothZeroReturnsSentinel() {
        assertEquals(2.0f, Distance.cosine(new float[]{0f, 0f}, new float[]{0f, 0f}));
    }

    @Test
    void cosineDimensionMismatchThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> Distance.cosine(new float[]{1f, 2f}, new float[]{1f, 2f, 3f}));
    }

    @Test
    void cosineNullThrows() {
        assertThrows(NullPointerException.class,
                () -> Distance.cosine(null, new float[]{1f}));
        assertThrows(NullPointerException.class,
                () -> Distance.cosine(new float[]{1f}, null));
    }

    // ---------- euclideanSq ----------

    @Test
    void euclideanSqIdenticalIsZero() {
        float[] a = {1f, 2f, 3f};
        assertEquals(0.0f, Distance.euclideanSq(a, a));
    }

    @Test
    void euclideanSqHandComputed() {
        // [0,0] vs [3,4] -> 9 + 16 = 25 (squared, not 5)
        assertEquals(25.0f, Distance.euclideanSq(new float[]{0f, 0f}, new float[]{3f, 4f}), EPS_SMALL);
    }

    @Test
    void euclideanSqIsSymmetric() {
        float[] a = {1f, 2f, 3f};
        float[] b = {4f, 6f, 8f};
        assertEquals(Distance.euclideanSq(a, b), Distance.euclideanSq(b, a), EPS_SMALL);
    }

    @Test
    void euclideanSqNonNegative() {
        float d = Distance.euclideanSq(new float[]{-1f, -2f, -3f}, new float[]{4f, 5f, 6f});
        assertTrue(d >= 0f, "expected non-negative, got: " + d);
    }

    @Test
    void euclideanSqAgainstZeroVector() {
        // [0,0,0] vs [1,2,3] -> 1 + 4 + 9 = 14
        assertEquals(14.0f, Distance.euclideanSq(new float[]{0f, 0f, 0f}, new float[]{1f, 2f, 3f}), EPS_SMALL);
    }

    @Test
    void euclideanSqDimensionMismatchThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> Distance.euclideanSq(new float[]{1f, 2f}, new float[]{1f, 2f, 3f}));
    }

    @Test
    void euclideanSqNullThrows() {
        assertThrows(NullPointerException.class,
                () -> Distance.euclideanSq(null, new float[]{1f}));
        assertThrows(NullPointerException.class,
                () -> Distance.euclideanSq(new float[]{1f}, null));
    }
}
