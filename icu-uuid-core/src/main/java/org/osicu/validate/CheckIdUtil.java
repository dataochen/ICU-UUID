package org.osicu.validate;

import org.osicu.annotation.IpList;
import org.osicu.annotation.IpPortList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author chendatao
 * @Description 校验工具类
 * @since 1.0
 */
public class CheckIdUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckIdUtil.class);

    /**
     * 通过自定义注解 校验对象
     *
     * @param obj
     * @throws IllegalArgumentException
     */
    public static void checkParam4Annotation(Object obj) throws IllegalArgumentException {
        if (Objects.isNull(obj)) {
            return;
        }
        Class<?> aClass = obj.getClass();
        while (!aClass.equals(Object.class)) {
            Field[] declaredFields = aClass.getDeclaredFields();
            // 获得访问私有实例域的权限
            AccessibleObject.setAccessible(declaredFields, true);
            for (Field declaredField : declaredFields) {
                try {
                    Object o = declaredField.get(obj);
                    if (Objects.isNull(o)) {
                        continue;
                    }
                    if (declaredField.isAnnotationPresent(IpList.class)) {
                        declaredField.set(obj, checkIps(o.toString(), declaredField.getName()));
                    } else if (declaredField.isAnnotationPresent(IpPortList.class)) {
                        declaredField.set(obj, checkIpPorts(o.toString(), declaredField.getName()));
                    }
                } catch (IllegalAccessException e) {
                    LOGGER.warn("校验参数报IllegalAccessException e={}", e);
                    continue;
                }
            }
            aClass = aClass.getSuperclass();
        }
    }

    /**
     * 校验ips
     *
     * @param ips
     * @return
     */
    private static String checkIps(String ips, String filedName) {
        if (StringUtils.isEmpty(ips)) {
            return ips;
        }
        String[] split = ips.split(",");
        LinkedHashSet<String> setIp = new LinkedHashSet<>();
        setIp.addAll(Arrays.asList(split));
//                过滤处理 trim
        Set<String> collect = setIp.stream().map(String::trim).collect(Collectors.toSet());
        checkIp(collect, filedName);
        return String.join(",", collect);
    }

    /**
     * 校验是否填的是ip
     *
     * @param ips
     */
    private static void checkIp(Set<String> ips, String filedName) {
        String pattern = "((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}";
        Pattern r = Pattern.compile(pattern);
        boolean b = ips.stream().anyMatch(ip -> !"#".equals(ip) && !r.matcher(ip).matches());
        if (b) {
            throw new IllegalArgumentException(filedName + " 配置的值格式不对；必须配置ip,多个ip用逗号 , 分隔");
        }
    }

    private static String checkIpPorts(String ipPorts, String filedName) {
        if (StringUtils.isEmpty(ipPorts)) {
            return ipPorts;
        }
        String[] split = ipPorts.split(",");
        LinkedHashSet<String> setIp = new LinkedHashSet<>();
        setIp.addAll(Arrays.asList(split));
//                过滤处理 trim
        Set<String> collect = setIp.stream().map(String::trim).collect(Collectors.toSet());
        return String.join(",", checkIpPort(collect, filedName));
    }

    /**
     * 校验是否填的是ip:port
     * 如果格式是ip： 或 ip 补全成ip:80
     * @param ipPorts
     */
    private static Set<String> checkIpPort(Set<String> ipPorts, String filedName) {
        String pattern = "((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}(?<port>:\\d*)?";
        Pattern r = Pattern.compile(pattern);
        HashSet<String> newSet = new HashSet<>(ipPorts.size());
        boolean b = ipPorts.stream().map(ip -> {
            Matcher matcher = r.matcher(ip);
            if (matcher.find()) {
                String port = matcher.group("port");
                if (StringUtils.isEmpty(port)) {
                    ip = ip + ":80";
                } else if (":".equals(port)) {
                    ip = ip + "80";
                }
            }
            newSet.add(ip);
            return ip;
        }).anyMatch(ip -> !r.matcher(ip).matches());
        if (b) {
            throw new IllegalArgumentException(filedName + " 配置的值格式不对；必须配置ip:port,多个ip:port用逗号 , 分隔");
        }
        return newSet;
    }

    public static void main(String[] args) {
        HashSet<String> strings = new HashSet<>();
        strings.add("127.0.0.1:8080");
        strings.add("127.0.0.1:80");
        strings.add("127.0.0.2");
        strings.add("127.0.0.1:1");
        strings.add("127.0.0.1:");
        Set<String> test = checkIpPort(strings, "test");
        test.forEach(System.out::println);
    }
}
