//package org.osicu.impl.localcache;
//
//import org.osicu.config.DataCenterProperties;
//import org.osicu.config.IdConfigProperties;
//import org.osicu.config.LocalCacheProperties;
//import org.osicu.route.CommonUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Objects;
//
///**
// * @author osicu
// * @Description 通过配置文件来获取机器的id
// * ip必须唯一
// * @date: 2020/11/3 16:54
// */
//public class DefaultLocalCache extends AbstractLocalCacheGenerateImpl {
//    private static final Logger log = LoggerFactory.getLogger(DefaultLocalCache.class);
//
//    public DefaultLocalCache(IdConfigProperties idConfigProperties) {
//        this.idConfigProperties = idConfigProperties;
//        init();
//    }
//
//    private Long workerIdCache;
//    private static String localIp;
//    private IdConfigProperties idConfigProperties;
//
//    static {
//        try {
//            localIp = InetAddress.getLocalHost().getHostAddress();
//        } catch (UnknownHostException e) {
//            throw new Error("不能够获取到本机ip，启动不成功。e=" + e.getMessage());
//        }
//    }
//
//    @Override
//    protected long getWorkerId()  {
//        return workerIdCache;
//    }
//
//    @Override
//    protected IdConfigProperties getLocalCacheProperties() {
//        return idConfigProperties;
//    }
//
//    @Override
//    public void printRange() {
//
//    }
//
//    private void init() {
//        LocalCacheProperties localCache = idConfigProperties.getLocalCache();
//        DataCenterProperties dataCenterProperties = localCache.getDataCenterProperties();
//        String ips1 = localCache.getWorkIdStrategy().getIps().getValue();
//        String[] split = ips1.split(",");
//        List<String> collect = Arrays.asList(split);
//        for (int i = 0; i < collect.size(); i++) {
//            if (localIp.equals(collect.get(i))) {
//                workerIdCache = (long) i;
//                if (Objects.nonNull(dataCenterProperties)) {
//                    int dataCenterIndex = dataCenterProperties.getDataCenterIndex();
//                    log.info("ID生成器启动，数据中心：{}", dataCenterIndex);
//                    workerIdCache = dataCenterIndex << CommonUtil.bitUp(localCache.getWorkIdStrategy().getMaxIpNum()) | workerIdCache;
//                }
//                return;
//            }
//        }
//        throw new IllegalArgumentException("没有找到ip");
//    }
//}
