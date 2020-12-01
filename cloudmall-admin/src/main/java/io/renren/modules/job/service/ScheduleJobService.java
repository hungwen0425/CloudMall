/**
 * Copyright (c) 2016-2019 人人開源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版權所有，侵權必究！
 */

package io.renren.modules.job.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.job.entity.ScheduleJobEntity;

import java.util.Map;

/**
 * 定時任務
 *
 * @author hungwen.tseng@gmail.com
 */
public interface ScheduleJobService extends IService<ScheduleJobEntity> {

	PageUtils queryPage(Map<String, Object> params);

	/**
	 * 保存定時任務
	 */
	void saveJob(ScheduleJobEntity scheduleJob);
	
	/**
	 * 更新定時任務
	 */
	void update(ScheduleJobEntity scheduleJob);
	
	/**
	 * 批量删除定時任務
	 */
	void deleteBatch(Long[] jobIds);
	
	/**
	 * 批量更新定時任務狀態
	 */
	int updateBatch(Long[] jobIds, int status);
	
	/**
	 * 立即執行
	 */
	void run(Long[] jobIds);
	
	/**
	 * 暂停運行
	 */
	void pause(Long[] jobIds);
	
	/**
	 * 恢复運行
	 */
	void resume(Long[] jobIds);
}
