package kache.support.load;

import kache.api.ICache;
import kache.api.ICacheLoad;

public class CacheLoadNone<K,V> implements ICacheLoad<K,V> {
    @Override
    public void load(ICache<K, V> cache) {

    }
}
