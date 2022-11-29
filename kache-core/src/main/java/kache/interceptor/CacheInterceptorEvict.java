package kache.interceptor;

import kache.api.ICacheEvict;
import kache.api.ICacheInterceptor;
import kache.api.ICacheInterceptorContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class CacheInterceptorEvict<K,V> implements ICacheInterceptor<K,V> {
    @Override
    public void before(ICacheInterceptorContext<K, V> context) {

    }

    @Override
    public void after(ICacheInterceptorContext<K, V> context) {
        ICacheEvict<K,V> evict = context.cache().evict();
        Method method = context.method();
        final K key = (K) context.params()[0];
        if("remove".equals(method.getName())) {
            evict.removeKey(key);
        } else {
            evict.updateKey(key);
        }
    }
}
