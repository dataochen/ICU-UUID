package org.osicu.config;

import org.hibernate.validator.HibernateValidator;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author osicu
 * @Description
 * @date: 2020/12/1 17:19
 */
@ConfigurationProperties(prefix = "icu.uuid")
@Component
public class IdConfigProperties {
    /**
     * 是否启动
     */
    private Boolean enable = true;

    @NestedConfigurationProperty
    private SnowFlakeProperties snowFlake;
    @NestedConfigurationProperty
    private LocalCacheProperties localCache;

    public SnowFlakeProperties getSnowFlake() {
        return snowFlake;
    }

    public void setSnowFlake(SnowFlakeProperties snowFlake) {
        this.snowFlake = snowFlake;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public LocalCacheProperties getLocalCache() {
        return localCache;
    }

    public void setLocalCache(LocalCacheProperties localCache) {
        this.localCache = localCache;
    }

    /**
     * 检查配置
     *
     * @throws IllegalArgumentException
     */
    public void checkParam() throws IllegalArgumentException {
        if (null != snowFlake) {
            String ips = snowFlake.getIps();
//                回写
            snowFlake.setIps(checkIps(ips));
            DataCenterProperties dataCenter = snowFlake.getDataCenter();
            if (null != dataCenter) {
                dataCenter.setIps(checkIps(dataCenter.getIps()));
            }
            ZkProperties zk = snowFlake.getZk();
//            if (null != zk) {
//                zk.setZkAddress(checkIpPorts(zk.getZkAddress()));
//            }
            check4Obj(snowFlake);
        }
        if (null != localCache) {
            String ips = localCache.getIps();
            localCache.setIps(checkIps(ips));
            DataCenterProperties dataCenter = localCache.getDataCenter();
            if (null != dataCenter) {
                dataCenter.setIps(checkIps(dataCenter.getIps()));
            }
            ZkProperties zk = localCache.getZk();
//            if (null != zk) {
//                zk.setZkAddress(checkIpPorts(zk.getZkAddress()));
//            }
            check4Obj(localCache);
        }
    }

    /**
     * 校验ips
     *
     * @param ips
     * @return
     */
    private String checkIps(String ips) {
        if (StringUtils.isEmpty(ips)) {
            return ips;
        }
        String[] split = ips.split(",");
        LinkedHashSet<String> setIp = new LinkedHashSet<>();
        setIp.addAll(Arrays.asList(split));
//                过滤处理 trim
        Set<String> collect = setIp.stream().map(String::trim).collect(Collectors.toSet());
        checkIp(collect);
        return String.join(",", collect);
    }

    /**
     * 校验是否填的是ip
     *
     * @param ips
     */
    private void checkIp(Set<String> ips) {
        String pattern = "((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}";
        Pattern r = Pattern.compile(pattern);
        boolean b = ips.stream().anyMatch(ip -> !"#".equals(ip) && !r.matcher(ip).matches());
        if (b) {
            throw new IllegalArgumentException("snowflake.ips配置的值格式不对；必须配置ip,多个ip用逗号 , 分隔");
        }
    }

    private String checkIpPorts(String ipPorts) {
        if (StringUtils.isEmpty(ipPorts)) {
            return ipPorts;
        }
        String[] split = ipPorts.split(",");
        LinkedHashSet<String> setIp = new LinkedHashSet<>();
        setIp.addAll(Arrays.asList(split));
//                过滤处理 trim
        Set<String> collect = setIp.stream().map(String::trim).collect(Collectors.toSet());
        checkIpPort(collect);
        return String.join(",", collect);
    }

    /**
     * 校验是否填的是ip:port
     *
     * @param ipPorts
     */
    private void checkIpPort(Set<String> ipPorts) {
        String pattern = "((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}(:\\d+)";
        Pattern r = Pattern.compile(pattern);
        boolean b = ipPorts.stream().anyMatch(ip -> !r.matcher(ip).matches());
        if (b) {
            throw new IllegalArgumentException("snowFlake.zk.zkAddress 配置的值格式不对；必须配置ip:port,多个ip:port用逗号 , 分隔");
        }
    }

    /**
     * 使用hibernate的注解来进行验证
     */
    private Validator validator = Validation.byProvider(HibernateValidator.class)
            .configure().failFast(true).buildValidatorFactory().getValidator();

    private void check4Obj(Object object) {
        Set<ConstraintViolation<Object>> validate = validator.validate(object);
        // 抛出检验异常
        if (validate.size() > 0) {
            String message = validate.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(","));
            throw new IllegalArgumentException(message);
        }
    }
}
