package kache.api;

import java.util.Map;
import java.util.function.Function;

public interface ICache<K,V> extends Map<K,V> {
    ICache<K, V> expire(final K key, final long timeInMills);
    ICache<K, V> expireAt(final K key, final long timeInMills);
    ICacheLoad<K,V> load();
    ICacheExpire<K,V> expire();
    ICachePersist<K,V> persist();
    V flight(Object key, Function<K,V> fun);
    V singleFlight(Object key, Function<K,V> fun);
}
