package kache.support.evict;

import kache.api.ICacheEvict;

public final class CacheEvicts {

    private CacheEvicts(){}

//    public static <K, V> ICacheEvict<K, V> none() {
//        return new CacheEvictNone<>();
//    }

    public static <K, V> ICacheEvict<K, V> fifo() {
        return new CacheEvictFIFO<>();
    }
}
