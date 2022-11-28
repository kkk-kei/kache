package kache.support.proxy.bs;

import kache.annotation.CacheInterceptor;
import kache.api.ICache;

import java.lang.reflect.Method;

public interface ICacheBsProxyContext {

    CacheInterceptor interceptor();
    ICache target();
    CacheBsProxyContext target(final ICache target);
    Object[] params();
    Method method();
    Object process() throws Throwable;

}
