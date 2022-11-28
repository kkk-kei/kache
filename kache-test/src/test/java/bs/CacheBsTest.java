package bs;

import kache.api.ICache;
import kache.bs.CacheBs;
import kache.support.load.CacheLoadAOF;
import kache.support.load.CacheLoadRDB;
import kache.support.persist.CachePersistAOF;
import kache.support.persist.CachePersistRDB;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class CacheBsTest {
    @Test
    public void cacheBsTest() {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(2)
                .build();
        cache.put("1", "1");
        cache.put("2", "2");
        cache.put("3", "3");
        cache.put("4", "4");
        Assert.assertEquals(2, cache.size());
        System.out.println(cache.keySet());
    }

    @Test
    public void expireTest() throws InterruptedException {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(3)
                .build();
        cache.put("1", "1");
        cache.put("2", "2");

        cache.expire("1",10);

        Assert.assertEquals(2, cache.size());

        TimeUnit.MILLISECONDS.sleep(50);
        Assert.assertEquals(1, cache.size());
        System.out.println(cache.keySet());
    }

    @Test
    public void rdbPersistTest() throws InterruptedException {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(3)
                .persist(new CachePersistRDB<>("1.rdb"))
                .build();
        cache.put("1", "1");
        cache.put("2", "2");

        TimeUnit.MILLISECONDS.sleep(1000);
    }

    @Test
    public void rdbLoadTest() throws InterruptedException {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(3)
                .load(new CacheLoadRDB<>("1.rdb"))
                .build();
        TimeUnit.MILLISECONDS.sleep(500);
        Assert.assertEquals(2, cache.size());
        System.out.println(cache.keySet());
    }

    @Test
    public void aofPersistTest() throws InterruptedException {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .persist(new CachePersistAOF<>("1.aof"))
                .build();
        cache.put("1", "1");
        cache.put("2", "2");
        cache.put("3", "3");
        cache.expire("1", 1000*60*60);
        cache.remove("2");
        TimeUnit.MILLISECONDS.sleep(1000);
    }

    @Test
    public void aofLoadTest() throws InterruptedException {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .load(new CacheLoadAOF<>("1.aof"))
                .build();
        TimeUnit.MILLISECONDS.sleep(2000);
        Assert.assertEquals(2, cache.size());
        System.out.println(cache.keySet());
    }
}
