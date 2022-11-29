package kache.interceptor;

import kache.api.ICacheInterceptor;

public final class CacheInterceptors {
    public static ICacheInterceptor aof() {
        return new CacheInterceptorAOF();
    }

    public static ICacheInterceptor evict() {
        return new CacheInterceptorEvict();
    }
}
