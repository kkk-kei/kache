package kache.support.load;

import kache.api.ICacheLoad;

public final class CacheLoads {

    private CacheLoads(){
    }

    public static <K,V> ICacheLoad<K,V> none() {
        return new CacheLoadNone<>();
    }

    public static <K,V> ICacheLoad<K,V> dbJson(final String dbPath) {
        return new CacheLoadRDB<>(dbPath);
    }
}
