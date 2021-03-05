package org.osicu.annotation;

import java.lang.annotation.*;

/**
 * @author osicu
 * @Description
 * 标示 ip 集合 逗号分隔
 * 例子：127.0.0.2,127.0.0.3,127.0.0.1,10.13.145.149
 * 如果ip重复 会自动过滤
 * 如果含有空白 会自动trim {@link String#trim()}
 * @date: 2021/2/7 16:42
 * @since 1.0
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IpList {
}
