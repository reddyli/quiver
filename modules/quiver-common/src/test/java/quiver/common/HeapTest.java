package quiver.common;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HeapTest {

    @Test
    void constructsWithPositiveCapacity() {
        Heap h = new Heap(1);
        assertEquals(0, h.size());
        assertTrue(h.isEmpty());
    }

    @Test
    void zeroCapacityThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Heap(0));
    }

    @Test
    void negativeCapacityThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Heap(-1));
    }

    @Test
    void emptyHeapDrainsToEmptyList() {
        Heap h = new Heap(5);
        List<Neighbour> out = h.drain();
        assertEquals(0, out.size());
        assertTrue(h.isEmpty());
    }

    @Test
    void fewerThanCapacityReturnsAllBestFirst() {
        Heap h = new Heap(5);
        h.offer(1L, 0.9f);
        h.offer(2L, 0.3f);
        h.offer(3L, 0.7f);
        List<Neighbour> out = h.drain();
        assertEquals(3, out.size());
        assertEquals(new Neighbour(2L, 0.3f), out.get(0));
        assertEquals(new Neighbour(3L, 0.7f), out.get(1));
        assertEquals(new Neighbour(1L, 0.9f), out.get(2));
    }

    @Test
    void exactlyCapacityReturnsAllBestFirst() {
        Heap h = new Heap(3);
        h.offer(1L, 0.9f);
        h.offer(2L, 0.3f);
        h.offer(3L, 0.7f);
        List<Neighbour> out = h.drain();
        assertEquals(3, out.size());
        assertEquals(0.3f, out.get(0).distance());
        assertEquals(0.7f, out.get(1).distance());
        assertEquals(0.9f, out.get(2).distance());
    }

    @Test
    void moreThanCapacityKeepsOnlyBestK() {
        Heap h = new Heap(3);
        float[] dists = {0.9f, 0.3f, 0.7f, 0.1f, 0.5f, 0.8f};
        for (int i = 0; i < dists.length; i++) {
            h.offer(i, dists[i]);
        }
        List<Neighbour> out = h.drain();
        assertEquals(3, out.size());
        // best 3 by distance: 0.1 (id=3), 0.3 (id=1), 0.5 (id=4)
        assertEquals(new Neighbour(3L, 0.1f), out.get(0));
        assertEquals(new Neighbour(1L, 0.3f), out.get(1));
        assertEquals(new Neighbour(4L, 0.5f), out.get(2));
    }

    @Test
    void allEqualDistancesTieBreakBySmallerId() {
        Heap h = new Heap(3);
        // insert ids in reverse order; smaller id should still win
        h.offer(10L, 0.5f);
        h.offer(3L,  0.5f);
        h.offer(7L,  0.5f);
        h.offer(1L,  0.5f);
        h.offer(20L, 0.5f);
        List<Neighbour> out = h.drain();
        assertEquals(List.of(1L, 3L, 7L), out.stream().map(Neighbour::id).toList());
    }

    @Test
    void mixedTiesRespectDistanceThenId() {
        Heap h = new Heap(4);
        h.offer(5L, 0.2f);
        h.offer(2L, 0.2f);
        h.offer(9L, 0.1f);
        h.offer(4L, 0.1f);
        h.offer(7L, 0.3f);
        List<Neighbour> out = h.drain();
        // best 4: (4, 0.1), (9, 0.1), (2, 0.2), (5, 0.2)
        assertEquals(new Neighbour(4L, 0.1f), out.get(0));
        assertEquals(new Neighbour(9L, 0.1f), out.get(1));
        assertEquals(new Neighbour(2L, 0.2f), out.get(2));
        assertEquals(new Neighbour(5L, 0.2f), out.get(3));
    }

    @Test
    void worstFirstSequenceStillProducesBestFirst() {
        Heap h = new Heap(3);
        // offered in descending distance order — adversarial for a naive impl
        for (int i = 0; i < 10; i++) {
            h.offer(i, 1.0f - i * 0.05f);
        }
        List<Neighbour> out = h.drain();
        assertEquals(3, out.size());
        for (int i = 0; i < out.size() - 1; i++) {
            assertTrue(out.get(i).distance() <= out.get(i + 1).distance(),
                    "not best-first at index " + i);
        }
    }

    @Test
    void randomisedMatchesNaiveBaseline() {
        final int n = 1000, k = 50;
        Random rng = new Random(42);
        List<Neighbour> all = new ArrayList<>(n);
        Heap h = new Heap(k);
        for (int i = 0; i < n; i++) {
            float d = rng.nextFloat();
            all.add(new Neighbour(i, d));
            h.offer(i, d);
        }
        all.sort(Comparator
                .comparingDouble((Neighbour x) -> x.distance())
                .thenComparingLong(Neighbour::id));
        List<Neighbour> expected = all.subList(0, k);
        assertEquals(expected, h.drain());
    }

    @Test
    void drainEmptiesTheHeap() {
        Heap h = new Heap(3);
        h.offer(1L, 0.5f);
        h.offer(2L, 0.3f);
        h.drain();
        assertEquals(0, h.size());
        assertTrue(h.isEmpty());
        assertEquals(0, h.drain().size());
    }
}
