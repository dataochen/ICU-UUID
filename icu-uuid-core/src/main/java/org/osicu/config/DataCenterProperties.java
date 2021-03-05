package org.osicu.config;

import javax.validation.constraints.Min;

/**
 * @author osicu
 * @Description
 * @date: 2020/12/1 17:41
 */
public class DataCenterProperties implements IdProperties{

    /**
     * dataCenterIndex从0开始
     */
    @Min(0)
    private int dataCenterIndex;
    /**
     * 最大数据中心数
     * 默认最大 4个数据中心
     */
    @Min(1)
    private int maxDateCenterNum = 1 << 2;


    public int getDataCenterIndex() {
        return dataCenterIndex;
    }

    public void setDataCenterIndex(int dataCenterIndex) {
        this.dataCenterIndex = dataCenterIndex;
    }


    public int getMaxDateCenterNum() {
        return maxDateCenterNum;
    }

    public void setMaxDateCenterNum(int maxDateCenterNum) {
        this.maxDateCenterNum = maxDateCenterNum;
    }

    @Override
    public void checkProperties() throws IllegalArgumentException {
        if (dataCenterIndex > maxDateCenterNum - 1) {
            throw new IllegalArgumentException("数据中心超限，你可以扩充maxDateCenterNum属性或者检查下dataCenterIndex是否正确。");
        }
    }
}
