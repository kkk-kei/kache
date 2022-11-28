package kache.support.persist;

import kache.api.ICache;
import kache.api.ICachePersist;

public class CachePersistNone<K,V> implements ICachePersist<K,V> {
    @Override
    public void persist(ICache<K, V> cache) {

    }
}
