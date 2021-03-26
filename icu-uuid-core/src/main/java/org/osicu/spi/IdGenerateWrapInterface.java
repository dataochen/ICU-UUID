package org.osicu.spi;

import org.osicu.IdGenerateInterface;
import org.osicu.config.IdConfigProperties;

/**
 * @author chendatao
 */
public interface IdGenerateWrapInterface extends IdGenerateInterface {
    /**
     * 传递参数
     *
     * @param idConfigProperties 配置参数
     */
    public void setIdConfigProperties(IdConfigProperties idConfigProperties);

    /**
     * 检查配置参数
     * @throws IllegalArgumentException 参数异常
     */
    public void checkParam() throws IllegalArgumentException;

}
