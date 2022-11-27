package kache.util;

/*
 * Copyright (c)  2019. houbinbin Inc.
 * heaven All rights reserved.
 */

import java.util.*;

public final class MapUtil {

    private MapUtil() {
    }

    /**
     * 判断map为空
     *
     * @param map map
     * @return {@code true} 为空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return null == map || 0 == map.size();
    }

    /**
     * 判断map为非空
     *
     * @param map map
     * @return {@code true} 非空
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

}
