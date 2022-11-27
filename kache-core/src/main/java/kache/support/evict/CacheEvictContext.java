package kache.support.evict;

import kache.api.ICache;
import kache.api.ICacheEvictContext;

public class CacheEvictContext<K,V> implements ICacheEvictContext<K,V> {

    private K key;
    private ICache<K,V> cache;
    private int size;

    public CacheEvictContext<K, V> key(K key) {
        this.key = key;
        return this;
    }

    public CacheEvictContext<K, V> cache(ICache<K, V> cache) {
        this.cache = cache;
        return this;
    }

    public CacheEvictContext<K, V> size(int size) {
        this.size = size;
        return this;
    }

    @Override
    public K key() {
        return key;
    }

    @Override
    public ICache<K, V> cache() {
        return cache;
    }

    @Override
    public int size() {
        return size;
    }
}
