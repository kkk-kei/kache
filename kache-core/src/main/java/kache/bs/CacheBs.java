package kache.bs;

import kache.api.ICache;
import kache.api.ICacheEvict;
import kache.core.Cache;
import kache.core.CacheContext;
import kache.support.evict.CacheEvicts;
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
    private ICacheEvict<K,V> evict = CacheEvicts.fifo();

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

    public ICache<K,V> build() {
        CacheContext<K,V> context = new CacheContext<>();
        context.cacheEvict(evict);
        context.map(map);
        context.size(size);
        return new Cache<>(context);
    }
}
