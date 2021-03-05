package org.osicu.config;

import org.osicu.annotation.IpList;

import javax.validation.constraints.NotBlank;

/**
 * @author chendatao
 */
public class Ips implements IdProperties{
    /**
     * {@value ,}分隔符
     */
    protected static final String SEPARATOR = ",";
    /**
     * ip串的值
     * 格式ip1,ip2
     */
    @IpList
    @NotBlank
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void checkProperties() throws IllegalArgumentException {

    }
}
