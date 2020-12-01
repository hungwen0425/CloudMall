/**
 * Copyright (c) 2018 人人開源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版權所有，侵權必究！
 */

package io.renren.datasource.annotation;

import java.lang.annotation.*;

/**
 * 多資料源註解
 *
 * @author hungwen.tseng@gmail.com
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DataSource {
    String value() default "";
}
