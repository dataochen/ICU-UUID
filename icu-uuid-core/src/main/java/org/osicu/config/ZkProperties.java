package org.osicu.config;

import javax.validation.constraints.NotBlank;

/**
 * @author osicu
 * @Description
 * @date: 2020/12/1 17:46
 */
public class ZkProperties {
    /**
     * 必须配置ip:port,多个ip:port用逗号 , 分隔
     * 例子：127.0.0.100:2181,127.0.0.101:2181
     */
    @NotBlank
    private String zkAddress;
    /**
     * 系统唯一标示 隔离不通业务系统 单个系统内保证id不重复
     */
    @NotBlank
    private String systemCode;
    /**
     * 锁超时时间
     * 默认5秒
     */
    private long lockTimeOut=5000;

    public String getZkAddress() {
        return zkAddress;
    }

    public void setZkAddress(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public long getLockTimeOut() {
        return lockTimeOut;
    }

    public void setLockTimeOut(long lockTimeOut) {
        this.lockTimeOut = lockTimeOut;
    }
}
