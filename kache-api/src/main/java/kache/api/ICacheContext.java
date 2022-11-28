package kache.api;

import java.util.Map;

public interface ICacheContext<K,V> {
    Map<K, V> map();
    int size();
    ICacheEvict<K,V> cacheEvict();
    ICacheLoad<K,V> cacheLoad();

    ICachePersist<K,V> cachePersist();
}
