package kache.support.persist;

import kache.api.ICachePersist;

public final class CachePersists {
    private CachePersists(){}

    /**
     * 无操作
     * @param <K> key
     * @param <V> value
     * @return 结果
     * @since 0.0.8
     */
    public static <K,V> ICachePersist<K,V> none() {
        return new CachePersistNone<>();
    }

    /**
     * 无操作
     * @param <K> key
     * @param <V> value
     * @param path 文件路径
     * @return 结果
     * @since 0.0.8
     */
    public static <K,V> ICachePersist<K,V> dbJson(final String path) {
        return new CachePersistDbJson<>(path);
    }
}
