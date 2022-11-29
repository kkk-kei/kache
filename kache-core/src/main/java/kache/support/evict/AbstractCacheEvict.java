package kache.support.evict;

import kache.api.ICacheEntry;
import kache.api.ICacheEvict;
import kache.api.ICacheEvictContext;

public abstract class AbstractCacheEvict<K,V> implements ICacheEvict<K,V> {
    @Override
    public ICacheEntry<K,V> evict(ICacheEvictContext<K, V> context) {
        return doEvict(context);
    }

    protected abstract ICacheEntry<K,V> doEvict(ICacheEvictContext<K, V> context);

    @Override
    public void updateKey(K key) {

    }

    @Override
    public void removeKey(K key) {

    }
}
