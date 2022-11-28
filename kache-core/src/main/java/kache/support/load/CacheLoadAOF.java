package kache.support.load;

import com.alibaba.fastjson.JSON;
import kache.annotation.CacheInterceptor;
import kache.api.ICache;
import kache.api.ICacheLoad;
import kache.core.Cache;
import kache.model.PersistAOFEntry;
import kache.util.CollectionUtil;
import kache.util.FileUtil;
import kache.util.ReflectMethodUtil;
import kache.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j(topic = "CacheLoadAOF")
public class CacheLoadAOF<K,V> implements ICacheLoad<K,V> {

    /**
     * 方法缓存
     *
     * 暂时比较简单，直接通过方法判断即可，不必引入参数类型增加复杂度。
     * @since 0.0.10
     */
    private static final Map<String, Method> METHOD_MAP = new HashMap<>();

    static {
        Method[] methods = Cache.class.getMethods();

        for(Method method : methods){
            CacheInterceptor cacheInterceptor = method.getAnnotation(CacheInterceptor.class);

            if(cacheInterceptor != null) {
                // 暂时
                if(cacheInterceptor.aof()) {
                    String methodName = method.getName();

                    METHOD_MAP.put(methodName, method);
                }
            }
        }

    }

    /**
     * 文件路径
     * @since 0.0.8
     */
    private final String dbPath;

    public CacheLoadAOF(String dbPath) {
        this.dbPath = dbPath;
    }

    @Override
    public void load(ICache<K, V> cache) {
        List<String> lines = FileUtil.readAllLines(dbPath);
        log.info("[load] 开始处理 path: {}", dbPath);
        if(CollectionUtil.isEmpty(lines)) {
            log.info("[load] path: {} 文件内容为空，直接返回", dbPath);
            return;
        }

        for(String line : lines) {
            if(StringUtil.isEmpty(line)) {
                continue;
            }

            // 执行
            // 简单的类型还行，复杂的这种反序列化会失败
            PersistAOFEntry entry = JSON.parseObject(line, PersistAOFEntry.class);

            final String methodName = entry.getMethodName();
            final Object[] objects = entry.getParams();

            final Method method = METHOD_MAP.get(methodName);
            // 反射调用
            ReflectMethodUtil.invoke(cache, method, objects);
        }
    }
}
