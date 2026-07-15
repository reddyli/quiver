package quiver.common;

import java.util.Arrays;
import java.util.List;

public final class Heap {

    private final int capacity;
    private final long[] ids;
    private final float[] distances;
    private int size;

    public Heap(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be > 0, got: " + capacity);
        }
        this.capacity = capacity;
        this.ids = new long[capacity];
        this.distances = new float[capacity];
        this.size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void offer(long id, float distance) {
        if (size < capacity) {
            ids[size] = id;
            distances[size] = distance;
            siftUp(size);
            size++;
        } else if (worse(distances[0], ids[0], distance, id)) {
            ids[0] = id;
            distances[0] = distance;
            siftDown(0);
        }
    }

    public List<Neighbour> drain() {
        Neighbour[] out = new Neighbour[size];
        for (int slot = size - 1; slot >= 0; slot--) {
            long id = ids[0];
            float d = distances[0];
            size--;
            if (size > 0) {
                ids[0] = ids[size];
                distances[0] = distances[size];
                siftDown(0);
            }
            out[slot] = new Neighbour(id, d);
        }
        return Arrays.asList(out);
    }

    private static boolean worse(float dA, long idA, float dB, long idB) {
        return dA > dB || (dA == dB && idA > idB);
    }

    private void siftUp(int i) {
        while (i > 0) {
            int parent = (i - 1) >>> 1;
            if (worse(distances[i], ids[i], distances[parent], ids[parent])) {
                swap(i, parent);
                i = parent;
            } else {
                break;
            }
        }
    }

    private void siftDown(int i) {
        while (true) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            int worst = i;
            if (left < size && worse(distances[left], ids[left], distances[worst], ids[worst])) {
                worst = left;
            }
            if (right < size && worse(distances[right], ids[right], distances[worst], ids[worst])) {
                worst = right;
            }
            if (worst == i) break;
            swap(i, worst);
            i = worst;
        }
    }

    private void swap(int i, int j) {
        long tmpId = ids[i];
        ids[i] = ids[j];
        ids[j] = tmpId;
        float tmpD = distances[i];
        distances[i] = distances[j];
        distances[j] = tmpD;
    }
}
