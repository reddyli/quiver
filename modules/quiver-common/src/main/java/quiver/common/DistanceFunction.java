package quiver.common;


@FunctionalInterface
public interface DistanceFunction {
    float calculate(float[] a, float[] b);
}
