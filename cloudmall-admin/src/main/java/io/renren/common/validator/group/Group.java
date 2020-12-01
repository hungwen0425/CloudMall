/**
 * Copyright (c) 2016-2019 人人開源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版權所有，侵權必究！
 */

package io.renren.common.validator.group;

import javax.validation.GroupSequence;

/**
 * 定義校驗順序，如果 AddGroup 組失敗，則 UpdateGroup 組不會再校驗
 *
 * @author hungwen.tseng@gmail.com
 */
@GroupSequence({AddGroup.class, UpdateGroup.class})
public interface Group {

}
