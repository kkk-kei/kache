package kache.support.proxy.bs;

import kache.annotation.CacheInterceptor;
import kache.api.ICache;

import java.lang.reflect.Method;

public class CacheBsProxyContext implements ICacheBsProxyContext {

    private ICache target;
    private Object[] params;
    private Method method;
    private CacheInterceptor interceptor;

    public static CacheBsProxyContext newInstance(){
        return new CacheBsProxyContext();
    }

    @Override
    public CacheInterceptor interceptor() {
        return interceptor;
    }

    @Override
    public ICache target() {
        return target;
    }

    @Override
    public CacheBsProxyContext target(ICache target) {
        this.target = target;
        return this;
    }

    @Override
    public Object[] params() {
        return params;
    }

    public CacheBsProxyContext params(Object[] params) {
        this.params = params;
        return this;
    }

    @Override
    public Method method() {
        return method;
    }

    public CacheBsProxyContext method(Method method) {
        this.method = method;
        this.interceptor = method.getAnnotation(CacheInterceptor.class);
        return this;
    }

    @Override
    public Object process() throws Throwable {
        return method.invoke(target,params);
    }
}
