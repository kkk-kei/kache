package kache.support.struct;

import kache.api.ICacheEntry;
import kache.exception.CacheRuntimeException;
import kache.model.CacheEntry;
import kache.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class LruMapDoubleLinkedList<K,V> implements ILruMap<K,V>{

    private Map<K,DoubleLinkedListNode<K,V>> indexMap;
    private DoubleLinkedListNode<K,V> head;
    private DoubleLinkedListNode<K,V> tail;

    public LruMapDoubleLinkedList() {
        this.indexMap = new HashMap<>();
        this.head = new DoubleLinkedListNode<>();
        this.tail = new DoubleLinkedListNode<>();

        this.head.next(this.tail);
        this.tail.pre(this.head);
    }

    @Override
    public ICacheEntry<K, V> removeEldest() {
        DoubleLinkedListNode<K, V> node = this.tail.pre();
        if(this.head==node){
            log.error("当前链表为空，无法进行删除");
            throw new CacheRuntimeException("不可删除头结点！");
        }
        return CacheEntry.of(node.key(), node.value());
    }

    @Override
    public void removeKey(K key) {
        DoubleLinkedListNode<K, V> node = indexMap.get(key);
        if(ObjectUtil.isNull(node)) return;
        DoubleLinkedListNode<K, V> pre = node.pre();
        DoubleLinkedListNode<K, V> next = node.next();

        pre.next(next);
        next.pre(pre);

        indexMap.remove(key);
    }

    @Override
    public void updateKey(K key) {
        removeKey(key);

        //TODO value呢？
        DoubleLinkedListNode<K, V> node = new DoubleLinkedListNode<>();
        node.key(key);

        DoubleLinkedListNode<K, V> next = head.next();
        head.next(node);
        node.pre(head);
        node.next(next);
        next.pre(node);

        indexMap.put(key,node);
    }

    @Override
    public boolean isEmpty() {
        return indexMap.isEmpty();
    }

    @Override
    public boolean contains(K key) {
        return indexMap.containsKey(key);
    }
}
