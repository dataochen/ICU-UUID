package org.osicu.impl.localcache;

/**
 * @author osicu
 * @Description
 * @date: 2020/12/23 14:33
 */
public class IdTableCache {
    private String systemCode;
    private long maxNo;

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }


    public long getMaxNo() {
        return maxNo;
    }

    public void setMaxNo(long maxNo) {
        this.maxNo = maxNo;
    }
}
