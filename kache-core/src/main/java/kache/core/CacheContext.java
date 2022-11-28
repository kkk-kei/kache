package kache.core;

import kache.api.ICacheContext;
import kache.api.ICacheEvict;
import kache.api.ICacheLoad;
import kache.api.ICachePersist;

import java.util.Map;

public class CacheContext<K,V> implements ICacheContext<K,V> {

    private Map<K, V> map;
    private int size;
    private ICacheEvict<K,V> cacheEvict;
    private ICacheLoad<K,V> cacheLoad;

    private ICachePersist<K,V> cachePersist;

    public CacheContext<K, V> cacheEvict(ICacheEvict<K, V> cacheEvict) {
        this.cacheEvict = cacheEvict;
        return this;
    }

    public CacheContext<K, V> map(Map<K, V> map) {
        this.map = map;
        return this;
    }

    public CacheContext<K, V> size(int size) {
        this.size = size;
        return this;
    }

    public CacheContext<K, V> cacheLoad(ICacheLoad<K,V> load) {
        this.cacheLoad = load;
        return this;
    }
    public CacheContext<K, V> cachePersist(ICachePersist<K,V> persist) {
        this.cachePersist = persist;
        return this;
    }

    @Override
    public Map<K, V> map() {
        return map;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public ICacheEvict<K, V> cacheEvict() {
        return cacheEvict;
    }

    @Override
    public ICacheLoad<K, V> cacheLoad() {
        return cacheLoad;
    }

    @Override
    public ICachePersist<K, V> cachePersist() {
        return cachePersist;
    }
}
