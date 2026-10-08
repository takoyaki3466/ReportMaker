package org.takoyaki.reportmaker.util;

import java.util.List;

public final class ListOrderUtil {
    private ListOrderUtil() {
    }

    public static <T> int move(List<T> items, int fromIndex, int offset) {
        int targetIndex = fromIndex + offset;
        if (fromIndex < 0 || targetIndex < 0 || targetIndex >= items.size()) {
            return fromIndex;
        }
        T item = items.remove(fromIndex);
        items.add(targetIndex, item);
        return targetIndex;
    }
}
