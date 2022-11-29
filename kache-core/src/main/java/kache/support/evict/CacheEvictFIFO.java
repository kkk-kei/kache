package kache.support.evict;

import kache.api.ICache;
import kache.api.ICacheEntry;
import kache.api.ICacheEvictContext;
import kache.core.Cache;
import kache.model.CacheEntry;
import kache.support.proxy.CacheProxy;

import java.util.LinkedList;
import java.util.Queue;

public class CacheEvictFIFO<K,V> extends AbstractCacheEvict<K,V> {
    private Queue<K> queue = new LinkedList<>();

    @Override
    protected ICacheEntry<K,V> doEvict(ICacheEvictContext<K,V> context) {
        CacheEntry<K,V> entry = null;
        final ICache<K,V> cache = context.cache();
        // 超过限制，执行移除
        if(cache.size() >= context.size()) {//map的size >= 自定义的size
            K evictKey = queue.remove();
            // 移除最开始的元素
            Cache<K,V> cachePoxy = (Cache<K, V>) CacheProxy.getProxy(cache);
            V eviceValue = cachePoxy.remove(evictKey);
            entry = new CacheEntry<>(evictKey,eviceValue);
        }
        // 将新加的元素放入队尾
        final K key = context.key();
        queue.add(key);
        return entry;
    }
}
