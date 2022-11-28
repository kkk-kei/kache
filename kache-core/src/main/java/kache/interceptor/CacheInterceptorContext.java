package kache.interceptor;

import kache.api.ICache;
import kache.api.ICacheInterceptorContext;

import java.lang.reflect.Method;

public class CacheInterceptorContext<K,V> implements ICacheInterceptorContext<K,V> {

    private ICache<K,V> cache;
    private Method method;
    private Object[] params;
    private Object result;

    public static <K,V> CacheInterceptorContext<K,V> newInstance() {
        return new CacheInterceptorContext<>();
    }

    @Override
    public ICache<K, V> cache() {
        return cache;
    }

    public CacheInterceptorContext<K, V> cache(ICache<K, V> cache) {
        this.cache = cache;
        return this;
    }

    @Override
    public Method method() {
        return method;
    }

    public CacheInterceptorContext<K, V> method(Method method) {
        this.method = method;
        return this;
    }

    @Override
    public Object[] params() {
        return params;
    }

    public CacheInterceptorContext<K, V> params(Object[] params) {
        this.params = params;
        return this;
    }

    @Override
    public Object result() {
        return result;
    }

    public CacheInterceptorContext<K, V> result(Object result) {
        this.result = result;
        return this;
    }
}
