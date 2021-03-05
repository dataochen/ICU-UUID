package org.osicu.annotation;

import java.lang.annotation.*;

/**
 * @author osicu
 * @Description
 * 标示 ip:port 集合 逗号分隔
 * 例子：127.0.0.100:2181,127.0.0.101:2181
 * 允许为空
 * 如果ip重复 会自动过滤
 * 如果含有空白 会自动trim {@link String#trim()}
 * 如果ip没有端口 默认80
 * @date: 2021/2/7 16:42
 * @since 1.0
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IpPortList {
}
