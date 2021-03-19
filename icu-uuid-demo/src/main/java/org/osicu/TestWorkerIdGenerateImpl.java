package org.osicu;

import org.osicu.spi.WorkeStrategyLegalInterface;

/**
 * @author chendatao
 */
public class TestWorkerIdGenerateImpl implements WorkeStrategyLegalInterface {
    @Override
    public void callBack4iiLegalWorkIdStrategy() {
        System.out.println("====test callBack4iiLegalWorkIdStrategy====");
    }
}
