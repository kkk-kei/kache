package kache.core;

import kache.annotation.CacheInterceptor;
import kache.api.*;
import kache.exception.CacheRuntimeException;
import kache.support.proxy.CacheProxy;
import kache.support.evict.CacheEvictContext;
import kache.support.expire.CacheExpire;
import kache.support.persist.InnerCachePersist;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class Cache<K,V> implements ICache<K,V> {

    private Map<K,V> map;
    private int sizeLimit;
    private ICacheEvict<K,V> evict;
    private ICacheExpire<K,V> expire;
    private ICacheLoad<K,V> load;
    private ICachePersist<K,V> persist;

    public Cache<K, V> map(Map<K, V> map) {
        this.map = map;
        return this;
    }

    public Cache<K, V> sizeLimit(int sizeLimit) {
        this.sizeLimit = sizeLimit;
        return this;
    }

    public Cache<K, V> evict(ICacheEvict<K,V> evict) {
        this.evict = evict;
        return this;
    }

    public Cache<K, V> expire(ICacheExpire<K,V> expire) {
        this.expire = expire;
        return this;
    }

    public Cache<K, V> load(ICacheLoad<K,V> load) {
        this.load = load;
        return this;
    }

    public Cache<K, V> persist(ICachePersist<K,V> persist) {
        this.persist = persist;
        return this;
    }

    public void init() {
        this.expire = new CacheExpire<>(this);
        this.load.load(this);
        // 初始化持久化
        if(this.persist != null) {
            new InnerCachePersist<>(this, persist);
        }
    }

    @Override
    public V get(Object key) {
        K genericKey = (K) key;
        this.expire.refreshExpire(Collections.singletonList(genericKey));
        return map.get(key);
    }

    @Override
    @CacheInterceptor(aof = true)
    public V put(K key, V value) {
        //1.1 尝试驱除
        CacheEvictContext<K,V> context = new CacheEvictContext<>();
        context.key(key).size(sizeLimit).cache(this);
        evict.evict(context);
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
        this.expire.refreshExpire(Collections.singletonList(genericKey));
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        this.refreshExpireAllKeys();
        return map.containsValue(value);
    }

    @Override
    @CacheInterceptor(aof = true)
    public V remove(Object key) {
        return map.remove(key);
    }

    @Override
    @CacheInterceptor(aof = true)
    public void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }

    @Override
    @CacheInterceptor(aof = true)
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
        Cache<K,V> cachePoxy = (Cache<K, V>) CacheProxy.getProxy(this);
        return cachePoxy.expireAt(key, expireTime);
    }

    @Override
    @CacheInterceptor(aof = true)
    public ICache<K, V> expireAt(K key, long timeInMills) {
        this.expire.expire(key,timeInMills);
        return this;
    }

    @Override
    public ICacheLoad<K, V> load() {
        return load;
    }

    @Override
    public ICacheExpire<K, V> expire() {
        return expire;
    }

    @Override
    public ICachePersist<K, V> persist() {
        return persist;
    }

    private void refreshExpireAllKeys() {
        this.expire.refreshExpire(map.keySet());
    }
}
