package com.ego.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/4/20
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@EnableDiscoveryClient
@EnableConfigServer
@SpringBootApplication
public class EgoConfigService {
    public static void main(String[] args) {
        SpringApplication.run(EgoConfigService.class, args);
    }

}
