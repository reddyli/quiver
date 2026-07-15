package quiver.common;

import java.util.Objects;

import static java.lang.Math.sqrt;

public final class Distance {

    private Distance() {}

    public static float cosine(float[] a, float[] b) {
        Objects.requireNonNull(a, "a");
        Objects.requireNonNull(b, "b");
        if (a.length != b.length) {
            throw new IllegalArgumentException(
                    "dimension mismatch: a.length=" + a.length + ", b.length=" + b.length);
        }

        float dot = 0.0f;
        float normA = 0.0f;
        float normB = 0.0f;
        for (int i = 0; i < a.length; i++) {
            float ai = a[i];
            float bi = b[i];
            dot   += ai * bi;
            normA += ai * ai;
            normB += bi * bi;
        }

        if (normA == 0.0f || normB == 0.0f) {
            return 2.0f;
        }

        return 1.0f - (float) (dot / (sqrt(normA) * sqrt(normB)));
    }

    public static float euclideanSq(float[] a, float[] b) {
        Objects.requireNonNull(a, "a");
        Objects.requireNonNull(b, "b");
        if (a.length != b.length) {
            throw new IllegalArgumentException(
                    "dimension mismatch: a.length=" + a.length + ", b.length=" + b.length);
        }

        float sum = 0.0f;
        for (int i = 0; i < a.length; i++) {
            float diff = a[i] - b[i];
            sum += diff * diff;
        }
        return sum;
    }
}
