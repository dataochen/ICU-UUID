package org.osicu.route;

/**
 * @author chendatao
 */
public class CommonUtil {

    /**
     * 计算一个数的 大于这个数并且最小的2的幂次方的幂数
     * 比如1000 最近的是1024 返回值为10
     */
   public static  int bitUp(int num) {
        int n = num - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        int i = (n < 0) ? 1 : n + 1;
        return (int) (Math.log(i) / Math.log(2));
    }
}
