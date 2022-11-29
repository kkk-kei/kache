package kache.api;

public interface ICacheEntry<K,V> {
    K key();
    V value();
}
