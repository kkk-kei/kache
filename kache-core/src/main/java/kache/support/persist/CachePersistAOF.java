package kache.support.persist;

import kache.api.ICache;
import kache.util.FileUtil;
import kache.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CachePersistAOF<K,V> extends CachePersistAdaptor<K,V>{
    private final String dbPath;
    private final List<String> bufferList = new ArrayList<>();

    public CachePersistAOF(String dbPath) {
        this.dbPath = dbPath;
    }

    @Override
    public void persist(ICache<K, V> cache) {
        log.info("开始 AOF 持久化到文件");
        // 1. 创建文件
        if(!FileUtil.exists(dbPath)) {
            FileUtil.createFile(dbPath);
        }
        // 2. 持久化追加到文件中
        FileUtil.append(dbPath, bufferList);
        // 3. 清空 buffer 列表
        bufferList.clear();
        log.info("完成 AOF 持久化到文件");
    }

    public void append(final String json) {
        if(StringUtil.isNotEmpty(json)) {
            bufferList.add(json);
        }
    }

    @Override
    public long delay() {
        return this.period();
    }

    @Override
    public long period() {
        return 1;
    }

    @Override
    public TimeUnit timeUnit() {
        return TimeUnit.SECONDS;
    }
}
