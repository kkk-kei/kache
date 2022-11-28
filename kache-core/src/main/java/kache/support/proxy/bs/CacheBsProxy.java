package kache.support.proxy.bs;

import kache.annotation.CacheInterceptor;
import kache.api.ICache;
import kache.api.ICacheInterceptor;
import kache.api.ICachePersist;
import kache.interceptor.CacheInterceptorContext;
import kache.interceptor.CacheInterceptors;
import kache.support.persist.CachePersistAOF;

public final class CacheBsProxy {

    private CacheBsProxy(){}

    /**
     * 代理上下文
     * @since 0.0.4
     */
    private ICacheBsProxyContext context;

    /**
     * 持久化拦截器
     * @since 0.0.10
     */
    @SuppressWarnings("all")
    private final ICacheInterceptor persistInterceptors = CacheInterceptors.aof();

    /**
     * 新建对象实例
     * @return 实例
     * @since 0.0.4
     */
    public static CacheBsProxy newInstance() {
        return new CacheBsProxy();
    }

    public CacheBsProxy context(ICacheBsProxyContext context) {
        this.context = context;
        return this;
    }

    /**
     * 执行
     * @return 结果
     * @since 0.0.4
     * @throws Throwable 异常
     */
    @SuppressWarnings("all")
    public Object execute() throws Throwable {
        //1. 开始的时间
        final long startMills = System.currentTimeMillis();
        final ICache cache = context.target();
        CacheInterceptorContext interceptorContext = CacheInterceptorContext.newInstance()
                .method(context.method())
                .params(context.params())
                .cache(context.target());
        CacheInterceptor cacheInterceptor = context.interceptor();
        //2. 正常执行
        Object result = context.process();

        // 方法执行完成
        this.interceptorHandler(cacheInterceptor, interceptorContext, cache, false);
        return result;
    }

    /**
     * 拦截器执行类
     * @param cacheInterceptor 缓存拦截器
     * @param interceptorContext 上下文
     * @param cache 缓存
     * @param before 是否执行执行
     * @since 0.0.5
     */
    @SuppressWarnings("all")
    private void interceptorHandler(CacheInterceptor cacheInterceptor,
                                    CacheInterceptorContext interceptorContext,
                                    ICache cache,
                                    boolean before) {
        if(cacheInterceptor != null) {

            //3. AOF 追加
            final ICachePersist cachePersist = cache.persist();
            if(cacheInterceptor.aof() && (cachePersist instanceof CachePersistAOF)) {
                if(before) {
                    persistInterceptors.before(interceptorContext);
                } else {
                    persistInterceptors.after(interceptorContext);
                }
            }
        }
    }
}
