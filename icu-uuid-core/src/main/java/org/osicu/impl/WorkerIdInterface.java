package org.osicu.impl;

import org.osicu.config.IdPropertiesBean;

/**
 * @author chendatao
 * @since 1.0
 */
public interface WorkerIdInterface {

    public long getWorkerId(IdPropertiesBean idPropertiesBean,String systemCode) throws Exception;
}
