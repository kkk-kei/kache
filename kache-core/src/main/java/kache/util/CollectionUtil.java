package kache.util;

/*
 * Copyright (c)  2019. houbinbin Inc.
 * heaven All rights reserved.
 */

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class CollectionUtil {

    /**
     * collection util
     */
    private CollectionUtil() {
    }

    /**
     * 空列表
     */
    public static final List EMPTY_LIST = Collections.emptyList();

    /**
     * 是否为空
     *
     * @param collection 集合
     * @return {@code true} 是
     */
    public static boolean isEmpty(Collection collection) {
        return null == collection
                || collection.isEmpty();    //更有可读性
    }

    /**
     * 是否不为空
     *
     * @param collection 集合
     * @return {@code true} 是
     * @since 1.1.2
     */
    public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }

}
