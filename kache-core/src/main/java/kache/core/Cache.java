package kache.core;

import kache.api.ICache;
import kache.api.ICacheContext;
import kache.api.ICacheEvict;
import kache.api.ICacheExpire;
import kache.exception.CacheRuntimeException;
import kache.support.evict.CacheEvictContext;
import kache.support.expire.CacheExpire;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class Cache<K,V> implements ICache<K,V> {

    private final Map<K,V> map;
    private final int sizeLimit;
    private final ICacheEvict<K,V> cacheEvict;
    private final ICacheExpire<K,V> cacheExpire;

    public Cache(ICacheContext<K, V> context) {
        this.map = context.map();
        this.sizeLimit = context.size();
        this.cacheEvict = context.cacheEvict();
        this.cacheExpire = new CacheExpire<>(this);
    }

    @Override
    public V get(Object key) {
        K genericKey = (K) key;
        this.cacheExpire.refreshExpire(Collections.singletonList(genericKey));
        return map.get(key);
    }

    @Override
    public V put(K key, V value) {
        //1.1 尝试驱除
        CacheEvictContext<K,V> context = new CacheEvictContext<>();
        context.key(key).size(sizeLimit).cache(this);
        cacheEvict.evict(context);
        //2. 判断驱除后的信息
        if(isSizeLimit()) {
            throw new CacheRuntimeException("当前队列已满，数据添加失败！");
        }
        //3. 执行添加
        return map.put(key, value);
    }

    @Override
    public int size() {
        this.refreshExpireAllKeys();
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        this.refreshExpireAllKeys();
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        K genericKey = (K) key;
        this.cacheExpire.refreshExpire(Collections.singletonList(genericKey));
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        this.refreshExpireAllKeys();
        return map.containsValue(value);
    }

    @Override
    public V remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        this.refreshExpireAllKeys();
        return map.keySet();
    }

    @Override
    public Collection<V> values() {
        this.refreshExpireAllKeys();
        return map.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        this.refreshExpireAllKeys();
        return map.entrySet();
    }

    private boolean isSizeLimit() {
        final int currentSize = this.size();
        return currentSize >= this.sizeLimit;
    }

    @Override
    public ICache<K, V> expire(K key, long timeInMills) {
        long expireTime = System.currentTimeMillis()+timeInMills;
        return this.expireAt(key,expireTime);
    }

    @Override
    public ICache<K, V> expireAt(K key, long timeInMills) {
        this.cacheExpire.expire(key,timeInMills);
        return this;
    }

    private void refreshExpireAllKeys() {
        this.cacheExpire.refreshExpire(map.keySet());
    }
}
