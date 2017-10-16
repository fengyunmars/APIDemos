package android.widget;

import android.util.Pair;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by prize on 2017/10/9.
 */

public class FGridLayout {

    public final static class FInterval {

        public final int min;
        public final int max;

        public FInterval(int min, int max) {
            this.min = min;
            this.max = max;
        }

        int size() {
            return max - min;
        }

        FInterval inverse() {
            return new FInterval(max, min);
        }


        @Override
        public boolean equals(Object that) {
            if (this == that) {
                return true;
            }
            if (that == null || getClass() != that.getClass()) {
                return false;
            }

            FInterval interval = (FInterval) that;

            if (max != interval.max) {
                return false;
            }
            //noinspection RedundantIfStatement
            if (min != interval.min) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = min;
            result = 31 * result + max;
            return result;
        }

        @Override
        public String toString() {
            return "[" + min + ", " + max + "]";
        }
    }

    public final static class FMutableInt {
        public int value;

        public FMutableInt() {
            reset();
        }

        public FMutableInt(int value) {
            this.value = value;
        }

        public void reset() {
            value = Integer.MIN_VALUE;
        }

        @Override
        public String toString() {
            return Integer.toString(value);
        }
    }



    public final static class FArc {
        public final FInterval span;
        public final FMutableInt value;
        public boolean valid = true;

        public FArc(FInterval span, FMutableInt value) {
            this.span = span;
            this.value = value;
        }

        @Override
        public String toString() {
            return span + " " + (!valid ? "+>" : "->") + " " + value;
        }
    }


    public final class FAxis {

        private static final int NEW = 0;
        private static final int PENDING = 1;
        private static final int COMPLETE = 2;

        // Group arcs by their first vertex 顶点 , returning an array of arrays.
        // This is linear in the number of arcs.
        FArc[][] groupFArcsByFirstVertex(FArc[] arcs) {
            int N = getCount() + 1; // the number of vertices
            FArc[][] result = new FArc[N][];
            int[] sizes = new int[N];
            for (FArc arc : arcs) {
                sizes[arc.span.min]++;
            }
            //src for (int i = 0; i < sizes.length; i++) {
            for (int i = 0; i < N; i++) {
                result[i] = new FArc[sizes[i]];
            }
            // reuse the sizes array to hold the current last elements as we insert each arc
            Arrays.fill(sizes, 0);
            for (FArc arc : arcs) {
                int i = arc.span.min;
                result[i][sizes[i]++] = arc;
            }

            return result;
        }
        // 拓扑学的
        public FArc[] topologicalSort(final FArc[] arcs) {
            return new Object() {
                FArc[] result = new FArc[arcs.length];
                int cursor = result.length - 1;
                FArc[][] arcsByVertex = groupFArcsByFirstVertex(arcs);
                int[] visited = new int[getCount() + 1];
                // loc is from 0 to N (getCount + 1)
                void walk(int loc) {
                    switch (visited[loc]) {
                        case NEW: {
                            visited[loc] = PENDING;
                            for (FArc arc : arcsByVertex[loc]) {
                                walk(arc.span.max);
                                result[cursor--] = arc;
                            }
                            visited[loc] = COMPLETE;
                            break;
                        }
                        case PENDING: {
                            // le singe est dans l'arbre
                            assert false;
                            break;
                        }
                        case COMPLETE: {
                            break;
                        }
                    }
                }

                FArc[] sort() {
                    for (int loc = 0, N = arcsByVertex.length; loc < N; loc++) {
                        walk(loc);
                    }
                    assert cursor == -1;
                    return result;
                }
            }.sort();
        }
    }

    int getCount() {
        return 99;
    }
    // Static utility methods

    static int max2(int[] a, int valueIfEmpty) {
        int result = valueIfEmpty;
        for (int i = 0, N = a.length; i < N; i++) {
            result = Math.max(result, a[i]);
        }
        return result;
    }

    // mean an extension of ArrayList which it's contains type Pair<K, V>
    final static class FAssoc<K, V> extends ArrayList<Pair<K, V>> {
        private final Class<K> keyType;
        private final Class<V> valueType;

        private FAssoc(Class<K> keyType, Class<V> valueType) {
            this.keyType = keyType;
            this.valueType = valueType;
        }

        public static <K, V> FAssoc<K, V> of(Class<K> keyType, Class<V> valueType) {
            return new FAssoc<K, V>(keyType, valueType);
        }

        public void put(K key, V value) {
            add(Pair.create(key, value));
        }

        @SuppressWarnings(value = "unchecked")
        public FPackedMap<K, V> pack() {
            int N = size();
            K[] keys = (K[]) Array.newInstance(keyType, N);
            V[] values = (V[]) Array.newInstance(valueType, N);
            for (int i = 0; i < N; i++) {
                keys[i] = get(i).first;
                values[i] = get(i).second;
            }
            return new FPackedMap<K, V>(keys, values);
        }
    }

    final static class FPackedMap<K, V> {
        public final int[] index;
        public final K[] keys;
        public final V[] values;

        FPackedMap(K[] keys, V[] values) {
            this.index = createIndex(keys);

            this.keys = compact(keys, index);
            this.values = compact(values, index);
        }

        public V getValue(int i) {
            return values[index[i]];
        }

        static <K> int[] createIndex(K[] keys) {
            int size = keys.length;
            int[] result = new int[size];

            Map<K, Integer> keyToIndex = new HashMap<K, Integer>();
            for (int i = 0; i < size; i++) {
                K key = keys[i];
                Integer index = keyToIndex.get(key);
                if (index == null) {
                    index = keyToIndex.size();
                    keyToIndex.put(key, index);
                }
                result[i] = index;
            }
            return result;
        }

        /*
        Create a compact 简洁的 array of keys or values using the supplied index.
         */
        static <K> K[] compact(K[] a, int[] index) {
            int size = a.length;
            Class<?> componentType = a.getClass().getComponentType();
            K[] result = (K[]) Array.newInstance(componentType, max2(index, -1) + 1);

            // this overwrite duplicates, retaining the last equivalent entry
            for (int i = 0; i < size; i++) {
                result[index[i]] = a[i];
            }
            return result;
        }
    }
}
