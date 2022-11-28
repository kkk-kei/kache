package kache.api;

import java.util.concurrent.TimeUnit;

public interface ICachePersist<K,V> {
    void persist(final ICache<K, V> cache);

    long delay();

    long period();

    TimeUnit timeUnit();
}
