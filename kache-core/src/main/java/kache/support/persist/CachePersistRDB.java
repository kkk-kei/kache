package kache.support.persist;

import com.alibaba.fastjson.JSON;
import kache.api.ICache;
import kache.model.PersistRDBEntry;
import kache.util.FileUtil;

import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class CachePersistRDB<K,V> extends CachePersistAdaptor<K,V> {
    private final String dbPath;

    public CachePersistRDB(String dbPath) {
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
            PersistRDBEntry<K,V> persistRDBEntry = new PersistRDBEntry<>();
            persistRDBEntry.setKey(key);
            persistRDBEntry.setValue(entry.getValue());
            persistRDBEntry.setExpire(expireTime);

            String line = JSON.toJSONString(persistRDBEntry);
            FileUtil.write(dbPath, line, StandardOpenOption.APPEND);
        }
    }

    @Override
    public long delay() {
        return this.period();
    }

    @Override
    public long period() {
        return 10;
    }

    @Override
    public TimeUnit timeUnit() {
        return TimeUnit.MINUTES;
    }
}
