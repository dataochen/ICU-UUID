package org.osicu.config;

import org.osicu.annotation.IpPortList;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.concurrent.TimeUnit;

/**
 * @author osicu
 * @Description
 * @date: 2020/12/1 17:46
 */
public class ZkProperties {
    /**
     * 格式ip[:port],多个ip[:port]用逗号 , 分隔
     * 例子：127.0.0.100:2181,127.0.0.101:2181,127.0.0.101,127.0.0.101:
     * 如果没有端口号 默认80
     * 支持去重和trim()
     */
    @NotBlank
    @IpPortList
    private String zkAddress;

    /**
     * 锁超时时间
     * 启动时 用于分布式锁 来获取唯一workId
     * 单位：毫秒 {@link TimeUnit#MILLISECONDS}
     * 默认5秒
     */
    @Min(1)
    private long lockTimeOut=5*1000;



    public String getZkAddress() {
        return zkAddress;
    }

    public void setZkAddress(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    public long getLockTimeOut() {
        return lockTimeOut;
    }

    public void setLockTimeOut(long lockTimeOut) {
        this.lockTimeOut = lockTimeOut;
    }
}
