package kache.support.evict;

import kache.api.ICache;
import kache.api.ICacheEntry;
import kache.api.ICacheEvictContext;
import kache.core.Cache;
import kache.model.CacheEntry;
import kache.support.proxy.CacheProxy;
import kache.support.struct.ILruMap;
import kache.support.struct.LruMapDoubleLinkedList;

public class CacheEvictLRU<K,V> extends AbstractCacheEvict<K,V>{

    private final ILruMap<K,V> lruMap;

    public CacheEvictLRU() {
        this.lruMap = new LruMapDoubleLinkedList<>();
    }

    @Override
    protected ICacheEntry<K, V> doEvict(ICacheEvictContext<K, V> context) {
        ICacheEntry<K,V> entry = null;
        final ICache<K,V> cache = context.cache();
        // 超过限制，执行移除
        if(cache.size() >= context.size()){
            ICacheEntry<K, V> evictEntry = lruMap.removeEldest();
            final K evictKey = evictEntry.key();
            Cache<K,V> cachePoxy = (Cache<K, V>) CacheProxy.getProxy(cache);
            V evictValue = cachePoxy.remove(evictKey);
            entry = CacheEntry.of(evictKey,evictValue);
        }
        return entry;
    }

    @Override
    public void updateKey(K key) {
        lruMap.updateKey(key);
    }

    @Override
    public void removeKey(K key) {
        lruMap.removeKey(key);
    }
}
