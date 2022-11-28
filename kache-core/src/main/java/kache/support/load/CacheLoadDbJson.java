package kache.support.load;

import com.alibaba.fastjson.JSON;
import kache.api.ICache;
import kache.api.ICacheLoad;
import kache.model.PersistEntry;
import kache.util.CollectionUtil;
import kache.util.FileUtil;
import kache.util.ObjectUtil;
import kache.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j(topic = "CacheLoadDbJson")
public class CacheLoadDbJson<K,V> implements ICacheLoad<K,V> {

    private final String dbPath;

    public CacheLoadDbJson(String dbPath) {
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
            PersistEntry<K,V> entry = JSON.parseObject(line, PersistEntry.class);

            K key = entry.getKey();
            V value = entry.getValue();
            Long expire = entry.getExpire();

            cache.put(key, value);
            if(ObjectUtil.isNotNull(expire)) {
                cache.expireAt(key, expire);
            }
        }
        //nothing...
    }
}
