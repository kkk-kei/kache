package kache.support.evict;

import kache.api.ICache;
import kache.api.ICacheEntry;
import kache.api.ICacheEvictContext;
import kache.core.Cache;
import kache.model.CacheEntry;
import kache.support.proxy.CacheProxy;
import kache.support.struct.ILruMap;
import kache.support.struct.LruMapDoubleLinkedList;

public class CacheEvictLRU2<K,V> extends AbstractCacheEvict<K,V>{

    private final ILruMap<K,V> firstLruMap;
    private final ILruMap<K,V> moreLruMap;

    public CacheEvictLRU2() {
        this.firstLruMap = new LruMapDoubleLinkedList<>();
        this.moreLruMap = new LruMapDoubleLinkedList<>();
    }

    @Override
    protected ICacheEntry<K, V> doEvict(ICacheEvictContext<K, V> context) {
        ICacheEntry<K,V> entry = null;
        final ICache<K, V> cache = context.cache();
        if(cache.size()>=context.size()){
            ICacheEntry<K, V> evictEntry = null;
            if(!firstLruMap.isEmpty()){
                evictEntry = firstLruMap.removeEldest();
            }else{
                evictEntry = moreLruMap.removeEldest();
            }
            K evictKey = evictEntry.key();
            Cache<K,V> cachePoxy = (Cache<K, V>) CacheProxy.getProxy(cache);
            V evictValue = cachePoxy.remove(evictKey);
            entry = CacheEntry.of(evictKey,evictValue);
        }
        return entry;
    }

    @Override
    public void updateKey(K key) {
        if(moreLruMap.contains(key)||firstLruMap.contains(key)){
            removeKey(key);
            moreLruMap.updateKey(key);
        }else{
            firstLruMap.updateKey(key);
        }
    }

    @Override
    public void removeKey(K key) {
        if(firstLruMap.contains(key)){
            firstLruMap.removeKey(key);
        }else{
            moreLruMap.removeKey(key);
        }
    }
}
