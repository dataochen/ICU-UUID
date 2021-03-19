package org.osicu;

import org.osicu.config.IdConfigProperties;
import org.osicu.spi.IdGenerateWrapInterface;

/**
 * @author chendatao
 * @since 1.0
 */
public class TestIdGenerateImpl  implements IdGenerateWrapInterface {
    @Override
    public long nextId() throws Exception {
        System.out.println("======TestIdGenerateImpl nextId=========");
        return 0;
    }

    @Override
    public void printRange() {
        System.out.println("======TestIdGenerateImpl printRange=========");
    }

    @Override
    public void setIdConfigProperties(IdConfigProperties idConfigProperties) {
        System.out.println("======== setIdConfigProperties "+idConfigProperties.getSystemCode());
    }

    @Override
    public void checkParam() {
        System.out.println("============ checkParam  =======");
    }
}
