package bs;

import kache.api.ICache;
import kache.bs.CacheBs;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class ExpireTest {
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
}
