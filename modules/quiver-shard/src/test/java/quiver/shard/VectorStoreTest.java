package quiver.shard;

import org.junit.jupiter.api.Test;
import quiver.common.Distance;
import quiver.common.DistanceFunction;
import quiver.common.Neighbour;
import quiver.common.Vector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VectorStoreTest {

    private static final float EPS = 1e-5f;

    @Test
    void constructsWithPositiveDim() {
        VectorStore s = new VectorStore(16);
        assertEquals(16, s.dim());
        assertEquals(0, s.size());
    }

    @Test
    void zeroOrNegativeDimThrows() {
        assertThrows(IllegalArgumentException.class, () -> new VectorStore(0));
        assertThrows(IllegalArgumentException.class, () -> new VectorStore(-1));
    }

    @Test
    void insertRejectsNull() {
        VectorStore s = new VectorStore(3);
        assertThrows(NullPointerException.class, () -> s.insert(null));
    }

    @Test
    void insertRejectsDimMismatch() {
        VectorStore s = new VectorStore(3);
        assertThrows(IllegalArgumentException.class,
                () -> s.insert(new Vector(1L, new float[]{1f, 2f})));
    }

    @Test
    void insertSameIdUpdatesInPlace() {
        VectorStore s = new VectorStore(3);
        s.insert(new Vector(1L, new float[]{1f, 0f, 0f}));
        s.insert(new Vector(1L, new float[]{0f, 1f, 0f}));
        assertEquals(1, s.size(), "same id should not add a new entry");
        // query aligned with new data should score best
        List<Neighbour> out = s.search(new float[]{0f, 1f, 0f}, 1, Distance::cosine);
        assertEquals(1L, out.get(0).id());
        assertEquals(0f, out.get(0).distance(), EPS);
    }

    @Test
    void searchOnEmptyStoreReturnsEmpty() {
        VectorStore s = new VectorStore(3);
        List<Neighbour> out = s.search(new float[]{1f, 2f, 3f}, 5, Distance::cosine);
        assertEquals(0, out.size());
    }

    @Test
    void searchReturnsAtMostSizeVectors() {
        VectorStore s = new VectorStore(3);
        s.insert(new Vector(1L, new float[]{1f, 0f, 0f}));
        s.insert(new Vector(2L, new float[]{0f, 1f, 0f}));
        List<Neighbour> out = s.search(new float[]{1f, 0f, 0f}, 10, Distance::cosine);
        assertEquals(2, out.size());
    }

    @Test
    void searchRejectsBadInputs() {
        VectorStore s = new VectorStore(3);
        s.insert(new Vector(1L, new float[]{1f, 2f, 3f}));
        assertThrows(NullPointerException.class,
                () -> s.search(null, 1, Distance::cosine));
        assertThrows(NullPointerException.class,
                () -> s.search(new float[]{1f, 2f, 3f}, 1, null));
        assertThrows(IllegalArgumentException.class,
                () -> s.search(new float[]{1f, 2f, 3f}, 0, Distance::cosine));
        assertThrows(IllegalArgumentException.class,
                () -> s.search(new float[]{1f, 2f}, 1, Distance::cosine));
        assertThrows(IllegalArgumentException.class,
                () -> s.search(new float[]{1f, Float.NaN, 3f}, 1, Distance::cosine));
    }

    @Test
    void referenceMatchesNaive_cosine() {
        assertMatchesNaive(Distance::cosine, 100, 16, 5, 7L);
    }

    @Test
    void referenceMatchesNaive_euclideanSq() {
        assertMatchesNaive(Distance::euclideanSq, 100, 16, 5, 11L);
    }

    @Test
    void referenceMatchesNaive_largeK() {
        assertMatchesNaive(Distance::cosine, 500, 32, 50, 42L);
    }

    // ---------- helpers ----------

    private static void assertMatchesNaive(DistanceFunction fn, int n, int dim, int k, long seed) {
        Random rng = new Random(seed);
        VectorStore store = new VectorStore(dim);
        List<Vector> all = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            float[] data = randomVector(rng, dim);
            Vector v = new Vector(i, data);
            all.add(v);
            store.insert(v);
        }
        float[] query = randomVector(rng, dim);

        List<Neighbour> actual = store.search(query, k, fn);

        List<Neighbour> expected = new ArrayList<>(n);
        for (Vector v : all) {
            expected.add(new Neighbour(v.id(), fn.calculate(query, v.data())));
        }
        expected.sort(Comparator
                .comparingDouble((Neighbour x) -> x.distance())
                .thenComparingLong(Neighbour::id));
        expected = expected.subList(0, k);

        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < k; i++) {
            assertEquals(expected.get(i).id(), actual.get(i).id(),
                    "id mismatch at rank " + i);
            assertEquals(expected.get(i).distance(), actual.get(i).distance(), EPS,
                    "distance mismatch at rank " + i);
        }
        assertTrue(actual.get(0).distance() <= actual.get(k - 1).distance(),
                "results not best-first");
    }

    private static float[] randomVector(Random rng, int dim) {
        float[] v = new float[dim];
        for (int i = 0; i < dim; i++) v[i] = rng.nextFloat() * 2f - 1f;
        return v;
    }
}
