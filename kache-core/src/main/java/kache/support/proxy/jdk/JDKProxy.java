package kache.support.proxy.jdk;

import kache.api.ICache;
import kache.support.proxy.ICacheProxy;
import kache.support.proxy.bs.CacheBsProxy;
import kache.support.proxy.bs.CacheBsProxyContext;
import kache.support.proxy.bs.ICacheBsProxyContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JDKProxy implements InvocationHandler, ICacheProxy {

    private final ICache target;

    public JDKProxy(ICache target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {
        ICacheBsProxyContext context = CacheBsProxyContext.newInstance()
                .method(method).params(params).target(target);
        return CacheBsProxy.newInstance().context(context).execute();
    }

    @Override
    public Object proxy() {
        InvocationHandler handler = new JDKProxy(target);
        return Proxy.newProxyInstance(handler.getClass().getClassLoader(),
                target.getClass().getInterfaces(), handler);
    }
}
