package org.osicu;

/**
 * @author osicu
 * @Description
 * @date: 2020/11/3 15:15
 */
public interface IdGenerateInterface {
    /**
     * 下一个id
     * @return
     * @throws Exception
     */
    public long nextId() throws Exception;

    /**
     * 打印当前配置内容 ID生成的范围
     * 最小ID，最大ID，ID位数
     * 预估信息
     */
    public void printRange();

}
