package kache.api;

public interface ICacheLoad<K,V> {
    void load(final ICache<K,V> cache);
}
