package org.osicu.client;

import org.osicu.config.IdConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnJava;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.system.JavaVersion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author osicu
 * @Description
 * @date: 2020/12/1 20:20
 */
@Configuration
@EnableConfigurationProperties(IdConfigProperties.class)
@ConditionalOnJava(value = JavaVersion.EIGHT)
@ConditionalOnProperty(prefix = "icu.uuid.",name = {"system-code"})
public class IdGeneratorConfiguration {
    @Autowired
    private IdConfigProperties idConfigProperties;

    @Bean(initMethod = "init", destroyMethod = "destroy")
    public IdGenerateClient idGenerateClient() {
        IdGenerateClient idGenerateClient = new IdGenerateClient(idConfigProperties);
        return idGenerateClient;
    }
}
