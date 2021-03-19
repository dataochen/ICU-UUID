package org.osicu.spi;

import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Properties;

/**
 * SPI 动态获取自定义的实现类
 *
 * @author chendatao
 */
public class SpiFactory {
    private static final String BASE_PATH = "META-INF/icuuuid.properties";

    public static <T> T getObject(Class<T> tClass) throws Exception {
        Properties properties = PropertiesLoaderUtils.loadAllProperties(BASE_PATH);
        String implClassName = properties.getProperty(tClass.getName());
        if (StringUtils.isEmpty(implClassName)) {
            return null;
        }
        Class<?> aClass = null;
        try {
            aClass = Class.forName(implClassName);
            if (aClass.isInterface() || aClass.isAnnotation()) {
                return null;
            }
            if (!Arrays.asList(aClass.getInterfaces()).contains(tClass)) {
                return null;
            }
        } catch (ClassNotFoundException e) {
            return null;
        }
        return (T) aClass.newInstance();
    }

}
