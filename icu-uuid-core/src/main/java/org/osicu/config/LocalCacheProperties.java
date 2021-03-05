package org.osicu.config;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @author osicu
 * @Description 本地缓存实现ID
 * @date: 2020/12/1 17:25
 */
public class LocalCacheProperties extends IdPropertiesImpl {
    /**
     * 阈值 负载
     * 剩余ID低于此阈值数量 生成新的一批ID
     * 默认低于25%步长ID时 继续获取
     * 例子：如果步长是100 那么当{@code org.osicu.impl.localcache.AbstractLocalCacheGenerateImpl#arrayBlockingQueue}剩余ID
     * 不足25时，生成新的一批ID
     */
    private float thresholdValue = 0.75F;

    /**
     * 步长
     * 默认100
     */
    @Max(value = 1000)
    @Min(value = 100)
    private int stepNum = 100;


    public float getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(float thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public int getStepNum() {
        return stepNum;
    }

    public void setStepNum(int stepNum) {
        this.stepNum = stepNum;
    }

    @Override
    public void checkProperties() throws IllegalArgumentException {
        if (thresholdValue <= 0 || thresholdValue >= 1) {
            throw new IllegalArgumentException("thresholdValue 应该在0-1之前 不包含0和1");
        }
        super.checkProperties();

    }
}
