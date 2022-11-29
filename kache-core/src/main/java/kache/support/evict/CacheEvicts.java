package kache.support.evict;

import kache.api.ICacheEvict;

public final class CacheEvicts {

    private CacheEvicts(){}

    public static <K, V> ICacheEvict<K, V> fifo() {
        return new CacheEvictFIFO<>();
    }

    public static <K, V> ICacheEvict<K, V> lru() {
        return new CacheEvictLRU<>();
    }
    public static <K, V> ICacheEvict<K, V> lru2() {
        return new CacheEvictLRU2<>();
    }
}
