package org.osicu;

import org.osicu.config.IdPropertiesBean;
import org.osicu.impl.WorkerIdInterface;

/**
 * @author chendatao
 * @since 1.0
 */
public class TestWorkerIdImpl implements WorkerIdInterface {
    @Override
    public long getWorkerId(IdPropertiesBean idPropertiesBean, String systemCode) throws Exception {
        System.out.println("=============test TestWorkerIdImpl============");
        return 0;
    }
}
