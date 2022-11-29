package bs;

import kache.api.ICache;
import kache.api.ICacheEvict;
import kache.bs.CacheBs;
import kache.support.evict.CacheEvicts;
import kache.support.load.CacheLoadAOF;
import kache.support.load.CacheLoadRDB;
import kache.support.persist.CachePersistAOF;
import kache.support.persist.CachePersistRDB;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

@Slf4j
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

    @Test
    public void flight() throws InterruptedException {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .build();
        for(int i=0;i<5;i++){
            new Thread(()->{
                String val = cache.flight("1", (key) -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    log.info("[singleFlight] get {}",key);
                    return key;
                });
                log.info("value {}",val);
            }).start();
        }
        TimeUnit.MILLISECONDS.sleep(1000);
    }

    @Test
    public void singleflight() throws InterruptedException {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .build();
        for(int i=0;i<5;i++){
            new Thread(()->{
                String val = cache.singleFlight("1", (key) -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    log.info("[singleFlight] get {}",key);
                    return key;
                });
                log.info("value {}",val);
            }).start();
        }
        TimeUnit.MILLISECONDS.sleep(1000);
    }

    @Test
    public void lruTest(){
        ICache<String, String> cache = CacheBs.<String, String>newInstance()
                .size(3)
                .evict(CacheEvicts.lru())
                .build();
        cache.put("1","1");
        cache.put("2","2");
        cache.put("3","3");
        cache.get("1"); //1 3 2
        cache.get("2"); //2 1 3
        cache.put("4","4");//4 2 1
        Assert.assertEquals(3, cache.size());
        cache.evict();
        System.out.println(cache.keySet());
    }
    @Test
    public void lru2Test(){
        ICache<String, String> cache = CacheBs.<String, String>newInstance()
                .size(3)
                .evict(CacheEvicts.lru2())
                .build();
        cache.put("1","1");
        cache.put("2","2");
        cache.put("3","3");
        cache.get("1");
        cache.get("2"); //(2,1)  (3)
        cache.put("4","4");
        cache.put("5","5");//(2,1) (5)
        Assert.assertEquals(3, cache.size());
        cache.evict()
        System.out.println(cache.keySet());
    }
}
