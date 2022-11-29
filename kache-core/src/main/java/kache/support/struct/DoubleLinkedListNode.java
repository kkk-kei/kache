package kache.support.struct;

public class DoubleLinkedListNode<K,V> {

    private K key;
    private V value;

    private DoubleLinkedListNode<K,V> pre;
    private DoubleLinkedListNode<K,V> next;

    public K key(){
        return key;
    }

    public DoubleLinkedListNode<K,V> key(K key){
        this.key = key;
        return this;
    }

    public V value(){
        return value;
    }

    public DoubleLinkedListNode<K, V> value(V value) {
        this.value = value;
        return this;
    }

    public DoubleLinkedListNode<K,V> pre(){
        return pre;
    }

    public DoubleLinkedListNode<K, V> pre(DoubleLinkedListNode<K, V> pre) {
        this.pre = pre;
        return this;
    }

    public DoubleLinkedListNode<K,V> next(){
        return next;
    }

    public DoubleLinkedListNode<K, V> next(DoubleLinkedListNode<K, V> next) {
        this.next = next;
        return this;
    }
}
