package kache.support.persist;

import com.alibaba.fastjson.JSON;
import kache.api.ICache;
import kache.api.ICachePersist;
import kache.model.PersistEntry;
import kache.util.FileUtil;

import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;

public class CachePersistDbJson<K,V> implements ICachePersist<K,V> {
    private final String dbPath;

    public CachePersistDbJson(String dbPath) {
        this.dbPath = dbPath;
    }

    @Override
    public void persist(ICache<K, V> cache) {
        Set<Map.Entry<K,V>> entrySet = cache.entrySet();

        // 创建文件
        FileUtil.createFile(dbPath);
        // 清空文件
        FileUtil.truncate(dbPath);

        for(Map.Entry<K,V> entry : entrySet) {
            K key = entry.getKey();
            Long expireTime = cache.expire().expireTime(key);
            PersistEntry<K,V> persistEntry = new PersistEntry<>();
            persistEntry.setKey(key);
            persistEntry.setValue(entry.getValue());
            persistEntry.setExpire(expireTime);

            String line = JSON.toJSONString(persistEntry);
            FileUtil.write(dbPath, line, StandardOpenOption.APPEND);
        }
    }
}
