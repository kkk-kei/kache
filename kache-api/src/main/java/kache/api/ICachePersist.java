package kache.api;

public interface ICachePersist<K,V> {
    void persist(final ICache<K, V> cache);
}
