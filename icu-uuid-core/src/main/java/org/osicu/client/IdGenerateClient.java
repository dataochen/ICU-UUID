package org.osicu.client;

import org.osicu.IdGenerateInterface;
import org.osicu.annotation.Singleton;
import org.osicu.config.IdConfigProperties;
import org.osicu.route.IdGenerateFactory;

/**
 *
 * @author chendatao
 * @since 1.0
 */
@Singleton
public class IdGenerateClient {
    public IdGenerateClient(IdConfigProperties idConfigProperties) {
        this.idConfigProperties = idConfigProperties;
    }

    private IdConfigProperties idConfigProperties;
    private IdGenerateInterface idGenerateInterface;

    /**
     * 是否可用
     * 0：不可用
     * 1：可用
     */
    private  volatile int status;

    public long nextId() throws Exception {
        if (status == 0) {
            throw new Exception("id生成器未成功启动，状态为不可用，请检查相应配置和日志。");
        }
        return idGenerateInterface.nextId();
    }

    public void printRange() {
        idGenerateInterface.printRange();
    }
    /**
     * 初始化
     */
    public void init() throws Exception {
//        校验配置
        idConfigProperties.checkParam();
//        2.路由算法
        idGenerateInterface = IdGenerateFactory.newInstance().getBean(idConfigProperties);
        status = 1;
    }

    public void destroy() {
//do something
        status = 0;
    }
}
