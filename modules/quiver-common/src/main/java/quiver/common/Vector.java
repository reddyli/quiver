package quiver.common;

import java.util.Objects;

public record Vector(long id, float[] data) {


    public Vector {
        Objects.requireNonNull(data, "data");
        if (data.length == 0) {
            throw new IllegalArgumentException("dim must be > 0");
        }
        for (int i = 0; i < data.length; i++) {
            if (!Float.isFinite(data[i])) {
                throw new IllegalArgumentException(
                        "non-finite value at index " + i + ": " + data[i]);
            }
        }
    }

    public int dim() {
        return data.length;
    }

    @Override
    public boolean equals(Object o) {
       if(o instanceof Vector object) {
           return object.id() == this.id();
       }
       return false;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        int preview = Math.min(4, data.length);
        StringBuilder sb = new StringBuilder(64);
        sb.append("Vector(id=").append(id).append(", dim=").append(data.length).append(", data=[");
        for (int i = 0; i < preview; i++) {
            if (i > 0) sb.append(", ");
            sb.append(data[i]);
        }
        if (data.length > preview) sb.append(", ...");
        sb.append("])");
        return sb.toString();
    }
}