package org.osicu.spi;

/**
 * @author chendatao
 */
public interface WorkeStrategyLegalInterface {
    /**
     * 当非法的workerId策略发生时 会回调此方法
     *
     * 回调方法
     */
    public void callBack4iiLegalWorkIdStrategy();

}
