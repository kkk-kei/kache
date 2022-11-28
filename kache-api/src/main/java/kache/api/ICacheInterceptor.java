package kache.api;

public interface ICacheInterceptor<K,V> {

    void before(ICacheInterceptorContext<K,V> context);
    void after(ICacheInterceptorContext<K,V> context);
}
