package quiver.shard;

import quiver.common.DistanceFunction;
import quiver.common.Heap;
import quiver.common.Neighbour;
import quiver.common.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class VectorStore {

    private final int dim;
    private final List<Vector> vectors;
    private final Map<Long, Integer> idToIndex;

    public VectorStore(int dim) {
        if (dim <= 0) {
            throw new IllegalArgumentException("dim must be > 0, got: " + dim);
        }
        this.dim = dim;
        this.vectors = new ArrayList<>();
        this.idToIndex = new HashMap<>();
    }

    public int dim() {
        return dim;
    }

    public int size() {
        return vectors.size();
    }

    public void insert(Vector v) {
        Objects.requireNonNull(v, "v");
        if (v.dim() != dim) {
            throw new IllegalArgumentException(
                    "dim mismatch: store=" + dim + ", vector=" + v.dim());
        }
        Integer existing = idToIndex.get(v.id());
        if (existing != null) {
            vectors.set(existing, v);
        } else {
            idToIndex.put(v.id(), vectors.size());
            vectors.add(v);
        }
    }

    public List<Neighbour> search(float[] query, int k, DistanceFunction fn) {
        Objects.requireNonNull(query, "query");
        Objects.requireNonNull(fn, "fn");
        if (k <= 0) {
            throw new IllegalArgumentException("k must be > 0, got: " + k);
        }
        if (query.length != dim) {
            throw new IllegalArgumentException(
                    "dim mismatch: store=" + dim + ", query=" + query.length);
        }
        for (int i = 0; i < query.length; i++) {
            if (!Float.isFinite(query[i])) {
                throw new IllegalArgumentException(
                        "non-finite query value at index " + i + ": " + query[i]);
            }
        }

        Heap heap = new Heap(k);
        for (Vector v : vectors) {
            heap.offer(v.id(), fn.calculate(query, v.data()));
        }
        return heap.drain();
    }
}
