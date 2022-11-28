package kache.support.proxy.cglib;

import kache.api.ICache;
import kache.support.proxy.ICacheProxy;
import kache.support.proxy.bs.CacheBsProxy;
import kache.support.proxy.bs.CacheBsProxyContext;
import kache.support.proxy.bs.ICacheBsProxyContext;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibProxy implements MethodInterceptor, ICacheProxy {

    private final ICache target;

    public CglibProxy(ICache target) {
        this.target = target;
    }


    @Override
    public Object intercept(Object o, Method method, Object[] params, MethodProxy methodProxy) throws Throwable {
        ICacheBsProxyContext context = CacheBsProxyContext.newInstance()
                .method(method).params(params).target(target);
        return CacheBsProxy.newInstance().context(context).execute();
    }

    @Override
    public Object proxy() {
        Enhancer enhancer = new Enhancer();
        //目标对象类
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(this);
        //通过字节码技术创建目标对象类的子类实例作为代理
        return enhancer.create();
    }
}
