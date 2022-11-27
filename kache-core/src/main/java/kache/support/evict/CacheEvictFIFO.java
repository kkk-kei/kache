package kache.support.evict;

import kache.api.ICache;
import kache.api.ICacheEvict;
import kache.api.ICacheEvictContext;

import java.util.LinkedList;
import java.util.Queue;

public class CacheEvictFIFO<K,V> implements ICacheEvict<K,V> {
    private Queue<K> queue = new LinkedList<>();
    @Override
    public void evict(ICacheEvictContext<K, V> context) {
        final ICache<K,V> cache = context.cache();
        // 超过限制，执行移除
        if(cache.size() >= context.size()) {//map的size >= 自定义的size
            K evictKey = queue.remove();
            // 移除最开始的元素
            cache.remove(evictKey);
        }
        // 将新加的元素放入队尾
        final K key = context.key();
        queue.add(key);
    }
}
