package bs;

import kache.api.ICache;
import kache.bs.CacheBs;
import org.junit.Assert;
import org.junit.Test;

public class CacheBsTest {
    @Test
    public void helloTest() {
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
}
