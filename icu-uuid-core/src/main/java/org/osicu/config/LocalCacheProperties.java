package org.osicu.config;

import org.osicu.route.CommonUtil;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

/**
 * @author osicu
 * @Description 本地缓存实现ID
 * @date: 2020/12/1 17:25
 */
public class LocalCacheProperties extends IdPropertiesBean {

    /**
     * 步长
     * 默认100
     */
    @Max(value = 10000)
    @Min(value = 100)
    private int stepNum = 100;
    /**
     * 自定义路径
     * 用于把数据存储在本地
     * 确保有读写权限 如果没有权限 会在启动时报IOException异常
     * 默认 /export/Data/id/
     */
    @Pattern(regexp = "^\\/(\\w+\\/?)+\\/$")
    private String basePath = "/export/Data/id/";
    /**
     * 每台机器支持的最大ID数量
     * 默认 17179869184 1百亿多
     */
    @Min(1)
    private long everyMaxNo = 1L << 34;

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }


    public int getStepNum() {
        return stepNum;
    }

    public void setStepNum(int stepNum) {
        this.stepNum = stepNum;
    }

    public long getEveryMaxNo() {
        return everyMaxNo;
    }

    public void setEveryMaxNo(long everyMaxNo) {
        this.everyMaxNo = everyMaxNo;
    }

    @Override
    public void checkProperties() throws IllegalArgumentException {
        int maxWorkerIdNum = getWorkIdStrategy().getMaxWorkerIdNum();
//        everyMaxNo+maxWorkerIdNum 是否超过Long类型的限制
        long maxNo = maxWorkerIdNum << CommonUtil.bitUp(everyMaxNo) | everyMaxNo;
        if (maxNo < 0||CommonUtil.bitUp(everyMaxNo)==0) {
            throw new IllegalArgumentException("参数异常 everyMaxNo或maxWorkerIdNum 设置过大，已超过Long类型的限制，请合理设置大小。");
        }
        super.checkProperties();

    }
}
