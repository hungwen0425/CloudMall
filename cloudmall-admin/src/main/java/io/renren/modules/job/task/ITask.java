/**
 * Copyright (c) 2016-2019 人人開源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版權所有，侵權必究！
 */

package io.renren.modules.job.task;

/**
 * 定時任務接口，所有定時任務都要實現該接口
 *
 * @author hungwen.tseng@gmail.com
 */
public interface ITask {

    /**
     * 執行定時任務接口
     *
     * @param params   参數，多参數使用JSON資料
     */
    void run(String params);
}