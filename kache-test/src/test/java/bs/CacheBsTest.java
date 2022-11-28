package bs;

import kache.api.ICache;
import kache.bs.CacheBs;
import kache.support.load.CacheLoadDbJson;
import kache.support.persist.CachePersistDbJson;
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
    public void persistTest() throws InterruptedException {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(3)
                .persist(new CachePersistDbJson<>("1.rdb"))
                .build();
        cache.put("1", "1");
        cache.put("2", "2");

        TimeUnit.MILLISECONDS.sleep(1000);
    }

    @Test
    public void loadTest() throws InterruptedException {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(3)
                .load(new CacheLoadDbJson<>("1.rdb"))
                .build();
        TimeUnit.MILLISECONDS.sleep(500);
        Assert.assertEquals(2, cache.size());
        System.out.println(cache.keySet());
    }

}
