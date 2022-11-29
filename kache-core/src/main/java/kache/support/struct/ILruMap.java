package kache.support.struct;

import kache.api.ICacheEntry;

public interface ILruMap<K,V> {
    ICacheEntry<K, V> removeEldest();
    void updateKey(final K key);
    void removeKey(final K key);
    boolean isEmpty();
    boolean contains(final K key);
}
