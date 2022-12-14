package kache.support.evict;

import kache.api.ICache;
import kache.api.ICacheEntry;
import kache.api.ICacheEvictContext;
import kache.core.Cache;
import kache.exception.CacheRuntimeException;
import kache.model.CacheEntry;
import kache.model.FreqNode;
import kache.support.proxy.CacheProxy;
import kache.util.CollectionUtil;
import kache.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

@Slf4j
public class CacheEvictLFU<K,V> extends AbstractCacheEvict<K,V>{

    private final Map<K, FreqNode<K,V>> keyMap;
    private final Map<Integer, LinkedHashSet<FreqNode<K,V>>> freqMap;
    private int minFreq;

    public CacheEvictLFU() {
        this.keyMap = new HashMap<>();
        this.freqMap = new HashMap<>();
        this.minFreq = 1;
    }

    @Override
    protected ICacheEntry<K, V> doEvict(ICacheEvictContext<K, V> context) {
        ICacheEntry<K, V> result = null;
        final ICache<K,V> cache = context.cache();
        // 超过限制，移除频次最低的元素
        if(cache.size() >= context.size()) {
            FreqNode<K,V> evictNode = this.getMinFreqNode();
            K evictKey = evictNode.key();
            Cache<K,V> cachePoxy = (Cache<K, V>) CacheProxy.getProxy(cache);
            V evictValue = cachePoxy.remove(evictKey);
            log.debug("淘汰最小频率信息, key: {}, value: {}, freq: {}",
                    evictKey, evictValue, evictNode.frequency());
            result = new CacheEntry<>(evictKey, evictValue);
        }
        return result;
    }

    @Override
    public void updateKey(final K key) {
        FreqNode<K, V> freqNode = keyMap.get(key);
        if(ObjectUtil.isNotNull(freqNode)){
            int freq = freqNode.frequency();
            LinkedHashSet<FreqNode<K, V>> oldSet = freqMap.get(freq);
            oldSet.remove(freqNode);
            if (minFreq == freq && oldSet.isEmpty()) {
                minFreq++;
                log.debug("minFreq 增加为：{}", minFreq);
            }
            freq++;
            freqNode.frequency(freq);
            addToFreqMap(freq,freqNode);
            keyMap.put(key,freqNode);
        }else{
            FreqNode<K, V> newNode = new FreqNode<>(key);
            addToFreqMap(1,newNode);
            minFreq = 1;
            this.keyMap.put(key,newNode);
        }
    }

    @Override
    public void removeKey(final K key) {
        FreqNode<K, V> freqNode = keyMap.remove(key);
        int freq = freqNode.frequency();
        LinkedHashSet<FreqNode<K, V>> set = freqMap.get(freq);
        set.remove(freqNode);
        log.debug("freq={} 移除元素节点：{}", freq, freqNode);
        if(CollectionUtil.isEmpty(set)&&minFreq == freq){
            minFreq--;
            log.debug("minFreq 降低为：{}", minFreq);
        }
    }

    private void addToFreqMap(final int frequency, FreqNode<K,V> freqNode) {
        LinkedHashSet<FreqNode<K,V>> set = freqMap.get(frequency);
        if (set == null) {
            set = new LinkedHashSet<>();
        }
        set.add(freqNode);
        freqMap.put(frequency, set);
        log.debug("freq={} 添加元素节点：{}", frequency, freqNode);
    }

    private FreqNode<K, V> getMinFreqNode() {
        LinkedHashSet<FreqNode<K,V>> set = freqMap.get(minFreq);
        if(CollectionUtil.isNotEmpty(set)) {
            return set.iterator().next();
        }
        throw new CacheRuntimeException("未发现最小频率的 Key");
    }
}
