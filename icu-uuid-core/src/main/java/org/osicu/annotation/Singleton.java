package org.osicu.annotation;

import java.lang.annotation.*;

/**
 * @author osicu
 * @Description
 * 单例模式标识
 * @date: 2021/2/7 16:42
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Singleton  {
}
