import org.osicu.client.IdGenerateClient;
import org.osicu.config.IdConfigProperties;
import org.osicu.config.LocalCacheProperties;

/**
 * @author osicu
 * @Description
 * @date: 2020/12/23 17:07
 */
public class IdTest {
    public static void main(String[] args) {
        IdConfigProperties idConfigProperties = new IdConfigProperties();
        idConfigProperties.setEnable(true);
        LocalCacheProperties localCacheProperties = new LocalCacheProperties();
        localCacheProperties.setIps("10.13.144.193");
        localCacheProperties.setSystemCode("mySystem");
        idConfigProperties.setLocalCache(localCacheProperties);
        IdGenerateClient idGenerateClient = new IdGenerateClient(idConfigProperties);
        idGenerateClient.init();
        for (int i = 0; i <1000 ; i++) {
            
        try {
            System.out.println(idGenerateClient.nextId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
    }
}
