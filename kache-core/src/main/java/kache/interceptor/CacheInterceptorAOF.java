package kache.interceptor;

import com.alibaba.fastjson.JSON;
import kache.api.ICache;
import kache.api.ICacheInterceptor;
import kache.api.ICacheInterceptorContext;
import kache.api.ICachePersist;
import kache.model.PersistAOFEntry;
import kache.support.persist.CachePersistAOF;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheInterceptorAOF<K,V> implements ICacheInterceptor<K,V> {
    @Override
    public void before(ICacheInterceptorContext context) {

    }

    @Override
    public void after(ICacheInterceptorContext context) {
        // 持久化类
        ICache<K,V> cache = context.cache();
        ICachePersist<K,V> persist = cache.persist();

        if(persist instanceof CachePersistAOF) {
            CachePersistAOF<K,V> cachePersistAof = (CachePersistAOF<K,V>) persist;

            String methodName = context.method().getName();
            PersistAOFEntry aofEntry = PersistAOFEntry.newInstance();
            aofEntry.setMethodName(methodName);
            aofEntry.setParams(context.params());

            String json = JSON.toJSONString(aofEntry);

            // 直接持久化
            log.debug("AOF 开始追加文件内容：{}", json);
            cachePersistAof.append(json);
            log.debug("AOF 完成追加文件内容：{}", json);
        }
    }
}
