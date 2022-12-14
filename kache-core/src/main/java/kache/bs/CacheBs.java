package kache.bs;

import kache.api.ICache;
import kache.api.ICacheEvict;
import kache.api.ICacheLoad;
import kache.api.ICachePersist;
import kache.core.Cache;
import kache.support.evict.CacheEvicts;
import kache.support.load.CacheLoads;
import kache.support.persist.CachePersists;
import kache.support.proxy.CacheProxy;
import kache.util.ArgUtil;

import java.util.HashMap;
import java.util.Map;

public final class CacheBs<K,V> {

    private CacheBs(){

    }

    public static <K,V> CacheBs<K,V> newInstance() {
        return new CacheBs<>();
    }

    private Map<K,V> map = new HashMap<>();
    private int size = Integer.MAX_VALUE;
    private ICacheEvict<K,V> evict = CacheEvicts.lru();
    private ICacheLoad<K,V> load = CacheLoads.none();
    private ICachePersist<K,V> persist = CachePersists.none();

    public CacheBs<K, V> map(Map<K, V> map) {
        ArgUtil.notNull(map, "map");
        this.map = map;
        return this;
    }

    public CacheBs<K, V> size(int size) {
        ArgUtil.notNegative(size, "size");
        this.size = size;
        return this;
    }

    public CacheBs<K, V> evict(ICacheEvict<K, V> evict) {
        this.evict = evict;
        return this;
    }

    public CacheBs<K, V> load(ICacheLoad<K,V> load){
        this.load = load;
        return this;
    }
    public CacheBs<K, V> persist(ICachePersist<K,V> persist){
        this.persist = persist;
        return this;
    }

    public ICache<K,V> build() {
        Cache<K,V> cache = new Cache<>();
        cache.map(map);
        cache.evict(evict);
        cache.sizeLimit(size);
        cache.load(load);
        cache.persist(persist);
        // 初始化
        cache.init();
        return CacheProxy.getProxy(cache);
    }
}
