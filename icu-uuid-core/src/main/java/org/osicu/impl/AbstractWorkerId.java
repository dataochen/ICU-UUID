package org.osicu.impl;

/**
 * @author chendatao
 */
public abstract class AbstractWorkerId {
    /**
     * 获取当前服务器的唯一id
     *
     * @return
     * @throws Exception
     */
    protected abstract long getWorkerId() throws Exception;
}
