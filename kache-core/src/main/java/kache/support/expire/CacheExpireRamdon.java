package kache.support.expire;

import kache.api.ICache;
import kache.api.ICacheExpire;
import kache.core.Cache;
import kache.exception.CacheRuntimeException;
import kache.support.proxy.CacheProxy;
import kache.util.CollectionUtil;
import kache.util.MapUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CacheExpireRamdon<K,V> implements ICacheExpire<K,V> {

    private static final int LIMIT = 100;

    /**
     * 过期 map
     *
     * 空间换时间
     * @since 0.0.3
     */
    private final Map<K, Long> expireMap = new HashMap<>();

    /**
     * 缓存实现
     * @since 0.0.3
     */
    private final ICache<K,V> cache;
    private volatile boolean fastMode = false;

    /**
     * 线程执行类
     * @since 0.0.3
     */
    private static final ScheduledExecutorService EXECUTOR_SERVICE
            = Executors.newSingleThreadScheduledExecutor();

    public CacheExpireRamdon(ICache<K, V> cache) {
        this.cache = cache;
        this.init();
    }

    /**
     * 初始化任务
     * @since 0.0.3
     */
    private void init() {
        EXECUTOR_SERVICE
                .scheduleAtFixedRate(new ExpireThread(), 100, 100, TimeUnit.MILLISECONDS);
    }

    /**
     * 定时执行任务
     * @since 0.0.3
     */
    private class ExpireThread implements Runnable {
        @Override
        public void run() {
            //1.判断是否为空
            if(MapUtil.isEmpty(expireMap)) {
                return;
            }
            if(fastMode){
                expireKeys(10L);
            }else{
                expireKeys(100L);
            }
        }
    }

    @Override
    public void expire(K key, long expireAt) {
        expireMap.put(key, expireAt);
    }

    @Override
    public void refreshExpire(Collection<K> keyList) {
        if(CollectionUtil.isEmpty(keyList)) {
            return;
        }
        // 判断大小，小的作为外循环。一般都是过期的 keys 比较小。
        if(keyList.size() <= expireMap.size()) {
            for(K key : keyList) {
                Long expireAt = expireMap.get(key);
                expireKey(key,expireAt);
            }
        } else {
            for(Map.Entry<K, Long> entry : expireMap.entrySet()) {
                this.expireKey(entry);
            }
        }
    }

    @Override
    public Long expireTime(K key) {
        return expireMap.get(key);
    }

    /**
     * 执行过期操作
     * @param entry 明细
     * @since 0.0.3
     */
    private void expireKey(Map.Entry<K, Long> entry) {
        final K key = entry.getKey();
        final Long expireAt = entry.getValue();
        // 删除的逻辑处理
        long currentTime = System.currentTimeMillis();
        if(currentTime >= expireAt) {
            expireMap.remove(key);
            // 再移除缓存，后续可以通过惰性删除做补偿
            Cache<K,V> cachePoxy = (Cache<K, V>) CacheProxy.getProxy(cache);
            cachePoxy.remove(key);
        }
    }

    /**
     * 过期处理 key
     * @param key key
     * @since 0.0.3
     */
    private boolean expireKey(final K key,final Long expireAt) {
        if(expireAt == null) {
            return false;
        }
        long currentTime = System.currentTimeMillis();
        if(currentTime >= expireAt) {
            expireMap.remove(key);
            Cache<K,V> cachePoxy = (Cache<K, V>) CacheProxy.getProxy(cache);
            cachePoxy.remove(key);
            return true;
        }
        return false;
    }

    private void expireKeys(final long timeoutMills) {
        // 设置超时时间 100ms
        final long timeLimit = System.currentTimeMillis() + timeoutMills;
        // 恢复 fastMode
        this.fastMode = false;

        //2. 获取 key 进行处理
        int count = 0;
        List<K> randomKeyBatch = getRandomKeyBatch(100);
        while (true) {
            //2.1 返回判断
            if(count >= LIMIT) {
                log.info("过期淘汰次数已经达到最大次数: {}，完成本次执行。", LIMIT);
                return;
            }
            if(System.currentTimeMillis() >= timeLimit) {
                this.fastMode = true;
                log.info("过期淘汰已经达到限制时间，中断本次执行，设置 fastMode=true;");
                return;
            }
            if(count>=randomKeyBatch.size()){
                this.fastMode = true;
                log.info("没有过期键值可以淘汰，中断本次执行，设置 fastMode=true;");
                return;
            }
            //2.2 随机过期
//            K key = getRandomKey();
            K key = randomKeyBatch.get(count);
            Long expireAt = expireMap.get(key);
            boolean expireFlag = expireKey(key,expireAt);
            log.debug("key: {} 过期执行结果 {}", key, expireFlag);

            //2.3 信息更新
            count++;
        }
    }

    private K getRandomKey() {
        Random random = ThreadLocalRandom.current();
        int randomIndex = random.nextInt(expireMap.size());

        // 遍历 keys
        Iterator<K> iterator = expireMap.keySet().iterator();
        int count = 0;
        while (iterator.hasNext()) {
            K key = iterator.next();

            if(count == randomIndex) {
                return key;
            }
            count++;
        }

        // 正常逻辑不会到这里
        throw new CacheRuntimeException("对应信息不存在");
    }

    private List<K> getRandomKeyBatch(final int sizeLimit) {
        Random random = ThreadLocalRandom.current();
        int randomIndex = random.nextInt(expireMap.size());

        // 遍历 keys
        Iterator<K> iterator = expireMap.keySet().iterator();
        int count = 0;

//        Set<K> keySet = new HashSet<>();
        List<K> keySet = new ArrayList<>();
        while (iterator.hasNext()) {
            // 判断列表大小
            if(keySet.size() >= sizeLimit) {
                return keySet;
            }

            K key = iterator.next();
            // index 向后的位置，全部放进来。
            if(count >= randomIndex) {
                keySet.add(key);
            }
            count++;
        }

        // 正常逻辑不会到这里
        throw new CacheRuntimeException("对应信息不存在");
    }

}
