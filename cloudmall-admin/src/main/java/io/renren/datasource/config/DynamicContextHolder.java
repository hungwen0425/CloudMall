/**
 * Copyright (c) 2018 人人開源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版權所有，侵權必究！
 */

package io.renren.datasource.config;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 多資料源上下文
 *
 * @author hungwen.tseng@gmail.com
 */
public class DynamicContextHolder {
    @SuppressWarnings("unchecked")
    private static final ThreadLocal<Deque<String>> CONTEXT_HOLDER = new ThreadLocal() {
        @Override
        protected Object initialValue() {
            return new ArrayDeque();
        }
    };

    /**
     * 取得當前線程資料源
     *
     * @return 資料源名稱
     */
    public static String peek() {
        return CONTEXT_HOLDER.get().peek();
    }

    /**
     * 設定當前線程資料源
     *
     * @param dataSource 資料源名稱
     */
    public static void push(String dataSource) {
        CONTEXT_HOLDER.get().push(dataSource);
    }

    /**
     * 清空當前線程資料源
     */
    public static void poll() {
        Deque<String> deque = CONTEXT_HOLDER.get();
        deque.poll();
        if (deque.isEmpty()) {
            CONTEXT_HOLDER.remove();
        }
    }

}