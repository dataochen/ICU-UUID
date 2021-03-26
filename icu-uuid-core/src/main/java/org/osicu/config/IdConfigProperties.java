package org.osicu.config;

import org.hibernate.validator.HibernateValidator;
import org.osicu.validate.CheckIdUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author osicu
 * @Description
 * @date: 2020/12/1 17:19
 * @since 1.0
 */
@ConfigurationProperties(prefix = "icu.uuid")
@Component
public class IdConfigProperties {
    /**
     * {@code snowFlake}和{@code localCache} 二选一
     * 如果配置 {@code snowFlake} 则路由成通过雪花算法来生成
     * 适用于长ID 推荐大于15时才使用
     */
    @NestedConfigurationProperty
    @Valid
    private SnowFlakeProperties snowFlake;
    /**
     * {@code snowFlake}和{@code localCache} 二选一
     * {@code localCache} 则路由成通过本地缓存递增算法来生成
     * 适用于短ID
     */
    @NestedConfigurationProperty
    @Valid
    private LocalCacheProperties localCache;
    /**
     * 系统标示 全局唯一  隔离不通业务系统 单个系统内保证id不重复
     * 必填
     */
    @NotBlank
    private String systemCode;
    /**
     * 扩展属性
     * 用户可以根据自己的需要 进行扩展
     */
    private Map<Object, Object> attributeMap;

    public SnowFlakeProperties getSnowFlake() {
        return snowFlake;
    }

    public void setSnowFlake(SnowFlakeProperties snowFlake) {
        this.snowFlake = snowFlake;
    }

    public LocalCacheProperties getLocalCache() {
        return localCache;
    }

    public void setLocalCache(LocalCacheProperties localCache) {
        this.localCache = localCache;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }


    public void checkParam() throws IllegalArgumentException {
//        validate注解 校验阶段
        check4Obj(this);
//        自定义注解校验阶段
        CheckIdUtil.checkParam4Annotation(this);
//        其他重要参数校验阶段
        if (Objects.isNull(snowFlake) == Objects.isNull(localCache)) {
            throw new IllegalArgumentException("snowFlake和localCache配置有且只能有一个");
        }
        if (Objects.nonNull(snowFlake)) {
            snowFlake.checkProperties();
        }
        if (Objects.nonNull(localCache)) {
            localCache.checkProperties();
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
            String message = validate.stream().map(x->x.getPropertyPath().toString()+x.getMessage()).collect(Collectors.joining(","));
            throw new IllegalArgumentException(message);
        }
    }
}
