package kache.api;

public interface ICacheEvict<K,V> {
    void evict(final ICacheEvictContext<K, V> context);
}
